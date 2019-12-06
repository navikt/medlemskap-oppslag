package no.nav.medlemskap.register

import no.nav.medlemskap.modell.PdlResponse

interface PdlApi {
    fun hentPerson(fnr: String): PdlResponse
}

class PdlApiImpl : PdlApi {

    override fun hentPerson(fnr: String): PdlResponse {
        TODO("implement graphQL query")
    }

}
