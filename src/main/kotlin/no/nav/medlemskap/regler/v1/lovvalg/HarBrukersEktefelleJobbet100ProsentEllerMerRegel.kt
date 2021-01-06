package no.nav.medlemskap.regler.v1.lovvalg

import no.nav.medlemskap.domene.Datagrunnlag
import no.nav.medlemskap.domene.InputPeriode
import no.nav.medlemskap.domene.Ytelse
import no.nav.medlemskap.domene.arbeidsforhold.Arbeidsforhold
import no.nav.medlemskap.domene.arbeidsforhold.Arbeidsforhold.Companion.harBrukerJobbetMerEnnGittStillingsprosentTilEnhverTid
import no.nav.medlemskap.regler.common.RegelId
import no.nav.medlemskap.regler.common.Resultat
import no.nav.medlemskap.regler.common.Resultat.Companion.ja
import no.nav.medlemskap.regler.common.Resultat.Companion.nei
import java.time.LocalDate

class HarBrukersEktefelleJobbet100ProsentEllerMerRegel(
    ytelse: Ytelse,
    private val periode: InputPeriode,
    førsteDagForYtelse: LocalDate?,
    private val arbeidsforholdEktefelle: List<Arbeidsforhold>,
    regelId: RegelId = RegelId.REGEL_11_6_1
) : LovvalgRegel(regelId, ytelse, periode, førsteDagForYtelse) {

    override fun operasjon(): Resultat {

        return when {
            arbeidsforholdEktefelle.harBrukerJobbetMerEnnGittStillingsprosentTilEnhverTid(100.0, kontrollPeriodeForArbeidsforhold, ytelse) -> ja(regelId)
            else -> nei(regelId)
        }
    }

    companion object {

        fun fraDatagrunnlag(datagrunnlag: Datagrunnlag): HarBrukersEktefelleJobbet100ProsentEllerMerRegel {
            return HarBrukersEktefelleJobbet100ProsentEllerMerRegel(
                ytelse = datagrunnlag.ytelse,
                periode = datagrunnlag.periode,
                førsteDagForYtelse = datagrunnlag.førsteDagForYtelse,
                arbeidsforholdEktefelle = datagrunnlag.dataOmEktefelle?.arbeidsforholdEktefelle ?: emptyList()
            )
        }
    }
}
