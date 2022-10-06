package no.nav.medlemskap.domene

import no.nav.medlemskap.regler.common.Funksjoner.erDelAv
import no.nav.medlemskap.regler.common.Funksjoner.isNotNullOrEmpty
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

/**
 * Journalpost fra Joark
 */
data class Journalpost(
    val datoOpprettet: String,
    val relevanteDatoer: List<RelevantDato>?,
    val journalpostId: String,
    val journalfortAvNavn: String?,
    val tittel: String?,
    val journalposttype: String?,
    val journalstatus: String?,
    val tema: String?,
    val sak: Sak?,
    val dokumenter: List<Dokument>?,
) {

    companion object {
        private val tillatteTemaer = listOf("MED", "UFM", "TRY")

        fun Journalpost.erMindreEnn1AarGammelt(): Boolean {
            val today = LocalDate.now()
            val reldato = this.relevanteDatoer?.find { it.datotype == Datotype.DATO_JOURNALFOERT }
            if (reldato != null) {
                val date = LocalDate.parse(reldato.dato, DateTimeFormatter.ISO_LOCAL_DATE_TIME)
                return date.isAfter(today.minusYears(1))
            }
            return true
        }

        fun Journalpost.hentJorurnalFoertDato(): String {
            val journalfoertDato = this.relevanteDatoer?.find { it.datotype == Datotype.DATO_JOURNALFOERT }
            return if (journalfoertDato != null)
                journalfoertDato!!.dato
            else {
                this.datoOpprettet
            }
        }

        fun List<Journalpost>.finnesDokumenterMedTillatteTeamer(): Boolean =
            this.dokumenterMedTillatteTemaer().isNotEmpty()

        fun List<Journalpost>.dokumenterMedTillatteTemaer(): List<Journalpost> =
            // this.filtrerVekkGamleUrelevanteDokumenter()
            this.filtrerVekkGamleMEDjournalposterBasertPaaNyesteMELsak()
                .filterNot { it.sak?.fagsakId?.contains("MEL-") == true }
                .filter { it.tema erDelAv tillatteTemaer }
                .filter { (it.journalfortAvNavn.isNullOrEmpty() || !it.journalfortAvNavn.contains("medlemskap-joark")) }

        fun List<Journalpost>.harDokument(): Boolean = this.isNotNullOrEmpty()

        fun List<Journalpost>.alleFagsakIDer(): List<String> {
            return this.map { journalpost -> journalpost.sak?.fagsakId.toString() }
        }

        fun List<Journalpost>.filtrerVekkGamleUrelevanteDokumenter(): List<Journalpost> {
            return this.filter { it.erMindreEnn1AarGammelt() }
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

data class RelevantDato(
    val dato: String,
    val datotype: Datotype
)
enum class Datotype {
    DATO_OPPRETTET,
    DATO_SENDT_PRINT,
    DATO_EKSPEDERT,
    DATO_JOURNALFOERT,
    DATO_REGISTRERT,
    DATO_AVS_RETUR,
    DATO_DOKUMENT,
    /**
     * This is a default enum value that will be used when attempting to deserialize unknown value.
     */
    __UNKNOWN_VALUE,
}
data class Sak(val fagsakId: String?)
