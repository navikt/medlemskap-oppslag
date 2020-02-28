package no.nav.medlemskap.routes

import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test

class Testutils {

    @Test
    fun `gyldig fødslesnummer gir true i verifikasjonsmetode`() {
        val aremark = "30117942593"
        Assertions.assertEquals(true, gyldigFnr(aremark))
    }

}
