package no.nav.medlemskap.regler.v1

import no.nav.medlemskap.domene.Datagrunnlag
import no.nav.medlemskap.domene.Ytelse
import no.nav.medlemskap.regler.common.Resultat

object ReglerService {

    fun kjørRegler(datagrunnlag: Datagrunnlag): Resultat {
        when (datagrunnlag.ytelse) {
            Ytelse.SYKEPENGER -> return Hovedregler(datagrunnlag).kjørHovedregler()
            Ytelse.DAGPENGER -> return DagPengeRegler(datagrunnlag).kjørHovedregler()
            else -> return Hovedregler(datagrunnlag).kjørHovedregler()
        }
    }
}
