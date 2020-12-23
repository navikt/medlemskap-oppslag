package no.nav.medlemskap.domene

import java.time.LocalDateTime

data class Oppholdstillatelse(
    val uttrekkstidspunkt: LocalDateTime?,
    val foresporselsfodselsnummer: String?,
    val avgjoerelse: Boolean?,
    val gjeldendeOppholdsstatus: Boolean?,
    val arbeidsadgang: Arbeidsadgang,
    val uavklartFlyktningstatus: Boolean?,
    val harFlyktningstatus: Boolean?
)

data class Arbeidsadgang(
    val harArbeidsadgang: Boolean,
    val arbeidsadgangType: ArbeidsAdgangType,
    val arbeidsomfang: Arbeidsomfang,
    val periode: Periode
)

data class GjeldendeOppholdsstatus(
    val uavklart: Boolean
)

enum class Arbeidsomfang(val type: String) {
    INGEN_KRAV_TIL_STILLINGSPROSENT("IngenKravTilStillingsprosent"),
    KUN_ARBEID_HELTID("KunArbeidHeltid"),
    KUN_ARBEID_DELTID("KunArbeidDeltid"),
    DELTID_SAMT_FERIER_HELTID("DeltidSamtFerierHeltid"),
    UAVKLART("Uavklart");

    companion object {
        fun fraJaNeiUavklartVerdi(jaNeiUavklartVerdi: String?): JaNeiUavklart? {
            if (jaNeiUavklartVerdi.isNullOrEmpty()) return null
            return JaNeiUavklart.valueOf(jaNeiUavklartVerdi.toUpperCase())
        }
    }
}

enum class ArbeidsAdgangType(val type: String) {
    BESTEMT_ARBEIDSGIVER_ELLER_OPPDRAGSGIVER("BestemtArbeidsgiverEllerOppdragsgiver"),
    BESTEMT_ARBEID_ELLER_OPPDRAG("BestemtArbeidEllerOppdrag"),
    BESTEMT_ARBEIDSGIVER_OG_ARBEID_ELLER_BESTEMT_OPPDRAGSGIVER_OG_OPPDRAG("BestemtArbeidsgiverOgArbeidEllerBestemtOppdragsgiverOgOppdrag"),
    GENERELL("Generell"),
    UAVKLART("Uavklart");

    companion object {
        fun fraJaNeiUavklartVerdi(jaNeiUavklartVerdi: String?): JaNeiUavklart? {
            if (jaNeiUavklartVerdi.isNullOrEmpty()) return null
            return JaNeiUavklart.valueOf(jaNeiUavklartVerdi.toUpperCase())
        }
    }
}

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
