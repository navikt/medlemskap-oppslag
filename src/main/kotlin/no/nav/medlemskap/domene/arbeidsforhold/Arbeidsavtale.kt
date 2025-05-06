package no.nav.medlemskap.domene.arbeidsforhold

import no.nav.medlemskap.domene.Kontrollperiode
import no.nav.medlemskap.domene.Periode
import no.nav.medlemskap.domene.arbeidsforhold.Arbeidsforhold.Companion.sortertArbeidsavtaleEtterPeriode
import no.nav.medlemskap.regler.common.erDatoerSammenhengende
import java.time.LocalDate
import java.time.temporal.ChronoUnit
import kotlin.math.abs

data class Arbeidsavtale(
    var periode: Periode,
    val gyldighetsperiode: Periode,
    val yrkeskode: String,
    val skipsregister: Skipsregister?,
    val fartsomraade: Fartsomraade?,
    val stillingsprosent: Double?,
    val beregnetAntallTimerPrUke: Double?,
    val skipstype: Skipstype?
) {
    fun getStillingsprosent(): Double {
        if (stillingsprosent == 0.0 && beregnetAntallTimerPrUke != null && beregnetAntallTimerPrUke > 0) {
            val beregnetStillingsprosent = (beregnetAntallTimerPrUke / 37.5) * 100
            return Math.round(beregnetStillingsprosent * 10.0) / 10.0
        }

        return stillingsprosent ?: 100.0
    }

    fun Arbeidsavtale.starterFørKontrollPerioden(periode: Kontrollperiode): Boolean {
        return this.gyldighetsperiode.fom != null && this.gyldighetsperiode.fom.isBefore(periode.fom)
    }
    fun Arbeidsavtale.slutterEtterKontrollPerioden(periode: Kontrollperiode): Boolean {
        return this.gyldighetsperiode.tom == null || this.gyldighetsperiode.tom.isAfter(periode.tom)
    }

    fun Arbeidsavtale.erArbeidsavtalenLøpendeIHelePerioden(periode: Kontrollperiode): Boolean {

        return this.starterFørKontrollPerioden(periode) && this.slutterEtterKontrollPerioden(periode)
    }

    private fun List<Arbeidsavtale>.arbeidsavtalerForKontrollperiode(kontrollPeriode: Kontrollperiode): List<Arbeidsavtale> {
        return this.filter { erArbeidsavtalenLøpendeIHelePerioden(kontrollPeriode) }
    }

    fun List<Arbeidsavtale>.sammenhengendeArbeidsavtaler(kontrollPeriode: Kontrollperiode, tillatDagersHullIPeriode: Long): List<Arbeidsavtale>? {
        return if (this.erSammenhengendeArbeidsavtaler(kontrollPeriode, tillatDagersHullIPeriode)) this
        else null
    }

    fun List<Arbeidsavtale>.erSammenhengendeArbeidsavtaler(kontrollPeriode: Kontrollperiode, tillatDagersHullIPeriode: Long): Boolean {
        var totaltAntallDagerDiff: Long = 0
        var forrigeTilDato: LocalDate? = null

        val sortertArbeidsavtaleEtterPeriode = this.arbeidsavtalerForKontrollperiode(kontrollPeriode).sortertArbeidsavtaleEtterPeriode()

        for (arbeidsavtale in sortertArbeidsavtaleEtterPeriode) {
            if (forrigeTilDato != null && !erDatoerSammenhengende(forrigeTilDato, arbeidsavtale.gyldighetsperiode.fom, tillatDagersHullIPeriode)
            ) {
                val antallDagerDiff = abs(ChronoUnit.DAYS.between(forrigeTilDato, arbeidsavtale.gyldighetsperiode.fom))
                totaltAntallDagerDiff += antallDagerDiff

                if (totaltAntallDagerDiff > tillatDagersHullIPeriode)
                    return false
            }
            if (arbeidsavtale.gyldighetsperiode.tom == null || forrigeTilDato == null || arbeidsavtale.gyldighetsperiode.tom.isAfter(forrigeTilDato))
                forrigeTilDato = arbeidsavtale.gyldighetsperiode.tom

            if (forrigeTilDato == null || forrigeTilDato.isAfter(kontrollPeriode.tom)) return true
        }

        if (forrigeTilDato != null) {
            return !forrigeTilDato.isBefore(kontrollPeriode.tom)
        }
        return true
    }
}
