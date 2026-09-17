package no.nav.medlemskap.services.medl

import no.nav.medlemskap.clients.medl.MedlClient
import java.time.LocalDate

class MedlService(private val medlClient: MedlClient) {

    suspend fun hentMedlemskapsunntak(ident: String, callId: String) =
        mapMedlemskapResultat(medlClient.hentMedlemskapsunntak(ident, callId))
}
