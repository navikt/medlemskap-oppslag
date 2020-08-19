package no.nav.medlemskap.regler.funksjoner

import no.nav.medlemskap.common.statsborgerskapUavklartForRegel
import no.nav.medlemskap.domene.Kontrollperiode
import no.nav.medlemskap.domene.Periode
import no.nav.medlemskap.domene.Statsborgerskap
import no.nav.medlemskap.domene.Ytelse
import no.nav.medlemskap.regler.common.Funksjoner.finnesI
import no.nav.medlemskap.regler.common.RegelId
import no.nav.medlemskap.regler.common.interval
import no.nav.medlemskap.regler.common.lagInstantStartOfDay
import java.time.LocalDate

object StatsborgerskapFunksjoner {

    infix fun List<Statsborgerskap>.hentStatsborgerskapFor(dato: LocalDate): List<String> =
            this.filter {
                Periode(it.fom, it.tom).interval().contains(lagInstantStartOfDay(dato))
            }.map { it.landkode }

    infix fun List<Statsborgerskap>.hentStatsborgerskapVedStartAvKontrollperiode(kontrollPeriode: Kontrollperiode): List<String> =
            this.hentStatsborgerskapFor(kontrollPeriode.fom)

    infix fun List<Statsborgerskap>.hentStatsborgerskapVedSluttAvKontrollperiode(kontrollPeriode: Kontrollperiode): List<String> =
            this.hentStatsborgerskapFor(kontrollPeriode.tom)

    fun List<Statsborgerskap>.registrerStatsborgerskapGrafana(kontrollPeriode: Kontrollperiode, ytelse: Ytelse, regelId: RegelId) =
            this.hentStatsborgerskapFor(kontrollPeriode.tom).forEach { statsborgerskapUavklartForRegel(it, ytelse, regelId).increment() }

    fun sjekkStatsborgerskap(statsborgerskap: List<Statsborgerskap>, kontrollPeriodeForStatsborgerskap: Kontrollperiode, landkoder: Map<String, String>): Boolean {
        val førsteStatsborgerskap = statsborgerskap.hentStatsborgerskapVedStartAvKontrollperiode(kontrollPeriodeForStatsborgerskap)
        val sisteStatsborgerskap = statsborgerskap.hentStatsborgerskapVedSluttAvKontrollperiode(kontrollPeriodeForStatsborgerskap)

        return landkoder finnesI førsteStatsborgerskap && landkoder finnesI sisteStatsborgerskap
    }
}

