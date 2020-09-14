package no.nav.medlemskap.services.aareg

import no.nav.medlemskap.clients.aareg.AaRegClient
import no.nav.medlemskap.clients.ereg.Ansatte
import no.nav.medlemskap.clients.ereg.EregClient
import no.nav.medlemskap.domene.Arbeidsforhold
import no.nav.medlemskap.services.pdl.PdlService
import java.time.LocalDate

class AaRegService(
    private val aaRegClient: AaRegClient,
    private val eregClient: EregClient,
    private val pdlService: PdlService
) {

    suspend fun hentArbeidsforhold(fnr: String, callId: String, fraOgMed: LocalDate, tilOgMed: LocalDate): List<Arbeidsforhold> {
        val arbeidsgiverOrg = ArbeidsgiverOrg(aaRegClient)
        // val arbeidsgiverPerson = ArbeidsgiverPerson(aaRegClient) - Tar ikke hensyn til person-arbeidsgivere inntil videre

        val arbeidsforhold = aaRegClient.hentArbeidsforhold(fnr, callId, fraOgMed, tilOgMed)

        val dataOmArbeidsgiver = mutableMapOf<String, ArbeidsgiverInfo>()
        val orgnummere = arbeidsgiverOrg.getOrg(fnr, callId, fraOgMed, tilOgMed)

        orgnummere.forEach { orgnummer ->
            val organisasjon = eregClient.hentOrganisasjon(orgnummer, callId)

            val juridiskEnhetOrgnummerEnhetstype = HashMap<String, String?>()

            organisasjon.getOrganisasjonsnumreJuridiskeEnheter().forEach {
                val enhetstypeForOrganisasjonsnummer = eregClient.hentEnhetstype(it, callId)
                juridiskEnhetOrgnummerEnhetstype[it] = enhetstypeForOrganisasjonsnummer
            }

            dataOmArbeidsgiver[orgnummer] = ArbeidsgiverInfo(
                arbeidsgiverEnhetstype = hentArbeidsgiverEnhetstype(orgnummer, callId),
                ansatte = organisasjon.organisasjonDetaljer?.ansatte,
                opphoersdato = organisasjon.organisasjonDetaljer?.opphoersdato,
                konkursStatus = organisasjon.organisasjonDetaljer?.statuser?.map { it -> it?.kode },
                juridiskEnhetOrgnummerEnhetstype = juridiskEnhetOrgnummerEnhetstype
            )
        }

        return mapAaregResultat(arbeidsforhold, dataOmArbeidsgiver)
    }

    data class ArbeidsgiverInfo(
        val arbeidsgiverEnhetstype: String?,
        val ansatte: List<Ansatte>?,
        val opphoersdato: LocalDate?,
        val konkursStatus: List<String?>?,
        val juridiskEnhetOrgnummerEnhetstype: Map<String, String?>
    )

    private suspend fun hentArbeidsgiverEnhetstype(orgnummer: String, callId: String): String? {
        return eregClient.hentEnhetstype(orgnummer, callId)
    }
}
