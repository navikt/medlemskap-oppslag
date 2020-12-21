package no.nav.medlemskap.services.udi
import no.nav.medlemskap.domene.Oppholdstillatelse
import no.nav.medlemskap.domene.Periode
import no.udi.mt_1067_nav_data.v1.*
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

    private fun mapHarArbeidsadgang(arbeidsadgang: Arbeidsadgang) : Boolean{
        if(arbeidsadgang.harArbeidsadgang.name.equals(JaNeiUavklart.JA.name)){
            return true
        }
        return false
    }

    private fun mapArbeidsadgang(arbeidsadgang: Arbeidsadgang) : no.nav.medlemskap.domene.Arbeidsadgang {
        return no.nav.medlemskap.domene.Arbeidsadgang(
                harArbeidsadgang =  mapHarArbeidsadgang(arbeidsadgang),
                periode = mapPeriode(arbeidsadgang)
        )



    }

    private fun mapPeriode(arbeidsadgang: Arbeidsadgang): Periode {

        return Periode(
                fom = arbeidsadgang.arbeidsadgangsPeriode.fra.asLocalDate(),
                tom = arbeidsadgang.arbeidsadgangsPeriode.til.asLocalDate()

        )
    }

    enum class JaNeiUavklart {
        JA,
        NEI,
        UAVKLART
    }


    private fun mapGjeldendeOppholdsstatus(gjeldendeOppholdsstatus: GjeldendeOppholdsstatus): Boolean {
        if(gjeldendeOppholdsstatus.eoSellerEFTAOpphold != null){
            return false
            //return mapEosEllerEftaopphold(gjeldendeOppholdsstatus.eoSellerEFTAOpphold)
        }
        if(gjeldendeOppholdsstatus.oppholdstillatelseEllerOppholdsPaSammeVilkar != null){
            return false
            //return mapOppholdstillatelseEllerOppholdsPaSammeVilkar(gjeldendeOppholdsstatus.oppholdstillatelseEllerOppholdsPaSammeVilkar)
        }
        if(gjeldendeOppholdsstatus.ikkeOppholdstillatelseIkkeOppholdsPaSammeVilkarIkkeVisum != null){
            return false
            //return mapIkkeOppholdstillatelseIkkeOppholdsPaSammeVilkarIkkeVisum(gjeldendeOppholdsstatus.ikkeOppholdstillatelseIkkeOppholdsPaSammeVilkarIkkeVisum)
        }
        if(gjeldendeOppholdsstatus.uavklart != null){
            return false
        }
        return false
    }

    private fun mapIkkeOppholdstillatelseIkkeOppholdsPaSammeVilkarIkkeVisum(ikkeOppholdstillatelseIkkeOppholdsPaSammeVilkarIkkeVisum: IkkeOppholdstillatelseIkkeOppholdsPaSammeVilkarIkkeVisum?) {
        TODO("Not yet implemented")
    }

    private fun mapOppholdstillatelseEllerOppholdsPaSammeVilkar(oppholdstillatelseEllerOppholdsPaSammeVilkar: OppholdstillatelseEllerOppholdsPaSammeVilkar?) : Boolean {
        val oppholdstype = oppholdstillatelseEllerOppholdsPaSammeVilkar?.oppholdstillatelse?.oppholdstillatelseType?.name
        //Legger begge slik for å få et bilde av hva som finnes
        if(oppholdstype.equals(OppholdstillatelseKategori.PERMANENT.name)){
            return true
        }
        if(oppholdstype.equals(OppholdstillatelseKategori.MIDLERTIDIG.name)){
            return true
        }
        return false
    }

    enum class OppholdstillatelseKategori{
        PERMANENT,
        MIDLERTIDIG
    }

    enum class EosOppholdsgrunnlag {
        VARIG,
        INGEN_INFORMASJON,
        FAMILIE,
        TJENESTEYTING_ELLER_ETABLERING,
        UAVKLART
    }



    private fun mapEosEllerEftaopphold(eoSellerEFTAOpphold: EOSellerEFTAOpphold?): Boolean {
        val oppholdsgrunnlag = eoSellerEFTAOpphold?.eoSellerEFTABeslutningOmOppholdsrett?.eosOppholdsgrunnlag?.name
        if(oppholdsgrunnlag.equals(EosOppholdsgrunnlag.VARIG.name) ||
           oppholdsgrunnlag.equals(EosOppholdsgrunnlag.INGEN_INFORMASJON.name) ||
           oppholdsgrunnlag.equals(EosOppholdsgrunnlag.FAMILIE.name)  ||
           oppholdsgrunnlag.equals(EosOppholdsgrunnlag.TJENESTEYTING_ELLER_ETABLERING.name)     ){
            return true
        }
        else if(oppholdsgrunnlag.equals(EosOppholdsgrunnlag.INGEN_INFORMASJON.name) ||
                oppholdsgrunnlag.equals(EosOppholdsgrunnlag.UAVKLART.name))
            return false

        return false

    }
    private fun XMLGregorianCalendar?.asLocalDate(): LocalDate? = this?.toGregorianCalendar()?.toZonedDateTime()?.toLocalDate()


    private fun XMLGregorianCalendar?.asDate(): LocalDateTime? = this?.toGregorianCalendar()?.toZonedDateTime()?.toLocalDateTime()
}
