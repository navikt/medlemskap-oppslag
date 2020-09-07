package no.nav.medlemskap.regler.v1.arbeidsforhold

import no.nav.medlemskap.domene.*
import no.nav.medlemskap.regler.common.Funksjoner.finnesMindreEnn
import no.nav.medlemskap.regler.common.RegelId
import no.nav.medlemskap.regler.common.Resultat
import no.nav.medlemskap.regler.common.ja
import no.nav.medlemskap.regler.common.nei
import no.nav.medlemskap.regler.funksjoner.ArbeidsforholdFunksjoner.antallAnsatteHosArbeidsgivere
import no.nav.medlemskap.regler.funksjoner.ArbeidsforholdFunksjoner.registrerAntallAnsatte
import no.nav.medlemskap.regler.funksjoner.ArbeidsforholdFunksjoner.registrerArbeidsgiver
import no.nav.medlemskap.regler.funksjoner.StatsborgerskapFunksjoner.registrerStatsborgerskapGrafana

class HarForetaketMerEnn5AnsatteRegel(
    ytelse: Ytelse,
    private val periode: InputPeriode,
    private val arbeidsforhold: List<Arbeidsforhold>,
    private val statsborgerskap: List<Statsborgerskap>,
    regelId: RegelId = RegelId.REGEL_5
) : ArbeidsforholdRegel(regelId, ytelse, periode) {

    override fun operasjon(): Resultat {
        if (arbeidsforhold.antallAnsatteHosArbeidsgivere(kontrollPeriodeForArbeidsforhold) finnesMindreEnn 6) {
            registrerDataForGrafana()
            return nei("Ikke alle arbeidsgivere har 6 ansatte eller flere")
        }

        return ja()
    }

    fun registrerDataForGrafana() {
        statsborgerskap.registrerStatsborgerskapGrafana(kontrollPeriodeForArbeidsforhold, ytelse, regelId)
        arbeidsforhold.registrerArbeidsgiver(kontrollPeriodeForArbeidsforhold, ytelse)
        arbeidsforhold.registrerAntallAnsatte(ytelse)
    }

    companion object {

        fun fraDatagrunnlag(datagrunnlag: Datagrunnlag): HarForetaketMerEnn5AnsatteRegel {
            return HarForetaketMerEnn5AnsatteRegel(
                ytelse = datagrunnlag.ytelse,
                periode = datagrunnlag.periode,
                arbeidsforhold = datagrunnlag.arbeidsforhold,
                statsborgerskap = datagrunnlag.personhistorikk.statsborgerskap
            )
        }
    }
}
