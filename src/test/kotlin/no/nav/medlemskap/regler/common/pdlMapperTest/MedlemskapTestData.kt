package no.nav.medlemskap.regler.common.pdlMapperTest

import no.nav.medlemskap.domene.*

class MedlemskapTestData {

    private val pdlData = PdlTestData()

    fun medlemskapBostedAdresse() = Adresse(
        landkode = "NOR",
        fom = pdlData.gyldigFraOgMedBosted().toLocalDate(),
        tom = pdlData.gyldigFraOgMedBosted2().minusDays(1).toLocalDate()
    )

    fun medlemskapBostedsAdresse2() = Adresse(
        landkode = "NOR",
        fom = pdlData.gyldigFraOgMedBosted2().toLocalDate(),
        tom = null
    )

    fun medlemskapStatsborgerskapUtland() = Statsborgerskap(
        landkode = pdlData.statsborgerskapUtland(),
        fom = pdlData.fomStatsborgerskapUtland(),
        tom = pdlData.tomStatsborgerskapUtland()
    )

    fun medlemskapStatsborgerskap() = Statsborgerskap(
        landkode = pdlData.statsborgerskapNorge(),
        fom = pdlData.statsborgerskapNORGyldigFraOgMed(),
        tom = null
    )

    fun medlemskapFamilierelasjoner() = Familierelasjon(
        relatertPersonsIdent = pdlData.barnTilBrukerFnr(),
        relatertPersonsRolle = Familierelasjonsrolle.BARN,
        minRolleForPerson = Familierelasjonsrolle.FAR,
        folkeregistermetadata = medlemskapFolkeregisterMetadata()
    )

    fun medlemskapFolkeregisterMetadata() = Folkeregistermetadata(
        ajourholdstidspunkt = pdlData.ajourholdstidspunkt(),
        gyldighetstidspunkt = pdlData.sivilstandGyldighetstidspunkt(),
        opphoerstidspunkt = null
    )

    fun medlemskapKontaktAdresse() = Adresse(
        landkode = pdlData.utenlandskAdresse().landkode,
        fom = pdlData.kontaktadresseGyldigFraOgMed().toLocalDate(),
        tom = null
    )

    fun medlemskapOppholdsadresse() = Adresse(
        landkode = pdlData.utenlandskAdresse2().landkode,
        fom = pdlData.oppholdsadresseGyldigFraOgMed().toLocalDate(),
        tom = null
    )

    fun medlemskapOppholdsadresseNor() = Adresse(
        landkode = "NOR",
        fom = pdlData.oppholdsadresseGyldigFraOgMed().toLocalDate(),
        tom = null
    )

    fun medlemskapSivilstand() = Sivilstand(
        type = Sivilstandstype.GIFT,
        gyldigTilOgMed = null,
        gyldigFraOgMed = pdlData.sivilstandGyldigFraOgMed(),
        relatertVedSivilstand = pdlData.ektefelleTilBrukerFnr(),
        folkeregistermetadata = medlemskapFolkeregisterMetadata()
    )
}
