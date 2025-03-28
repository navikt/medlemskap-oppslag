package no.nav.medlemskap.regler.v1.arbeidsforhold.permisjon

import no.nav.medlemskap.domene.Datagrunnlag
import no.nav.medlemskap.domene.Periode
import no.nav.medlemskap.domene.Ytelse
import no.nav.medlemskap.domene.arbeidsforhold.Arbeidsforhold
import no.nav.medlemskap.domene.arbeidsforhold.Arbeidsforhold.Companion.hentAllePermisjonerSiden
import no.nav.medlemskap.domene.arbeidsforhold.PermisjonPermitteringType
import no.nav.medlemskap.regler.common.RegelId
import no.nav.medlemskap.regler.common.Resultat
import no.nav.medlemskap.regler.common.Resultat.Companion.ja
import no.nav.medlemskap.regler.common.Resultat.Companion.nei
import no.nav.medlemskap.regler.v1.arbeidsforhold.ArbeidsforholdRegel
import java.time.LocalDate

class ErPeriodeForPermissjonAvsluttetForMedEn30DagerSidenOgTypeForelderRegel(
    ytelse: Ytelse,
    private val startDatoForYtelse: LocalDate,
    private val arbeidsforhold: List<Arbeidsforhold>,
    private val kontrollperiode: Periode,
    private val fnr: String,
    regelId: RegelId = RegelId.REGEL_57,
) : ArbeidsforholdRegel(regelId, ytelse, startDatoForYtelse) {

    override fun operasjon(): Resultat {


        val allePermisjonerSomLøperForMindreEnEtÅrSiden = arbeidsforhold.hentAllePermisjonerSiden(startDatoForYtelse.minusYears(1))
        val permisjonerSomIkkeVarAvsluttet30DagerForForsteDagSykOgIkkeBarnePensjon =
            allePermisjonerSomLøperForMindreEnEtÅrSiden
                .filterNot {
                    (it.periode.tom==null || it.periode.tom.isAfter(startDatoForYtelse.minusDays(30))
                            && it.type == PermisjonPermitteringType.PERMISJON_MED_FORELDREPENGER)

                }



        if (permisjonerSomIkkeVarAvsluttet30DagerForForsteDagSykOgIkkeBarnePensjon.isEmpty()){
            return ja(regelId)
        }
        return nei(regelId)

    }


    companion object {

        fun fraDatagrunnlag(datagrunnlag: Datagrunnlag): ErPeriodeForPermissjonAvsluttetForMedEn30DagerSidenOgTypeForelderRegel {
            return ErPeriodeForPermissjonAvsluttetForMedEn30DagerSidenOgTypeForelderRegel(
                ytelse = datagrunnlag.ytelse,
                startDatoForYtelse = datagrunnlag.startDatoForYtelse,
                arbeidsforhold = datagrunnlag.arbeidsforhold,
                kontrollperiode = Periode(datagrunnlag.periode.fom, datagrunnlag.periode.tom),
                fnr = datagrunnlag.fnr,
            )
        }
    }
}


