package no.nav.medlemskap.domene

import no.nav.medlemskap.clients.aareg.AaRegClient
import java.time.LocalDate

class ArbeidsgiverOrg(val aaRegClient: AaRegClient) {

    suspend fun getOrg(fnr: String, callId: String, fraOgMed: LocalDate? = null, tilOgMed: LocalDate? = null): List<String> {
        val opplysningspliktigArbeidsgiver =
                aaRegClient.hentArbeidsforhold(fnr, callId, fraOgMed, tilOgMed).map { it.arbeidsgiver }

        return opplysningspliktigArbeidsgiver.mapNotNull { it.organisasjonsnummer }
    }
}