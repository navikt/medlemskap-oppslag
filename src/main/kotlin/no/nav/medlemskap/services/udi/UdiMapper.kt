package no.nav.medlemskap.services.udi
import no.nav.medlemskap.domene.*
import no.nav.medlemskap.domene.Oppholdstillatelse
import no.nav.medlemskap.domene.Periode
import no.udi.mt_1067_nav_data.v1.*
import no.udi.mt_1067_nav_data.v1.Arbeidsadgang
import no.udi.mt_1067_nav_data.v1.GjeldendeOppholdsstatus
import no.udi.mt_1067_nav_data.v1.JaNeiUavklart
import java.time.LocalDate
import java.time.LocalDateTime
import javax.xml.datatype.XMLGregorianCalendar

object UdiMapper {
    fun mapTilOppholdstillatelser(oppholdstillatelse: HentPersonstatusResultat): Oppholdstillatelse {
        return Oppholdstillatelse(
            uttrekkstidspunkt = oppholdstillatelse.uttrekkstidspunkt.asDate(),
            foresporselsfodselsnummer = oppholdstillatelse.foresporselsfodselsnummer,
            gjeldendeOppholdsstatus = null,
            avgjoerelse = null,
            harFlyktningstatus = null,
            uavklartFlyktningstatus = null,
            arbeidsadgang = mapArbeidsadgang(oppholdstillatelse.arbeidsadgang)
        )
    }

    private fun mapHarArbeidsadgang(arbeidsadgang: Arbeidsadgang): Boolean {
        if (arbeidsadgang.harArbeidsadgang.name == JaNeiUavklart.JA.name) {
            return true
        }
        return false
    }

    private fun mapArbeidsadgang(arbeidsadgang: Arbeidsadgang): no.nav.medlemskap.domene.Arbeidsadgang {
        return no.nav.medlemskap.domene.Arbeidsadgang(
            harArbeidsadgang = mapHarArbeidsadgang(arbeidsadgang),
            arbeidsadgangType = mapArbeidsadgangType(arbeidsadgang),
            arbeidsomfang = mapArbeidsadgangOmfang(arbeidsadgang),
            periode = mapPeriode(arbeidsadgang)
        )
    }

    private fun mapArbeidsadgangOmfang(arbeidsadgang: Arbeidsadgang) =
        when (arbeidsadgang.arbeidsOmfang) {
            ArbeidOmfangKategori.KUN_ARBEID_HELTID -> ArbeidomfangKategori.KUN_ARBEID_HELTID
            ArbeidOmfangKategori.KUN_ARBEID_DELTID -> ArbeidomfangKategori.KUN_ARBEID_DELTID
            ArbeidOmfangKategori.DELTID_SAMT_FERIER_HELTID -> ArbeidomfangKategori.DELTID_SAMT_FERIER_HELTID
            ArbeidOmfangKategori.INGEN_KRAV_TIL_STILLINGSPROSENT -> ArbeidomfangKategori.INGEN_KRAV_TIL_STILLINGSPROSENT
            ArbeidOmfangKategori.UAVKLART -> ArbeidomfangKategori.UAVKLART
        }
    private fun mapArbeidsadgangType(arbeidsadgang: Arbeidsadgang) =
        when (arbeidsadgang.typeArbeidsadgang) {
            ArbeidsadgangType.BESTEMT_ARBEIDSGIVER_ELLER_OPPDRAGSGIVER -> ArbeidsAdgangType.BESTEMT_ARBEIDSGIVER_ELLER_OPPDRAGSGIVER
            ArbeidsadgangType.BESTEMT_ARBEID_ELLER_OPPDRAG -> ArbeidsAdgangType.BESTEMT_ARBEID_ELLER_OPPDRAG
            ArbeidsadgangType.GENERELL -> ArbeidsAdgangType.GENERELL
            ArbeidsadgangType.UAVKLART -> ArbeidsAdgangType.UAVKLART
            ArbeidsadgangType.BESTEMT_ARBEIDSGIVER_OG_ARBEID_ELLER_BESTEMT_OPPDRAGSGIVER_OG_OPPDRAG -> ArbeidsAdgangType.BESTEMT_ARBEIDSGIVER_OG_ARBEID_ELLER_BESTEMT_OPPDRAGSGIVER_OG_OPPDRAG
        }

