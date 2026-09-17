package no.nav.medlemskap.regler.v1.brukergruppe

import no.nav.medlemskap.domene.Datagrunnlag
import no.nav.medlemskap.domene.Ytelse
import no.nav.medlemskap.regler.common.RegelId
import no.nav.medlemskap.regler.common.RegelId.*
import no.nav.medlemskap.regler.common.Regelflyt
import no.nav.medlemskap.regler.common.Regelflyt.Companion.konklusjonJa
import no.nav.medlemskap.regler.common.Regelflyt.Companion.konklusjonUavklart
import no.nav.medlemskap.regler.common.Regelflyt.Companion.regelflytJa
import no.nav.medlemskap.regler.common.Regler
import no.nav.medlemskap.regler.common.Svar
import no.nav.medlemskap.regler.v1.RegelFactory

class ReglerForBrukerGruppe(
    ytelse: Ytelse,
    regelFactory: RegelFactory,
    overstyrteRegler: Map<RegelId, Svar>
) : Regler(ytelse, regelFactory, overstyrteRegler) {

    override fun hentHovedflyt(): Regelflyt {
        val HarBrukerArbeidsforholdFlyt = lagRegelflyt(
            regel = hentRegel(REGEL_17),
            hvisJa = regelflytJa(ytelse, REGEL_BRUKERGRUPPE),
            hvisNei = konklusjonUavklart(ytelse, REGEL_BRUKERGRUPPE)
        )
        return HarBrukerArbeidsforholdFlyt
    }


    companion object {
        fun fraDatagrunnlag(datagrunnlag: Datagrunnlag): ReglerForBrukerGruppe {
            with(datagrunnlag) {
                return ReglerForBrukerGruppe(
                    ytelse = ytelse,
                    regelFactory = RegelFactory(datagrunnlag),
                    overstyrteRegler = datagrunnlag.overstyrteRegler
                )
            }
        }
    }
}
