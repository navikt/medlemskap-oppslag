package no.nav.medlemskap.regler.v1.regelflyt.arbeid

import no.nav.medlemskap.regler.common.Regelflyt.Companion.konklusjonUavklart
import no.nav.medlemskap.domene.Datagrunnlag
import no.nav.medlemskap.domene.Ytelse
import no.nav.medlemskap.regler.common.RegelId
import no.nav.medlemskap.regler.common.RegelId.*
import no.nav.medlemskap.regler.common.Regelflyt
import no.nav.medlemskap.regler.common.Regelflyt.Companion.regelflytJa
import no.nav.medlemskap.regler.common.Regler
import no.nav.medlemskap.regler.common.Svar
import no.nav.medlemskap.regler.v1.RegelFactory

class ReglerForPermisjoner(
    ytelse: Ytelse,
    regelFactory: RegelFactory,
    overstyrteRegler: Map<RegelId, Svar>
) : Regler(ytelse, regelFactory, overstyrteRegler) {

    override fun hentHovedflyt(): Regelflyt {
        val ErSummenAvPermisjonenMerEnn60DagerSiste12Mnd = lagRegelflyt(
            regel = hentRegel(REGEL_33),
            hvisJa = konklusjonUavklart(ytelse, REGEL_PERMISJONER),
            hvisNei = regelflytJa(ytelse, REGEL_PERMISJONER),
        )

        val HarBrukerPermisjonSiste12MånederFlyt = lagRegelflyt(
            regel = hentRegel(REGEL_32),
            hvisJa = ErSummenAvPermisjonenMerEnn60DagerSiste12Mnd,
            hvisNei = regelflytJa(ytelse, REGEL_PERMISJONER),
        )
        return HarBrukerPermisjonSiste12MånederFlyt
    }

    companion object {
        fun fraDatagrunnlag(datagrunnlag: Datagrunnlag): ReglerForPermisjoner {
            with(datagrunnlag) {
                return ReglerForPermisjoner(
                    ytelse = ytelse,
                    regelFactory = RegelFactory(datagrunnlag),
                    overstyrteRegler = datagrunnlag.overstyrteRegler
                )
            }
        }
    }
}
