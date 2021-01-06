package no.nav.medlemskap.cucumber.steps.udi

import no.udi.mt_1067_nav_data.v1.*
import javax.xml.datatype.XMLGregorianCalendar

class UdiOppholdstillatelseBuilder {
    var uttrekkstidspunkt: XMLGregorianCalendar? = null
    var foresporselsfodselsnummer = String()
    var gjeldendePerson: GjeldendePerson? = null
    var gjeldendeOppholdsstatus: GjeldendeOppholdsstatus? = null
    var arbeidsadgang: Arbeidsadgang? = null
    var avgjorelsehistorikk: Avgjorelser? = null
    var uavklartFlyktningstatus: Boolean? = null
    var harFlyktningstatus: Boolean? = null
    var historikkHarFlyktningstatus: Boolean? = null
    var soknadOmBeskyttelseUnderBehandling: SoknadOmBeskyttelseUnderBehandling? = null

    fun build(): HentPersonstatusResultat {
        val oppholdstillatelse = HentPersonstatusResultat()
        oppholdstillatelse.arbeidsadgang = arbeidsadgang
        oppholdstillatelse.uttrekkstidspunkt = uttrekkstidspunkt
        oppholdstillatelse.avgjorelsehistorikk = avgjorelsehistorikk
        oppholdstillatelse.foresporselsfodselsnummer = foresporselsfodselsnummer
        oppholdstillatelse.gjeldendePerson = gjeldendePerson
        oppholdstillatelse.gjeldendeOppholdsstatus = gjeldendeOppholdsstatus
        oppholdstillatelse.soknadOmBeskyttelseUnderBehandling = soknadOmBeskyttelseUnderBehandling
        oppholdstillatelse.isUavklartFlyktningstatus = uavklartFlyktningstatus
        oppholdstillatelse.isHarFlyktningstatus = harFlyktningstatus
        oppholdstillatelse.isHistorikkHarFlyktningstatus = historikkHarFlyktningstatus
        return oppholdstillatelse
    }
}
