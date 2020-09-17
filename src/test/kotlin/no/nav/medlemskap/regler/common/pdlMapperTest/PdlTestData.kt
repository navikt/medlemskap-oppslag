package no.nav.medlemskap.regler.common.pdlMapperTest

import no.nav.medlemskap.clients.pdl.generated.HentPerson
import java.time.LocalDate
import java.time.LocalDateTime

class PdlTestData {

    fun statsborgerskapNorge() = "NOR"
    fun statsborgerskapUtland() = "SWE"
    fun fomStatsborgerskapUtland() = LocalDate.of(1976, 7, 15)
    fun tomStatsborgerskapUtland() = LocalDate.of(1989, 12, 31)
    fun statsborgerskapNORGyldigFraOgMed() = LocalDate.of(1990, 1, 1)

    fun sivilstandGyldigFraOgMed() = LocalDate.of(1990, 10, 10)
    fun sivilstandGyldighetstidspunkt() = LocalDateTime.of(1990, 10, 10, 1, 1)

    fun familierelasjonerGyldighetstidspunkt() = LocalDateTime.of(1990, 10, 10, 1, 1)

    fun angittFlyttedatoBosted() = LocalDate.of(1990, 1, 1)
    fun angittFlyttedatoBosted2() = LocalDate.of(1995, 1, 1)
    fun gyldigFraOgMedBosted() = LocalDateTime.of(1990, 1, 1, 1, 1)
    fun gyldigTilOgMedBosted() = LocalDateTime.of(1995, 1, 1, 1, 1)
    fun gyldigFraOgMedBosted2() = LocalDateTime.of(1995, 1, 2, 1, 1)

    fun coAdressenavn() = "Donald Duck"
    fun vegadresse() = HentPerson.Vegadresse(postnummer = "8072")
    fun matrikkeladresse() = HentPerson.Matrikkeladresse(postnummer = "8072")
    fun ukjentBosted() = HentPerson.UkjentBosted("Bodo kommune")

    fun oppholdsadresseGyldigFraOgMed() = LocalDateTime.of(1991, 8, 1, 1, 1)
    fun kontaktadresseGyldigFraOgMed() = LocalDateTime.of(2010, 9, 2, 1, 1)

    fun registrertDato() = LocalDate.of(2020, 8, 1)

    fun ajourholdstidspunkt() = LocalDateTime.of(2020, 6, 20, 10, 0)
    fun gyldighetstidspunkt() = LocalDateTime.of(1990, 10, 10, 1, 1)
    fun opphoerstidspunkt() = null

    fun systemkilde() = "FREG"
    fun kilde() = "NAV"
    fun registrertAv() = "Z990200"
    fun opplysningsId() = "1234"
    fun master() = "FREG"

    fun oppholdAnnetSted() = "PENDLER"
    fun utenlandskadresseIFrittFormat() = HentPerson.UtenlandskAdresseIFrittFormat(landkode = "SWE")
    fun utenlandskAdresse2() = HentPerson.UtenlandskAdresse2(landkode = "SWE")
    fun utenlandskAdresse() = HentPerson.UtenlandskAdresse(landkode = "BEL")

    fun pdlFamilieRelasjon() = HentPerson.Familierelasjon(
        relatertPersonsIdent = barnTilBrukerFnr(),
        relatertPersonsRolle = HentPerson.Familierelasjonsrolle.BARN,
        minRolleForPerson = HentPerson.Familierelasjonsrolle.FAR,
        folkeregistermetadata = folkeregistermetadata(null)
    )

    fun pdlStatsborgerskapUtland() = HentPerson.Statsborgerskap(
        land = statsborgerskapUtland(),
        gyldigFraOgMed = fomStatsborgerskapUtland().toString(),
        gyldigTilOgMed = tomStatsborgerskapUtland().toString()
    )

    fun pdlStatsborgerskapNorge() = HentPerson.Statsborgerskap(
        land = statsborgerskapNorge(),
        gyldigFraOgMed = statsborgerskapNORGyldigFraOgMed().toString(),
        gyldigTilOgMed = null
    )

    // Finnes tom og fom i Bostedsdataene --> BRUKE DEM?
    fun pdlBostedadresse() = HentPerson.Bostedsadresse(
        angittFlyttedato = angittFlyttedatoBosted().toString(),
        coAdressenavn = coAdressenavn(),
        vegadresse = vegadresse(),
        matrikkeladresse = matrikkeladresse(),
        ukjentBosted = ukjentBosted(),
        folkeregistermetadata = folkeregistermetadata2(
            gyldighetstidspunkt = gyldigFraOgMedBosted().toString(),
            opphoerstidspunkt = gyldigFraOgMedBosted2().minusDays(1).toString()
        ),
        metadata = metadata()
    )

