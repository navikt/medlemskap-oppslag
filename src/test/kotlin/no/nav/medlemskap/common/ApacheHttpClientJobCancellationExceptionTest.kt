package no.nav.medlemskap.common

import io.ktor.client.request.get
import io.ktor.client.request.url
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

class ApacheHttpClientJobCancellationExceptionTest {

    @Test
    fun `JobCancellationException kastes naar backend service ikke naas`() {
        //Ref https://github.com/ktorio/ktor/issues/1592

        assertThrows<CancellationException> {
            runBlocking {
                apacheHttpClient.get<String> {
                    url("http://localhost:9987/finnesIkke")
                }
            }
        }
    }
}



