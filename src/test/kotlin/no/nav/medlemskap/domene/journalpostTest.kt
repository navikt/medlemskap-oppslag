package no.nav.medlemskap.domene

import no.nav.medlemskap.domene.Journalpost.Companion.finnesDokumenterMedTillatteTeamer
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test

class journalpostTest {
    @Test
    fun `filtrerer ut dokumenter som ikke er relevante`() {
        val dokument = listOf(
            Journalpost(
                journalpostId = "",
                journalfortAvNavn = "",
                journalposttype = "",
                journalstatus = "",
                tittel = "",
                tema = "",
                dokumenter = null
            )
        )
        val finnesDokument = dokument.finnesDokumenterMedTillatteTeamer()

        Assertions.assertFalse(finnesDokument)
    }

    @Test
    fun `filtrerer ikke ut dokumenter som ikke er relevante`() {
        val dokument = listOf(
            Journalpost(
                journalpostId = "",
                journalfortAvNavn = "",
                journalposttype = "",
                journalstatus = "",
                tittel = "",
                tema = "MED",
                dokumenter = null
            )
        )
        val finnesDokument = dokument.finnesDokumenterMedTillatteTeamer()

        Assertions.assertTrue(finnesDokument)
    }

    @Test
    fun `filtrerer ut dokumenter uavhengig av tema som vi ikke oppretter selv`() {
        val dokument = listOf(
            Journalpost(
                journalpostId = "",
                journalfortAvNavn = "medlemskap-joark-listener",
                journalposttype = "",
                journalstatus = "",
                tittel = "",
                tema = "MED",
                dokumenter = null
            )
        )
        val finnesDokument = dokument.finnesDokumenterMedTillatteTeamer()

        Assertions.assertFalse(finnesDokument)
    }

    @Test
    fun `filtrerer ikke ut dokumenter med null verdi i journalført av navn`() {
        val dokument = listOf(
            Journalpost(
                journalpostId = "",
                journalfortAvNavn = null,
                journalposttype = "",
                journalstatus = "",
                tittel = "",
                tema = "MED",
                dokumenter = null
            )
        )
        val finnesDokument = dokument.finnesDokumenterMedTillatteTeamer()

        Assertions.assertTrue(finnesDokument)
    }
}
