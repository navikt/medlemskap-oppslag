package no.nav.medlemskap.domene

import no.nav.medlemskap.regler.common.Funksjoner.erDelAv

/**
 * Journalpost fra Joark
 */
data class Journalpost(
    val journalpostId: String,
    val journalfortAvNavn: String?,
    val tittel: String?,
    val journalposttype: String?,
    val journalstatus: String?,
    val tema: String?,
    val dokumenter: List<Dokument>?
) {
    companion object {
        private val tillatteTemaer = listOf("MED", "UFM", "TRY")

        fun List<Journalpost>.finnesDokumenterMedTillatteTeamer(): Boolean =
            this.dokumenterMedTillatteTemaer().isNotEmpty()

        fun List<Journalpost>.dokumenterMedTillatteTemaer(): List<Journalpost> =
            this.filter { it.tema erDelAv tillatteTemaer && !it.journalfortAvNavn?.contains("medlemskap-joark")!! }
    }
}

/*
 * Gosys saksdokument
 */
data class Dokument(
    val dokumentId: String,
    val tittel: String?
)
