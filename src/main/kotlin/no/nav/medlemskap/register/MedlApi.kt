package no.nav.medlemskap.register

import no.nav.medlemskap.modell.MedlResponse

interface MedlApi {
    fun medlResponse(fnr: String): MedlResponse
}

class MedlApiImpl : MedlApi {
    override fun medlResponse(fnr: String): MedlResponse {
        TODO("implement medl query")
    }

}
