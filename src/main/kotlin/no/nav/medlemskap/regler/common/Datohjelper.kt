package no.nav.medlemskap.regler.common

import no.nav.medlemskap.domene.InputPeriode
import no.nav.medlemskap.domene.Kontrollperiode
import no.nav.medlemskap.domene.Ytelse
import java.time.LocalDate
import java.time.ZoneId
import java.time.format.DateTimeFormatter

class Datohjelper(val periode: InputPeriode, val ytelse: Ytelse) {

    fun kontrollPeriodeForPersonhistorikk(): Kontrollperiode {
        return when (ytelse) {
            Ytelse.SYKEPENGER -> Kontrollperiode(
                fom = førsteSykedag().minusMonths(12),
                tom = førsteSykedag()
            )
            Ytelse.DAGPENGER -> defaultDagpengePeriode()
            Ytelse.ENSLIG_FORSORGER -> defaultEnsligForsørgerPeriode()
            Ytelse.LOVME -> defaultPeriode()
            Ytelse.LOVME_FUNKSJONELLE_TESTER -> defaultPeriode()
        }
    }

    fun kontrollPeriodeForMedl(): Kontrollperiode {
        return when (ytelse) {
            Ytelse.SYKEPENGER -> Kontrollperiode(
                fom = førsteSykedag().minusMonths(12),
                tom = førsteSykedag()
            )
            Ytelse.DAGPENGER -> defaultDagpengePeriode()
            Ytelse.ENSLIG_FORSORGER -> defaultEnsligForsørgerPeriode()
            Ytelse.LOVME -> defaultPeriode()
            Ytelse.LOVME_FUNKSJONELLE_TESTER -> defaultPeriode()
        }
    }

    fun førsteSykedag() = periode.fom.minusDays(1)
    fun førsteDagpengedag() = periode.fom.minusDays(1)
    fun førsteEnsligForsørgerdag() = periode.fom.minusDays(1)

    private fun defaultDagpengePeriode() = Kontrollperiode(førsteDagpengedag().minusMonths(12), førsteDagpengedag())
    private fun defaultEnsligForsørgerPeriode() = Kontrollperiode(førsteEnsligForsørgerdag().minusMonths(12), førsteEnsligForsørgerdag())
    private fun defaultPeriode() = Kontrollperiode(periode.fom.minusDays(1).minusMonths(12), periode.fom.minusDays(1))

    fun tilOgMedDag(): LocalDate {
        return when (ytelse) {
            Ytelse.SYKEPENGER -> førsteSykedag()
            Ytelse.DAGPENGER -> førsteDagpengedag()
            Ytelse.ENSLIG_FORSORGER -> førsteEnsligForsørgerdag()
            Ytelse.LOVME -> periode.fom.minusDays(1)
            Ytelse.LOVME_FUNKSJONELLE_TESTER -> periode.fom.minusDays(1)
        }
    }

    fun kontrollPeriodeForArbeidsforhold(): Kontrollperiode {
        return when (ytelse) {
            Ytelse.SYKEPENGER -> Kontrollperiode(
                fom = førsteSykedag().minusMonths(12),
                tom = førsteSykedag()
            )
            Ytelse.DAGPENGER -> defaultDagpengePeriode()
            Ytelse.ENSLIG_FORSORGER -> defaultEnsligForsørgerPeriode()
            Ytelse.LOVME -> defaultPeriode()
            Ytelse.LOVME_FUNKSJONELLE_TESTER -> defaultPeriode()
        }
    }

    companion object {
        private val datoFormatter = DateTimeFormatter.ofPattern("d.MM.yyyy")

        fun parseDato(dato: String): LocalDate {
            return LocalDate.parse(dato, datoFormatter)
        }
    }
}

fun erDatoerSammenhengende(sluttDato: LocalDate, startDato: LocalDate?): Boolean = sluttDato.isAfter(startDato?.minusDays(3))

fun LocalDate.startOfDayInstant() = this.atStartOfDay(ZoneId.systemDefault()).toInstant()