    fun pdlBostedadresse2() = HentPerson.Bostedsadresse(
        angittFlyttedato = angittFlyttedatoBosted().toString(),
        coAdressenavn = coAdressenavn(),
        vegadresse = vegadresse(),
        matrikkeladresse = matrikkeladresse(),
        ukjentBosted = ukjentBosted(),
        folkeregistermetadata = folkeregistermetadata2(
            gyldighetstidspunkt = gyldigFraOgMedBosted2().toString(),
            opphoerstidspunkt = null
        ),
        metadata = metadata()
    )

    fun pdlOppadholdsadresseUtenlandskErNull() = HentPerson.Oppholdsadresse(
        coAdressenavn = coAdressenavn(),
        gyldigFraOgMed = oppholdsadresseGyldigFraOgMed().toString(),
        folkeregistermetadata = folkeregistermetadata2(
            gyldighetstidspunkt = oppholdsadresseGyldigFraOgMed().toString(),
            opphoerstidspunkt = null
        ),
        utenlandskAdresse = null,
        oppholdAnnetSted = null,
        metadata = metadata2()

    )
    fun pdlOppholdsadresse() = HentPerson.Oppholdsadresse(
        coAdressenavn = coAdressenavn(),
        gyldigFraOgMed = oppholdsadresseGyldigFraOgMed().toString(),
        folkeregistermetadata = folkeregistermetadata2(
            gyldighetstidspunkt = oppholdsadresseGyldigFraOgMed().toString(),
            opphoerstidspunkt = null
        ),
        utenlandskAdresse = utenlandskAdresse2(),
        oppholdAnnetSted = null,
        metadata = metadata2()
    )

    fun pdlKontaktadresse() = HentPerson.Kontaktadresse(
        gyldigFraOgMed = kontaktadresseGyldigFraOgMed().toString(),
        gyldigTilOgMed = null,
        coAdressenavn = coAdressenavn(),
        type = HentPerson.KontaktadresseType.Utland,
        utenlandskAdresse = utenlandskAdresse(),
        folkeregistermetadata = folkeregistermetadata2(
            gyldighetstidspunkt = kontaktadresseGyldigFraOgMed().toString(),
            opphoerstidspunkt = null
        ),
        metadata = metadata2(),
        utenlandskAdresseIFrittFormat = null

    )

    fun pdlSivilstand() = HentPerson.Sivilstand(
        type = HentPerson.Sivilstandstype.GIFT,
        gyldigFraOgMed = sivilstandGyldigFraOgMed().toString(),
        relatertVedSivilstand = ektefelleTilBrukerFnr(),
        folkeregistermetadata = folkeregistermetadata2(
            gyldighetstidspunkt = sivilstandGyldighetstidspunkt().toString(),
            opphoerstidspunkt = null
        )

    )

    fun brukerFnr() = "15076500565"
    fun ektefelleTilBrukerFnr() = "10108000398"
    fun barnTilBrukerFnr() = "09069534888"

    fun folkeregistermetadata(opphoerstidspunkt: String?) = HentPerson.Folkeregistermetadata(
        ajourholdstidspunkt = ajourholdstidspunkt().toString(),
        gyldighetstidspunkt = gyldighetstidspunkt().toString(),
        opphoerstidspunkt = opphoerstidspunkt
    )

    fun folkeregistermetadata2(gyldighetstidspunkt: String?, opphoerstidspunkt: String?) = HentPerson.Folkeregistermetadata2(
        ajourholdstidspunkt = ajourholdstidspunkt().toString(),
        gyldighetstidspunkt = gyldighetstidspunkt,
        opphoerstidspunkt = opphoerstidspunkt,
        aarsak = null,
        kilde = null,
        sekvens = null
    )

    private fun endring() = HentPerson.Endring(
        type = HentPerson.Endringstype.OPPRETT,
        registrert = registrertDato().toString(),
        registrertAv = registrertAv(),
        systemkilde = systemkilde(),
        kilde = kilde()
    )
    private fun endring2() = HentPerson.Endring2(
        type = HentPerson.Endringstype.OPPRETT,
        registrert = registrertDato().toString(),
        registrertAv = registrertAv(),
        systemkilde = systemkilde(),
        kilde = kilde()
    )
    private fun endringer() = listOf(endring())

    private fun metadata() = HentPerson.Metadata(
        opplysningsId = opplysningsId(),
        master = master(),
        endringer = endringer(),
        historisk = true
    )

    private fun metadata2() = HentPerson.Metadata2(
        opplysningsId = opplysningsId(),
        master = master(),
        endringer = listOf(endring2()),
        historisk = true
    )

    fun pdlPersonBruker() = HentPerson.Person(
        statsborgerskap = listOf(pdlStatsborgerskapUtland(), pdlStatsborgerskapNorge()),
        sivilstand = listOf(pdlSivilstand()),
        familierelasjoner = listOf(pdlFamilieRelasjon()),
        bostedsadresse = listOf(pdlBostedadresse(), pdlBostedadresse2()),
        oppholdsadresse = listOf(pdlOppholdsadresse(), pdlOppadholdsadresseUtenlandskErNull()),
        kontaktadresse = listOf(pdlKontaktadresse())
    )
}
