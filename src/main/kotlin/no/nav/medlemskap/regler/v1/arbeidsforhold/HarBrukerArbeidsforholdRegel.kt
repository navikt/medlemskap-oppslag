package no.nav.medlemskap.regler.v1.arbeidsforhold

import no.nav.medlemskap.domene.Datagrunnlag
import no.nav.medlemskap.domene.InputPeriode
import no.nav.medlemskap.domene.Ytelse
import no.nav.medlemskap.domene.arbeidsforhold.Arbeidsforhold
import no.nav.medlemskap.regler.common.RegelId
import no.nav.medlemskap.regler.common.Resultat
import no.nav.medlemskap.regler.common.Resultat.Companion.ja
import no.nav.medlemskap.regler.common.Resultat.Companion.nei
import java.time.LocalDate

class HarBrukerArbeidsforholdRegel(
    ytelse: Ytelse,
    private val periode: InputPeriode,
    private val førsteDagForYtelse: LocalDate?,
    private val arbeidsforhold: List<Arbeidsforhold>,
    regelId: RegelId = RegelId.REGEL_17
) : ArbeidsforholdRegel(regelId, ytelse, periode, førsteDagForYtelse) {

    override fun operasjon(): Resultat {
        return when {
            arbeidsforhold.isEmpty() -> nei(regelId)
            else -> ja(regelId)
        }
    }

    companion object {

        fun fraDatagrunnlag(datagrunnlag: Datagrunnlag): HarBrukerArbeidsforholdRegel {
            return HarBrukerArbeidsforholdRegel(
                ytelse = datagrunnlag.ytelse,
                periode = datagrunnlag.periode,
                førsteDagForYtelse = datagrunnlag.førsteDagForYtelse,
                arbeidsforhold = datagrunnlag.arbeidsforhold
            )
        }
    }
}
