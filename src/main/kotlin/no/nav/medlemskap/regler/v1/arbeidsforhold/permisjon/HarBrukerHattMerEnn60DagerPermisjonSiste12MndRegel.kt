package no.nav.medlemskap.regler.v1.arbeidsforhold.permisjon

import no.nav.medlemskap.domene.Datagrunnlag
import no.nav.medlemskap.domene.Periode
import no.nav.medlemskap.domene.Ytelse
import no.nav.medlemskap.domene.arbeidsforhold.Arbeidsforhold
import no.nav.medlemskap.domene.arbeidsforhold.Arbeidsforhold.Companion.hentAllePermisjonerSiden
import no.nav.medlemskap.domene.arbeidsforhold.Arbeidsforhold.Companion.totaltantallDagerIKontrollPeriode
import no.nav.medlemskap.regler.common.RegelId
import no.nav.medlemskap.regler.common.Resultat
import no.nav.medlemskap.regler.common.Resultat.Companion.ja
import no.nav.medlemskap.regler.common.Resultat.Companion.nei
import no.nav.medlemskap.regler.v1.arbeidsforhold.ArbeidsforholdRegel
import java.time.LocalDate

class HarBrukerHattMerEnn60DagerPermisjonSiste12MndRegel(
    ytelse: Ytelse,
    private val startDatoForYtelse: LocalDate,
    private val arbeidsforhold: List<Arbeidsforhold>,
    private val kontrollperiode: Periode,
    private val fnr: String,
    regelId: RegelId = RegelId.REGEL_55,
) : ArbeidsforholdRegel(regelId, ytelse, startDatoForYtelse) {

    override fun operasjon(): Resultat {


        val allePermisjonerSomLøperForMindreEnEtÅrSiden = arbeidsforhold.hentAllePermisjonerSiden(startDatoForYtelse.minusYears(1))
        val totaltAntallDagerPermisjon = allePermisjonerSomLøperForMindreEnEtÅrSiden.totaltantallDagerIKontrollPeriode(kontrollPeriodeForArbeidsforhold)
        if (totaltAntallDagerPermisjon > 60){
            return ja(regelId)
        }
        return nei(regelId)

    }


    companion object {

        fun fraDatagrunnlag(datagrunnlag: Datagrunnlag): HarBrukerHattMerEnn60DagerPermisjonSiste12MndRegel {
            return HarBrukerHattMerEnn60DagerPermisjonSiste12MndRegel(
                ytelse = datagrunnlag.ytelse,
                startDatoForYtelse = datagrunnlag.startDatoForYtelse,
                arbeidsforhold = datagrunnlag.arbeidsforhold,
                kontrollperiode = Periode(datagrunnlag.periode.fom, datagrunnlag.periode.tom),
                fnr = datagrunnlag.fnr,
            )
        }
    }
}


