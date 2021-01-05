package no.nav.medlemskap.routes

import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import no.nav.medlemskap.clients.Services
import no.nav.medlemskap.common.FeatureToggles
import no.nav.medlemskap.common.endretStatsborgerskapSisteÅretCounter
import no.nav.medlemskap.common.flereStatsborgerskapCounter
import no.nav.medlemskap.common.ytelseCounter
import no.nav.medlemskap.domene.*
import no.nav.medlemskap.domene.Kontrollperiode.Companion.førsteDatoForYtelse
import no.nav.medlemskap.domene.Ytelse.Companion.name
import no.nav.medlemskap.domene.arbeidsforhold.Arbeidsforhold
import no.nav.medlemskap.domene.barn.DataOmBarn
import no.nav.medlemskap.domene.barn.PersonhistorikkBarn
import no.nav.medlemskap.domene.ektefelle.DataOmEktefelle
import no.nav.medlemskap.domene.ektefelle.PersonhistorikkEktefelle
import no.nav.medlemskap.domene.personhistorikk.Familierelasjon.Companion.hentFnrTilBarn
import no.nav.medlemskap.domene.personhistorikk.Familierelasjon.Companion.hentFnrTilEktefelle
import no.nav.medlemskap.domene.personhistorikk.Personhistorikk
import no.nav.medlemskap.domene.personhistorikk.Statsborgerskap.Companion.harEndretSisteÅret
import no.nav.medlemskap.regler.funksjoner.ArbeidsforholdFunksjoner.fraOgMedDatoForArbeidsforhold
import no.nav.medlemskap.regler.funksjoner.ArbeidsforholdFunksjoner.registrerAntallAnsatteHosJuridiskEnhet
import java.time.LocalDate

