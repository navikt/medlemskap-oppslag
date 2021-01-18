package no.nav.medlemskap.cucumber.steps.udi

import no.udi.mt_1067_nav_data.v1.*

class UdiGjeldendeOppholdstatusBuilder {
    var eoSellerEFTAOpphold: EOSellerEFTAOpphold? = null
    var oppholdstillatelseEllerOppholdsPaSammeVilkar: OppholdstillatelseEllerOppholdsPaSammeVilkar? = null
    var ikkeOppholdstillatelseIkkeOppholdsPaSammeVilkarIkkeVisum: IkkeOppholdstillatelseIkkeOppholdsPaSammeVilkarIkkeVisum? = null
    var uavklart: Uavklart? = null

    fun build(): GjeldendeOppholdsstatus {
        var gjeldendeOppholdsstatus = GjeldendeOppholdsstatus()
        gjeldendeOppholdsstatus.eoSellerEFTAOpphold = eoSellerEFTAOpphold
        gjeldendeOppholdsstatus.oppholdstillatelseEllerOppholdsPaSammeVilkar = oppholdstillatelseEllerOppholdsPaSammeVilkar
        gjeldendeOppholdsstatus.ikkeOppholdstillatelseIkkeOppholdsPaSammeVilkarIkkeVisum = ikkeOppholdstillatelseIkkeOppholdsPaSammeVilkarIkkeVisum
        return gjeldendeOppholdsstatus
    }
}
