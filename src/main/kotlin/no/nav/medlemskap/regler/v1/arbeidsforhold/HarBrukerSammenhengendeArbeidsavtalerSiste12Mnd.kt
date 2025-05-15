package no.nav.medlemskap.regler.v1.arbeidsforhold

import no.nav.medlemskap.domene.Datagrunnlag
import no.nav.medlemskap.domene.Ytelse
import no.nav.medlemskap.domene.arbeidsforhold.Arbeidsavtale.Companion.arbeidsavtalerForKontrollperiode
import no.nav.medlemskap.domene.arbeidsforhold.Arbeidsavtale.Companion.erSammenhengendeArbeidsavtaler
import no.nav.medlemskap.domene.arbeidsforhold.Arbeidsavtale.Companion.harIngenArbeidsavtaler
import no.nav.medlemskap.domene.arbeidsforhold.Arbeidsforhold
import no.nav.medlemskap.domene.arbeidsforhold.Arbeidsforhold.Companion.arbeidsforholdForKontrollPeriode
import no.nav.medlemskap.domene.arbeidsforhold.Arbeidsforhold.Companion.harIngenArbeidsforhold
import no.nav.medlemskap.regler.common.RegelId
import no.nav.medlemskap.regler.common.Resultat
import no.nav.medlemskap.regler.common.Resultat.Companion.ja
import no.nav.medlemskap.regler.common.Resultat.Companion.nei
import java.time.LocalDate

class HarBrukerSammenhengendeArbeidsavtalerSiste12Mnd(
    ytelse: Ytelse,
    startDatoForYtelse: LocalDate,
    private val arbeidsforhold: List<Arbeidsforhold>,
    regelId: RegelId = RegelId.REGEL_66
) : ArbeidsforholdRegel(regelId, ytelse, startDatoForYtelse) {

    override fun operasjon(): Resultat {

        val arbeidforholdForKontrollPeriode = arbeidsforhold.arbeidsforholdForKontrollPeriode(kontrollPeriodeForArbeidsforhold)
        if (arbeidforholdForKontrollPeriode.harIngenArbeidsforhold()) return nei(regelId)

        val arbeidsforholdet = arbeidforholdForKontrollPeriode.first()

        val arbeidsavtalerForKontrollPeriode = arbeidsforholdet.arbeidsavtaler.arbeidsavtalerForKontrollperiode(kontrollPeriodeForArbeidsforhold)
        if (arbeidsavtalerForKontrollPeriode.harIngenArbeidsavtaler()) return nei(regelId)

        return when {
            arbeidsavtalerForKontrollPeriode.erSammenhengendeArbeidsavtaler(kontrollPeriodeForArbeidsforhold, 0) -> ja(regelId)
            else -> nei(regelId)
        }
    }


    companion object {

        fun fraDatagrunnlag(datagrunnlag: Datagrunnlag): HarBrukerSammenhengendeArbeidsavtalerSiste12Mnd {
            return HarBrukerSammenhengendeArbeidsavtalerSiste12Mnd(
                ytelse = datagrunnlag.ytelse,
                startDatoForYtelse = datagrunnlag.startDatoForYtelse,
                arbeidsforhold = datagrunnlag.arbeidsforhold
            )
        }
    }
}
