package no.nav.medlemskap.services.pdl

import io.github.resilience4j.retry.Retry
import io.ktor.client.HttpClient
import io.ktor.client.request.header
import io.ktor.client.request.options
import io.ktor.client.request.post
import io.ktor.client.request.url
import io.ktor.client.statement.HttpResponse
import io.ktor.http.ContentType
import io.ktor.http.HttpHeaders
import mu.KotlinLogging
import no.nav.medlemskap.common.exceptions.GraphqlError
import no.nav.medlemskap.common.exceptions.IdenterIkkeFunnet
import no.nav.medlemskap.common.exceptions.PersonIkkeFunnet
import no.nav.medlemskap.common.objectMapper
import no.nav.medlemskap.config.Configuration
import no.nav.medlemskap.domene.Adresse
import no.nav.medlemskap.domene.Personhistorikk
import no.nav.medlemskap.domene.Personstatus
import no.nav.medlemskap.domene.Statsborgerskap
import no.nav.medlemskap.services.runWithRetryAndMetrics
import no.nav.medlemskap.services.sts.StsRestClient

private val logger = KotlinLogging.logger { }

class PdlClient(
        private val baseUrl: String,
        private val stsClient: StsRestClient,
        private val configuration: Configuration,
        private val httpClient: HttpClient,
        private val retry: Retry? = null
) {
    suspend fun hentIdenter(fnr: String, callId: String): HentIdenterResponse {

        return runWithRetryAndMetrics("PDL", "HentIdenter", retry) {
            httpClient.post<HentIdenterResponse> {
                url("$baseUrl")
                header(HttpHeaders.Authorization, "Bearer ${stsClient.oidcToken()}")
                header(HttpHeaders.ContentType, ContentType.Application.Json)
                header(HttpHeaders.Accept, ContentType.Application.Json)
                header("Nav-Call-Id", callId)
                header("Nav-Consumer-Token", "Bearer ${stsClient.oidcToken()}")
                header("Nav-Consumer-Id", configuration.sts.username)
                body = hentIndenterQuery(fnr)
            }
        }
    }


    suspend fun hentPerson(fnr: String, callId: String): HentPdlPersonResponse{
        return runWithRetryAndMetrics("PDL", "HentPerson", retry) {
            httpClient.post<HentPdlPersonResponse> {
                url("$baseUrl")
                header(HttpHeaders.Authorization, "Bearer ${stsClient.oidcToken()}")
                header(HttpHeaders.ContentType, ContentType.Application.Json)
                header(HttpHeaders.Accept, ContentType.Application.Json)
                header("Nav-Call-Id", callId)
                header("TEMA", "MED")
                header("Nav-Consumer-Token", "Bearer ${stsClient.oidcToken()}")
                header("Nav-Consumer-Id", configuration.sts.username)
                body = hentPersonQuery(fnr)
            }
        }

    }

    suspend fun hentNasjonalitet(fnr: String, callId: String): String {
        return runWithRetryAndMetrics("PDL", "HentNasjonalitet", retry) {
            httpClient.post<String> {
                url("$baseUrl")
                header(HttpHeaders.Authorization, "Bearer ${stsClient.oidcToken()}")
                header(HttpHeaders.ContentType, ContentType.Application.Json)
                header(HttpHeaders.Accept, ContentType.Application.Json)
                header("Nav-Call-Id", callId)
                header("Nav-Consumer-Token", "Bearer ${stsClient.oidcToken()}")
                header("Nav-Consumer-Id", configuration.sts.username)
                body = hentNasjonalitetQuery(fnr)
            }
        }

    }

    suspend fun healthCheck(): HttpResponse {
        return httpClient.options {
            url("$baseUrl")
            header(HttpHeaders.Accept, ContentType.Application.Json)
            header("Nav-Consumer-Id", configuration.sts.username)
        }
    }
}

class PdlService(private val pdlClient: PdlClient, private val clusterName: String = "dev-fss") {

