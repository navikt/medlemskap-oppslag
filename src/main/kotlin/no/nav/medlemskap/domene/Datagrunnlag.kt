package no.nav.medlemskap.domene

import no.nav.medlemskap.domene.ektefelle.DataOmEktefelle
import java.time.LocalDate

data class Datagrunnlag(
        val periode: InputPeriode,
        val brukerinput: Brukerinput,
        val personhistorikk: Personhistorikk,
        val pdlpersonhistorikk: Personhistorikk?,
        val medlemskap: List<Medlemskap> = listOf(),
        val arbeidsforhold: List<Arbeidsforhold> = listOf(),
        val oppgaver: List<Oppgave> = listOf(),
        val dokument: List<Journalpost> = listOf(),
        val ytelse: Ytelse,
        val personHistorikkRelatertePersoner: List<PersonhistorikkRelatertPerson> = listOf(),
        val dataOmEktefelle: DataOmEktefelle?
)

data class Periode(
        val fom: LocalDate?,
        val tom: LocalDate?
)

data class Kontrollperiode(
        val fom: LocalDate,
        val tom: LocalDate
) {
    fun tilPeriode(): Periode {
        return Periode(fom, tom)
    }
}

data class Bruksperiode(
        val fom: LocalDate?,
        val tom: LocalDate?
) {
    fun tilPeriode(): Periode {
        return Periode(fom, tom)
    }
}

data class Gyldighetsperiode(
        val fom: LocalDate?,
        val tom: LocalDate?
) {
    fun tilPeriode(): Periode {
        return Periode(fom, tom)
    }
}



