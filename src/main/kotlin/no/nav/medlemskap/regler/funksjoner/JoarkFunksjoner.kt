package no.nav.medlemskap.regler.funksjoner

import no.nav.medlemskap.domene.Journalpost
import no.nav.medlemskap.regler.common.Funksjoner.erDelAv

object JoarkFunksjoner {

    private val tillatteTemaer = listOf("MED", "UFM", "TRY")

    fun List<Journalpost>.finnesDokumenterMedTillatteTeamer(): Boolean =
        this.dokumenterMedTillatteTemaer().isNotEmpty()

    fun List<Journalpost>.dokumenterMedTillatteTemaer(): List<Journalpost> =
        this.filter { it.tema erDelAv tillatteTemaer }
}
