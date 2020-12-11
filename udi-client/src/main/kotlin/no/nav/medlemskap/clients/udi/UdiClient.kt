package no.nav.medlemskap.clients.udi


import io.github.resilience4j.kotlin.retry.executeSuspendFunction
import io.github.resilience4j.retry.Retry
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import no.udi.mt_1067_nav_data.v1.HentPersonstatusParameter
import no.udi.mt_1067_nav_data.v1.HentPersonstatusResultat
import v1.mt_1067_nav.no.udi.HentPersonstatusRequestType
import v1.mt_1067_nav.no.udi.HentPersonstatusResponseType
import v1.mt_1067_nav.no.udi.MT1067NAVV1Interface
import java.util.*
import javax.xml.datatype.DatatypeFactory


class UdiClient(
        private val mT1067NAVV1Interface: MT1067NAVV1Interface,
        private val retry: Retry? = null
) {

    companion object {
        val dataTypeFactory: DatatypeFactory = DatatypeFactory.newInstance()
    }

    suspend fun hentOppholdstatusResulat(fnr: String): HentPersonstatusResultat? {
        retry?.let {
            return it.executeSuspendFunction {
                hentOppholdstatusRequest(fnr)?.resultat
            }
        }
        return hentOppholdstatusRequest(fnr)?.resultat
    }

    suspend fun hentOppholdstatusRequest(fnr: String): HentPersonstatusResponseType? {

        return withContext(Dispatchers.Default) {
            mT1067NAVV1Interface.hentPersonstatus(mapRequest(fnr))
        }
    }


    fun mapRequest(fnr: String): HentPersonstatusRequestType? {
        val type = HentPersonstatusRequestType()
        val param = HentPersonstatusParameter()
        param.fodselsnummer = fnr
        param.isInkluderArbeidsadgang = true
        param.isInkluderAvgjorelsehistorikk = true
        param.isManuellOppgVedUavklartArbeidsadgang = true
        param.isInkluderSoknadOmBeskyttelseUnderBehandling = true
        param.avgjorelserFraDato = null
        type.parameter = param
        return type
    }

    fun healthCheck() {
        TODO("Not yet implemented")
    }
}
