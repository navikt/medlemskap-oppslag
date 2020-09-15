package no.nav.medlemskap.regler.v1.arbeidsforhold

import no.nav.medlemskap.domene.*
import no.nav.medlemskap.regler.common.RegelId
import no.nav.medlemskap.regler.common.Resultat
import no.nav.medlemskap.regler.common.ja
import no.nav.medlemskap.regler.common.nei
import no.nav.medlemskap.regler.funksjoner.ArbeidsforholdFunksjoner.erSammenhengendeIKontrollPeriode
import no.nav.medlemskap.regler.funksjoner.StatsborgerskapFunksjoner.registrerStatsborgerskapGrafana

class HarBrukerSammenhengendeArbeidsforholdRegel(
    ytelse: Ytelse,
    private val periode: InputPeriode,
    private val arbeidsforhold: List<Arbeidsforhold>,
    private val statsborgerskap: List<Statsborgerskap>,
    regelId: RegelId = RegelId.REGEL_3
) : ArbeidsforholdRegel(regelId, ytelse, periode) {

    override fun operasjon(): Resultat {
        return if (!arbeidsforhold.erSammenhengendeIKontrollPeriode(kontrollPeriodeForArbeidsforhold, ytelse)) {
            statsborgerskap.registrerStatsborgerskapGrafana(kontrollPeriodeForArbeidsforhold, ytelse, regelId)
            nei("Arbeidstaker har ikke sammenhengende arbeidsforhold siste 12 mnd")
        } else {
            ja()
        }
    }

    companion object {

        fun fraDatagrunnlag(datagrunnlag: Datagrunnlag): HarBrukerSammenhengendeArbeidsforholdRegel {
            return HarBrukerSammenhengendeArbeidsforholdRegel(
                ytelse = datagrunnlag.ytelse,
                periode = datagrunnlag.periode,
                arbeidsforhold = datagrunnlag.arbeidsforhold,
                statsborgerskap = datagrunnlag.pdlpersonhistorikk?.statsborgerskap
            )
        }
    }
}
