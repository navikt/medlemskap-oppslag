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

class HarBrukerJobbet100ProsentEllerMerRegel(
    ytelse: Ytelse,
    private val periode: InputPeriode,
    private val arbeidsforhold: List<Arbeidsforhold>,
    regelId: RegelId = RegelId.REGEL_11_2_2_1
) : LovvalgRegel(regelId, ytelse, periode) {

    override fun operasjon(): Resultat {
        gjennomsnittligStillingsprosentCounter(arbeidsforhold.beregnGjennomsnittligStillingsprosentForGrafana(kontrollPeriodeForArbeidsforhold), ytelse)

        return when {
            arbeidsforhold.harBrukerJobbetMerEnnGittStillingsprosentTilEnhverTid(100.0, kontrollPeriodeForArbeidsforhold, ytelse) -> ja()
            else -> nei("Bruker har ikke jobbet 100% eller mer i løpet av periode.")
        }
    }

    companion object {

        fun fraDatagrunnlag(datagrunnlag: Datagrunnlag, regelId: RegelId): HarBrukerJobbet100ProsentEllerMerRegel {
            return HarBrukerJobbet100ProsentEllerMerRegel(
                ytelse = datagrunnlag.ytelse,
                periode = datagrunnlag.periode,
                arbeidsforhold = datagrunnlag.arbeidsforhold,
                regelId = regelId
            )
        }
    }
}
