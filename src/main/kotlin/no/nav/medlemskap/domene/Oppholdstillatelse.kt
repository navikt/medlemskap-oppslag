package no.nav.medlemskap.domene

import java.time.LocalDateTime

data class Oppholdstillatelse(
    val uttrekkstidspunkt: LocalDateTime?,
    val foresporselsfodselsnummer: String?,
    val avgjoerelse: Boolean?,
    val gjeldendeOppholdsstatus: OppholdstillatelsePaSammeVilkar?,
    val arbeidsadgang: Arbeidsadgang?,
    val uavklartFlyktningstatus: Boolean?,
    val harFlyktningstatus: Boolean?
) {

    fun harGyldigArbeidstillatelseForPeriode(periode: Periode): Boolean {
        return arbeidsadgang != null && arbeidsadgang.harArbeidsadgang &&
            arbeidsadgang.arbeidsomfang != null && arbeidsadgang.arbeidsomfang == ArbeidomfangKategori.KUN_ARBEID_HELTID &&
            arbeidsadgang.arbeidsadgangType != null && arbeidsadgang.arbeidsadgangType == ArbeidsadgangType.GENERELL &&
            arbeidsadgang.periode.encloses(periode)
    }

    fun harGyldigOppholdstillatelseForPeriode(periode: Periode): Boolean {
        return gjeldendeOppholdsstatus != null && gjeldendeOppholdsstatus.harTillatelse &&
            gjeldendeOppholdsstatus.periode.encloses(periode)
    }
}

data class Arbeidsadgang(
    val periode: Periode,
    val harArbeidsadgang: Boolean,
    val arbeidsadgangType: ArbeidsadgangType?,
    val arbeidsomfang: ArbeidomfangKategori?
)

data class OppholdstillatelsePaSammeVilkar(
    val periode: Periode,
    val harTillatelse: Boolean
)

data class GjeldendeOppholdsstatus(
    val uavklart: Boolean
)

enum class ArbeidomfangKategori(val kodeverdi: String) {
    INGEN_KRAV_TIL_STILLINGSPROSENT("IngenKravTilStillingsprosent"),
    KUN_ARBEID_HELTID("KunArbeidHeltid"),
    KUN_ARBEID_DELTID("KunArbeidDeltid"),
    DELTID_SAMT_FERIER_HELTID("DeltidSamtFerierHeltid"),
    UAVKLART("Uavklart");

    companion object {
        fun fraArbeidomfang(arbeidsomfangVerdi: String?): ArbeidomfangKategori? {
            return values().firstOrNull { it.kodeverdi == arbeidsomfangVerdi }
        }
    }
}

enum class ArbeidsadgangType(val kodeverdi: String) {
    BESTEMT_ARBEIDSGIVER_ELLER_OPPDRAGSGIVER("BestemtArbeidsgiverEllerOppdragsgiver"),
    BESTEMT_ARBEID_ELLER_OPPDRAG("BestemtArbeidEllerOppdrag"),
    BESTEMT_ARBEIDSGIVER_OG_ARBEID_ELLER_BESTEMT_OPPDRAGSGIVER_OG_OPPDRAG("BestemtArbeidsgiverOgArbeidEllerBestemtOppdragsgiverOgOppdrag"),
    GENERELL("Generell"),
    UAVKLART("Uavklart");

    companion object {
        fun fraArbeidsadgangType(kodeverdi: String?): ArbeidsadgangType? {
            return values().firstOrNull { it.kodeverdi == kodeverdi }
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
