package no.nav.medlemskap.regler.v1

import no.nav.medlemskap.regler.assertSvar
import no.nav.medlemskap.regler.common.RegelId.REGEL_2
import no.nav.medlemskap.regler.common.Svar
import no.nav.medlemskap.regler.evaluer
import no.nav.medlemskap.regler.personer.Personleser
import org.junit.jupiter.api.Test

class ReglerForGrunnforordningenTest {

    private val personleser = Personleser()

    @Test
    fun `person som er statsborger i et EØS-land `() {
        assertSvar(REGEL_2, Svar.JA, evaluer(personleser.brukerErStatsborgerIEØSLand()), Svar.JA)
    }

    @Test
    fun `person som ikke er statsborger i et EØS-land `() {
        assertSvar(REGEL_2, Svar.NEI, evaluer(personleser.brukerErIkkeStatsborgerIEØSLand()), Svar.UAVKLART)
    }
}
