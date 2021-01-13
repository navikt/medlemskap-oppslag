package no.nav.medlemskap.cucumber.steps.udi

import no.udi.mt_1067_nav_data.v1.EOSellerEFTAGrunnlagskategoriOppholdsrett
import no.udi.mt_1067_nav_data.v1.Periode
import javax.xml.datatype.XMLGregorianCalendar

class EOSellerEFTABeslutningOmOppholdsrett {
    var oppholdsrettsPeriode: Periode? = null
    var effektueringsdato: XMLGregorianCalendar? = null
    var eosOppholdsgrunnlag: EOSellerEFTAGrunnlagskategoriOppholdsrett? = null
}
