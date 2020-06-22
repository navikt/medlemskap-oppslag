package no.nav.medlemskap.regler.funksjoner

import no.nav.medlemskap.domene.Periode
import no.nav.medlemskap.domene.Statsborgerskap
import no.nav.medlemskap.regler.common.interval
import no.nav.medlemskap.regler.common.lagInstantStartOfDay
import java.time.LocalDate

object StatsborgerskapFunksjoner {

    infix fun List<Statsborgerskap>.hentStatsborgerskapFor(dato: LocalDate): List<String> =
            this.filter {
                Periode(it.fom, it.tom).interval().contains(lagInstantStartOfDay(dato))
            }.map { it.landkode }

    infix fun List<Statsborgerskap>.hentStatsborgerskapVedStartAvKontrollperiode(kontrollPeriode: Periode): List<String> =
            this.hentStatsborgerskapFor(kontrollPeriode.fom!!)

    infix fun List<Statsborgerskap>.hentStatsborgerskapVedSluttAvKontrollperiode(kontrollPeriode: Periode): List<String> =
            this.hentStatsborgerskapFor(kontrollPeriode.tom!!)



}