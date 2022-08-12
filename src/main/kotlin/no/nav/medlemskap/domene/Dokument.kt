package no.nav.medlemskap.domene

import no.nav.medlemskap.regler.common.Funksjoner.erDelAv
import no.nav.medlemskap.regler.common.Funksjoner.isNotNullOrEmpty
import java.time.LocalDateTime

/**
 * Journalpost fra Joark
 */
data class Journalpost(
    val datoOpprettet: String,
    val journalpostId: String,
    val journalfortAvNavn: String?,
    val tittel: String?,
    val journalposttype: String?,
    val journalstatus: String?,
    val tema: String?,
    val sak: Sak?,
    val dokumenter: List<Dokument>?
) {
    companion object {
        private val tillatteTemaer = listOf("MED", "UFM", "TRY")

        fun List<Journalpost>.finnesDokumenterMedTillatteTeamer(): Boolean =
            this.dokumenterMedTillatteTemaer().isNotEmpty()

        fun List<Journalpost>.dokumenterMedTillatteTemaer(): List<Journalpost> =
            this.filtrerVekkGamleMEDjournalposterBasertPaaNyesteMELsak()
                .filterNot { it.sak?.fagsakId?.contains("MEL-") == true }
                .filter { it.tema erDelAv tillatteTemaer }
                .filter { (it.journalfortAvNavn.isNullOrEmpty() || !it.journalfortAvNavn.contains("medlemskap-joark")) }

        fun List<Journalpost>.harDokument(): Boolean = this.isNotNullOrEmpty()

        fun List<Journalpost>.alleFagsakIDer(): List<String> {
            return this.map { journalpost -> journalpost.sak?.fagsakId.toString() }
        }

        fun List<Journalpost>.filtrerVekkGamleMEDjournalposterBasertPaaNyesteMELsak(): List<Journalpost> {
            var nyesteDato: LocalDateTime? = null

            if (this.any { it.datoOpprettet == "null" || it.datoOpprettet.isEmpty() })
                return this

            this.forEach {
                val dato = LocalDateTime.parse(it.datoOpprettet)
                if (it.sak?.fagsakId?.contains("MEL") == true && (nyesteDato == null || dato.isAfter(nyesteDato)))
                    nyesteDato = LocalDateTime.parse(it.datoOpprettet)
            }

            return this.filterNot { it.tema.equals("MED") && nyesteDato?.isAfter(LocalDateTime.parse(it.datoOpprettet)) == true }
        }
    }
}

/*
 * Gosys saksdokument
 */
data class Dokument(
    val dokumentId: String,
    val tittel: String?
)

data class Sak(val fagsakId: String?)
