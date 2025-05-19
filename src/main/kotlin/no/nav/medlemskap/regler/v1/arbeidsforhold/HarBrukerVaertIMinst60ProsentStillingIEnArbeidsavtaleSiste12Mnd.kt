package no.nav.medlemskap.regler.v1.arbeidsforhold

import no.nav.medlemskap.domene.Datagrunnlag
import no.nav.medlemskap.domene.Ytelse
import no.nav.medlemskap.domene.arbeidsforhold.Arbeidsavtale.Companion.arbeidsavtalerForKontrollperiode
import no.nav.medlemskap.domene.arbeidsforhold.Arbeidsavtale.Companion.harIngenArbeidsavtaler
import no.nav.medlemskap.domene.arbeidsforhold.Arbeidsavtale.Companion.sammenhengendeArbeidsavtaler
import no.nav.medlemskap.domene.arbeidsforhold.Arbeidsforhold
import no.nav.medlemskap.domene.arbeidsforhold.Arbeidsforhold.Companion.arbeidsforholdForKontrollPeriode
import no.nav.medlemskap.domene.arbeidsforhold.Arbeidsforhold.Companion.harIngenArbeidsforhold
import no.nav.medlemskap.regler.common.RegelId
import no.nav.medlemskap.regler.common.Resultat
import no.nav.medlemskap.regler.common.Resultat.Companion.ja
import no.nav.medlemskap.regler.common.Resultat.Companion.nei
import java.time.LocalDate

class HarBrukerVaertIMinst60ProsentStillingIEnArbeidsavtaleSiste12Mnd(
    ytelse: Ytelse,
    startDatoForYtelse: LocalDate,
    private val arbeidsforhold: List<Arbeidsforhold>,
    regelId: RegelId = RegelId.REGEL_66
) : ArbeidsforholdRegel(regelId, ytelse, startDatoForYtelse) {

    override fun operasjon(): Resultat {

        val arbeidforholdForKontrollPeriode = arbeidsforhold.arbeidsforholdForKontrollPeriode(kontrollPeriodeForArbeidsforhold)
        if (arbeidforholdForKontrollPeriode.harIngenArbeidsforhold()) return nei(regelId)

        val arbeidsavtalerForKonterollPeriode = arbeidforholdForKontrollPeriode.first().arbeidsavtaler
            .arbeidsavtalerForKontrollperiode(kontrollPeriodeForArbeidsforhold)

        // Finnes det en arbeidsavtale som har vart 12 mnd +
        // Er stillingsprosenten lik 60% eller mer?
        // OK

        return when {
            arbeidsavtalerForKonterollPeriode.harIngenArbeidsavtaler() -> nei(regelId)
            arbeidsavtalerForKonterollPeriode.sammenhengendeArbeidsavtaler(kontrollPeriodeForArbeidsforhold, 0)
                .firstOrNull { it.stillingsprosent!! >= 60 } != null -> ja(regelId)
            else -> nei(regelId)
        }
    }


    companion object {

        fun fraDatagrunnlag(datagrunnlag: Datagrunnlag): HarBrukerVaertIMinst60ProsentStillingIEnArbeidsavtaleSiste12Mnd {
            return HarBrukerVaertIMinst60ProsentStillingIEnArbeidsavtaleSiste12Mnd(
                ytelse = datagrunnlag.ytelse,
                startDatoForYtelse = datagrunnlag.startDatoForYtelse,
                arbeidsforhold = datagrunnlag.arbeidsforhold
            )
        }
    }
}
