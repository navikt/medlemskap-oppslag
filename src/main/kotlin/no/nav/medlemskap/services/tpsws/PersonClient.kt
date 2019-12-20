package no.nav.medlemskap.services.tpsws

import no.nav.tjeneste.virksomhet.person.v3.binding.PersonV3
import no.nav.tjeneste.virksomhet.person.v3.informasjon.NorskIdent
import no.nav.tjeneste.virksomhet.person.v3.informasjon.PersonIdent
import no.nav.tjeneste.virksomhet.person.v3.informasjon.Personidenter
import no.nav.tjeneste.virksomhet.person.v3.meldinger.HentPersonhistorikkRequest
import no.nav.tjeneste.virksomhet.person.v3.meldinger.HentPersonhistorikkResponse

class PersonClient(private val personV3: PersonV3) {

    fun hentPersonHistorikk(fnr: String): HentPersonhistorikkResponse =
            personV3.hentPersonhistorikk(hentPersonHistorikkRequest(fnr))

    private fun hentPersonHistorikkRequest(fnr: String) =
            HentPersonhistorikkRequest().apply {
                withAktoer(PersonIdent().apply {
                    withIdent(NorskIdent().apply {
                        withIdent(fnr)
                        withType(Personidenter().apply {
                            withKodeRef("FNR")
                        })
                    })
                })
            }
}
