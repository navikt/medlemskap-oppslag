package no.nav.medlemskap.services.udi
import javax.xml.datatype.XMLGregorianCalendar
import no.nav.medlemskap.domene.Oppholdstillatelse
import no.udi.mt_1067_nav_data.v1.HentPersonstatusResultat
import java.time.LocalDateTime

object UdiMapper {
    fun mapTilOppholdstillatelser(oppholdstillatelse: HentPersonstatusResultat): Oppholdstillatelse? {
        return Oppholdstillatelse(
                uttrekkstidspunkt = oppholdstillatelse.uttrekkstidspunkt.asDate(),
                foresporselsfodselsnummer = oppholdstillatelse.foresporselsfodselsnummer,
                avgjoerelse = oppholdstillatelse.avgjorelsehistorikk.isUavklart,
                uavklartFlyktningstatus = oppholdstillatelse.isUavklartFlyktningstatus,
                gjeldendeOppholdsstatus = null,
                harFlyktningstatus = oppholdstillatelse.isHarFlyktningstatus,
                arbeidsadgang = null
        )
    }

    private fun XMLGregorianCalendar?.asDate(): LocalDateTime? = this?.toGregorianCalendar()?.toZonedDateTime()?.toLocalDateTime()
}