package no.nav.medlemskap.domene

import java.time.LocalDateTime

data class Oppholdstillatelse(
    val uttrekkstidspunkt: LocalDateTime,
    val foresporselsfodselsnummer: String,
    val avgjoerelse: Boolean,
    val gjeldendeOppholdsstatus: Boolean,
    val arbeidsadgang: JaNeiUavklart,
    val uavklartFlyktningstatus: Boolean,
    val harFlyktningstatus: Boolean
)

data class GjeldendeOppholdsstatus(
    val uavklart: Boolean
)

enum class JaNeiUavklart(val jaNeiUavklart: String) {
    JA("Ja"),
    NEI("Nei"),
    UAVKLART("Uavklart");

    companion object {
        fun fraJaNeiUavklartVerdi(jaNeiUavklartVerdi: String?): JaNeiUavklart? {
            if (jaNeiUavklartVerdi.isNullOrEmpty()) return null
            return valueOf(jaNeiUavklartVerdi.toUpperCase())
        }
    }
}
