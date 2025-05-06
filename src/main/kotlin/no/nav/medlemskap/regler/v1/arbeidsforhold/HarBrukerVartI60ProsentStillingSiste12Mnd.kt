package no.nav.medlemskap.regler.v1.arbeidsforhold

import no.nav.medlemskap.domene.Datagrunnlag
import no.nav.medlemskap.domene.Ytelse
import no.nav.medlemskap.domene.arbeidsforhold.Arbeidsforhold
import no.nav.medlemskap.domene.arbeidsforhold.Arbeidsforhold.Companion.arbeidsforholdForKontrollPeriode
import no.nav.medlemskap.domene.arbeidsforhold.Arbeidsforhold.Companion.erArbeidsforholdetLøpendeIHelePerioden

import no.nav.medlemskap.regler.common.RegelId
import no.nav.medlemskap.regler.common.Resultat
import no.nav.medlemskap.regler.common.Resultat.Companion.ja
import no.nav.medlemskap.regler.common.Resultat.Companion.nei
import java.time.LocalDate

class HarBrukerVartI60ProsentStillingSiste12Mnd(
    ytelse: Ytelse,
    startDatoForYtelse: LocalDate,
    private val arbeidsforhold: List<Arbeidsforhold>,
    regelId: RegelId = RegelId.REGEL_64
) : ArbeidsforholdRegel(regelId, ytelse, startDatoForYtelse) {

    override fun operasjon(): Resultat {

        val arbeidforholdForKontrollPeriode = arbeidsforhold.arbeidsforholdForKontrollPeriode(kontrollPeriodeForArbeidsforhold)
        if (arbeidforholdForKontrollPeriode.isEmpty()) {
            return nei(regelId)
        }

        if (arbeidforholdForKontrollPeriode.first()
                .erArbeidsforholdetLøpendeIHelePerioden(kontrollPeriodeForArbeidsforhold) == null){
            return ja(regelId)
        }
        return nei(regelId)
    }



    companion object {

        fun fraDatagrunnlag(datagrunnlag: Datagrunnlag): HarBrukerVartI60ProsentStillingSiste12Mnd {
            return HarBrukerVartI60ProsentStillingSiste12Mnd(
                ytelse = datagrunnlag.ytelse,
                startDatoForYtelse = datagrunnlag.startDatoForYtelse,
                arbeidsforhold = datagrunnlag.arbeidsforhold
            )
        }
    }
}
