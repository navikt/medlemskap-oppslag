package no.nav.medlemskap.routes

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import mu.KotlinLogging
import no.bekk.bekkopen.person.FodselsnummerValidator
import no.nav.medlemskap.clients.Services
import no.nav.medlemskap.common.ytelseCounter
import no.nav.medlemskap.domene.*
import no.nav.medlemskap.domene.Ytelse.Companion.metricName
import no.nav.medlemskap.domene.barn.DataOmBarn
import no.nav.medlemskap.domene.barn.PersonhistorikkBarn
import no.nav.medlemskap.domene.ektefelle.DataOmEktefelle
import no.nav.medlemskap.domene.ektefelle.PersonhistorikkEktefelle
import no.nav.medlemskap.regler.funksjoner.RelasjonFunksjoner
import no.nav.medlemskap.regler.funksjoner.RelasjonFunksjoner.filtrerBarnUnder25Aar
import java.util.*
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

    val dataOmEktefelle : DataOmEktefelle?
    val dataOmBrukersBarn: List<DataOmBarn>?

    val aktorIder = services.pdlService.hentAlleAktorIder(fnr, callId)
    val personHistorikkFraPdl = hentPersonhistorikkFraPdl(services, fnr, callId)
    val medlemskapsunntakRequest = async { services.medlService.hentMedlemskapsunntak(fnr, callId) }
    val arbeidsforholdRequest = async { services.aaRegService.hentArbeidsforhold(fnr, callId, fraOgMedDatoForArbeidsforhold(periode), periode.tom) }
    val journalPosterRequest = async { services.safService.hentJournaldata(fnr, callId) }
    val gosysOppgaver = async { services.oppgaveService.hentOppgaver(aktorIder, callId) }
  //  val personhistorikkForFamilie = hentPersonhistorikkForFamilieAsync(personHistorikkFraPdl, services, periode)


    val fnrTilBarn = hentFnrTilBarn(personHistorikkFraPdl.familierelasjoner)
    dataOmBrukersBarn = if (fnr.isNotEmpty()) hentDataOmBarn(fnrTilBarn, services, callId) else null


    val fnrTilEktefelle = hentFnrTilEktefelle(personHistorikkFraPdl)
    dataOmEktefelle = if (fnrTilEktefelle.isNullOrEmpty()) hentDataOmEktefelle(fnrTilEktefelle, services, callId, periode) else null


    val medlemskap = medlemskapsunntakRequest.await()
    val arbeidsforhold = arbeidsforholdRequest.await()
    val journalPoster = journalPosterRequest.await()
    val oppgaver = gosysOppgaver.await()
    val ytelse: Ytelse = finnYtelse(ytelseFraRequest, clientId)

    ytelseCounter(ytelse.metricName()).increment()

    Datagrunnlag(
            periode = periode,
            brukerinput = brukerinput,
            pdlpersonhistorikk = personHistorikkFraPdl,
            medlemskap = medlemskap,
            arbeidsforhold = arbeidsforhold,
            oppgaver = oppgaver,
            dokument = journalPoster,
            ytelse = ytelse,
            dataOmBarn = dataOmBrukersBarn,
            dataOmEktefelle = dataOmEktefelle
    )
}

suspend fun hentDataOmBarn(fnrBarn: List<String>, services: Services, callId: String): List<DataOmBarn> {

    return fnrBarn
            .map { DataOmBarn(
                    personhistorikkBarn = hentPersonHistorikkForBarn(it, services, callId))
            }
}

private suspend fun CoroutineScope.hentDataOmEktefelle(fnrTilEktefelle: String?, services: Services, callId: String, periode: InputPeriode): DataOmEktefelle? {
    if (fnrTilEktefelle != null) {
        val personhistorikkEktefelle = hentPersonHistorikkForEktefelle(fnrTilEktefelle, services, callId)
        val arbeidsforholdEktefelleReguest = async { services.aaRegService.hentArbeidsforhold(fnrTilEktefelle, callId, fraOgMedDatoForArbeidsforhold(periode), periode.tom) }
        val arbeidsforholdEktefelle = arbeidsforholdEktefelleReguest.await()

        return DataOmEktefelle(
                personhistorikkEktefelle = personhistorikkEktefelle,
                arbeidsforholdEktefelle = arbeidsforholdEktefelle)
    }
    return null
}

suspend fun hentPersonHistorikkForEktefelle(fnrTilEktefelle: String, services: Services, callId: String): PersonhistorikkEktefelle {
    return services.pdlService.hentPersonHistorikkTilEktefelle(fnrTilEktefelle, callId)
}

suspend fun hentPersonHistorikkForBarn(fnrTilBarn: String, services: Services, callId: String): PersonhistorikkBarn {
    return services.pdlService.hentPersonHistorikkTilBarn(fnrTilBarn, callId)

}

//Midlertidig kode, ekstra feilhåndtering fordi integrasjonen vår mot PDL ikke er helt 100% ennå..
private suspend fun hentPersonhistorikkFraPdl(services: Services, fnr: String, callId: String): Personhistorikk {
    return services.pdlService.hentPersonHistorikk(fnr, callId)
}

private fun hentFnrTilEktefelle(personHistorikkFraPdl: Personhistorikk?): String? {
    val fnrTilEktefelle =
            personHistorikkFraPdl?.sivilstand
                    ?.filter { it.relatertVedSivilstand != null }
                    ?.filter { FodselsnummerValidator.isValid(it.relatertVedSivilstand) }
                    ?.filter { it.type == Sivilstandstype.GIFT || it.type == Sivilstandstype.REGISTRERT_PARTNER }
                    ?.map { it.relatertVedSivilstand }
                    ?.lastOrNull()
    return fnrTilEktefelle
}

fun Familierelasjon.erBarn() = this.relatertPersonsRolle == Familierelasjonsrolle.BARN

fun hentFnrTilBarn(familierelasjoner: List<Familierelasjon>): List<String> {
    return familierelasjoner
            .filter { FodselsnummerValidator.isValid(it.relatertPersonsIdent) }
            .filter { it.erBarn() }
            .filter { it.relatertPersonsIdent.filtrerBarnUnder25Aar()}
            .map { it.relatertPersonsIdent }
}


private fun fraOgMedDatoForArbeidsforhold(periode: InputPeriode) = periode.fom.minusYears(1).minusDays(1)


