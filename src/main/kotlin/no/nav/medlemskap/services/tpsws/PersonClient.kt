package no.nav.medlemskap.services.tpsws

import io.github.resilience4j.kotlin.retry.executeSuspendFunction
import io.github.resilience4j.retry.Retry
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import mu.KotlinLogging
import no.nav.tjeneste.virksomhet.person.v3.binding.PersonV3
import no.nav.tjeneste.virksomhet.person.v3.informasjon.NorskIdent
import no.nav.tjeneste.virksomhet.person.v3.informasjon.Periode
import no.nav.tjeneste.virksomhet.person.v3.informasjon.PersonIdent
import no.nav.tjeneste.virksomhet.person.v3.informasjon.Personidenter
import no.nav.tjeneste.virksomhet.person.v3.meldinger.HentPersonhistorikkRequest
import no.nav.tjeneste.virksomhet.person.v3.meldinger.HentPersonhistorikkResponse
import java.time.ZonedDateTime
import java.util.*
import javax.xml.datatype.DatatypeFactory
import javax.xml.datatype.XMLGregorianCalendar

class PersonClient(
        private val personV3: PersonV3,
        private val retry: Retry? = null
) {

    companion object {
        val HISTORIKK_FRA_OG_MED: XMLGregorianCalendar = DatatypeFactory.newInstance()
                .newXMLGregorianCalendar(GregorianCalendar.from(ZonedDateTime.now().minusYears(3)))

        private val logger = KotlinLogging.logger { }
    }

    suspend fun hentPersonHistorikk(fnr: String) : HentPersonhistorikkResponse {
        retry?.let {
            return it.executeSuspendFunction {
                hentPersonHistorikkRequest(fnr)
            }
        }
        return hentPersonHistorikkRequest(fnr)
    }

    suspend fun hentPersonHistorikkRequest(fnr: String): HentPersonhistorikkResponse {
        return withContext(Dispatchers.Default) {
            personV3.hentPersonhistorikk(lagHentPersonHistorikkRequest(fnr))
        }
    }

    private fun lagHentPersonHistorikkRequest(
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

    suspend fun healthCheck() {
        withContext(Dispatchers.Default) {
            personV3.ping()
        }
    }
}
