package no.nav.medlemskap.domene.arbeidsforhold

import no.nav.medlemskap.domene.Periode
import java.time.LocalDate
import java.time.temporal.ChronoUnit

data class PermisjonPermittering(
    val periode: Periode,
    val permisjonPermitteringId: String,
    val prosent: Double?,
    val type: PermisjonPermitteringType,
    val varslingskode: String?
) {
    companion object {

        fun PermisjonPermittering.antallDagerPermisjon() : Double {
            return this.periode.fom!!.until(this.periode.tom, ChronoUnit.DAYS).toDouble() + 1
        }

        fun List<PermisjonPermittering>.permisjonerIMellom(avklaringsDato: LocalDate): List<PermisjonPermittering> {
            return this.filter { it.periode.tom != null }
                .filter { it.periode.tom!!.isBefore(avklaringsDato) }
        }
    }

}
