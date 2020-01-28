package no.nav.medlemskap.services.tpsws

import io.mockk.every
import io.mockk.mockk
import no.nav.medlemskap.common.exceptions.PersonBleIkkeFunnet
import no.nav.medlemskap.common.exceptions.PersonHarSikkerhetsbegrensing
import no.nav.tjeneste.virksomhet.person.v3.binding.HentPersonhistorikkPersonIkkeFunnet
import no.nav.tjeneste.virksomhet.person.v3.binding.HentPersonhistorikkSikkerhetsbegrensning
import no.nav.tjeneste.virksomhet.person.v3.binding.PersonV3
import no.nav.tjeneste.virksomhet.person.v3.feil.PersonIkkeFunnet
import no.nav.tjeneste.virksomhet.person.v3.feil.Sikkerhetsbegrensning
import org.junit.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

class PersonServiceTest {

    @Test
    fun `personhistorikk skal få PersonBleIkkeFunnet hvis PersonV3 kaster ikke funnet feil`() {
        val personV3 = mockk<PersonV3>()

        every {
            personV3.hentPersonhistorikk(any())
        } throws (HentPersonhistorikkPersonIkkeFunnet("Ikke funnet", PersonIkkeFunnet()))

        val personService = PersonService(PersonClient(personV3))
        assertFailsWith<PersonBleIkkeFunnet> { personService.personhistorikk("123456") }
    }

    @Test
    fun `personhistorikk skal få Sikkerhetsbegrensing hvis PersonV2 kaster sikkerhetsbegrensing feil`() {
        val personV3 = mockk<PersonV3>()

        every {
            personV3.hentPersonhistorikk(any())
        } throws (HentPersonhistorikkSikkerhetsbegrensning("Sikkerhetsbegrensing", Sikkerhetsbegrensning()))

        val personService = PersonService(PersonClient(personV3))
        assertFailsWith<PersonHarSikkerhetsbegrensing>{ personService.personhistorikk("123456") }
    }

    @Test
    fun `personhistorikk leverer person med norsk bostedsadresse`() {
        val personV3 = mockk<PersonV3>()

        every {
            personV3.hentPersonhistorikk(any())
        } returns mockPersonMedBostedNorge()

        val personService = PersonService(PersonClient(personV3))
        val result = mapPersonhistorikkResultat(personService.personhistorikk("1234567"))

        assertEquals("NOR", result.bostedsadresser[0].landkode)
    }

}
