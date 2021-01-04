package no.nav.medlemskap.domene

import no.nav.medlemskap.domene.arbeidsforhold.Arbeidsforhold
import no.nav.medlemskap.domene.barn.DataOmBarn
import no.nav.medlemskap.domene.ektefelle.DataOmEktefelle
import no.nav.medlemskap.domene.personhistorikk.Personhistorikk
import no.nav.medlemskap.domene.personhistorikk.Statsborgerskap
import no.nav.medlemskap.domene.personhistorikk.Statsborgerskap.Companion.gyldigeStatsborgerskap
import no.nav.medlemskap.regler.common.RegelId
import no.nav.medlemskap.regler.common.Svar
import java.time.LocalDate

data class Datagrunnlag(
    val periode: InputPeriode,
    val førsteDagForYtelse: LocalDate?,
    val brukerinput: Brukerinput,
    val pdlpersonhistorikk: Personhistorikk,
    val medlemskap: List<Medlemskap> = listOf(),
    val arbeidsforhold: List<Arbeidsforhold> = listOf(),
    val oppgaver: List<Oppgave> = listOf(),
    val dokument: List<Journalpost> = listOf(),
    val ytelse: Ytelse,
    val dataOmBarn: List<DataOmBarn>?,
    val dataOmEktefelle: DataOmEktefelle?,
    val overstyrteRegler: Map<RegelId, Svar> = mapOf(),
    val oppholdstillatelse: Oppholdstillatelse?
) {
    private val kontrollPeriodeForPersonhistorikk =
        Kontrollperiode.kontrollPeriodeForPersonhistorikk(periode, førsteDagForYtelse)

    fun gyldigeStatsborgerskap(): List<String> {
        val statsborgerskap: List<Statsborgerskap> = pdlpersonhistorikk.statsborgerskap

        return statsborgerskap.gyldigeStatsborgerskap(kontrollPeriodeForPersonhistorikk)
    }
}