suspend fun defaultCreateDatagrunnlag(
    request: Request,
    callId: String,
    services: Services,
    clientId: String?
): Datagrunnlag = coroutineScope {

    val dataOmEktefelle: DataOmEktefelle?
    val dataOmBrukersBarn: List<DataOmBarn>?

    val arbeidsforholdRequest = async {
        services.aaRegService.hentArbeidsforhold(
            request.fnr,
            callId,
            fraOgMedDatoForArbeidsforhold(førsteDatoForYtelse(request.periode, request.førsteDagForYtelse)),
            request.periode.tom
        )
    }

    val aktorIder = services.pdlService.hentAlleAktorIder(request.fnr, callId)
    val personHistorikkFraPdl = hentPersonhistorikkFraPdl(services, request.fnr, callId)
    val medlemskapsunntakRequest = async { services.medlService.hentMedlemskapsunntak(request.fnr, callId) }
    val journalPosterRequest = async { services.safService.hentJournaldata(request.fnr, callId) }
    val gosysOppgaver = async { services.oppgaveService.hentOppgaver(aktorIder, callId) }

    val fnrTilBarn = hentFnrTilBarn(
        personHistorikkFraPdl.familierelasjoner,
        førsteDatoForYtelse(request.periode, request.førsteDagForYtelse)
    )
    dataOmBrukersBarn = if (!fnrTilBarn.isNullOrEmpty()) hentDataOmBarn(fnrTilBarn, services, callId) else null

    val fnrTilEktefelle = hentFnrTilEktefelle(personHistorikkFraPdl)
    dataOmEktefelle = if (!fnrTilEktefelle.isNullOrEmpty()) hentDataOmEktefelle(
        fnrTilEktefelle = fnrTilEktefelle,
        periode = request.periode,
        førsteDatoForYtelse = førsteDatoForYtelse(request.periode, request.førsteDagForYtelse),
        services = services,
        callId = callId
    ) else null

    val medlemskap = medlemskapsunntakRequest.await()
    val arbeidsforhold = arbeidsforholdRequest.await()
    val journalPoster = journalPosterRequest.await()
    val oppgaver = gosysOppgaver.await()
    val ytelse: Ytelse = finnYtelse(request.ytelse, clientId)

    val oppholdstillatelse = if (FeatureToggles.FEATURE_UDI.enabled //&&
        //personHistorikkFraPdl.statsborgerskap.erAnnenStatsborger(request.periode, request.førsteDagForYtelse)
    ) {
        val oppholdsstatusRequest = async { services.udiService.hentOppholdstillatelseer(request.fnr) }
        oppholdsstatusRequest.await()
    } else {
        null
    }

    ytelseCounter(ytelse.name()).increment()

    registrerStatsborgerskapDataForGrafana(personHistorikkFraPdl, request.periode, request.førsteDagForYtelse, ytelse)

    arbeidsforhold.registrerAntallAnsatteHosJuridiskEnhet(ytelse)

    Datagrunnlag(
        periode = request.periode,
        førsteDagForYtelse = request.førsteDagForYtelse,
        brukerinput = request.brukerinput,
        pdlpersonhistorikk = personHistorikkFraPdl,
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

suspend fun hentDataOmBarn(fnrBarn: List<String>, services: Services, callId: String): List<DataOmBarn> {
    val dataOmBarn: MutableList<DataOmBarn> = ArrayList()

    fnrBarn.forEach {
        val personhistorikkBarn = hentPersonHistorikkForBarn(it, services, callId)
        if (personhistorikkBarn != null) {
            dataOmBarn.add(DataOmBarn(personhistorikkBarn))
        }
    }

    return dataOmBarn
}

private suspend fun hentDataOmEktefelle(
    fnrTilEktefelle: String?,
    services: Services,
    callId: String,
    periode: InputPeriode,
    førsteDatoForYtelse: LocalDate
): DataOmEktefelle? {
    if (fnrTilEktefelle != null) {
        val personhistorikkEktefelle =
            hentPersonHistorikkForEktefelle(fnrTilEktefelle, førsteDatoForYtelse, services, callId)
        if (personhistorikkEktefelle == null) {
            return null
        }
        val arbeidsforholdEktefelle = try {
            services.aaRegService.hentArbeidsforhold(
                fnr = fnrTilEktefelle,
                fraOgMed = fraOgMedDatoForArbeidsforhold(førsteDatoForYtelse),
                tilOgMed = periode.tom,
                callId = callId
            )
        } catch (t: Exception) {
            emptyList<Arbeidsforhold>()
        }

        return DataOmEktefelle(
            personhistorikkEktefelle = personhistorikkEktefelle,
            arbeidsforholdEktefelle = arbeidsforholdEktefelle
        )
    }
    return null
}

private suspend fun hentPersonHistorikkForEktefelle(
    fnrTilEktefelle: String,
    førsteDatoForYtelse: LocalDate,
    services: Services,
    callId: String
): PersonhistorikkEktefelle? {
    return services.pdlService.hentPersonHistorikkTilEktefelle(fnrTilEktefelle, førsteDatoForYtelse, callId)
}

private suspend fun hentPersonHistorikkForBarn(
    fnrTilBarn: String,
    services: Services,
    callId: String
): PersonhistorikkBarn? {
    return services.pdlService.hentPersonHistorikkTilBarn(fnrTilBarn, callId)
}

private suspend fun hentPersonhistorikkFraPdl(services: Services, fnr: String, callId: String): Personhistorikk {
    return services.pdlService.hentPersonHistorikkTilBruker(fnr, callId)
}

private fun registrerStatsborgerskapDataForGrafana(
    personHistorikkFraPdl: Personhistorikk,
    periode: InputPeriode,
    førsteDagForYtelse: LocalDate?,
    ytelse: Ytelse
) {
    if (personHistorikkFraPdl.statsborgerskap.size > 1) {
        flereStatsborgerskapCounter(personHistorikkFraPdl.statsborgerskap.size.toString(), ytelse).increment()
    }

    val fraOgMedDato = førsteDagForYtelse ?: periode.fom

    val statsborgerskapEndretSisteÅret = personHistorikkFraPdl.statsborgerskap
        .harEndretSisteÅret(Kontrollperiode(fom = fraOgMedDato.minusYears(1), tom = periode.tom))

    endretStatsborgerskapSisteÅretCounter(statsborgerskapEndretSisteÅret, ytelse).increment()
}
