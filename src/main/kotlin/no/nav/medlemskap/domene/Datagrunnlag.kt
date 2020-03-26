package no.nav.medlemskap.domene
import no.nav.medlemskap.services.pdl.HentPdlPersonResponse
import java.time.LocalDate


data class Datagrunnlag (
        val periode: InputPeriode,
        val pdl: PersonData,
        val brukerinput: Brukerinput,
        val personhistorikk: HentPdlPersonResponse,
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
