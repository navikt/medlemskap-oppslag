package no.nav.medlemskap.domene

import java.time.LocalDate

data class Datagrunnlag (
        val periode: InputPeriode,
        val brukerinput: Brukerinput,
        val personhistorikk: Personhistorikk,
        val pdlpersonhistorikk: Personhistorikk,
        val medlemskapsunntak: List<Medlemskapsunntak> = listOf(),
        val arbeidsforhold: List<Arbeidsforhold> = listOf(),
        val inntekt: List<Inntekt> = listOf(),
        val oppgaver: List<Oppgave> = listOf(),
        val dokument: List<Journalpost> = listOf()
)

data class Periode (
        val fom: LocalDate?,
        val tom: LocalDate?
)
