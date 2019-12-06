package no.nav.medlemskap

import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import no.nav.medlemskap.modell.MedlResponse
import no.nav.medlemskap.modell.PdlResponse
import no.nav.medlemskap.register.MedlApiImpl
import no.nav.medlemskap.register.PdlApiImpl

data class DataCollectorResult(val pdlResponse: PdlResponse, val medlResponse: MedlResponse)

suspend fun collectData(fnr: String): DataCollectorResult = coroutineScope {
    val pdlResult = async { PdlApiImpl().hentPerson(fnr) }
    val medlResult = async { MedlApiImpl().medlResponse(fnr) }

    DataCollectorResult(pdlResult.await(), medlResult.await())
}
