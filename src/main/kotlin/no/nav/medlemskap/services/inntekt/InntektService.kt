package no.nav.medlemskap.services.inntekt

import no.nav.medlemskap.clients.inntekt.InntektClient
import java.time.LocalDate

class InntektService(private val inntektClient: InntektClient) {

    suspend fun hentInntektListe(ident: String, callId: String, fraOgMed: LocalDate?, tilOgMed: LocalDate?) =
        mapInntektResultat(inntektClient.hentInntektListe(ident, callId, fraOgMed, tilOgMed))
}
