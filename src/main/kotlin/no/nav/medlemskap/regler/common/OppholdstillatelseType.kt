package no.nav.medlemskap.regler.common

import no.nav.medlemskap.domene.Datagrunnlag

//Funksjonen sjekker om oppholdstillatelsen er av typen EØS eller EFTA
fun harEØSEllerEftaOppholdstillatelse(datagrunnlag: Datagrunnlag): Boolean {
    val eøsEllerEFTAOpphold = datagrunnlag.oppholdstillatelse?.gjeldendeOppholdsstatus?.eosellerEFTAOpphold
    return eøsEllerEFTAOpphold != null

}