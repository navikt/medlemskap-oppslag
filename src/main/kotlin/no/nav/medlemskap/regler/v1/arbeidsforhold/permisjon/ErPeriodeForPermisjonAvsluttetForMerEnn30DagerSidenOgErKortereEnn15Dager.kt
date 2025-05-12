package no.nav.medlemskap.regler.v1.arbeidsforhold.permisjon

import no.nav.medlemskap.domene.Datagrunnlag
import no.nav.medlemskap.domene.Periode
import no.nav.medlemskap.domene.Ytelse
import no.nav.medlemskap.domene.arbeidsforhold.Arbeidsforhold
import no.nav.medlemskap.domene.arbeidsforhold.Arbeidsforhold.Companion.hentAllePermisjonerSiden
import no.nav.medlemskap.domene.arbeidsforhold.PermisjonPermittering.Companion.antallDagerPermisjon
import no.nav.medlemskap.regler.common.RegelId
import no.nav.medlemskap.regler.common.Resultat
import no.nav.medlemskap.regler.common.Resultat.Companion.ja
import no.nav.medlemskap.regler.common.Resultat.Companion.nei
import no.nav.medlemskap.regler.v1.arbeidsforhold.ArbeidsforholdRegel
import java.time.LocalDate

class ErPeriodeForPermisjonAvsluttetForMerEnn30DagerSidenOgErKortereEnn15Dager(
    ytelse: Ytelse,
    private val startDatoForYtelse: LocalDate,
    private val arbeidsforhold: List<Arbeidsforhold>,
    private val kontrollperiode: Periode,
    private val fnr: String,
    regelId: RegelId = RegelId.REGEL_58,
) : ArbeidsforholdRegel(regelId, ytelse, startDatoForYtelse) {

    override fun operasjon(): Resultat {

        val permisjonerEttÅrTilbake = arbeidsforhold.hentAllePermisjonerSiden(startDatoForYtelse.minusYears(1))

        val permisjonerSomErAvsluttetForMerEnn30DagerSiden = permisjonerEttÅrTilbake
            .filter { it.periode.tom != null }
            .filter { it.periode.tom!!.isBefore(startDatoForYtelse.minusDays(30)) }
        if(permisjonerSomErAvsluttetForMerEnn30DagerSiden.isEmpty()) return nei(regelId)


        return if(permisjonerSomErAvsluttetForMerEnn30DagerSiden.first().antallDagerPermisjon() < 15) ja(regelId)
        else nei(regelId)

    }

    companion object {

        fun fraDatagrunnlag(datagrunnlag: Datagrunnlag): ErPeriodeForPermisjonAvsluttetForMerEnn30DagerSidenOgErKortereEnn15Dager {
            return ErPeriodeForPermisjonAvsluttetForMerEnn30DagerSidenOgErKortereEnn15Dager(
                ytelse = datagrunnlag.ytelse,
                startDatoForYtelse = datagrunnlag.startDatoForYtelse,
                arbeidsforhold = datagrunnlag.arbeidsforhold,
                kontrollperiode = Periode(datagrunnlag.periode.fom, datagrunnlag.periode.tom),
                fnr = datagrunnlag.fnr,
            )
        }
    }
}


