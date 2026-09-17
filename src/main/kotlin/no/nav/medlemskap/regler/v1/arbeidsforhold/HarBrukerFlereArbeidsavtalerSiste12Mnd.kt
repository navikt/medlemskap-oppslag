package no.nav.medlemskap.regler.v1.arbeidsforhold

import no.nav.medlemskap.domene.Datagrunnlag
import no.nav.medlemskap.domene.Ytelse
import no.nav.medlemskap.domene.arbeidsforhold.Arbeidsavtale.Companion.arbeidsavtalerForKontrollperiode
import no.nav.medlemskap.domene.arbeidsforhold.Arbeidsavtale.Companion.harFlereArbeidsavtaler
import no.nav.medlemskap.domene.arbeidsforhold.Arbeidsforhold
import no.nav.medlemskap.domene.arbeidsforhold.Arbeidsforhold.Companion.arbeidsforholdForKontrollPeriode
import no.nav.medlemskap.domene.arbeidsforhold.Arbeidsforhold.Companion.harIngenArbeidsforhold
import no.nav.medlemskap.regler.common.RegelId
import no.nav.medlemskap.regler.common.Resultat
import no.nav.medlemskap.regler.common.Resultat.Companion.ja
import no.nav.medlemskap.regler.common.Resultat.Companion.nei
import java.time.LocalDate

class HarBrukerFlereArbeidsavtalerSiste12Mnd(
    ytelse: Ytelse,
    startDatoForYtelse: LocalDate,
    private val arbeidsforhold: List<Arbeidsforhold>,
    regelId: RegelId = RegelId.REGEL_65
) : ArbeidsforholdRegel(regelId, ytelse, startDatoForYtelse) {

    override fun operasjon(): Resultat {

        val arbeidforholdForKontrollPeriode = arbeidsforhold.arbeidsforholdForKontrollPeriode(kontrollPeriodeForArbeidsforhold)
        if (arbeidforholdForKontrollPeriode.harIngenArbeidsforhold()) return nei(regelId)

        val arbeidsforholdet = arbeidforholdForKontrollPeriode.first()
        val arbeidsavtalerForKontrollPerioden = arbeidsforholdet.arbeidsavtaler.arbeidsavtalerForKontrollperiode(kontrollPeriodeForArbeidsforhold)

        return when {
            arbeidsavtalerForKontrollPerioden.harFlereArbeidsavtaler() -> ja(regelId)
            else -> nei(regelId)
        }
    }


    companion object {

        fun fraDatagrunnlag(datagrunnlag: Datagrunnlag): HarBrukerFlereArbeidsavtalerSiste12Mnd {
            return HarBrukerFlereArbeidsavtalerSiste12Mnd(
                ytelse = datagrunnlag.ytelse,
                startDatoForYtelse = datagrunnlag.startDatoForYtelse,
                arbeidsforhold = datagrunnlag.arbeidsforhold
            )
        }
    }
}
