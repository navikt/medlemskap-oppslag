package no.nav.medlemskap.routes

import io.ktor.application.call
import io.ktor.auth.authenticate
import io.ktor.auth.authentication
import io.ktor.auth.jwt.JWTPrincipal
import io.ktor.features.BadRequestException
import io.ktor.features.callId
import io.ktor.request.receive
import io.ktor.response.respond
import io.ktor.routing.Routing
import io.ktor.routing.post
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import mu.KotlinLogging
import no.nav.medlemskap.clients.Services
import no.nav.medlemskap.common.apiCounter
import no.nav.medlemskap.common.exceptions.KonsumentIkkeFunnet
import no.nav.medlemskap.common.ytelseCounter
import no.nav.medlemskap.config.Configuration
import no.nav.medlemskap.domene.*
import no.nav.medlemskap.domene.Ytelse.Companion.metricName
import no.nav.medlemskap.regler.common.Resultat
import no.nav.medlemskap.regler.v1.Hovedregler
import java.time.LocalDate
import java.time.LocalDateTime
import java.util.*
import javax.xml.ws.soap.SOAPFaultException

private val logger = KotlinLogging.logger { }

private val secureLogger = KotlinLogging.logger("tjenestekall")


fun Routing.evalueringRoute(
        services: Services,
        configuration: Configuration) {

    authenticate("azureAuth") {
        post("/") {
            apiCounter().increment()

            val callerPrincipal: JWTPrincipal = call.authentication.principal()!!
            val azp = callerPrincipal.payload.getClaim("azp").asString()
            secureLogger.info("EvalueringRoute: azp-claim i principal-token: {}", azp)

            val request = validerRequest(call.receive())
            val callId = call.callId ?: UUID.randomUUID().toString()

            val datagrunnlag = createDatagrunnlag(
                    fnr = request.fnr,
                    callId = callId,
                    periode = request.periode,
                    brukerinput = request.brukerinput,
                    services = services,
                    clientId = azp,
                    ytelseFraRequest = request.ytelse)
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
}

fun Routing.evalueringTestRoute(
        services: Services,
        configuration: Configuration) {
    logger.info("autentiserer IKKE kallet")
    post("/") {
        apiCounter().increment()
        val request = validerRequest(call.receive())
        val callId = call.callId ?: UUID.randomUUID().toString()

        val datagrunnlag = createDatagrunnlag(
                fnr = request.fnr,
                callId = callId,
                periode = request.periode,
                brukerinput = request.brukerinput,
                services = services,
                clientId = null,
                ytelseFraRequest = request.ytelse)
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

private fun validerRequest(request: Request): Request {
    if (request.periode.tom.isBefore(request.periode.fom)) {
        throw BadRequestException("Periode tom kan ikke være før periode fom")
    }

    if (request.periode.fom.isBefore(LocalDate.of(2016, 1, 1))) {
        throw BadRequestException("Periode fom kan ikke være før 2016-01-01")
    }

    if (!gyldigFnr(request.fnr)) {
        throw BadRequestException("Ugyldig fødselsnummer")
    }

    return request
}

private suspend fun createDatagrunnlag(
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

fun finnYtelse(ytelseFraRequest: Ytelse?, clientId: String?) =
        (ytelseFraRequest ?: Ytelse.fromClientId(clientId))
                ?: throw KonsumentIkkeFunnet("Fant ikke clientId i mapping til ytelse. Ta kontakt med medlemskap-teamet for tilgang til tjenesten.")

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


private fun fraOgMedDatoForArbeidsforhold(periode: InputPeriode) = periode.fom.minusYears(1).minusDays(1)

private fun evaluerData(datagrunnlag: Datagrunnlag): Resultat =
        Hovedregler(datagrunnlag).kjørHovedregler()

private fun Resultat.sisteRegel() =
        if (this.delresultat.isEmpty()) {
            this
        } else {
            this.delresultat.last()
        }