    suspend fun hentAktorId(fnr: String, callId: String): String {
        val pdlResponse = pdlClient.hentIdenter(fnr, callId)

        // Hack for å overleve manglende aktørID i ikke-konsistente data i Q2
        if (pdlResponse.errors != null && clusterName == "dev-fss") {
            return "111111111111"
        }

        pdlResponse.errors?.let { errors ->
            logger.warn { "Fikk følgende feil fra PDL: ${objectMapper.writeValueAsString(errors)}" }
            throw GraphqlError(errors.first(), "PDL")
        }

        return pdlResponse.data.hentIdenter?.identer?.first {
            !it.historisk && it.gruppe == IdentGruppe.AKTORID
        }?.ident ?: throw IdenterIkkeFunnet()
    }

    suspend fun hentPersonHistorikk(fnr: String, callId: String): Personhistorikk{
        return mapTilPersonHistorikk(pdlClient.hentPerson(fnr, callId))

/*        // Hack for å overleve manglende aktørID i ikke-konsistente data i Q2
        if (pdlResponse.errors != null && clusterName == "dev-fss") {
            return "111111111111"
        }

        pdlResponse.errors?.let { errors ->
            logger.warn { "Fikk følgende feil fra PDL: ${objectMapper.writeValueAsString(errors)}" }
            throw GraphqlError(errors.first(), "PDL")
        }*/

    }

    private fun mapTilPersonHistorikk(person: HentPdlPersonResponse): Personhistorikk {
        val statsborgerskap: List<Statsborgerskap> = person.data?.hentPerson?.statsborgerskap?.map {
            Statsborgerskap(
                    landkode = it.land,
                    fom = it.gyldigFraOgMed,
                    tom = it.gyldigTilOgMed
            )
        } ?: throw PersonIkkeFunnet("PDL")

        val personstatuser: List<Personstatus> = person.data.hentPerson.folkeregisterpersonstatus.map {
            Personstatus(
                    personstatus = it.status,
                    fom = it.folkeregistermetadata.gyldighetstidspunkt?.toLocalDate(),
                    tom = it.folkeregistermetadata.opphoerstidspunkt?.toLocalDate()
            )
        }

        val bostedsadresser: List<Adresse> = person.data.hentPerson.bostedsadresse.map {
            Adresse(
                    adresselinje = it.adresse ?: it.matrikkeladresse?.bruksenhetsnummer ?: "Ukjent adresse",
                    landkode = "",
                    fom = it.folkeregisterMetadata.gyldighetstidspunkt?.toLocalDate(),
                    tom = it.folkeregisterMetadata.opphoerstidspunkt?.toLocalDate()
            )
        }

        val postadresser: List<Adresse> = person.data.hentPerson.bostedsadresse.map {
            Adresse(
                    adresselinje = it.adresse ?: it.matrikkeladresse?.bruksenhetsnummer ?: "Ukjent adresse",
                    landkode = "",
                    fom = it.folkeregisterMetadata.gyldighetstidspunkt?.toLocalDate(),
                    tom = it.folkeregisterMetadata.opphoerstidspunkt?.toLocalDate()
            )
        }

        val midlertidigAdresser: List<Adresse> = person.data.hentPerson.bostedsadresse.map {
            Adresse(
                    adresselinje = it.adresse ?: it.matrikkeladresse?.bruksenhetsnummer ?: "Ukjent adresse",
                    landkode = "",
                    fom = it.folkeregisterMetadata.gyldighetstidspunkt?.toLocalDate(),
                    tom = it.folkeregisterMetadata.opphoerstidspunkt?.toLocalDate()
            )
        }

        val sivilstand: List<Sivilstand> = person.data.hentPerson.sivilstand.map {
            Sivilstand(
                    type = it.type,
                    gyldigFraOgMed = it.gyldigFraOgMed,
                    relatertVedSivilstand = it.relatertVedSivilstand,
                    folkeregisterMetadata = it.folkeregisterMetadata
            )
        }

        val familierelasjoner: List<Familierelasjon> = person.data.hentPerson.familierelasjoner.map {
            Familierelasjon(
                    relatertPersonIdent = it.relatertPersonIdent,
                    relatertPersonsRolle = it.relatertPersonsRolle,
                    minRolleForPerson = it.minRolleForPerson,
                    folkeregisterMetadata = it.folkeregisterMetadata
            )
        }

        return Personhistorikk(statsborgerskap, personstatuser, bostedsadresser, postadresser, midlertidigAdresser, sivilstand, familierelasjoner)
    }

}


