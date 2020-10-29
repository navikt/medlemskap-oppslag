package no.nav.medlemskap.domene

import no.nav.medlemskap.domene.barn.DataOmBarn
import no.nav.medlemskap.domene.ektefelle.DataOmEktefelle
import no.nav.medlemskap.regler.common.RegelId
import no.nav.medlemskap.regler.common.Svar
import java.time.LocalDate
import java.time.temporal.ChronoUnit

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
    val overstyrteRegler: Map<RegelId, Svar> = mapOf()
)

data class Kontrollperiode(
    val fom: LocalDate,
    val tom: LocalDate
) {
    val periode = Periode(fom, tom)
    val antallDager = fom.until(tom, ChronoUnit.DAYS).toDouble()
}

data class Bruksperiode(
    val fom: LocalDate?,
    val tom: LocalDate?
)

data class Gyldighetsperiode(
    val fom: LocalDate?,
    val tom: LocalDate?
)
