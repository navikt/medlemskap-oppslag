package no.nav.medlemskap.services.udi

import no.nav.medlemskap.clients.udi.UdiClient
import no.nav.medlemskap.common.arbeidsadgangtyperCounter

class UdiService(private val udiClient: UdiClient) {

    suspend fun hentOppholdstillatelseer(fnr: String) {
        val udiResultat = udiClient.hentOppholdstatusResultat(fnr = fnr)?.let { UdiMapper.mapTilOppholdstillatelser(it) }
        arbeidsadgangtyperCounter(udiResultat?.arbeidsadgang?.arbeidsadgangType?.name).increment()
    }
}
