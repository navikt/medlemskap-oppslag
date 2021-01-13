package no.nav.medlemskap.services.udi

import no.nav.medlemskap.clients.udi.UdiClient
import no.nav.medlemskap.common.arbeidsadgangtyperCounter
import no.nav.medlemskap.common.harArbeidsadgangCounter
import no.nav.medlemskap.common.harTillatelseCounter
import no.nav.medlemskap.common.oppholdstillatelseTypeCounter
import no.nav.medlemskap.domene.Oppholdstillatelse

class UdiService(private val udiClient: UdiClient) {

    suspend fun hentOppholdstillatelseer(fnr: String): Oppholdstillatelse? {
        val udiResultat = udiClient.hentOppholdstatusResultat(fnr = fnr)?.let { UdiMapper.mapTilOppholdstillatelser(it) }

        arbeidsadgangtyperCounter(udiResultat?.arbeidsadgang?.arbeidsadgangType?.name).increment()
        harArbeidsadgangCounter(udiResultat?.arbeidsadgang?.harArbeidsadgang).increment()
        harTillatelseCounter(udiResultat?.gjeldendeOppholdsstatus?.harTillatelse).increment()
        oppholdstillatelseTypeCounter(udiResultat?.gjeldendeOppholdsstatus).increment()

        return udiResultat
    }
}
