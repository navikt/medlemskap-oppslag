package no.nav.medlemskap.regler.v1.arbeidsforhold

import no.nav.medlemskap.domene.Datagrunnlag
import no.nav.medlemskap.domene.Ytelse
import no.nav.medlemskap.domene.arbeidsforhold.Arbeidsavtale.Companion.arbeidsavtalerForKontrollperiode
import no.nav.medlemskap.domene.arbeidsforhold.Arbeidsavtale.Companion.grupperAvtaler
import no.nav.medlemskap.domene.arbeidsforhold.Arbeidsavtale.Companion.harIngenArbeidsavtaler
import no.nav.medlemskap.domene.arbeidsforhold.Arbeidsavtale.Companion.harVartHeleKontrollperioden
import no.nav.medlemskap.domene.arbeidsforhold.Arbeidsavtale.Companion.sammenhengendeArbeidsavtaler
import no.nav.medlemskap.domene.arbeidsforhold.Arbeidsforhold
import no.nav.medlemskap.domene.arbeidsforhold.Arbeidsforhold.Companion.arbeidsforholdForKontrollPeriode
import no.nav.medlemskap.domene.arbeidsforhold.Arbeidsforhold.Companion.harIngenArbeidsforhold
import no.nav.medlemskap.regler.common.RegelId
import no.nav.medlemskap.regler.common.Resultat
import no.nav.medlemskap.regler.common.Resultat.Companion.ja
import no.nav.medlemskap.regler.common.Resultat.Companion.nei
import java.time.LocalDate

class HarBrukerVaertIMinst60ProsentStillingIEnAvArbeidsavtaleSiste12Mnd(
    ytelse: Ytelse,
    startDatoForYtelse: LocalDate,
    private val arbeidsforhold: List<Arbeidsforhold>,
    regelId: RegelId = RegelId.REGEL_66
) : ArbeidsforholdRegel(regelId, ytelse, startDatoForYtelse) {

    override fun operasjon(): Resultat {

        val arbeidforholdForKontrollPeriode = arbeidsforhold
            .arbeidsforholdForKontrollPeriode(kontrollPeriodeForArbeidsforhold)
        if (arbeidforholdForKontrollPeriode.harIngenArbeidsforhold()) return nei(regelId)

        val arbeidsavtalerForKontrollPeriode = arbeidforholdForKontrollPeriode.first().arbeidsavtaler
            .arbeidsavtalerForKontrollperiode(kontrollPeriodeForArbeidsforhold)
        if (arbeidsavtalerForKontrollPeriode.harIngenArbeidsavtaler()) return nei(regelId)


        return when {
            arbeidsavtalerForKontrollPeriode
                .filter { it.stillingsprosent!! >= 60 }
                .sammenhengendeArbeidsavtaler(kontrollPeriodeForArbeidsforhold, 0)
                .grupperAvtaler()
                .any { it.harVartHeleKontrollperioden(kontrollPeriodeForArbeidsforhold) }
                -> ja(regelId)

            else -> nei(regelId)
        }
    }

    companion object {

        fun fraDatagrunnlag(datagrunnlag: Datagrunnlag): HarBrukerVaertIMinst60ProsentStillingIEnAvArbeidsavtaleSiste12Mnd {
            return HarBrukerVaertIMinst60ProsentStillingIEnAvArbeidsavtaleSiste12Mnd(
                ytelse = datagrunnlag.ytelse,
                startDatoForYtelse = datagrunnlag.startDatoForYtelse,
                arbeidsforhold = datagrunnlag.arbeidsforhold
            )
        }
    }
}
