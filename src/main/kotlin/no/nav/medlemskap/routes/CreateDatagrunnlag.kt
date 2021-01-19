package no.nav.medlemskap.routes

import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import no.nav.medlemskap.clients.Services
import no.nav.medlemskap.common.FeatureToggles
import no.nav.medlemskap.common.endretStatsborgerskapSisteÅretCounter
import no.nav.medlemskap.common.flereStatsborgerskapCounter
import no.nav.medlemskap.common.ytelseCounter
import no.nav.medlemskap.domene.*
import no.nav.medlemskap.domene.Kontrollperiode.Companion.startDatoForYtelse
import no.nav.medlemskap.domene.Ytelse.Companion.name
import no.nav.medlemskap.domene.arbeidsforhold.Arbeidsforhold.Companion.fraOgMedDatoForArbeidsforhold
import no.nav.medlemskap.domene.arbeidsforhold.Arbeidsforhold.Companion.registrerAntallAnsatteHosJuridiskEnhet
import no.nav.medlemskap.domene.personhistorikk.Familierelasjon
import no.nav.medlemskap.domene.personhistorikk.Statsborgerskap
import no.nav.medlemskap.domene.personhistorikk.Statsborgerskap.Companion.harEndretSisteÅret
import no.nav.medlemskap.services.FamilieService
import java.time.LocalDate

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

    val aktorIder = services.pdlService.hentAlleAktorIder(request.fnr, callId)
    val medlemskapsunntakRequest = async { services.medlService.hentMedlemskapsunntak(request.fnr, callId) }
    val journalPosterRequest = async { services.safService.hentJournaldata(request.fnr, callId) }
    val gosysOppgaver = async { services.oppgaveService.hentOppgaver(aktorIder, callId) }

    val personHistorikk = services.pdlService.hentPersonHistorikkTilBruker(request.fnr, callId)

    val dataOmBrukersBarn = familieService.hentDataOmBarn(
        Familierelasjon.hentFnrTilBarn(
            personHistorikk.familierelasjoner,
            startDatoForYtelse
        ),
        callId
    )

    val dataOmEktefelle = familieService.hentDataOmEktefelle(
        fnrTilEktefelle = Familierelasjon.hentFnrTilEktefelle(personHistorikk),
        periode = request.periode,
        startDatoForYtelse = startDatoForYtelse,
        callId = callId
    )

    val medlemskap = medlemskapsunntakRequest.await()
    val arbeidsforhold = arbeidsforholdRequest.await()
    val journalPoster = journalPosterRequest.await()
    val oppgaver = gosysOppgaver.await()
    val ytelse: Ytelse = finnYtelse(request.ytelse, clientId)

    val oppholdstillatelse = if (FeatureToggles.FEATURE_UDI.enabled
        // personHistorikk.statsborgerskap.erAnnenStatsborger(startDatoForYtelse)
    ) {
        val oppholdsstatusRequest = async { services.udiService.hentOppholdstillatelseer(request.fnr) }
        oppholdsstatusRequest.await()
    } else {
        null
    }

    ytelseCounter(ytelse.name()).increment()

    personHistorikk.statsborgerskap.registrerStatsborgerskapDataForGrafana(startDatoForYtelse, request.periode, ytelse)

    arbeidsforhold.registrerAntallAnsatteHosJuridiskEnhet(ytelse)

    Datagrunnlag(
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
        oppholdstillatelse = oppholdstillatelse
    )
}

private fun List<Statsborgerskap>.registrerStatsborgerskapDataForGrafana(
    startDatoForYtelse: LocalDate,
    periode: InputPeriode,
    ytelse: Ytelse
) {
    if (size > 1) {
        flereStatsborgerskapCounter(size.toString(), ytelse).increment()
    }

    val statsborgerskapEndretSisteÅret = harEndretSisteÅret(Kontrollperiode(fom = startDatoForYtelse.minusYears(1), tom = periode.tom))

    endretStatsborgerskapSisteÅretCounter(statsborgerskapEndretSisteÅret, ytelse).increment()
}
