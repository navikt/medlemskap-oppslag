package no.nav.medlemskap.domene

import java.time.LocalDate
import java.time.LocalDateTime

data class Oppholdstillatelse(
    val uttrekkstidspunkt: LocalDateTime?,
    val foresporselsfodselsnummer: String?,
    val avgjoerelse: Boolean?,
    val gjeldendeOppholdsstatus: GjeldendeOppholdsstatus?,
    val arbeidsadgang: Arbeidsadgang?,
    val uavklartFlyktningstatus: Boolean?,
    val harFlyktningstatus: Boolean?
) {
    fun harGyldigOppholdOgArbeidstillatelse(): Boolean {
        if (gjeldendeOppholdsstatus != null) {
            return gjeldendeOppholdsstatus!!.oppholdstillatelsePaSammeVilkar != null && gjeldendeOppholdsstatus!!.oppholdstillatelsePaSammeVilkar?.harTillatelse == true &&
                arbeidsadgang != null && arbeidsadgang.harArbeidsadgang == true &&
                arbeidsadgang.arbeidsomfang != null && arbeidsadgang.arbeidsomfang == ArbeidomfangKategori.KUN_ARBEID_HELTID &&
                arbeidsadgang.arbeidsadgangType != null && arbeidsadgang.arbeidsadgangType == ArbeidsadgangType.GENERELL
        }
        return false
    }

    fun harGyldigOppholdOgArbeidstillatelseForPeriode(
        inputPeriode: InputPeriode,
        førsteDagForYtelse: LocalDate?
    ): Boolean {
        if (gjeldendeOppholdsstatus == null) {
            return false
        }
        return harGyldigOppholdOgArbeidstillatelse() &&
            gjeldendeOppholdsstatus.oppholdstillatelsePaSammeVilkar?.periode?.encloses(
            Kontrollperiode.kontrollPeriodeForOppholdstillatelse(
                inputPeriode,
                førsteDagForYtelse
            ).periode
        ) ?: false &&
            arbeidsadgang!!.periode?.encloses(
            Kontrollperiode.kontrollPeriodeForOppholdstillatelse(
                inputPeriode,
                førsteDagForYtelse
            ).periode
        ) ?: false
    }
}

data class Arbeidsadgang(
    val periode: Periode?,
    val harArbeidsadgang: Boolean?,
    val arbeidsadgangType: ArbeidsadgangType?,
    val arbeidsomfang: ArbeidomfangKategori?
)

data class OppholdstillatelsePaSammeVilkar(
    val periode: Periode?,
    val type: OppholdstillaelsePaSammeVilkarType?,
    val harTillatelse: Boolean?
)

data class GjeldendeOppholdsstatus(
    val oppholdstillatelsePaSammeVilkar: OppholdstillatelsePaSammeVilkar?,
    val eosellerEFTAOpphold: EOSellerEFTAOpphold?,
    val uavklart: Uavklart?,
    val ikkeOppholdstillatelseIkkeOppholdsPaSammeVilkarIkkeVisum: IkkeOppholdstillatelseIkkeOppholdsPaSammeVilkarIkkeVisum?
)

data class IkkeOppholdstillatelseIkkeOppholdsPaSammeVilkarIkkeVisum(
    val utvistMedInnreiseForbud: no.nav.medlemskap.domene.UtvistMedInnreiseForbud?,
    var avslagEllerBortfallAvPOBOSellerTilbakekallEllerFormeltVedtak: no.nav.medlemskap.domene.AvslagEllerBortfallAvPOBOSellerTilbakekallEllerFormeltVedtak?,
    var ovrigIkkeOpphold: no.nav.medlemskap.domene.OvrigIkkeOpphold?
)

data class UtvistMedInnreiseForbud(
    val innreiseForbud: JaNeiUavklart?
)

data class AvslagEllerBortfallAvPOBOSellerTilbakekallEllerFormeltVedtak(
    val avgjoerselsesDato: LocalDate?
)

data class OvrigIkkeOpphold(
    val ovrigIkkeOppholdsKategori: OvrigIkkeOppholdsKategori?
)

data class Uavklart(
    val uavklart: String?
)
data class EOSellerEFTAOpphold(
    val periode: Periode?,
    val EOSellerEFTAOppholdType: EOSellerEFTAOppholdType
)

enum class OvrigIkkeOppholdsKategori(val kodeverdi: String) {
    OPPHEVET_INNREISEFORBUD("OpphevetInnreiseforbud"),
    ANNULERING_AV_VISUM("AnnuleringAvVisum"),
    UTLOPT_OPPHOLDSTILLATELSE("UtloptOppholdstillatelse"),
    UTLOPT_EO_SELLER_EFTA_OPPHOLDSRETT_ELLER_EO_SELLER_EFTA_OPPHOLDSTILLATELSE("UtloptEOSellerEFTAOppholdsrettEllerEOSellerEFTAOppholdstillatelse");

    companion object {
        fun fraOvrigIkkeOppholdsKategoriType(
            ovrigIkkeOppholdsKategori: String?
        ): OvrigIkkeOppholdsKategori? {
            return OvrigIkkeOppholdsKategori.values()
                .firstOrNull { it.kodeverdi == ovrigIkkeOppholdsKategori }
        }
    }
}

enum class EOSellerEFTAOppholdType(val kodeverdi: String) {
    EOS_ELLER_EFTA_BESLUTNING_OM_OPPHOLDSRETT("EOSellerEFTABeslutningOmOppholdsrett"),
    EOS_ELLER_EFTA_VEDTAK_OM_VARIG_OPPHOLDSRETT("EOSellerEFTAVedtakOmVarigOppholdsrett"),
    EOS_ELLER_EFTA_OPPHOLDSTILLATELSE("EOSellerEFTAOppholdstillatelse");

    companion object {
        fun fraEOSellerEFTAOppholdType(EOSellerEFTAOppholdTypeVerdi: String?): EOSellerEFTAOppholdType? {
            return values().firstOrNull { it.kodeverdi == EOSellerEFTAOppholdTypeVerdi }
        }
    }
}

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

enum class OppholdstillaelsePaSammeVilkarType(val kodeverdi: String) {
    PERMANENT("Permanent"),
    MIDLERTIDIG("Midlertidig");

    companion object {
        fun fraOppholdstillatelsePaSammeVilkarType(oppholdPaSammeVilkarVerdi: String?): OppholdstillaelsePaSammeVilkarType? {
            return values().firstOrNull { it.kodeverdi == oppholdPaSammeVilkarVerdi }
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
