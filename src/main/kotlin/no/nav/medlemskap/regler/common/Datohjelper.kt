package no.nav.medlemskap.regler.common

import java.time.LocalDate
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import kotlin.math.min

object Datohjelper {
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

fun erDatoerSammenhengende(sluttDato: LocalDate, startDato: LocalDate?): Boolean =
    sluttDato.isAfter(startDato?.minusDays(4))

fun LocalDate.startOfDayInstant() = this.atStartOfDay(ZoneId.systemDefault()).toInstant()
