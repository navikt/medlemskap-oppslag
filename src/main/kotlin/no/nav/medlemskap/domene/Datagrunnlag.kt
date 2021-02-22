package no.nav.medlemskap.domene

import no.nav.medlemskap.domene.Kontrollperiode.Companion.startDatoForYtelse
import no.nav.medlemskap.domene.arbeidsforhold.Arbeidsforhold
import no.nav.medlemskap.domene.arbeidsforhold.Arbeidsforhold.Companion.gyldigeOrgnummer
import no.nav.medlemskap.domene.barn.DataOmBarn
import no.nav.medlemskap.domene.ektefelle.DataOmEktefelle
import no.nav.medlemskap.domene.personhistorikk.Personhistorikk
import no.nav.medlemskap.domene.personhistorikk.Statsborgerskap.Companion.erAnnenStatsborger
import no.nav.medlemskap.domene.personhistorikk.Statsborgerskap.Companion.erNorskBorger
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
    val startDatoForYtelse = startDatoForYtelse(periode, førsteDagForYtelse)
    private val kontrollPeriodeForPersonhistorikk =
        Kontrollperiode.kontrollPeriodeForPersonhistorikk(startDatoForYtelse)

    private val kontrollPeriodeForArbeidsforhold =
        Kontrollperiode.kontrollPeriodeForArbeidsforhold(startDatoForYtelse)

    fun gyldigeStatsborgerskap(): List<String> {
        return pdlpersonhistorikk.statsborgerskap.gyldigeStatsborgerskap(kontrollPeriodeForPersonhistorikk)
    }

    fun erTredjelandsborger(): Boolean {
        return pdlpersonhistorikk.statsborgerskap.erAnnenStatsborger(startDatoForYtelse)
    }

    fun erNorskBorger(): Boolean {
        return pdlpersonhistorikk.statsborgerskap.erNorskBorger(kontrollPeriodeForPersonhistorikk)
    }

    fun gyldigeOrgnummer(): List<String> {
        return arbeidsforhold.gyldigeOrgnummer(kontrollPeriodeForArbeidsforhold)
    }
}
