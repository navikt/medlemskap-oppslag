package no.nav.medlemskap.regler.v1.arbeidsforhold

import no.nav.medlemskap.domene.Arbeidsforhold
import no.nav.medlemskap.domene.Datagrunnlag
import no.nav.medlemskap.domene.InputPeriode
import no.nav.medlemskap.domene.Ytelse
import no.nav.medlemskap.regler.common.RegelId
import no.nav.medlemskap.regler.common.Resultat
import no.nav.medlemskap.regler.common.ja
import no.nav.medlemskap.regler.common.nei
import no.nav.medlemskap.regler.funksjoner.ArbeidsforholdFunksjoner.erArbeidsforholdetOffentligSektor
import no.nav.medlemskap.regler.v1.lovvalg.LovvalgRegel

class ErArbeidsforholdetOffentligSektor(
    ytelse: Ytelse,
    private val periode: InputPeriode,
    private val arbeidsforhold: List<Arbeidsforhold>,
    regelId: RegelId = RegelId.REGEL_14
) : LovvalgRegel(regelId, ytelse, periode) {

    override fun operasjon(): Resultat {
        return when {
            erArbeidsforholdetOffentligSektor(arbeidsforhold, kontrollPeriodeForArbeidsforhold, ytelse) -> ja()
            else -> nei()
        }
    }

    companion object {

        fun fraDatagrunnlag(datagrunnlag: Datagrunnlag): ErArbeidsforholdetOffentligSektor {
            return ErArbeidsforholdetOffentligSektor(
                ytelse = datagrunnlag.ytelse,
                periode = datagrunnlag.periode,
                arbeidsforhold = datagrunnlag.arbeidsforhold
            )
        }
    }
}