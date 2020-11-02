package no.nav.medlemskap.regler.common

import no.nav.medlemskap.domene.InputPeriode
import no.nav.medlemskap.domene.Kontrollperiode
import no.nav.medlemskap.domene.Ytelse
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import kotlin.math.min

class Datohjelper(val periode: InputPeriode, val førsteDagForYtelse: LocalDate?, val ytelse: Ytelse) {

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

    fun førsteSykedag() = førsteDatoForYtelse()
    fun førsteDagpengedag() = førsteDatoForYtelse()
    fun førsteEnsligForsørgerdag() = førsteDatoForYtelse()

    fun førsteDatoForYtelse(): LocalDate {
        if (førsteDagForYtelse != null) {
            return førsteDagForYtelse
        } else {
            return periode.fom.minusDays(1)
        }
    }

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
        private val norskDatoFormatter = DateTimeFormatter.ofPattern("d.MM.yyyy")
        private val isoDatoFormatter = DateTimeFormatter.ISO_LOCAL_DATE

        fun parseDato(dato: String): LocalDate {
            if (dato.contains(".")) {
                return LocalDate.parse(dato, norskDatoFormatter)
            } else {
                return LocalDate.parse(dato, isoDatoFormatter)
            }
        }

        fun parseIsoDato(dato: String?): LocalDate? {
            if (dato == null) {
                return null
            }

            return LocalDate.parse(
                dato.substring(0, min(dato.length, 10)),
                isoDatoFormatter
            )
        }

        fun parseIsoDatoTid(datoTid: String?): LocalDateTime? {
            if (datoTid == null) {
                return null
            }

            return LocalDateTime.parse(datoTid, DateTimeFormatter.ISO_DATE_TIME)
        }
    }
}

fun erDatoerSammenhengende(sluttDato: LocalDate, startDato: LocalDate?): Boolean = sluttDato.isAfter(startDato?.minusDays(4))

fun LocalDate.startOfDayInstant() = this.atStartOfDay(ZoneId.systemDefault()).toInstant()
