package no.nav.medlemskap.routes

import io.ktor.application.call
import io.ktor.auth.authenticate
import io.ktor.features.BadRequestException
import io.ktor.features.callId
import io.ktor.request.receive
import io.ktor.response.respond
import io.ktor.routing.Routing
import io.ktor.routing.post
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import mu.KotlinLogging
import no.nav.medlemskap.common.apiCounter
import no.nav.medlemskap.config.Configuration
import no.nav.medlemskap.domene.*
import no.nav.medlemskap.regler.common.Personfakta
import no.nav.medlemskap.regler.common.Resultat
import no.nav.medlemskap.regler.v1.Hovedregler
import no.nav.medlemskap.services.Services
import java.time.LocalDate
import java.time.LocalDateTime
import java.util.*

private val logger = KotlinLogging.logger { }

private val secureLogger = KotlinLogging.logger("tjenestekall")


fun Routing.evalueringRoute(
        services: Services,
        useAuthentication: Boolean,
        configuration: Configuration) {

    fun receiveAndRespond() {
        post("/") {
            apiCounter().increment()
            val request = validerRequest(call.receive())
            val callId = call.callId ?: UUID.randomUUID().toString()
            val aktorId = services.pdlService.hentAktorId(request.fnr, callId)
            val datagrunnlag = createDatagrunnlag(
                    fnr = request.fnr,
                    callId = callId,
                    aktoer = aktorId,
                    periode = request.periode,
                    brukerinput = request.brukerinput,
                    services = services)
            val resultat = evaluerData(datagrunnlag)
            val response = Response(
                    tidspunkt = LocalDateTime.now(),
                    versjonRegler = "v1",
                    versjonTjeneste = configuration.commitSha,
                    datagrunnlag = datagrunnlag,
                    resultat = resultat
            )
            secureLogger.info("{} konklusjon gitt for bruker {} på regel {}", resultat.svar.name, request.fnr, resultat.sisteRegel())
            secureLogger.info("For bruker {} er responsen {}", request.fnr, response)

            call.respond(response)
        }
    }

    if (useAuthentication) {
        authenticate {
            receiveAndRespond()
        }
    } else {
        receiveAndRespond()
    }
}

private fun validerRequest(request: Request): Request {
    if (request.periode.tom.isBefore(request.periode.fom)) {
        throw BadRequestException("Periode tom kan ikke være før periode fom")
    }

    if (request.periode.fom.isBefore(LocalDate.of(2016, 1, 1))) {
        throw BadRequestException("Periode fom kan ikke være før 2016-01-01")
    }

    if (!gyldigFnr(request.fnr)) {
        throw BadRequestException("Ugyldig fødslesnummer")
    }

    return request
}

private suspend fun createDatagrunnlag(
        fnr: String,
        callId: String,
        aktoer: String,
        periode: InputPeriode,
        brukerinput: Brukerinput,
        services: Services): Datagrunnlag = coroutineScope {


    val pdlHistorikkRequest = services.pdlService.hentPersonHistorikk(fnr, callId)
    val historikkFraTpsRequest = async { services.personService.personhistorikk(fnr, periode.fom) }
    val medlemskapsunntakRequest = async { services.medlService.hentMedlemskapsunntak(fnr, callId) }
    val arbeidsforholdRequest = async { services.aaRegService.hentArbeidsforhold(fnr, callId, fraOgMedDatoForArbeidsforhold(periode), periode.tom) }
    val inntektListeRequest = async { services.inntektService.hentInntektListe(fnr, callId, periode.fom, periode.tom) }
    val journalPosterRequest = async { services.safService.hentJournaldata(fnr, callId) }
    val gosysOppgaver = async { services.oppgaveService.hentOppgaver(aktoer, callId) }
    val familierelasjonBarnPersonHistorikkRequest = async {
        folkeregistrertFamilierelasjonBarn(pdlHistorikkRequest, periode, services)
    }
    val sivilstandPersonHistorikkRequest = async {
        folkeregistrertSivilstand(pdlHistorikkRequest, periode, services)
    }

    val historikkFraTps = historikkFraTpsRequest.await()
    val medlemskapsunntak = medlemskapsunntakRequest.await()
    val arbeidsforhold = arbeidsforholdRequest.await()
    val inntektListe = inntektListeRequest.await()
    val journalPoster = journalPosterRequest.await()
    val oppgaver = gosysOppgaver.await()
    val familierelasjonBarnPersonHistorikk = familierelasjonBarnPersonHistorikkRequest.await()
    val sivilstandPersonHistorikk = sivilstandPersonHistorikkRequest.await()

    val familierelasjonOgSivilstand = familierelasjonBarnPersonHistorikk.plus(sivilstandPersonHistorikk)



    Datagrunnlag(
            periode = periode,
            brukerinput = brukerinput,
            personhistorikk = historikkFraTps,
            pdlpersonhistorikk = pdlHistorikkRequest,
            medlemskapsunntak = medlemskapsunntak,
            arbeidsforhold = arbeidsforhold,
            inntekt = inntektListe,
            oppgaver = oppgaver,
            dokument = journalPoster,
            familierelasjonOgSivilstand = familierelasjonOgSivilstand
    )


}

suspend fun folkeregistrertFamilierelasjonBarn(personhistorikk: Personhistorikk, periode: InputPeriode, services: Services): List<PersonhistorikkRelatertPerson> {
    return personhistorikk.familierelasjoner
            .filter { it.erBarn() }
            .filter { barnUnder18aar(it, services, periode) }
            .map { hentRelatertPersonHistorikk(it, periode, services) }
}

suspend fun barnUnder18aar(familierelasjon: Familierelasjon, services: Services, periode: InputPeriode): Boolean {
    val aarstall = periode.tom.year
    val aldersGrense = 18
    val hentFoedselsaarTilBarn = services.pdlService.hentFoedselsaar(familierelasjon.relatertPersonIdent, UUID.randomUUID().toString())

    return (aarstall - hentFoedselsaarTilBarn) < aldersGrense
}

suspend fun hentRelatertPersonHistorikk(familierelasjon: Familierelasjon, periode: InputPeriode, services: Services): PersonhistorikkRelatertPerson {
    return try{
        services.personService.personhistorikkRelatertPerson(familierelasjon.relatertPersonIdent, periode.fom)
    } catch (e: Exception) {
        logger.error { e }
        PersonhistorikkRelatertPerson(
                bostedsadresser = emptyList(),
                personstatuser = emptyList(),
                postadresser = emptyList(),
                midlertidigAdresser = emptyList()
        )
    }
}

suspend fun folkeregistrertSivilstand(personhistorikk: Personhistorikk, periode: InputPeriode, services: Services) : List<PersonhistorikkRelatertPerson> {
    return personhistorikk.sivilstand.map { services.personService.personhistorikkRelatertPerson(it.relatertVedSivilstand, periode.fom) }
}

private fun fraOgMedDatoForArbeidsforhold(periode: InputPeriode) = periode.fom.minusYears(1).minusDays(1)

private fun evaluerData(datagrunnlag: Datagrunnlag): Resultat =
        Hovedregler(Personfakta.initialiserFakta(datagrunnlag)).kjørHovedregler()

private fun Resultat.sisteRegel() =
        if (this.delresultat.isEmpty()) {
            this
        } else {
            this.delresultat.last()
        }

private fun Familierelasjon.erBarn() = this.relatertPersonsRolle == Familierelasjonsrolle.BARN
