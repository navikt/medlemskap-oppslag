import no.nav.medlemskap.clients.aareg.*
import no.nav.medlemskap.clients.ereg.*
import no.nav.medlemskap.cucumber.steps.aareg.aaregBuilder.AnsettelsesPeriodeBuilder
import no.nav.medlemskap.cucumber.steps.aareg.aaregBuilder.ArbeidsforholdBuilder
import no.nav.medlemskap.cucumber.steps.aareg.eregBuilder.OrganisasjonBuilder
import no.nav.medlemskap.services.aareg.ArbeidsforholdOrganisasjon
import java.time.LocalDate
import java.time.LocalDateTime

class AaregBuilder() {
    var organisasjonsBuilder = OrganisasjonBuilder()

    var arbeidsforhold = ArbeidsforholdBuilder().build()
    var organisasjon = organisasjonsBuilder.build()
    var juridiskEnhet = mutableListOf<Organisasjon>(organisasjonsBuilder.build())

    fun build(): List<ArbeidsforholdOrganisasjon> {
        return listOf(
                ArbeidsforholdOrganisasjon(
                        arbeidsforhold = arbeidsforhold,
                        organisasjon = organisasjon,
                        juridiskeEnheter = juridiskEnhet
                )
        )
    }

}


