package no.nav.medlemskap.regler.v1

import no.nav.medlemskap.domene.*
import no.nav.medlemskap.regler.common.Svar
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import java.time.LocalDate

class RegelsettForOpplysningerTest {
    @Test
    fun `person uten medl, gosys eller joark data får nei`() {
        assertEquals(Svar.NEI, evaluerReglerForOpplysninger())
    }

    @Test
    fun `person med data i medl får ja`() {
        val medlemskap = Medlemskap(
                dekning = null,
                fraOgMed = LocalDate.of(2019, 1, 1),
                tilOgMed = LocalDate.of(2020, 1, 30),
                erMedlem = true,
                lovvalg  = "ENDL",
                lovvalgsland = "NOR"
        )

        assertEquals(Svar.JA, evaluerReglerForOpplysninger(medlemskap = listOf(medlemskap)))
    }

    @Test
    fun `person med åpnet oppgave i gosys får ja`() {
        val oppgave = Oppgave(
                aktivDato = LocalDate.of(2020, 1,1 ),
                prioritet = Prioritet.NORM,
                status = Status.AAPNET,
                tema = "MED"
        )

        assertEquals(Svar.JA, evaluerReglerForOpplysninger(oppgaver = listOf(oppgave)))
    }

    @Test
    fun `person uten uåpnet oppgave i gosys får nei`() {
        val oppgave = Oppgave(
                aktivDato = LocalDate.of(2020, 1,1 ),
                prioritet = Prioritet.NORM,
                status = Status.FERDIGSTILT,
                tema = "MED"
        )

        assertEquals(Svar.NEI, evaluerReglerForOpplysninger(oppgaver = listOf(oppgave)))
    }

    @Test
    fun `person med dokument med tillatt tema i joark får ja`() {
        val journalpost = Journalpost(
            journalpostId = "123",
            tittel = "Test",
            journalposttype = "T",
            journalstatus = "AAPEN",
            tema = "MED",
            dokumenter = null
        )

        assertEquals(Svar.JA, evaluerReglerForOpplysninger(dokument = listOf(journalpost)))
    }

    @Test
    fun `person uten dokument med tillatt tema i joark får nei`() {
        val journalpost = Journalpost(
                journalpostId = "123",
                tittel = "Test",
                journalposttype = "T",
                journalstatus = "AAPEN",
                tema = "IKKE TILLATT",
                dokumenter = null
        )

        assertEquals(Svar.NEI, evaluerReglerForOpplysninger(dokument = listOf(journalpost)))
    }

    private fun evaluerReglerForOpplysninger(
            medlemskap: List<Medlemskap> = emptyList(),
            oppgaver: List<Oppgave> = emptyList(),
            dokument: List<Journalpost> = emptyList()): Svar {
        val regelsett = ReglerForRegistrerteOpplysninger(medlemskap, oppgaver, dokument)
        return regelsett.hentHovedRegel().utfør(mutableListOf()).svar
    }
}