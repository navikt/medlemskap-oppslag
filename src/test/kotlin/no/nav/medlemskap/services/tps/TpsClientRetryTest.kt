package no.nav.medlemskap.services.tps

import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.runBlocking
import no.nav.medlemskap.config.retryRegistry
import no.nav.medlemskap.services.tpsws.PersonClient
import no.nav.tjeneste.virksomhet.person.v3.binding.HentPersonhistorikkPersonIkkeFunnet
import no.nav.tjeneste.virksomhet.person.v3.binding.PersonV3
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

class TpsClientRetryTest {

    private val tpsRetry = retryRegistry.retry("TPS")

    @Test
    fun `skal retrye paa en runtimeexception`() {
        val mockPersonV3 = mockk<PersonV3>()
        val personClient = PersonClient(mockPersonV3, tpsRetry)
        every { mockPersonV3.hentPersonhistorikk(any()) } throws RuntimeException("Det feila..")

        assertThrows<RuntimeException> {
            runBlocking {
                personClient.hentPersonHistorikk("100")
            }
        }
        verify(exactly = 2) { mockPersonV3.hentPersonhistorikk(any()) }
    }

    @Test
    fun `skal ikke retrye paa en funksjonell exception`() {
        val mockPersonV3 = mockk<PersonV3>()
        val personClient = PersonClient(mockPersonV3)
        every { mockPersonV3.hentPersonhistorikk(any()) } throws HentPersonhistorikkPersonIkkeFunnet("Det feila..", null)

        assertThrows<HentPersonhistorikkPersonIkkeFunnet> {
            runBlocking {
                personClient.hentPersonHistorikk("100")
            }
        }
        verify(exactly = 1) { mockPersonV3.hentPersonhistorikk(any()) }
    }

}