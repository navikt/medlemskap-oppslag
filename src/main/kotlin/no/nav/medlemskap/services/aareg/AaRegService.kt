package no.nav.medlemskap.services.aareg

import no.nav.medlemskap.clients.aareg.AaRegArbeidsforhold
import no.nav.medlemskap.clients.aareg.AaRegClient
import no.nav.medlemskap.clients.ereg.EregClient
import no.nav.medlemskap.clients.ereg.Organisasjon
import no.nav.medlemskap.domene.Arbeidsforhold
import java.time.LocalDate

class AaRegService(
    private val aaRegClient: AaRegClient,
    private val eregClient: EregClient
) {
    suspend fun hentArbeidsforhold(fnr: String, callId: String, fraOgMed: LocalDate, tilOgMed: LocalDate): List<Arbeidsforhold> {
        // val arbeidsgiverPerson = ArbeidsgiverPerson(aaRegClient) - Tar ikke hensyn til person-arbeidsgivere inntil videre

        val arbeidsforhold = aaRegClient.hentArbeidsforhold(fnr, callId, fraOgMed, tilOgMed)
        val arbeidsforholdMedOrganisasjon = mutableListOf<ArbeidsforholdOrganisasjon>()

        for (aaRegArbeidsforhold in arbeidsforhold) {
            if (aaRegArbeidsforhold.arbeidsgiver.organisasjonsnummer != null) {
                val organisasjon = eregClient.hentOrganisasjon(aaRegArbeidsforhold.arbeidsgiver.organisasjonsnummer, callId)

                val juridiskEnhetstypeMap = mutableMapOf<String, String>()
                organisasjon.getOrganisasjonsnumreJuridiskeEnheter().forEach {
                    val enhetstype = eregClient.hentEnhetstype(it, callId)
                    if (enhetstype != null) {
                        juridiskEnhetstypeMap[it] = enhetstype
                    }
                }
                arbeidsforholdMedOrganisasjon.add(ArbeidsforholdOrganisasjon(aaRegArbeidsforhold, organisasjon, juridiskEnhetstypeMap))
            }
        }

        return mapArbeidsforhold(arbeidsforholdMedOrganisasjon)
    }
}

data class ArbeidsforholdOrganisasjon(
    val arbeidsforhold: AaRegArbeidsforhold,
    val organisasjon: Organisasjon,
    val juridiskeEnhetstyper: Map<String, String>
)
