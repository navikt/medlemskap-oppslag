package no.nav.medlemskap.services.pdl

import mu.KotlinLogging
import no.nav.medlemskap.clients.pdl.PdlClient
import no.nav.medlemskap.clients.pdl.generated.HentIdenter
import no.nav.medlemskap.common.exceptions.GraphqlError
import no.nav.medlemskap.common.exceptions.IdenterIkkeFunnet
import no.nav.medlemskap.common.exceptions.PersonIkkeFunnet
import no.nav.medlemskap.common.objectMapper
import no.nav.medlemskap.domene.Personhistorikk
import no.nav.medlemskap.domene.barn.PersonhistorikkBarn
import no.nav.medlemskap.domene.ektefelle.PersonhistorikkEktefelle
import no.nav.medlemskap.services.pdl.mapper.PdlMapper
import no.nav.medlemskap.services.pdl.mapper.PdlMapperBarn
import no.nav.medlemskap.services.pdl.mapper.PdlMapperEktefelle

class PdlService(private val pdlClient: PdlClient, private val clusterName: String = "dev-fss") {

    private val logger = KotlinLogging.logger { }

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

        return pdlResponse.data?.hentIdenter?.identer
            ?.filter { it.gruppe == HentIdenter.IdentGruppe.AKTORID }
            ?.map { it.ident } ?: throw IdenterIkkeFunnet()
    }

    suspend fun hentPersonHistorikkTilBruker(fnr: String, callId: String): Personhistorikk {
        val hentPersonhistorikkTilBrukerRespons = pdlClient.hentPerson(fnr, callId)

        hentPersonhistorikkTilBrukerRespons.errors?.let { errors ->
            logger.warn { "Fikk følgende feil fra PDL: ${objectMapper.writeValueAsString(errors)}" }
            throw GraphqlError(errors.first(), "PDL")
        }

        if (hentPersonhistorikkTilBrukerRespons.data?.hentPerson != null) {
            return PdlMapper.mapTilPersonHistorikkTilBruker(hentPersonhistorikkTilBrukerRespons.data?.hentPerson!!)
        } else throw PersonIkkeFunnet("PDL")
    }

    suspend fun hentPersonHistorikkTilEktefelle(fnrTilEktefelle: String, callId: String): PersonhistorikkEktefelle {
        val hentPersonhistorikkTilEktefelleResponse = pdlClient.hentPerson(fnrTilEktefelle, callId)

        hentPersonhistorikkTilEktefelleResponse.errors?.let { errors ->
            logger.warn { "Fikk følgende feil fra PDL: ${objectMapper.writeValueAsString(errors)}" }
            throw GraphqlError(errors.first(), "PDL")
        }

        if (hentPersonhistorikkTilEktefelleResponse.data?.hentPerson != null) {
            return PdlMapperEktefelle
                .mapPersonhistorikkTilEktefelle(fnrTilEktefelle, hentPersonhistorikkTilEktefelleResponse.data?.hentPerson!!)
        } else throw PersonIkkeFunnet("PDL")
    }

    suspend fun hentPersonHistorikkTilBarn(fnrTilBarn: String, callId: String): PersonhistorikkBarn {
        val hentPersonHistorikkTilBarnRespons = pdlClient.hentPerson(fnrTilBarn, callId)

        hentPersonHistorikkTilBarnRespons.errors?.let { errors ->
            logger.warn { "Fikk følgende feil fra PDL: ${objectMapper.writeValueAsString(errors)}" }
            throw GraphqlError(errors.first(), "PDL")
        }

        if (hentPersonHistorikkTilBarnRespons.data?.hentPerson != null) {
            return PdlMapperBarn.mapPersonhistorikkTilBarn(fnrTilBarn, hentPersonHistorikkTilBarnRespons.data?.hentPerson!!)
        } else throw PersonIkkeFunnet("PDL")
    }
}