    private fun mapPeriode(arbeidsadgang: Arbeidsadgang): Periode {

        return Periode(
            fom = arbeidsadgang.arbeidsadgangsPeriode.fra.asLocalDate(),
            tom = arbeidsadgang.arbeidsadgangsPeriode.til.asLocalDate()

        )
    }

    //Utkommenterte da Helle sa vi ikke skulle bruke det under, venter med å fjerne det til vi har hatt gjennomgang

    private fun mapGjeldendeOppholdsstatus(gjeldendeOppholdsstatus: GjeldendeOppholdsstatus): Boolean {
        if (gjeldendeOppholdsstatus.eoSellerEFTAOpphold != null) {
            return false
            // return mapEosEllerEftaopphold(gjeldendeOppholdsstatus.eoSellerEFTAOpphold)
        }
        if (gjeldendeOppholdsstatus.oppholdstillatelseEllerOppholdsPaSammeVilkar != null) {
            return false
            // return mapOppholdstillatelseEllerOppholdsPaSammeVilkar(gjeldendeOppholdsstatus.oppholdstillatelseEllerOppholdsPaSammeVilkar)
        }
        if (gjeldendeOppholdsstatus.ikkeOppholdstillatelseIkkeOppholdsPaSammeVilkarIkkeVisum != null) {
            return false
            // return mapIkkeOppholdstillatelseIkkeOppholdsPaSammeVilkarIkkeVisum(gjeldendeOppholdsstatus.ikkeOppholdstillatelseIkkeOppholdsPaSammeVilkarIkkeVisum)
        }
        if (gjeldendeOppholdsstatus.uavklart != null) {
            return false
        }
        return false
    }

    private fun mapIkkeOppholdstillatelseIkkeOppholdsPaSammeVilkarIkkeVisum(ikkeOppholdstillatelseIkkeOppholdsPaSammeVilkarIkkeVisum: IkkeOppholdstillatelseIkkeOppholdsPaSammeVilkarIkkeVisum?) {
        TODO("Not yet implemented")
    }

    private fun mapOppholdstillatelseEllerOppholdsPaSammeVilkar(oppholdstillatelseEllerOppholdsPaSammeVilkar: OppholdstillatelseEllerOppholdsPaSammeVilkar?): Boolean {
        val oppholdstype = oppholdstillatelseEllerOppholdsPaSammeVilkar?.oppholdstillatelse?.oppholdstillatelseType?.name
        // Legger begge slik for å få et bilde av hva som finnes
        if (oppholdstype.equals(OppholdstillatelseKategori.PERMANENT.name)) {
            return true
        }
        if (oppholdstype.equals(OppholdstillatelseKategori.MIDLERTIDIG.name)) {
            return true
        }
        return false
    }

    enum class OppholdstillatelseKategori {
        PERMANENT,
        MIDLERTIDIG
    }

    enum class EosOppholdsgrunnlag {
        VARIG,
        INGEN_INFORMASJON,
        FAMILIE,
        TJENESTEYTING_ELLER_ETABLERING,
        UAVKLART;

        companion object {
            fun EosOppholdsgrunnlag.erOppholdsgrunnlagAvklart(): Boolean {
                return (this == VARIG || this == FAMILIE || this == TJENESTEYTING_ELLER_ETABLERING)
            }
        }
    }

    private fun XMLGregorianCalendar?.asLocalDate(): LocalDate? = this?.toGregorianCalendar()?.toZonedDateTime()?.toLocalDate()

    private fun XMLGregorianCalendar?.asDate(): LocalDateTime? = this?.toGregorianCalendar()?.toZonedDateTime()?.toLocalDateTime()
}
