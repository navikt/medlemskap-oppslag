package no.nav.medlemskap.domene

import no.nav.medlemskap.domene.Journalpost.Companion.finnesDokumenterMedTillatteTeamer
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test

class journalpostTest {
    @Test
    fun `filtrerer ut dokumenter som ikke er relevante`() {
        val dokument = listOf(
            Journalpost(
                relevanteDatoer = null,
                datoOpprettet = "",
                journalpostId = "",
                journalfortAvNavn = "",
                journalposttype = "",
                journalstatus = "",
                tittel = "",
                tema = "",
                sak = null,
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
                datoOpprettet = "",
                relevanteDatoer = null,
                journalpostId = "",
                journalfortAvNavn = "",
                journalposttype = "",
                journalstatus = "",
                tittel = "",
                tema = "MED",
                sak = null,
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
                datoOpprettet = "",
                relevanteDatoer = null,
                journalpostId = "",
                journalfortAvNavn = "medlemskap-joark-listener",
                journalposttype = "",
                journalstatus = "",
                tittel = "",
                tema = "MED",
                sak = null,
                dokumenter = null
            )
        )
        val finnesDokument = dokument.finnesDokumenterMedTillatteTeamer()

        Assertions.assertFalse(finnesDokument)
    }

    @Test
    fun `filtrerer  ut dokumenter der journalpostDato er mer en 1 år gammelt`() {
        val dokument = listOf(
            Journalpost(
                datoOpprettet = "",
                relevanteDatoer = listOf(RelevantDato("2020-05-25T19:43:43", Datotype.DATO_JOURNALFOERT)),
                journalpostId = "",
                journalfortAvNavn = null,
                journalposttype = "",
                journalstatus = "",
                tittel = "",
                tema = "MED",
                sak = null,
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
                datoOpprettet = "",
                relevanteDatoer = null,
                journalpostId = "",
                journalfortAvNavn = null,
                journalposttype = "",
                journalstatus = "",
                tittel = "",
                tema = "MED",
                sak = null,
                dokumenter = null
            )
        )
        val finnesDokument = dokument.finnesDokumenterMedTillatteTeamer()

        Assertions.assertTrue(finnesDokument)
    }

    @Test
    fun `test`() {
        val dokument = listOf(
            Journalpost(
                datoOpprettet = "2021-09-13T14:50:43",
                relevanteDatoer = null,
                journalpostId = "",
                journalfortAvNavn = null,
                journalposttype = "",
                journalstatus = "",
                tittel = "",
                tema = "MED",
                sak = null,
                dokumenter = null
            ),
            Journalpost(
                datoOpprettet = "2021-08-13T14:50:43",
                relevanteDatoer = null,
                journalpostId = "",
                journalfortAvNavn = null,
                journalposttype = "",
                journalstatus = "",
                tittel = "",
                tema = "MED",
                sak = Sak("MEL-123456789"),
                dokumenter = null
            ),
            Journalpost(
                datoOpprettet = "2021-07-13T14:50:43",
                relevanteDatoer = null,
                journalpostId = "",
                journalfortAvNavn = null,
                journalposttype = "",
                journalstatus = "",
                tittel = "",
                tema = "MED",
                sak = null,
                dokumenter = null
            ),
            Journalpost(
                datoOpprettet = "2021-06-13T14:50:43",
                relevanteDatoer = null,
                journalpostId = "",
                journalfortAvNavn = null,
                journalposttype = "",
                journalstatus = "",
                tittel = "",
                tema = "MED",
                sak = null,
                dokumenter = null
            ),
            Journalpost(
                datoOpprettet = "2021-05-13T14:50:43",
                relevanteDatoer = null,
                journalpostId = "",
                journalfortAvNavn = null,
                journalposttype = "",
                journalstatus = "",
                tittel = "",
                tema = "MED",
                sak = null,
                dokumenter = null
            )
        )

        val filtrerteDokumenter = dokument.finnesDokumenterMedTillatteTeamer()

        Assertions.assertTrue(filtrerteDokumenter)
    }
}
