package no.nav.medlemskap.config

import io.github.resilience4j.retry.RetryConfig
import io.github.resilience4j.retry.RetryRegistry
import java.time.Duration

val retryConfig: RetryConfig = RetryConfig
        .custom<RetryConfig>()
        .maxAttempts(2)
        .waitDuration(Duration.ofSeconds(1))
        .retryExceptions(RuntimeException::class.java)
        .build();

val retryRegistry: RetryRegistry = RetryRegistry.of(retryConfig)

val aaRegRetry = retryRegistry.retry("AaReg")
val inntektRetry = retryRegistry.retry("Inntekt")
val medlRetry = retryRegistry.retry("Medl")
val oppgaveRetry = retryRegistry.retry("Oppgave")
val pdlRetry = retryRegistry.retry("PDL")
val safRetry = retryRegistry.retry("Saf")
val stsRetry = retryRegistry.retry("STS")
val tpsRetry = retryRegistry.retry("TPS")
