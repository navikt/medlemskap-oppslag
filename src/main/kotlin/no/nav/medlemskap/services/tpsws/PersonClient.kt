package no.nav.medlemskap.services.tpsws

import no.nav.tjeneste.virksomhet.person.v3.binding.PersonV3
import no.nav.tjeneste.virksomhet.person.v3.informasjon.NorskIdent
import no.nav.tjeneste.virksomhet.person.v3.informasjon.Periode
import no.nav.tjeneste.virksomhet.person.v3.informasjon.PersonIdent
import no.nav.tjeneste.virksomhet.person.v3.informasjon.Personidenter
import no.nav.tjeneste.virksomhet.person.v3.meldinger.HentPersonhistorikkRequest
import no.nav.tjeneste.virksomhet.person.v3.meldinger.HentPersonhistorikkResponse
import org.apache.xml.security.stax.ext.XMLSecurityConstants.datatypeFactory
import java.time.ZonedDateTime
import java.util.*
import javax.xml.datatype.XMLGregorianCalendar

class PersonClient(private val personV3: PersonV3) {

    companion object {
        val HISTORIKK_FRA_OG_MED: XMLGregorianCalendar = datatypeFactory
                .newXMLGregorianCalendar(GregorianCalendar.from(ZonedDateTime.now().minusYears(3)))
    }

    fun hentPersonHistorikk(fnr: String): HentPersonhistorikkResponse =
            personV3.hentPersonhistorikk(hentPersonHistorikkRequest(fnr))

    private fun hentPersonHistorikkRequest(
            fnr: String,
            fraOgMed: XMLGregorianCalendar = HISTORIKK_FRA_OG_MED
    ): HentPersonhistorikkRequest =
            HentPersonhistorikkRequest().apply {
                this.aktoer = PersonIdent().withIdent(
                        NorskIdent()
                                .withIdent(fnr)
                                .withType(Personidenter().withValue("FNR"))
                )
                this.periode = Periode().apply {
                    fom = fraOgMed
                }
            }
}
