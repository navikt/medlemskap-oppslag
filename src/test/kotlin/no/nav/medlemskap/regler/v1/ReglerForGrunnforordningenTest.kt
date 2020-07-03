package no.nav.medlemskap.regler.v1

import no.nav.medlemskap.regler.assertSvar
import no.nav.medlemskap.regler.common.Svar
import no.nav.medlemskap.regler.evaluer
import no.nav.medlemskap.regler.personer.Personleser
import org.junit.jupiter.api.Test

class ReglerForGrunnforordningenTest {

    private val personleser = Personleser()

    @Test
    fun `person som er statsborger i et EØS-land `() {
        assertSvar("2", Svar.JA, evaluer(personleser.brukerErStatsborgerIEØSLand()), Svar.UAVKLART)
    }

    @Test
    fun `person som ikke er statsborger i et EØS-land `() {
        assertSvar("2", Svar.NEI, evaluer(personleser.brukerErIkkeStatsborgerIEØSLand()), Svar.UAVKLART)
    }

}