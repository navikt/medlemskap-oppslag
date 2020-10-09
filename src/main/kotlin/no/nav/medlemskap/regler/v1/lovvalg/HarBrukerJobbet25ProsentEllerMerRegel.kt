package no.nav.medlemskap.regler.v1.lovvalg

import no.nav.medlemskap.common.gjennomsnittligStillingsprosentCounter
import no.nav.medlemskap.domene.Arbeidsforhold
import no.nav.medlemskap.domene.Datagrunnlag
import no.nav.medlemskap.domene.InputPeriode
import no.nav.medlemskap.domene.Ytelse
import no.nav.medlemskap.regler.common.RegelId
import no.nav.medlemskap.regler.common.Resultat
import no.nav.medlemskap.regler.common.ja
import no.nav.medlemskap.regler.common.nei
import no.nav.medlemskap.regler.funksjoner.ArbeidsforholdFunksjoner.beregnGjennomsnittligStillingsprosentForGrafana
import no.nav.medlemskap.regler.funksjoner.ArbeidsforholdFunksjoner.harBrukerJobbetMerEnnGittStillingsprosentTilEnhverTid

class HarBrukerJobbet25ProsentEllerMerRegel(
    ytelse: Ytelse,
    private val periode: InputPeriode,
    private val arbeidsforhold: List<Arbeidsforhold>,
    regelId: RegelId = RegelId.REGEL_12
) : LovvalgRegel(regelId, ytelse, periode) {

    override fun operasjon(): Resultat {
        gjennomsnittligStillingsprosentCounter(arbeidsforhold.beregnGjennomsnittligStillingsprosentForGrafana(kontrollPeriodeForArbeidsforhold), ytelse)
        return when {
            arbeidsforhold.harBrukerJobbetMerEnnGittStillingsprosentTilEnhverTid(25.0, kontrollPeriodeForArbeidsforhold, ytelse) -> ja()
            else -> nei("Bruker har ikke jobbet 25% eller mer i løpet av periode.")
        }
    }

    companion object {

        fun fraDatagrunnlag(datagrunnlag: Datagrunnlag): HarBrukerJobbet25ProsentEllerMerRegel {
            return HarBrukerJobbet25ProsentEllerMerRegel(
                ytelse = datagrunnlag.ytelse,
                periode = datagrunnlag.periode,
                arbeidsforhold = datagrunnlag.arbeidsforhold
            )
        }
    }
}
