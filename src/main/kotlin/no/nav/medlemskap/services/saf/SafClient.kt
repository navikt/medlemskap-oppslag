package no.nav.medlemskap.services.saf

import io.ktor.client.request.header
import io.ktor.client.request.post
import io.ktor.client.request.url
import io.ktor.http.ContentType
import io.ktor.http.HttpHeaders
import no.nav.medlemskap.common.defaultHttpClient
import no.nav.medlemskap.services.sts.StsRestClient

private const val TEMA_MEDLEMSKAP = "MED"
private const val TEMA_UNNTAK_FRA_MEDLEMSKAP = "UFM"
private const val TEMA_TRYGDEAVGIFT = "TRY"
private const val ANTALL_JOURNALPOSTER = 10

class SafClient(val baseUrl: String, val stsClient: StsRestClient, val callIdGenerator: () -> String) {

    suspend fun hentJournaldata(fnr: String): SafResponse {
        //TODO("Skal det sjekkes bare for en gitt periode??")
        val query =
                """
                { "query": "query { dokumentoversiktBruker(brukerId: {id: \"$fnr\", type: FNR}, tema: [$TEMA_MEDLEMSKAP, $TEMA_UNNTAK_FRA_MEDLEMSKAP, $TEMA_TRYGDEAVGIFT], foerste: $ANTALL_JOURNALPOSTER) { journalposter { journalpostId tittel journalposttype journalstatus tema datoOpprettet dokumenter { dokumentInfoId tittel } } } }" }
                """.trimIndent()

        return defaultHttpClient.post<SafResponse>(body = query) {
            url("$baseUrl")
            header(HttpHeaders.Authorization, "Bearer ${stsClient.oidcToken()}")
            header(HttpHeaders.ContentType, ContentType.Application.Json)
            header(HttpHeaders.Accept, ContentType.Application.Json)
            header("Nav-Callid", callIdGenerator.invoke())
            header("Nav-Consumer-Id", "p3-medlemskap")
            //header(HttpHeaders.AcceptCharset, Charsets)
        }
    }
}


