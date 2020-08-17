package no.nav.medlemskap.clients.aareg

import io.github.resilience4j.retry.Retry
import io.ktor.client.HttpClient
import io.ktor.client.features.ClientRequestException
import io.ktor.client.request.get
import io.ktor.client.request.header
import io.ktor.client.request.parameter
import io.ktor.client.request.url
import io.ktor.client.statement.HttpResponse
import io.ktor.http.ContentType
import io.ktor.http.HttpHeaders
import mu.KotlinLogging
import no.nav.medlemskap.clients.ereg.Ansatte
import no.nav.medlemskap.clients.ereg.EregClient
import no.nav.medlemskap.clients.pdl.PdlService
import no.nav.medlemskap.clients.runWithRetryAndMetrics
import no.nav.medlemskap.clients.sts.StsRestClient
import no.nav.medlemskap.domene.Arbeidsforhold
import no.nav.medlemskap.domene.ArbeidsgiverOrg
import no.nav.medlemskap.domene.ArbeidsgiverPerson
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.*


class AaRegClient(
        private val baseUrl: String,
        private val stsClient: StsRestClient,
        private val httpClient: HttpClient,
        private val retry: Retry? = null
) {

    companion object {
        private val logger = KotlinLogging.logger { }
        private const val IKKE_EKSISTERENDE_FNR = "01010100000"
    }

    suspend fun hentArbeidsforhold(fnr: String, callId: String, fraOgMed: LocalDate? = null, tilOgMed: LocalDate? = null): List<AaRegArbeidsforhold> {
        val oidcToken = stsClient.oidcToken()
        return runCatching {
            runWithRetryAndMetrics("AaReg", "ArbeidsforholdV1", retry) {
                httpClient.get<List<AaRegArbeidsforhold>> {
                    url("$baseUrl/v1/arbeidstaker/arbeidsforhold")
                    header(HttpHeaders.Authorization, "Bearer ${oidcToken}")
                    header(HttpHeaders.Accept, ContentType.Application.Json)
                    header("Nav-Call-Id", callId)
                    header("Nav-Personident", fnr)
                    header("Nav-Consumer-Token", "Bearer ${oidcToken}")
                    fraOgMed?.let { parameter("ansettelsesperiodeFom", fraOgMed.tilIsoFormat()) }
                    tilOgMed?.let { parameter("ansettelsesperiodeTom", tilOgMed.tilIsoFormat()) }
                    parameter("historikk", "true")
                    parameter("regelverk", "ALLE")
                }
            }
        }.fold(
                onSuccess = { liste -> liste },
                onFailure = { error ->
                    when (error) {
                        is ClientRequestException -> {
                            if (error.response.status.value == 404) {
                                listOf()
                            } else {
                                throw error
                            }
                        }
                        else -> throw error
                    }
                }
        )
    }

    suspend fun healthCheck(): HttpResponse {
        val oidcToken = stsClient.oidcToken()
        return httpClient.get {
            url("$baseUrl/v1/arbeidstaker/arbeidsforhold")
            header(HttpHeaders.Authorization, "Bearer ${oidcToken}")
            header(HttpHeaders.Accept, ContentType.Application.Json)
            header("Nav-Call-Id", UUID.randomUUID().toString())
            header("Nav-Personident", IKKE_EKSISTERENDE_FNR)
            header("Nav-Consumer-Token", "Bearer ${oidcToken}")
            parameter("historikk", "false")
            parameter("regelverk", "ALLE")
        }
    }

    private fun LocalDate.tilIsoFormat() = this.format(DateTimeFormatter.ISO_LOCAL_DATE)
}

class AaRegService(
        private val aaRegClient: AaRegClient,
        private val eregClient: EregClient,
        private val pdlService: PdlService
) {

    suspend fun hentArbeidsforhold(fnr: String, callId: String, fraOgMed: LocalDate, tilOgMed: LocalDate): List<Arbeidsforhold> {
        val arbeidsgiverOrg = ArbeidsgiverOrg(aaRegClient)
        val arbeidsgiverPerson = ArbeidsgiverPerson(aaRegClient)

        val arbeidsforhold = aaRegClient.hentArbeidsforhold(fnr, callId, fraOgMed, tilOgMed)

        val dataOmArbeidsgiver = mutableMapOf<String, ArbeidsgiverInfo>()
        val orgnummere = arbeidsgiverOrg.getOrg(fnr, callId, fraOgMed, tilOgMed)

        orgnummere.forEach { orgnummer ->
            val logger = KotlinLogging.logger { }
            val organisasjon = eregClient.hentOrganisasjon(orgnummer, callId)

            logger.info { organisasjon }

            dataOmArbeidsgiver[orgnummer] = ArbeidsgiverInfo(
                    arbeidsgiverEnhetstype = hentArbeidsgiverEnhetstype(orgnummer, callId),
                    ansatte = organisasjon.organisasjonDetaljer?.ansatte,
                    opphoersdato = organisasjon.organisasjonDetaljer?.opphoersdato,
                    konkursStatus = organisasjon.organisasjonDetaljer?.statuser?.map { it -> it?.kode }

            )
            logger.info { dataOmArbeidsgiver[orgnummer] }

        }

        return mapAaregResultat(arbeidsforhold, dataOmArbeidsgiver)
    }

    data class ArbeidsgiverInfo(val arbeidsgiverEnhetstype: String?,
                                val ansatte: List<Ansatte>?,
                                val opphoersdato: LocalDate?,
                                val konkursStatus: List<String?>?)


    private suspend fun hentArbeidsgiverEnhetstype(orgnummer: String, callId: String): String? {
        return eregClient.hentEnhetstype(orgnummer, callId)
    }

}