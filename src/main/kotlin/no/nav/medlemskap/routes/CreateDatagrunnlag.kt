package no.nav.medlemskap.routes

import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import mu.KotlinLogging
import no.nav.medlemskap.clients.Services
import no.nav.medlemskap.common.ytelseCounter
import no.nav.medlemskap.domene.Datagrunnlag
import no.nav.medlemskap.domene.Kontrollperiode.Companion.startDatoForYtelse
import no.nav.medlemskap.domene.Request
import no.nav.medlemskap.domene.Ytelse
import no.nav.medlemskap.domene.Ytelse.Companion.name
import no.nav.medlemskap.domene.arbeidsforhold.Arbeidsforhold.Companion.fraOgMedDatoForArbeidsforhold
import no.nav.medlemskap.domene.personhistorikk.Personhistorikk
import no.nav.medlemskap.services.FamilieService

private val secureLogger = KotlinLogging.logger("tjenestekall")

suspend fun defaultCreateDatagrunnlag(
    request: Request,
    callId: String,
    services: Services,
    clientId: String?
): Datagrunnlag = coroutineScope {
    val familieService = FamilieService(services.aaRegService, services.pdlService)
    val startDatoForYtelse = startDatoForYtelse(request.periode, request.førsteDagForYtelse)

    val arbeidsforholdRequest = async {
        services.aaRegService.hentArbeidsforhold(
            request.fnr,
            callId,
            fraOgMedDatoForArbeidsforhold(startDatoForYtelse),
            request.periode.tom
        )
    }

    val aktorIder = null // = services.pdlService.hentAlleAktorIder(request.fnr, callId)
    val medlemskapsunntakRequest = async { services.medlService.hentMedlemskapsunntak(request.fnr, callId) }
    // dette kallet øker med 4 tråder for hver request
    val journalPosterRequest = async { services.safService.hentJournaldata(request.fnr, callId) }
    val gosysOppgaver = null // = async { services.oppgaveService.hentOppgaver(aktorIder, callId) }

    val personHistorikk = null // services.pdlService.hentPersonHistorikkTilBruker(request.fnr, callId)

    val dataOmBrukersBarn = null
    /*
    val dataOmBrukersBarn = familieService.hentDataOmBarn(
        ForelderBarnRelasjon.hentFnrTilBarn(
            personHistorikk.forelderBarnRelasjon,
            startDatoForYtelse
        ),
        callId
    )

     */

    val dataOmEktefelle = null
    /*

    val dataOmEktefelle = familieService.hentDataOmEktefelle(
        fnrTilEktefelle = ForelderBarnRelasjon.hentFnrTilEktefelle(personHistorikk),
        periode = request.periode,
        startDatoForYtelse = startDatoForYtelse,
        callId = callId
    )
    */

    val medlemskap = medlemskapsunntakRequest.await()
    val arbeidsforhold = arbeidsforholdRequest.await()
    val journalPoster = journalPosterRequest.await()
    // val oppgaver = gosysOppgaver.await()
    val ytelse: Ytelse = finnYtelse(request.ytelse, clientId)

    val oppholdstillatelse = null
    /*
    val oppholdstillatelse = if (FeatureToggles.FEATURE_UDI.enabled &&
        personHistorikk.statsborgerskap.erAnnenStatsborger(startDatoForYtelse)
    ) {
        val oppholdsstatusRequest = async {
            try {
                services.udiClient.oppholdstillatelse(UdiRequest(request.fnr), callId)
            } catch (hpf: HentPersonstatusFault) {
                secureLogger.warn {
                    kv("fnr", request.fnr)
                    kv("NAV-call-id", callId)
                    kv(
                        "datagrunnlag",
                        objectMapper.writeValueAsString(
                            Datagrunnlag(
                                fnr = request.fnr,
                                periode = request.periode,
                                førsteDagForYtelse = request.førsteDagForYtelse,
                                brukerinput = request.brukerinput,
                                pdlpersonhistorikk = personHistorikk,
                                medlemskap = medlemskap,
                                arbeidsforhold = arbeidsforhold,
                                oppgaver = oppgaver,
                                dokument = journalPoster,
                                ytelse = ytelse,
                                dataOmBarn = dataOmBrukersBarn,
                                dataOmEktefelle = dataOmEktefelle,
                                overstyrteRegler = request.overstyrteRegler,
                                oppholdstillatelse = null
                            )
                        )
                    )
                }
                throw hpf
            }
        }
        oppholdsstatusRequest.await()
    } else {
        null
    }
    */

    ytelseCounter(ytelse.name()).increment()

    Datagrunnlag(
        fnr = request.fnr,
        periode = request.periode,
        førsteDagForYtelse = request.førsteDagForYtelse,
        brukerinput = request.brukerinput,
        pdlpersonhistorikk = Personhistorikk(
            emptyList(), emptyList(), emptyList(), emptyList(), emptyList(), emptyList(),
            emptyList()
        ), // personHistorikk,
        medlemskap = medlemskap,
        arbeidsforhold = arbeidsforhold,
        oppgaver = emptyList(),
        dokument = journalPoster,
        ytelse = ytelse,
        dataOmBarn = dataOmBrukersBarn,
        dataOmEktefelle = dataOmEktefelle,
        overstyrteRegler = request.overstyrteRegler,
        oppholdstillatelse = oppholdstillatelse
    )
}
