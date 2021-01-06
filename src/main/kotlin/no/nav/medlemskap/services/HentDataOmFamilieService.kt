package no.nav.medlemskap.services

import no.nav.medlemskap.domene.InputPeriode
import no.nav.medlemskap.domene.arbeidsforhold.Arbeidsforhold
import no.nav.medlemskap.domene.barn.DataOmBarn
import no.nav.medlemskap.domene.ektefelle.DataOmEktefelle
import no.nav.medlemskap.domene.personhistorikk.Familierelasjon
import no.nav.medlemskap.domene.personhistorikk.Personhistorikk
import no.nav.medlemskap.services.aareg.AaRegService
import no.nav.medlemskap.services.pdl.PdlService
import java.time.LocalDate

class HentDataOmFamilieService(private val aaRegService: AaRegService, private val pdlService: PdlService) {

    suspend fun hentDataOmBarn(
        familierelasjoner: List<Familierelasjon>,
        startDatoForYtelse: LocalDate,
        callId: String
    ): List<DataOmBarn> {
        val fnrTilBarn = Familierelasjon.hentFnrTilBarn(
            familierelasjoner,
            startDatoForYtelse
        )

        return fnrTilBarn
            .mapNotNull { pdlService.hentPersonHistorikkTilBarn(it, callId) }
            .map { DataOmBarn(it) }
    }

    suspend fun hentDataOmEktefelle(
        personHistorikk: Personhistorikk,
        callId: String,
        periode: InputPeriode,
        startDatoForYtelse: LocalDate
    ): DataOmEktefelle? {
        val fnrTilEktefelle = Familierelasjon.hentFnrTilEktefelle(personHistorikk) ?: return null

        val personhistorikkEktefelle =
            pdlService.hentPersonHistorikkTilEktefelle(fnrTilEktefelle, startDatoForYtelse, callId) ?: return null
        val arbeidsforholdEktefelle = try {
            aaRegService.hentArbeidsforhold(
                fnr = fnrTilEktefelle,
                fraOgMed = Arbeidsforhold.fraOgMedDatoForArbeidsforhold(startDatoForYtelse),
                tilOgMed = periode.tom,
                callId = callId
            )
        } catch (t: Exception) {
            emptyList<Arbeidsforhold>()
        }

        return DataOmEktefelle(
            personhistorikkEktefelle = personhistorikkEktefelle,
            arbeidsforholdEktefelle = arbeidsforholdEktefelle
        )
    }
}
