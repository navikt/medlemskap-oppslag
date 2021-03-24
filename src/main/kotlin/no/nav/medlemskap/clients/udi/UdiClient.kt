package no.nav.medlemskap.clients.udi

import io.github.resilience4j.retry.Retry
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import mu.KotlinLogging
import no.nav.medlemskap.clients.runWithRetryAndMetrics
import no.nav.medlemskap.common.exceptions.OppholdstatusIkkeFunnetException
import no.nav.medlemskap.common.exceptions.PersonErDoedUdi
import no.nav.medlemskap.common.exceptions.PersonIkkeFunnetUDI
import no.udi.mt_1067_nav_data.v1.HentPersonstatusParameter
import no.udi.mt_1067_nav_data.v1.HentPersonstatusResultat
import v1.mt_1067_nav.no.udi.HentPersonstatusFault
import v1.mt_1067_nav.no.udi.HentPersonstatusRequestType
import v1.mt_1067_nav.no.udi.HentPersonstatusResponseType
import v1.mt_1067_nav.no.udi.MT1067NAVV1Interface
import java.time.LocalDateTime
import javax.xml.datatype.DatatypeFactory

class UdiClient(
    private val mT1067NAVV1Interface: MT1067NAVV1Interface,
    private val retry: Retry? = null
) {
    private val FEILKODE_PERSON_FINNES_IKKE = 10003
    private val FEILKODE_PERSON_ER_DOED = 10623

    private val secureLogger = KotlinLogging.logger("tjenestekall")

    companion object {
        val dataTypeFactory: DatatypeFactory = DatatypeFactory.newInstance()
        private val logger = KotlinLogging.logger { }
    }

    suspend fun hentOppholdstatusResultat(fnr: String): HentPersonstatusResultat? {
        return runWithRetryAndMetrics("UDI", "hentOppholdstillatelseV1", retry) {
            hentOppholdstatusRequest(fnr)?.resultat
        }
    }

    private suspend fun hentOppholdstatusRequest(fnr: String): HentPersonstatusResponseType? {

        return withContext(Dispatchers.Default) {
            secureLogger.info("Request: ${mapRequest(fnr)}")

            try {
                mT1067NAVV1Interface.hentPersonstatus(mapRequest(fnr))
            } catch (hentPersonstatusFault: HentPersonstatusFault) {
                val errorCode = hentPersonstatusFault.faultInfo.kodeId
                if (errorCode == FEILKODE_PERSON_FINNES_IKKE) {
                    throw PersonIkkeFunnetUDI("Person ikke funnet i UDI")
                }
                if (errorCode == FEILKODE_PERSON_ER_DOED) {
                    throw PersonErDoedUdi("Person er død fra udi", hentPersonstatusFault)
                } else {
                    logger.warn("Kall mot UDI feilet ", hentPersonstatusFault)
                    throw OppholdstatusIkkeFunnetException("InnhentInfoFraUDI feilet, Oppholdstatus ikke funnet for person")
                }
            }
        }
    }

    fun mapRequest(fnr: String): HentPersonstatusRequestType {
        val type = HentPersonstatusRequestType()
        val param = HentPersonstatusParameter()
        param.fodselsnummer = fnr
        param.isInkluderArbeidsadgang = true
        param.isInkluderAvgjorelsehistorikk = true
        param.isManuellOppgVedUavklartArbeidsadgang = true
        param.isInkluderSoknadOmBeskyttelseUnderBehandling = true
        param.avgjorelserFraDato =
            DatatypeFactory.newInstance().newXMLGregorianCalendar(LocalDateTime.now().minusYears(1).toString())
        type.parameter = param
        return type
    }
/*
    suspend fun healthCheck() {
        withContext(Dispatchers.Default) {
            mT1067NAVV1Interface.ping(PingRequestType())
        }
    }
 */
}
