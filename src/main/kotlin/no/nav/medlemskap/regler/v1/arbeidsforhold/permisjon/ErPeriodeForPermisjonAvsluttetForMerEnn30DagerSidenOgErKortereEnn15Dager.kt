package no.nav.medlemskap.regler.v1.arbeidsforhold.permisjon

import no.nav.medlemskap.domene.Datagrunnlag
import no.nav.medlemskap.domene.Periode
import no.nav.medlemskap.domene.Ytelse
import no.nav.medlemskap.domene.arbeidsforhold.Arbeidsforhold
import no.nav.medlemskap.domene.arbeidsforhold.Arbeidsforhold.Companion.hentAllePermisjonerSiden
import no.nav.medlemskap.regler.common.RegelId
import no.nav.medlemskap.regler.common.Resultat
import no.nav.medlemskap.regler.common.Resultat.Companion.ja
import no.nav.medlemskap.regler.common.Resultat.Companion.nei
import no.nav.medlemskap.regler.v1.arbeidsforhold.ArbeidsforholdRegel
import java.time.LocalDate
import java.time.temporal.ChronoUnit

class ErPeriodeForPermisjonAvsluttetForMerEnn30DagerSidenOgErKortereEnn15Dager(
    ytelse: Ytelse,
    private val startDatoForYtelse: LocalDate,
    private val arbeidsforhold: List<Arbeidsforhold>,
    private val kontrollperiode: Periode,
    private val fnr: String,
    regelId: RegelId = RegelId.REGEL_58,
) : ArbeidsforholdRegel(regelId, ytelse, startDatoForYtelse) {

    override fun operasjon(): Resultat {

        val allePermisjonerSomLøperForMindreEnnEttÅrSiden = arbeidsforhold.hentAllePermisjonerSiden(startDatoForYtelse.minusYears(1))
        val permisjonerSomErAvsluttetForMerEnn30DagerSiden = allePermisjonerSomLøperForMindreEnnEttÅrSiden
            .filter {
                it.periode.tom!!.isBefore(startDatoForYtelse.minusDays(30))
            }

        if (permisjonerSomErAvsluttetForMerEnn30DagerSiden.isEmpty()) {
            return nei(regelId)
        }

        val permisjonSomErKortereEnn15Dager = erPeriodenMindreEnn15Dager(permisjonerSomErAvsluttetForMerEnn30DagerSiden.first().periode)

        if (permisjonSomErKortereEnn15Dager) {
            return ja(regelId)
        }

        return nei(regelId)

    }

    fun erPeriodenMindreEnn15Dager(periode: Periode) : Boolean {
        val antallDager = periode.fom!!.until(periode.tom, ChronoUnit.DAYS).toDouble() + 1
        return antallDager < 15
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


