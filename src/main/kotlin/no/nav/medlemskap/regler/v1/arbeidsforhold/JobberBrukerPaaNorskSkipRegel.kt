package no.nav.medlemskap.regler.v1.arbeidsforhold

import no.nav.medlemskap.domene.*
import no.nav.medlemskap.regler.common.Funksjoner.kunInneholder
import no.nav.medlemskap.regler.common.RegelId
import no.nav.medlemskap.regler.common.Resultat
import no.nav.medlemskap.regler.common.ja
import no.nav.medlemskap.regler.common.nei
import no.nav.medlemskap.regler.funksjoner.ArbeidsforholdFunksjoner.sisteArbeidsforholdSkipsregister

class JobberBrukerPaaNorskSkipRegel(
        ytelse: Ytelse,
        private val periode: InputPeriode,
        private val arbeidsforhold: List<Arbeidsforhold>,
        regelId: RegelId = RegelId.REGEL_7_1
) : ArbeidsforholdRegel(regelId, ytelse, periode) {

    override fun operasjon(): Resultat {
        return when {
            arbeidsforhold sisteArbeidsforholdSkipsregister kontrollPeriodeForArbeidsforhold kunInneholder Skipsregister.NOR.name -> ja()
            else -> nei()
        }
    }

    companion object {

        fun fraDatagrunnlag(datagrunnlag: Datagrunnlag): JobberBrukerPaaNorskSkipRegel {
            return JobberBrukerPaaNorskSkipRegel(
                    ytelse = datagrunnlag.ytelse,
                    periode = datagrunnlag.periode,
                    arbeidsforhold = datagrunnlag.arbeidsforhold
            )
        }
    }
}