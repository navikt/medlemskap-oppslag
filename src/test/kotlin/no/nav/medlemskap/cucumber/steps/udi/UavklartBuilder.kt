package no.nav.medlemskap.cucumber.steps.udi

import no.udi.mt_1067_nav_data.v1.Uavklart

class UavklartBuilder {
    var uavklart = Uavklart()

    fun build(): Uavklart {
        return uavklart
    }
}
