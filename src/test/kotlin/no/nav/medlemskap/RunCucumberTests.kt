package no.nav.medlemskap



import io.cucumber.junit.Cucumber
import io.cucumber.junit.CucumberOptions
import org.junit.jupiter.api.Test
import org.junit.runner.RunWith


@RunWith(Cucumber::class)
@CucumberOptions(
          features = ["src/test/resources/dokumentasjon/features/hovedregler/*.feature"],
          glue = ["no.nav.medlemskap.cucumber.steps"]
//          tags = ["not @ignored"]
)
class RunCucumberTests {

    @Test
    fun dummyTest() {
    }
}
