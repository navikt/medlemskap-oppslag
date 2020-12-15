package no.nav.medlemskap.services.udi

import no.nav.medlemskap.clients.udi.UdiClient

class UdiService(private val udiClient: UdiClient) {

    suspend fun hentOppholdstillatelseer(fnr: String) =
        udiClient.hentOppholdstatusResulat(fnr = fnr)?.let { UdiMapper.mapTilOppholdstillatelser(it) }
}
