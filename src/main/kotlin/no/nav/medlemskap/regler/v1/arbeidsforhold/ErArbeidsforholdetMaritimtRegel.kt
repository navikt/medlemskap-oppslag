package no.nav.medlemskap.regler.v1.arbeidsforhold

import no.nav.medlemskap.domene.*
import no.nav.medlemskap.regler.common.Funksjoner.alleEr
import no.nav.medlemskap.regler.common.RegelId
import no.nav.medlemskap.regler.common.Resultat
import no.nav.medlemskap.regler.common.ja
import no.nav.medlemskap.regler.common.nei
import no.nav.medlemskap.regler.funksjoner.ArbeidsforholdFunksjoner.arbeidsforholdForYrkestype

class ErArbeidsforholdetMaritimtRegel(
        ytelse: Ytelse,
        private val periode: InputPeriode,
        private val arbeidsforhold: List<Arbeidsforhold>,
        regelId: RegelId = RegelId.REGEL_7
) : ArbeidsforholdRegel(regelId, ytelse, periode) {

    override fun operasjon(): Resultat {
        return when {
            arbeidsforhold.arbeidsforholdForYrkestype(kontrollPeriodeForArbeidsforhold) alleEr Arbeidsforholdstype.MARITIM.navn -> ja()
            else -> nei()
        }
    }

    companion object {

        fun fraDatagrunnlag(datagrunnlag: Datagrunnlag): ErArbeidsforholdetMaritimtRegel {
            return ErArbeidsforholdetMaritimtRegel(
                    ytelse = datagrunnlag.ytelse,
                    periode = datagrunnlag.periode,
                    arbeidsforhold = datagrunnlag.arbeidsforhold
            )
        }
    }
}