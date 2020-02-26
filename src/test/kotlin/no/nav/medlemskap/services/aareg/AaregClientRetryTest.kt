package no.nav.medlemskap.services.aareg

import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import io.mockk.spyk
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.runBlocking
import no.nav.medlemskap.config.retryRegistry
import no.nav.medlemskap.services.sts.StsRestClient
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import java.time.LocalDate


class AaregClientRetryTest {

    private val testRetry = retryRegistry.retry("AaReg")

    @Test
    fun `JobCancellationException kastes naar backend service ikke naas`() {
        //Ref https://github.com/ktorio/ktor/issues/1592
        val callId = "12345"

        val stsClient: StsRestClient = mockk()
        coEvery { stsClient.oidcToken() } returns "dummytoken"

        val client = spyk(AaRegClient("http://localhost:9987/finnesIkke", stsClient, testRetry))

        assertThrows<CancellationException> {
            runBlocking { client.hentArbeidsforhold("26104635775", callId, LocalDate.of(2010, 1, 1), LocalDate.of(2016, 1, 1)) }
        }

        coVerify(exactly = 2) { client.hentArbeidsforhold(any(), any(), any(), any()) }
    }
}



