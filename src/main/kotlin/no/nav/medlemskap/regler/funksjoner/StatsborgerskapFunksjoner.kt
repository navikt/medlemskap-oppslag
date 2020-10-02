package no.nav.medlemskap.regler.funksjoner

import no.nav.medlemskap.common.statsborgerskapUavklartForRegel
import no.nav.medlemskap.domene.Kontrollperiode
import no.nav.medlemskap.domene.Periode
import no.nav.medlemskap.domene.Statsborgerskap
import no.nav.medlemskap.domene.Ytelse
import no.nav.medlemskap.regler.common.RegelId
import java.time.LocalDate

object StatsborgerskapFunksjoner {

    infix fun List<Statsborgerskap>.hentStatsborgerskapFor(dato: LocalDate): List<String> =
        this.filter {
            it.overlapper(dato)
        }.map { it.landkode }

    infix fun List<Statsborgerskap>.harEndretSisteÅret(kontrollPeriode: Kontrollperiode): Boolean =
        this.any { erStatsborgerskapetInnenforPerioden(it, kontrollPeriode) }

    private fun erStatsborgerskapetInnenforPerioden(it: Statsborgerskap, kontrollPeriode: Kontrollperiode): Boolean =
        kontrollPeriode.periode.encloses(Periode(fom = it.fom, tom = it.fom)) || kontrollPeriode.periode.encloses(Periode(fom = it.tom, tom = it.tom))

    infix fun List<Statsborgerskap>.hentStatsborgerskapVedStartAvKontrollperiode(kontrollPeriode: Kontrollperiode): List<String> =
        this.hentStatsborgerskapFor(kontrollPeriode.fom)

    infix fun List<Statsborgerskap>.hentStatsborgerskapVedSluttAvKontrollperiode(kontrollPeriode: Kontrollperiode): List<String> =
        this.hentStatsborgerskapFor(kontrollPeriode.tom)

    fun List<Statsborgerskap>.registrerStatsborgerskapGrafana(kontrollPeriode: Kontrollperiode, ytelse: Ytelse, regelId: RegelId) =
        this.hentStatsborgerskapFor(kontrollPeriode.tom).forEach { statsborgerskapUavklartForRegel(it, ytelse, regelId).increment() }

    fun sjekkStatsborgerskap(statsborgerskap: List<Statsborgerskap>, kontrollPeriodeForStatsborgerskap: Kontrollperiode, funk: (String) -> Boolean): Boolean {
        val førsteStatsborgerskap = statsborgerskap.hentStatsborgerskapVedStartAvKontrollperiode(kontrollPeriodeForStatsborgerskap)
        val sisteStatsborgerskap = statsborgerskap.hentStatsborgerskapVedSluttAvKontrollperiode(kontrollPeriodeForStatsborgerskap)

        return førsteStatsborgerskap.any { funk(it) } && sisteStatsborgerskap.any { funk(it) }
    }
}
