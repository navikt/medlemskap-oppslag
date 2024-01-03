package no.nav.medlemskap.services.aareg

import mu.KotlinLogging
import net.logstash.logback.argument.StructuredArguments
import net.logstash.logback.argument.StructuredArguments.kv
import no.nav.medlemskap.clients.aareg.AaRegArbeidsforhold
import no.nav.medlemskap.clients.aareg.AaRegClient
import no.nav.medlemskap.clients.ereg.EregClient
import no.nav.medlemskap.clients.ereg.Organisasjon
import no.nav.medlemskap.domene.arbeidsforhold.Arbeidsforhold
import java.time.LocalDate

private val secureLogger = KotlinLogging.logger("tjenestekall")

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

                val juridiskeEnheter = mutableListOf<Organisasjon>()
                organisasjon.getOrganisasjonsnumreJuridiskeEnheter().forEach {
                    juridiskeEnheter.add(eregClient.hentOrganisasjon(it, callId))
                }
                arbeidsforholdMedOrganisasjon.add(ArbeidsforholdOrganisasjon(aaRegArbeidsforhold, organisasjon, juridiskeEnheter))
            }
        }

        val arbeidsforholdV2 = aaRegClient.hentArbeidsforholdV2(fnr, callId, fraOgMed, tilOgMed)

        secureLogger.info {
            kv("fnr", fnr)
            kv("NAV-call-id", callId)
            kv("Aareg v2", arbeidsforholdV2)
            kv("Aareg v1", arbeidsforhold)
        }

        return mapArbeidsforhold(arbeidsforholdMedOrganisasjon)
    }
}

data class ArbeidsforholdOrganisasjon(
    val arbeidsforhold: AaRegArbeidsforhold,
    val organisasjon: Organisasjon,
    val juridiskeEnheter: List<Organisasjon>
)
