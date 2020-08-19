package no.nav.medlemskap.services.pdl

import mu.KotlinLogging
import no.nav.medlemskap.clients.pdl.IdentGruppe
import no.nav.medlemskap.clients.pdl.PdlClient
import no.nav.medlemskap.common.exceptions.GraphqlError
import no.nav.medlemskap.common.exceptions.IdenterIkkeFunnet
import no.nav.medlemskap.common.objectMapper
import no.nav.medlemskap.domene.Personhistorikk
import no.nav.medlemskap.domene.ektefelle.PersonhistorikkEktefelle
import no.nav.medlemskap.domene.Statsborgerskap

class PdlService(private val pdlClient: PdlClient, private val clusterName: String = "dev-fss") {

    private val logger = KotlinLogging.logger { }

    suspend fun hentAktorId(fnr: String, callId: String): String {
        val pdlResponse = pdlClient.hentIdenter(fnr, callId)

        // Hack for å overleve manglende aktørID i ikke-konsistente data i Q2
        if (pdlResponse.errors != null && clusterName == "dev-fss") {
            return "111111111111"
        }

        pdlResponse.errors?.let { errors ->
            logger.warn { "Fikk følgende feil fra PDL: ${objectMapper.writeValueAsString(errors)}" }
            throw GraphqlError(errors.first(), "PDL")
        }

        return pdlResponse.data.hentIdenter?.identer?.first {
            !it.historisk && it.gruppe == IdentGruppe.AKTORID
        }?.ident ?: throw IdenterIkkeFunnet()
    }

    suspend fun hentAlleAktorIder(fnr: String, callId: String): List<String> {
        val pdlResponse = pdlClient.hentIdenter(fnr, callId)

        // Hack for å overleve manglende aktørID i ikke-konsistente data i Q2
        if (pdlResponse.errors != null && clusterName == "dev-fss") {
            return listOf("111111111111")
        }

        pdlResponse.errors?.let { errors ->
            logger.warn { "Fikk følgende feil fra PDL: ${objectMapper.writeValueAsString(errors)}" }
            throw GraphqlError(errors.first(), "PDL")
        }

        return pdlResponse.data.hentIdenter?.identer
                ?.filter { it.gruppe == IdentGruppe.AKTORID }
                ?.map { it.ident } ?: throw IdenterIkkeFunnet()

    }

    suspend fun hentPersonHistorikk(fnr: String, callId: String): Personhistorikk {
        return PdlMapper.mapTilPersonHistorikk(pdlClient.hentPerson(fnr, callId))

/*        // Hack for å overleve manglende aktørID i ikke-konsistente data i Q2
        if (pdlResponse.errors != null && clusterName == "dev-fss") {
            return "111111111111"
        }

        pdlResponse.errors?.let { errors ->
            logger.warn { "Fikk følgende feil fra PDL: ${objectMapper.writeValueAsString(errors)}" }
            throw GraphqlError(errors.first(), "PDL")
        }*/

    }

    suspend fun hentPersonHistorikkTilEktefelle(fnrTilEktefelle: String, callId: String): PersonhistorikkEktefelle {
        return PdlMapper.mapPersonhistorikkTilEktefelle(fnrTilEktefelle, pdlClient.hentPerson(fnrTilEktefelle, callId))
    }

    suspend fun hentFoedselsaar(fnr: String, callId: String): Int {
        return PdlMapper.mapTilFoedselsaar(pdlClient.hentFoedselsaar(fnr, callId))
    }

    suspend fun hentStatsborgerskap(fnr: String, callId: String): List<Statsborgerskap>? {
        val statsborgerskap = pdlClient.hentNasjonalitet(fnr, callId).data?.hentPerson?.statsborgerskap?.ifEmpty {
            logger.warn("PDL fant ikke person")
            emptyList()
        }
        return statsborgerskap?.map { PdlMapper.mapStatsborgerskap(it) }
    }
}