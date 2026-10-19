package no.nav.medlemskap.services

import mu.KotlinLogging
import net.logstash.logback.argument.StructuredArguments.kv
import no.nav.medlemskap.domene.InputPeriode
import no.nav.medlemskap.domene.arbeidsforhold.Arbeidsforhold
import no.nav.medlemskap.domene.barn.DataOmBarn
import no.nav.medlemskap.domene.ektefelle.DataOmEktefelle
import no.nav.medlemskap.services.aareg.AaRegService
import no.nav.medlemskap.services.pdl.PdlService
import org.slf4j.MarkerFactory
import java.time.LocalDate

class FamilieService(private val aaRegService: AaRegService, private val pdlService: PdlService) {

    private val logger = KotlinLogging.logger { }
    private val teamLogs = MarkerFactory.getMarker("TEAM_LOGS")

    suspend fun hentDataOmBarn(
        fnrTilBarn: List<String>,
        callId: String
    ): List<DataOmBarn> {
        return fnrTilBarn
            .mapNotNull { pdlService.hentPersonHistorikkTilBarn(it, callId) }
            .map { DataOmBarn(it) }
    }

    suspend fun hentDataOmEktefelle(
        fnrTilEktefelle: String?,
        callId: String,
        periode: InputPeriode,
        startDatoForYtelse: LocalDate
    ): DataOmEktefelle? {
        if (fnrTilEktefelle == null) {
            logger.info(teamLogs, "Hopper over ektefelleoppslag: fant ikke gyldig ident i sivilstand.", kv("callId", callId))
            return null
        }

        logger.info(teamLogs, "Henter personhistorikk til ektefelle fra PDL.", kv("callId", callId))
        val personhistorikkEktefelle =
            pdlService.hentPersonHistorikkTilEktefelle(fnrTilEktefelle, startDatoForYtelse, callId)
        if (personhistorikkEktefelle == null) {
            logger.info(teamLogs, "Ingen tilgjengelig personhistorikk til ektefelle fra PDL. Hopper over AaReg.", kv("callId", callId))
            return null
        }

        logger.info(teamLogs, "Henter arbeidsforhold til ektefelle fra AaReg.", kv("callId", callId))
        val arbeidsforholdEktefelle = try {
            aaRegService.hentArbeidsforhold(
                fnr = fnrTilEktefelle,
                fraOgMed = Arbeidsforhold.fraOgMedDatoForArbeidsforhold(startDatoForYtelse),
                tilOgMed = periode.tom,
                callId = callId
            )
        } catch (t: Exception) {
            logger.warn(teamLogs, "Henting av arbeidsforhold til ektefelle feilet. Bruker tom liste.", kv("callId", callId), t)
            emptyList<Arbeidsforhold>()
        }

        val dataOmEktefelle = DataOmEktefelle(
            personhistorikkEktefelle = personhistorikkEktefelle,
            arbeidsforholdEktefelle = arbeidsforholdEktefelle
        )
        logger.info(teamLogs, "Hentet data om ektefelle.", kv("callId", callId), kv("dataOmEktefelle", dataOmEktefelle))
        return dataOmEktefelle
    }
}
