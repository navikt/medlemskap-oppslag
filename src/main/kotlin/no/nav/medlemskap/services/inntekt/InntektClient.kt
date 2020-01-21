package no.nav.medlemskap.services.inntekt

import io.ktor.client.request.*
import io.ktor.http.ContentType
import io.ktor.http.HttpHeaders
import no.nav.medlemskap.common.defaultHttpClient
import no.nav.medlemskap.config.Configuration
import no.nav.medlemskap.configuration
import no.nav.medlemskap.modell.inntekt.ArbeidsinntektMaaned
import no.nav.medlemskap.modell.inntekt.HentInntektListeRequest
import no.nav.medlemskap.modell.inntekt.Ident
import no.nav.medlemskap.modell.inntekt.InntektskomponentResponse
import no.nav.medlemskap.services.sts.StsRestClient
import java.time.LocalDate
import java.time.format.DateTimeFormatter

class InntektClient(
        val baseUrl: String,
        val stsClient: StsRestClient,
        val callIdGenerator: () -> String,
        val config: Configuration = configuration) {

    suspend fun hentInntektListe(ident: String, fraOgMed: LocalDate? = null, tilOgMed: LocalDate? = null): InntektskomponentResponse {
        val token = stsClient.oidcToken()
        return defaultHttpClient.post {
            url("$baseUrl/hentinntektliste")
            header(HttpHeaders.Authorization, "Bearer $token")
            header(HttpHeaders.ContentType, ContentType.Application.Json)
            header("Nav-Consumer-Id", config.sts.username)
            header("Nav-Call-Id", callIdGenerator.invoke())
            body = HentInntektListeRequest(
                    ident = Ident(ident, "NATURLIG_IDENT"),
                    ainntektsfilter = "MedlemskapA-inntekt",
                    maanedFom = fraOgMed?.tilAarOgMnd(),
                    maanedTom = tilOgMed?.tilAarOgMnd(),
                    formaal = "Medlemskap") //Må diskutere med Helle om vi skal bruke Medlemskap eller sykepenger som formål
        }

    }

    private fun LocalDate.tilAarOgMnd() = this.format(DateTimeFormatter.ofPattern("yyyy-MM"))
}









