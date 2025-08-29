package no.nav.medlemskap.regler.v1.arbeidsforhold

import no.nav.medlemskap.domene.Datagrunnlag
import no.nav.medlemskap.domene.Ytelse
import no.nav.medlemskap.domene.arbeidsforhold.Arbeidsavtale.Companion.arbeidsavtalerForKontrollperiode
import no.nav.medlemskap.domene.arbeidsforhold.Arbeidsavtale.Companion.harIngenArbeidsavtaler
import no.nav.medlemskap.domene.arbeidsforhold.Arbeidsforhold
import no.nav.medlemskap.domene.arbeidsforhold.Arbeidsforhold.Companion.arbeidsforholdForKontrollPeriode
import no.nav.medlemskap.domene.arbeidsforhold.Arbeidsforhold.Companion.harIngenArbeidsforhold
import no.nav.medlemskap.domene.personhistorikk.Personhistorikk
import no.nav.medlemskap.regler.common.RegelId
import no.nav.medlemskap.regler.common.Resultat
import no.nav.medlemskap.regler.common.Resultat.Companion.ja
import no.nav.medlemskap.regler.common.Resultat.Companion.nei
import java.time.LocalDate

class HarBrukerVaertIMinst60ProsentStillingSiste12MndPaaEnArbeidsavtale(
    ytelse: Ytelse,
    startDatoForYtelse: LocalDate,
    private val arbeidsforhold: List<Arbeidsforhold>,
    regelId: RegelId = RegelId.REGEL_64,
    personhistorikk: Personhistorikk
) : ArbeidsforholdRegel(regelId, ytelse, startDatoForYtelse, personhistorikk) {

    override fun operasjon(): Resultat {

        val arbeidforholdForKontrollPeriode = arbeidsforhold.arbeidsforholdForKontrollPeriode(kontrollPeriodeForArbeidsforhold)
        if (arbeidforholdForKontrollPeriode.harIngenArbeidsforhold()) return nei(regelId)

        val arbeidsforholdet = arbeidforholdForKontrollPeriode.first()

        val arbeidsavtalerForKontrollPeriode = arbeidsforholdet.arbeidsavtaler.arbeidsavtalerForKontrollperiode(kontrollPeriodeForArbeidsforhold)
        if (arbeidsavtalerForKontrollPeriode.harIngenArbeidsavtaler()) return nei(regelId)

        val arbeidsavtalen = arbeidsavtalerForKontrollPeriode.first()

        return when {
            arbeidsavtalen.stillingsprosent!! >= statsborgerskapsrelatertStillingsprosent -> ja(regelId)
            else -> nei(regelId)
        }
    }


    companion object {

        fun fraDatagrunnlag(datagrunnlag: Datagrunnlag): HarBrukerVaertIMinst60ProsentStillingSiste12MndPaaEnArbeidsavtale {
            return HarBrukerVaertIMinst60ProsentStillingSiste12MndPaaEnArbeidsavtale(
                ytelse = datagrunnlag.ytelse,
                startDatoForYtelse = datagrunnlag.startDatoForYtelse,
                arbeidsforhold = datagrunnlag.arbeidsforhold,
                personhistorikk = datagrunnlag.pdlpersonhistorikk
            )
        }
    }
}
