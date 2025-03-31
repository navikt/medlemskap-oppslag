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


        val HarBrukerHattMerEn60DagerPermisjonSiste12Mnd = lagRegelflyt(
            regel = hentRegel(REGEL_55),
            hvisJa = konklusjonUavklart(ytelse, REGEL_PERMISJONER),
            hvisNei =  regelflytJa(ytelse, REGEL_PERMISJONER),
        )

        val ErPeriodeMedEn30DagerSidenOgForeldrePermisjon = lagRegelflyt(
            regel = hentRegel(REGEL_57),
            hvisJa = regelflytJa(ytelse, REGEL_PERMISJONER),
            hvisNei =konklusjonUavklart(ytelse, REGEL_PERMISJONER),
        )

        val HarBrukerEnPeriodeMedPermisjonNaa = lagRegelflyt(
            regel = hentRegel(REGEL_54),
            hvisJa = HarBrukerHattMerEn60DagerPermisjonSiste12Mnd,
            hvisNei = ErPeriodeMedEn30DagerSidenOgForeldrePermisjon,
        )

        val ErSummenAvPermisjonerMerEn60Dager = lagRegelflyt(
            regel = hentRegel(REGEL_51),
            hvisJa = konklusjonUavklart(ytelse, REGEL_PERMISJONER),
            hvisNei = regelflytJa(ytelse, REGEL_PERMISJONER),
        )

        val HarBrukerBareEPeriodeMedPensjon = lagRegelflyt(
            regel = hentRegel(REGEL_50),
            hvisJa = HarBrukerEnPeriodeMedPermisjonNaa,
            hvisNei = ErSummenAvPermisjonerMerEn60Dager,
        )
        return HarBrukerBareEPeriodeMedPensjon
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
