package no.nav.medlemskap.services.udi
import no.nav.medlemskap.domene.*
import no.nav.medlemskap.domene.ArbeidsadgangType
import no.nav.medlemskap.domene.ArbeidsadgangType.Companion.fraArbeidsadgangType
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
            gjeldendeOppholdsstatus = mapGjeldendeOppholdsstatus(oppholdstillatelse.gjeldendeOppholdsstatus),
            avgjoerelse = null,
            harFlyktningstatus = null,
            uavklartFlyktningstatus = null,
            arbeidsadgang = mapArbeidsadgang(oppholdstillatelse.arbeidsadgang)
        )
    }

    private fun mapArbeidsadgang(arbeidsadgang: Arbeidsadgang?): no.nav.medlemskap.domene.Arbeidsadgang? {
        return no.nav.medlemskap.domene.Arbeidsadgang(
            harArbeidsadgang = arbeidsadgang?.harArbeidsadgang == JaNeiUavklart.JA,
            arbeidsadgangType = mapArbeidsadgangType(arbeidsadgang?.typeArbeidsadgang),
            arbeidsomfang = Arbeidsomfang.fraArbeidsomfangVerdi(arbeidsadgang?.arbeidsOmfang?.name),
            periode = mapPeriode(arbeidsadgang?.arbeidsadgangsPeriode)
        )
    }

    private fun mapArbeidsadgangType(arbeidsadgangType: no.udi.mt_1067_nav_data.v1.ArbeidsadgangType?): ArbeidsadgangType? {
        return fraArbeidsadgangType(arbeidsadgangType?.value())
    }

    private fun mapPeriode(periode: no.udi.mt_1067_nav_data.v1.Periode?): Periode {
        return Periode(
            fom = periode?.fra.asLocalDate(),
            tom = periode?.til.asLocalDate()
        )
    }

    private fun mapGjeldendeOppholdsstatus(gjeldendeOppholdsstatus: GjeldendeOppholdsstatus): OppholdstillatelsePaSammeVilkar? {
        if (gjeldendeOppholdsstatus.eoSellerEFTAOpphold != null) {
            if (gjeldendeOppholdsstatus.eoSellerEFTAOpphold.eoSellerEFTABeslutningOmOppholdsrett != null) {
                val oppholdsrettsPeriode =
                    gjeldendeOppholdsstatus.eoSellerEFTAOpphold.eoSellerEFTABeslutningOmOppholdsrett.oppholdsrettsPeriode
                val eosEllerEftaOppholdPeriode = Periode(oppholdsrettsPeriode.fra.asLocalDate(), oppholdsrettsPeriode.til.asLocalDate())
                val harTillatelse = gjeldendeOppholdsstatus.eoSellerEFTAOpphold.eoSellerEFTABeslutningOmOppholdsrett.eosOppholdsgrunnlag
            }

            // return mapEosEllerEftaopphold(gjeldendeOppholdsstatus.eoSellerEFTAOpphold)
        }
        if (gjeldendeOppholdsstatus.oppholdstillatelseEllerOppholdsPaSammeVilkar != null) {
            val oppholdstillatelsePaSammeVilkarPeriode = Periode(
                gjeldendeOppholdsstatus.oppholdstillatelseEllerOppholdsPaSammeVilkar.oppholdstillatelsePeriode.fra.asLocalDate(),
                gjeldendeOppholdsstatus.oppholdstillatelseEllerOppholdsPaSammeVilkar.oppholdstillatelsePeriode.til.asLocalDate()
            )
            val harTillatelse = gjeldendeOppholdsstatus.oppholdstillatelseEllerOppholdsPaSammeVilkar?.oppholdstillatelse?.oppholdstillatelseType != null
            return OppholdstillatelsePaSammeVilkar(oppholdstillatelsePaSammeVilkarPeriode, harTillatelse)
        }
        if (gjeldendeOppholdsstatus.ikkeOppholdstillatelseIkkeOppholdsPaSammeVilkarIkkeVisum != null) {
            return null
            // return mapIkkeOppholdstillatelseIkkeOppholdsPaSammeVilkarIkkeVisum(gjeldendeOppholdsstatus.ikkeOppholdstillatelseIkkeOppholdsPaSammeVilkarIkkeVisum)
        }
        if (gjeldendeOppholdsstatus.uavklart != null) {
            return null
        }
        return null
    }

    private fun mapIkkeOppholdstillatelseIkkeOppholdsPaSammeVilkarIkkeVisum(ikkeOppholdstillatelseIkkeOppholdsPaSammeVilkarIkkeVisum: IkkeOppholdstillatelseIkkeOppholdsPaSammeVilkarIkkeVisum?) {
    }

    private fun mapOppholdstillatelseEllerOppholdsPaSammeVilkar(oppholdstillatelseEllerOppholdsPaSammeVilkar: OppholdstillatelseEllerOppholdsPaSammeVilkar?): Boolean {
        return oppholdstillatelseEllerOppholdsPaSammeVilkar?.oppholdstillatelse?.oppholdstillatelseType != null // Permanent eller midlertidig
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
