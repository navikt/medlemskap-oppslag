package no.nav.medlemskap.regler.v1

import no.nav.medlemskap.domene.Datagrunnlag
import no.nav.medlemskap.domene.InputPeriode
import no.nav.medlemskap.domene.Ytelse
import no.nav.medlemskap.regler.common.RegelId
import no.nav.medlemskap.regler.common.RegelId.*
import no.nav.medlemskap.regler.common.Regelflyt
import no.nav.medlemskap.regler.common.Regelflyt.Companion.konklusjonUavklart
import no.nav.medlemskap.regler.common.Regelflyt.Companion.regelflytJa
import no.nav.medlemskap.regler.common.Regelflyt.Companion.regelflytUavklart
import no.nav.medlemskap.regler.common.Regler
import no.nav.medlemskap.regler.common.Svar

class ReglerForMaritim(
    ytelse: Ytelse,
    regelFactory: RegelFactory,
    overstyrteRegler: Map<RegelId, Svar>
) : Regler(ytelse, regelFactory, overstyrteRegler) {

    override fun hentHovedflyt(): Regelflyt {

        val harSammenhengendeArbeidsforholdSiste12MånederPåNORSkipflyt = lagRegelflyt(
            regel = hentRegel(REGEL_36),
            hvisJa = regelflytJa(ytelse, REGEL_MARITIM),
            hvisNei = regelflytUavklart(ytelse, REGEL_MARITIM)
        )

        val harBrukerMaritimtArbeidsforholdSiste12Månederflyt = lagRegelflyt(
            regel = hentRegel(REGEL_42),
            hvisJa = regelflytUavklart(ytelse, REGEL_MARITIM),
            hvisNei = regelflytJa(ytelse, REGEL_MARITIM)
        )

        val erBrukerIMaritimtArbeidsforholdDagenFørStartdatoForYtelseFlyt = lagRegelflyt(
            regel = hentRegel(REGEL_35),
            hvisJa = harSammenhengendeArbeidsforholdSiste12MånederPåNORSkipflyt,
            hvisNei = harBrukerMaritimtArbeidsforholdSiste12Månederflyt
        )

        return erBrukerIMaritimtArbeidsforholdDagenFørStartdatoForYtelseFlyt
    }

    companion object {
        fun fraDatagrunnlag(datagrunnlag: Datagrunnlag): ReglerForMaritim {
            with(datagrunnlag) {
                return ReglerForMaritim(
                    ytelse = ytelse,
                    regelFactory = RegelFactory(datagrunnlag),
                    overstyrteRegler = datagrunnlag.overstyrteRegler
                )
            }
        }
    }
}
