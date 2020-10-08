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

class HarBrukersEktefelleJobbet100ProsentEllerMerRegel(
    ytelse: Ytelse,
    private val periode: InputPeriode,
    private val arbeidsforholdEktefelle: List<Arbeidsforhold>,
    regelId: RegelId = RegelId.REGEL_11_6_1
) : LovvalgRegel(regelId, ytelse, periode) {

    override fun operasjon(): Resultat {
        gjennomsnittligStillingsprosentCounter(arbeidsforholdEktefelle.beregnGjennomsnittligStillingsprosentForGrafana(kontrollPeriodeForArbeidsforhold), ytelse)
        return when {
            arbeidsforholdEktefelle.harBrukerJobbetMerEnnGittStillingsprosentTilEnhverTid(100.0, kontrollPeriodeForArbeidsforhold, ytelse) -> ja()
            else -> nei("Brukers ektefelle har ikke jobbet 100% eller mer i løpet av periode.")
        }
    }

    companion object {

        fun fraDatagrunnlag(datagrunnlag: Datagrunnlag): HarBrukersEktefelleJobbet100ProsentEllerMerRegel {
            return HarBrukersEktefelleJobbet100ProsentEllerMerRegel(
                ytelse = datagrunnlag.ytelse,
                periode = datagrunnlag.periode,
                arbeidsforholdEktefelle = datagrunnlag.dataOmEktefelle?.arbeidsforholdEktefelle ?: emptyList()
            )
        }
    }
}
