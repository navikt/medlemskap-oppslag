package no.nav.medlemskap

import io.cucumber.junit.Cucumber
import io.cucumber.junit.CucumberOptions
import org.junit.runner.RunWith

@RunWith(Cucumber::class)
@CucumberOptions(
        features = ["src/test/resources/dokumentasjon/features"],
        glue = ["no.nav.medlemskap.cucumber.steps"],
        plugin = ["pretty", "html:build/cucumber"],
        tags = "not @ignored",
        strict = true,
        monochrome = false
)
class RunCucumberTest
