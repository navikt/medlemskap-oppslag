package no.nav.medlemskap.services.udi

import no.nav.medlemskap.clients.udi.UdiClient

class UdiService(private val udiClient: UdiClient) {

    suspend fun hentOppholdstillatelseer(ident: String) =
            udiClient.hentOppholdstillatelser(ident)?.let { UdiMapper.mapTilOppholdstillatelser(it) }

}
