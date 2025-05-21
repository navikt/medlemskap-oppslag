package no.nav.medlemskap.domene.arbeidsforhold

import no.nav.medlemskap.domene.Kontrollperiode
import no.nav.medlemskap.domene.Periode
import no.nav.medlemskap.domene.Periode.Companion.slutterEtterKontrollPerioden
import no.nav.medlemskap.domene.Periode.Companion.starterFørKontrollPerioden
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

    companion object {
        fun List<Arbeidsavtale>.sortertArbeidsavtaleEtterPeriode(): List<Arbeidsavtale> {
            return this.sortedBy { it.gyldighetsperiode.fom }
        }

        fun List<Arbeidsavtale>.arbeidsavtalerForKontrollperiode(kontrollPeriode: Kontrollperiode): List<Arbeidsavtale> {
            return this.filter { it.gyldighetsperiode.overlapper(kontrollPeriode) }
        }

        fun List<Arbeidsavtale>.sammenhengendeArbeidsavtaler(
            kontrollPeriode: Kontrollperiode,
            tillatDagersHullIPeriode: Long
        ): List<Arbeidsavtale> {
            return if (this.erSammenhengendeArbeidsavtaler(kontrollPeriode, tillatDagersHullIPeriode)) this
            else emptyList()
        }

        fun List<Arbeidsavtale>.harFlereArbeidsavtaler(): Boolean {
            return this.size > 1
        }

        fun List<Arbeidsavtale>.harIngenArbeidsavtaler(): Boolean {
            return this.isEmpty()
        }

        fun Arbeidsavtale.erArbeidsavtalenLøpendeIHelePerioden(periode: Kontrollperiode): Boolean {
            return this.starterFørKontrollPerioden(periode) && this.slutterEtterKontrollPerioden(periode)
        }

        fun Arbeidsavtale.starterFørKontrollPerioden(periode: Kontrollperiode): Boolean {
            return this.gyldighetsperiode.fom != null && this.gyldighetsperiode.fom.isBefore(periode.fom)
        }

        fun Arbeidsavtale.slutterEtterKontrollPerioden(periode: Kontrollperiode): Boolean {
            return this.gyldighetsperiode.tom == null || this.gyldighetsperiode.tom.isAfter(periode.tom)
        }


        fun List<Arbeidsavtale>.erSammenhengendeArbeidsavtaler(
            kontrollPeriode: Kontrollperiode,
            tillatDagersHullIPeriode: Long
        ): Boolean {
            var totaltAntallDagerDiff: Long = 0
            var forrigeTilDato: LocalDate? = null

            val sortertArbeidsavtaleEtterPeriode =
                this.arbeidsavtalerForKontrollperiode(kontrollPeriode).sortertArbeidsavtaleEtterPeriode()

            for (arbeidsavtale in sortertArbeidsavtaleEtterPeriode) {
                if (forrigeTilDato != null && !erDatoerSammenhengende(
                        forrigeTilDato,
                        arbeidsavtale.gyldighetsperiode.fom,
                        tillatDagersHullIPeriode
                    )
                ) {
                    val antallDagerDiff =
                        abs(ChronoUnit.DAYS.between(forrigeTilDato, arbeidsavtale.gyldighetsperiode.fom))
                    totaltAntallDagerDiff += antallDagerDiff

                    if (totaltAntallDagerDiff > tillatDagersHullIPeriode)
                        return false
                }
                if (arbeidsavtale.gyldighetsperiode.tom == null || forrigeTilDato == null || arbeidsavtale.gyldighetsperiode.tom.isAfter(
                        forrigeTilDato
                    )
                )
                    forrigeTilDato = arbeidsavtale.gyldighetsperiode.tom

                if (forrigeTilDato == null || forrigeTilDato.isAfter(kontrollPeriode.tom)) return true
            }

            if (forrigeTilDato != null) {
                return !forrigeTilDato.isBefore(kontrollPeriode.tom)
            }
            return true
        }


        fun Arbeidsavtale.slutt(): LocalDate = this.gyldighetsperiode.tom ?: LocalDate.MAX
        fun Arbeidsavtale.start(): LocalDate? = this.gyldighetsperiode.fom

        fun Arbeidsavtale.erSekvensiellEtter(forrige: Arbeidsavtale): Boolean {
            return this.start() == forrige.slutt()
        }

        fun Arbeidsavtale.overlapperMed(annen: Arbeidsavtale): Boolean {
            return !(this.slutt().isBefore(annen.start()) || this.start()!!.isAfter(annen.slutt()))
        }

        fun List<Arbeidsavtale>.grupperAvtaler(): List<List<Arbeidsavtale>> {
            val ubehandlet = this.toMutableSet()
            val grupper = mutableListOf<List<Arbeidsavtale>>()

            while (ubehandlet.isNotEmpty()) {
                val start = ubehandlet.minByOrNull { it.start()!! }!!
                val kjede = mutableListOf(start)
                ubehandlet.remove(start)

                var nåværende = start
                var neste = ubehandlet.find { it.erSekvensiellEtter(nåværende) }

                while (neste != null) {
                    kjede.add(neste)
                    ubehandlet.remove(neste)
                    nåværende = neste
                    neste = ubehandlet.find { it.erSekvensiellEtter(nåværende) }
                }

                if (kjede.size > 1) {
                    grupper.add(kjede)
                } else {
                    // sjekk om den overlapper med noen
                    val overlappende = ubehandlet.filter { it.overlapperMed(start) }
                    if (overlappende.isEmpty()) {
                        grupper.add(kjede)
                    } else {
                        grupper.add(listOf(start))
                    }
                }
            }

            // resterende overlappende eller isolerte
            ubehandlet.forEach { grupper.add(listOf(it)) }

            return grupper
        }


        fun List<Arbeidsavtale>.harVartHeleKontrollperioden(kontrollPeriode: Kontrollperiode): Boolean {
            val førsteArbeidsavtale = this.firstOrNull()
            val sisteArbeidsavtale = this.lastOrNull()

            if (førsteArbeidsavtale == null || sisteArbeidsavtale == null) {
                return false
            }

            return førsteArbeidsavtale.periode.starterFørKontrollPerioden(kontrollPeriode) &&
                    sisteArbeidsavtale.periode.slutterEtterKontrollPerioden(kontrollPeriode)
        }

    }
}
