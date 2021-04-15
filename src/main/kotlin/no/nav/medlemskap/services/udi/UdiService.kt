package no.nav.medlemskap.services.udi

import no.nav.medlemskap.clients.udi.UdiClient
import no.nav.medlemskap.domene.Oppholdstillatelse

class UdiService(private val udiClient: UdiClient) {

    suspend fun hentOppholdstillatelseer(fnr: String): Oppholdstillatelse? {
        return udiClient.hentOppholdstatusResultat(fnr = fnr)?.let { UdiMapper.mapTilOppholdstillatelse(it) }
    }
}
