package no.nav.medlemskap.common.healthcheck

import io.restassured.RestAssured.get
import no.nav.medlemskap.common.ServerTest
import org.hamcrest.Matchers.hasItems
import org.hamcrest.Matchers.hasSize
import org.junit.jupiter.api.Test


class HealthReporterTest : ServerTest() {

    @Test
    fun testHealthCheckResponse() {
        get("/healthCheck")
                .then()
                .statusCode(503)
                .body("healthy", hasSize<Int>(0))
                .and().body("unhealthy", hasSize<Int>(7))
                .and().body("unhealthy.name", hasItems("Inntekt", "Medl", "Oppg", "PDL", "SAF", "STS", "TPS"))
    }

}

