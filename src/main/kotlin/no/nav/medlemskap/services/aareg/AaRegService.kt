package no.nav.medlemskap.services.aareg

import mu.KotlinLogging
import no.nav.medlemskap.clients.aareg.AaRegClient
import no.nav.medlemskap.clients.ereg.EregClient
import no.nav.medlemskap.domene.Arbeidsforhold
import no.nav.medlemskap.domene.Arbeidsgiver
import no.nav.medlemskap.services.ereg.mapOrganisasjonTilArbeidsgiver
import java.time.LocalDate

private val secureLogger = KotlinLogging.logger("tjenestekall")

class AaRegService(
    private val aaRegClient: AaRegClient,
    private val eregClient: EregClient
) {
    suspend fun hentArbeidsforhold(fnr: String, callId: String, fraOgMed: LocalDate, tilOgMed: LocalDate): List<Arbeidsforhold> {
        val arbeidsgiverOrg = ArbeidsgiverOrg(aaRegClient)
        // val arbeidsgiverPerson = ArbeidsgiverPerson(aaRegClient) - Tar ikke hensyn til person-arbeidsgivere inntil videre

        val arbeidsforhold = aaRegClient.hentArbeidsforhold(fnr, callId, fraOgMed, tilOgMed)

        val organisasjonMappedAsArbeidsgiverList = mutableListOf<Arbeidsgiver>()
        val orgnummere = arbeidsgiverOrg.getOrg(fnr, callId, fraOgMed, tilOgMed)

        orgnummere.forEach { orgnummer ->
            val organisasjon = eregClient.hentOrganisasjon(orgnummer, callId)
            val juridiskEnhetOrgnummerEnhetstype = HashMap<String, String?>()

            organisasjon.getOrganisasjonsnumreJuridiskeEnheter().forEach {
                juridiskEnhetOrgnummerEnhetstype[it] = eregClient.hentEnhetstype(it, callId)
            }

            if (orgnummer.length == 9) {
                secureLogger.info("Organisasjon: ${organisasjon.organisasjonsnummer} av type: ${organisasjon.type} koblet til juridisk enhet: ${organisasjon.getOrganisasjonsnumreJuridiskeEnheter()} av type $juridiskEnhetOrgnummerEnhetstype")
            }

            organisasjonMappedAsArbeidsgiverList.add(mapOrganisasjonTilArbeidsgiver(organisasjon, juridiskEnhetOrgnummerEnhetstype))
        }

        return mapAaregResultat(arbeidsforhold, organisasjonMappedAsArbeidsgiverList)
    }
}
