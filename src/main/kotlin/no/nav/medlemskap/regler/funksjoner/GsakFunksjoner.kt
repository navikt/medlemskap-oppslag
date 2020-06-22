package no.nav.medlemskap.regler.funksjoner

import no.nav.medlemskap.domene.Oppgave
import no.nav.medlemskap.domene.Status
import no.nav.medlemskap.regler.common.Funksjoner.erDelAv

object GsakFunksjoner {

    fun List<Oppgave>.finnesAapneOppgaver(): Boolean = this.aapneOppgaver().isNotEmpty()

    private fun List<Oppgave>.aapneOppgaver(): List<Oppgave> =
            this.filter { it.tema erDelAv tillatteTemaer && it.status erDelAv tillatteStatuser }


    private val tillatteTemaer = listOf("MED", "UFM", "TRY")
    private val tillatteStatuser = listOf(Status.AAPNET, Status.OPPRETTET, Status.UNDER_BEHANDLING)

}