package no.nav.medlemskap.services.udi
import no.nav.medlemskap.domene.Oppholdstillatelse
import no.udi.mt_1067_nav_data.v1.*
import java.time.LocalDateTime
import javax.xml.datatype.XMLGregorianCalendar

object UdiMapper {
    fun mapTilOppholdstillatelser(oppholdstillatelse: HentPersonstatusResultat): Oppholdstillatelse? {
        return Oppholdstillatelse(
            uttrekkstidspunkt = oppholdstillatelse.uttrekkstidspunkt.asDate(),
            foresporselsfodselsnummer = oppholdstillatelse.foresporselsfodselsnummer,
            gjeldendeOppholdsstatus = mapGjeldendeOppholdsstatus(oppholdstillatelse.gjeldendeOppholdsstatus),
            avgjoerelse = null,
            harFlyktningstatus = null,
            uavklartFlyktningstatus = null ,
            arbeidsadgang = null
        )
    }

    private fun mapGjeldendeOppholdsstatus(gjeldendeOppholdsstatus: GjeldendeOppholdsstatus): Boolean {
        if(gjeldendeOppholdsstatus.eoSellerEFTAOpphold != null){
            mapEosEllerEftaopphold(gjeldendeOppholdsstatus.eoSellerEFTAOpphold)
        }
        if(gjeldendeOppholdsstatus.oppholdstillatelseEllerOppholdsPaSammeVilkar != null){
            mapOppholdstillatelseEllerOppholdsPaSammeVilkar(gjeldendeOppholdsstatus.oppholdstillatelseEllerOppholdsPaSammeVilkar)
        }
        if(gjeldendeOppholdsstatus.ikkeOppholdstillatelseIkkeOppholdsPaSammeVilkarIkkeVisum != null){
            mapIkkeOppholdstillatelseIkkeOppholdsPaSammeVilkarIkkeVisum(gjeldendeOppholdsstatus.ikkeOppholdstillatelseIkkeOppholdsPaSammeVilkarIkkeVisum)
        }
        if(gjeldendeOppholdsstatus.uavklart != null){
            return false
        }

    }

    private fun mapIkkeOppholdstillatelseIkkeOppholdsPaSammeVilkarIkkeVisum(ikkeOppholdstillatelseIkkeOppholdsPaSammeVilkarIkkeVisum: IkkeOppholdstillatelseIkkeOppholdsPaSammeVilkarIkkeVisum?) {
        TODO("Not yet implemented")
    }

    private fun mapOppholdstillatelseEllerOppholdsPaSammeVilkar(oppholdstillatelseEllerOppholdsPaSammeVilkar: OppholdstillatelseEllerOppholdsPaSammeVilkar?) {
        TODO("Not yet implemented")
    }

    private fun mapEosEllerEftaopphold(eoSellerEFTAOpphold: EOSellerEFTAOpphold?) {

    }

    private fun XMLGregorianCalendar?.asDate(): LocalDateTime? = this?.toGregorianCalendar()?.toZonedDateTime()?.toLocalDateTime()
}
