package no.nav.medlemskap.routes

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import mu.KotlinLogging
import no.nav.medlemskap.clients.Services
import no.nav.medlemskap.common.ytelseCounter
import no.nav.medlemskap.domene.*
import no.nav.medlemskap.domene.Ytelse.Companion.metricName
import javax.xml.ws.soap.SOAPFaultException


private val logger = KotlinLogging.logger { }

private val secureLogger = KotlinLogging.logger("tjenestekall")

suspend fun defaultCreateDatagrunnlag(
        fnr: String,
        callId: String,
        periode: InputPeriode,
        brukerinput: Brukerinput,
        services: Services,
        clientId: String?,
        ytelseFraRequest: Ytelse?): Datagrunnlag = coroutineScope {

    val aktorIder = services.pdlService.hentAlleAktorIder(fnr, callId)
    val personHistorikkFraPdl = hentPersonhistorikkFraPdl(services, fnr, callId)
    val historikkFraTpsRequest = async { services.personService.personhistorikk(fnr, periode.fom) }
    val medlemskapsunntakRequest = async { services.medlService.hentMedlemskapsunntak(fnr, callId) }
    val arbeidsforholdRequest = async { services.aaRegService.hentArbeidsforhold(fnr, callId, fraOgMedDatoForArbeidsforhold(periode), periode.tom) }
    val journalPosterRequest = async { services.safService.hentJournaldata(fnr, callId) }
    val gosysOppgaver = async { services.oppgaveService.hentOppgaver(aktorIder, callId) }

    val personhistorikkForFamilie = hentPersonhistorikkForFamilieAsync(personHistorikkFraPdl, services, periode)

    val historikkFraTps = historikkFraTpsRequest.await()
    val medlemskap = medlemskapsunntakRequest.await()
    val arbeidsforhold = arbeidsforholdRequest.await()
    val journalPoster = journalPosterRequest.await()
    val oppgaver = gosysOppgaver.await()
    val ytelse: Ytelse = finnYtelse(ytelseFraRequest, clientId)
    ytelseCounter(ytelse.metricName()).increment()

    Datagrunnlag(
            periode = periode,
            brukerinput = brukerinput,
            personhistorikk = historikkFraTps,
            pdlpersonhistorikk = personHistorikkFraPdl,
            medlemskap = medlemskap,
            arbeidsforhold = arbeidsforhold,
            oppgaver = oppgaver,
            dokument = journalPoster,
            ytelse = ytelse,
            personHistorikkRelatertePersoner = personhistorikkForFamilie
    )
}


private fun fraOgMedDatoForArbeidsforhold(periode: InputPeriode) = periode.fom.minusYears(1).minusDays(1)


//Midlertidig kode, ekstra feilhåndtering fordi integrasjonen vår mot PDL ikke er helt 100% ennå..
private suspend fun hentPersonhistorikkFraPdl(services: Services, fnr: String, callId: String): Personhistorikk? {
    return try {
        services.pdlService.hentPersonHistorikk(fnr, callId)
    } catch (e: Exception) {
        logger.error("hentPersonHistorikk feiler", e)
        secureLogger.error("hentPersonHistorikk feiler for fnr {}", fnr, e)
        null
    }
}

//Midlertidig kode, ekstra feilhåndtering fordi integrasjonen vår mot PDL ikke er helt 100% ennå..
private suspend fun CoroutineScope.hentPersonhistorikkForFamilieAsync(personHistorikkFraPdl: Personhistorikk?, services: Services, periode: InputPeriode): List<PersonhistorikkRelatertPerson> {
    return personHistorikkFraPdl?.let {
        try {
            services.personService.hentPersonhistorikkForRelevantFamilie(it, periode)
        } catch (sfe: SOAPFaultException) {
            logger.error("SoapFault under henting av personhistorikk for familie", sfe)
            //Må forstå mer av TPS svarte med FEIL, folgende status: S016007F og folgende melding: FØDSELSNR ER IKKE ENTYDIG
            secureLogger.error("SoapFault mot TPS for familierelasjoner {} og sivilstand {}", it.familierelasjoner, it.sivilstand, sfe)
            emptyList<PersonhistorikkRelatertPerson>()
        } catch (e: Exception) {
            logger.error("Feilet under henting av personhistorikk for familie", e)
            emptyList<PersonhistorikkRelatertPerson>()
        }
    } ?: emptyList<PersonhistorikkRelatertPerson>()
}