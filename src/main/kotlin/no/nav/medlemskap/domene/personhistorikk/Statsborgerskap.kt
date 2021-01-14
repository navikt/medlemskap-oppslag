package no.nav.medlemskap.domene.personhistorikk

import no.nav.medlemskap.common.statsborgerskapUavklartForRegel
import no.nav.medlemskap.domene.Eøsland
import no.nav.medlemskap.domene.Kontrollperiode
import no.nav.medlemskap.domene.Periode
import no.nav.medlemskap.domene.Ytelse
import no.nav.medlemskap.regler.common.RegelId
import java.time.LocalDate

data class Statsborgerskap(
    val landkode: String,
    val fom: LocalDate?,
    val tom: LocalDate?
) {
    private val periode = Periode(fom, tom)

    fun overlapper(dato: LocalDate): Boolean {
        return periode.overlapper(dato)
    }

    companion object {

        infix fun List<Statsborgerskap>.gyldigeStatsborgerskap(
            kontrollPeriodeForPersonhistorikk: Kontrollperiode
        ): List<String> {
            val statsborgerskapFørst = this
                .filter { it.overlapper(kontrollPeriodeForPersonhistorikk.fom) }
                .map { it.landkode }
                .toSet()
            val statsborgerskapSist = this
                .filter { it.overlapper(kontrollPeriodeForPersonhistorikk.tom) }
                .map { it.landkode }
                .toSet()

            return statsborgerskapFørst.filter { statsborgerskapSist.contains(it) }
        }

        infix fun List<Statsborgerskap>.erSveitsiskBorger(
            kontrollPeriodeForPersonhistorikk: Kontrollperiode
        ): Boolean {
            return gyldigeStatsborgerskap(kontrollPeriodeForPersonhistorikk).any { Eøsland.erSveitsisk(it) }
        }

        infix fun List<Statsborgerskap>.erBritiskBorger(
            kontrollPeriodeForPersonhistorikk: Kontrollperiode
        ): Boolean {
            return gyldigeStatsborgerskap(kontrollPeriodeForPersonhistorikk).any { Eøsland.erBritisk(it) }
        }

        infix fun List<Statsborgerskap>.erNorskBorger(
            kontrollPeriodeForPersonhistorikk: Kontrollperiode
        ): Boolean {
            return gyldigeStatsborgerskap(kontrollPeriodeForPersonhistorikk).any { Eøsland.erNorsk(it) }
        }

        infix fun List<Statsborgerskap>.erNordiskBorger(
            kontrollPeriodeForPersonhistorikk: Kontrollperiode
        ): Boolean {
            return gyldigeStatsborgerskap(kontrollPeriodeForPersonhistorikk).any { Eøsland.erNordisk(it) }
        }

        infix fun List<Statsborgerskap>.erEøsBorger(
            kontrollPeriodeForPersonhistorikk: Kontrollperiode
        ): Boolean {
            return gyldigeStatsborgerskap(kontrollPeriodeForPersonhistorikk).any { Eøsland.erEØSland(it) }
        }

        infix fun List<Statsborgerskap>.hentStatsborgerskapFor(dato: LocalDate): List<String> =
            this.filter {
                it.overlapper(dato)
            }.map { it.landkode }

        infix fun List<Statsborgerskap>.harEndretSisteÅret(kontrollPeriode: Kontrollperiode): Boolean =
            this.any { erStatsborgerskapetInnenforPerioden(it, kontrollPeriode) }

        private fun erStatsborgerskapetInnenforPerioden(it: Statsborgerskap, kontrollPeriode: Kontrollperiode): Boolean =
            kontrollPeriode.periode.encloses(Periode(fom = it.fom, tom = it.fom)) || kontrollPeriode.periode.encloses(
                Periode(fom = it.tom, tom = it.tom)
            )

        fun List<Statsborgerskap>.erAnnenStatsborger(startDatoForYtelse: LocalDate): Boolean {
            val kontrollPeriodeForPersonhistorikk =
                Kontrollperiode.kontrollPeriodeForPersonhistorikk(startDatoForYtelse)

            return !erEøsBorger(kontrollPeriodeForPersonhistorikk)
        }

        fun List<Statsborgerskap>.registrerStatsborgerskapGrafana(kontrollPeriode: Kontrollperiode, ytelse: Ytelse, regelId: RegelId) =
            this.hentStatsborgerskapFor(kontrollPeriode.tom).forEach {
                statsborgerskapUavklartForRegel(
                    it,
                    ytelse,
                    regelId
                ).increment()
            }
    }
}