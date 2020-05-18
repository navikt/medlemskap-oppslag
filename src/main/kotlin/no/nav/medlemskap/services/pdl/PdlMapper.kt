package no.nav.medlemskap.services.pdl

import no.nav.medlemskap.common.exceptions.PersonIkkeFunnet
import no.nav.medlemskap.domene.*
import no.nav.medlemskap.domene.Familierelasjon
import no.nav.medlemskap.domene.Folkeregistermetadata
import no.nav.medlemskap.domene.Sivilstand
import no.nav.medlemskap.domene.Statsborgerskap

fun mapTilPersonHistorikk(person: HentPdlPersonResponse): Personhistorikk {
    val statsborgerskap: List<Statsborgerskap> = person.data?.hentPerson?.statsborgerskap?.map {
        Statsborgerskap(
                landkode = it.land,
                fom = it.gyldigFraOgMed,
                tom = it.gyldigTilOgMed
        )
    } ?: throw PersonIkkeFunnet("PDL")

    val personstatuser: List<Personstatus> = person.data.hentPerson.folkeregisterpersonstatus.map {
        Personstatus(
                personstatus = it.status,
                fom = it.folkeregistermetadata.gyldighetstidspunkt?.toLocalDate(),
                tom = it.folkeregistermetadata.opphoerstidspunkt?.toLocalDate()
        )
    }

    val bostedsadresser: List<Adresse> = person.data.hentPerson.bostedsadresse.map {
        Adresse(
                adresselinje = it.adresse ?: it.matrikkeladresse?.bruksenhetsnummer ?: "Ukjent adresse",
                landkode = "",
                fom = it.folkeregisterMetadata.gyldighetstidspunkt?.toLocalDate(),
                tom = it.folkeregisterMetadata.opphoerstidspunkt?.toLocalDate()
        )
    }

    val postadresser: List<Adresse> = emptyList()

    val midlertidigAdresser: List<Adresse> = emptyList()

    val sivilstand: List<Sivilstand> = person.data.hentPerson.sivilstand
            .filter { it.type == Sivilstandstype.GIFT || it.type == Sivilstandstype.REGISTRERT_PARTNER }
            .map {
        Sivilstand(
                type = mapSivilstandType(it.type),
                gyldigFraOgMed = it.gyldigFraOgMed,
                relatertVedSivilstand = it.relatertVedSivilstand,
                folkeregisterMetadata = mapFolkeregisterMetadata(it.folkeregisterMetadata)
        )
    }

    val familierelasjoner: List<Familierelasjon> = person.data.hentPerson.familierelasjoner.
    filter { it.relatertPersonsRolle == Familierelasjonsrolle.BARN }
            .map {
        Familierelasjon(
                relatertPersonIdent = it.relatertPersonIdent,
                relatertPersonsRolle = mapFamileRelasjonsrolle(it.relatertPersonsRolle),
                minRolleForPerson = mapFamileRelasjonsrolle(it.minRolleForPerson),
                folkeregisterMetadata = mapFolkeregisterMetadata(it.folkeregisterMetadata)
        )
    }

    return Personhistorikk(statsborgerskap, personstatuser, bostedsadresser, postadresser, midlertidigAdresser, sivilstand, familierelasjoner)
}

private fun mapFamileRelasjonsrolle(relatertPersonsRolle: Familierelasjonsrolle): no.nav.medlemskap.domene.Familierelasjonsrolle {
    return when (relatertPersonsRolle) {
        Familierelasjonsrolle.BARN -> no.nav.medlemskap.domene.Familierelasjonsrolle.BARN
        Familierelasjonsrolle.MOR -> no.nav.medlemskap.domene.Familierelasjonsrolle.MOR
        Familierelasjonsrolle.FAR -> no.nav.medlemskap.domene.Familierelasjonsrolle.FAR
        Familierelasjonsrolle.MEDMOR -> no.nav.medlemskap.domene.Familierelasjonsrolle.MEDMOR
    }
}

private fun mapSivilstandType(type: Sivilstandstype): no.nav.medlemskap.domene.Sivilstandstype {
    return when (type) {
        Sivilstandstype.UOPPGITT -> no.nav.medlemskap.domene.Sivilstandstype.UOPPGITT
        Sivilstandstype.UGIFT -> no.nav.medlemskap.domene.Sivilstandstype.UGIFT
        Sivilstandstype.GIFT -> no.nav.medlemskap.domene.Sivilstandstype.GIFT
        Sivilstandstype.ENKE_ELLER_ENKEMANN -> no.nav.medlemskap.domene.Sivilstandstype.ENKE_ELLER_ENKEMANN
        Sivilstandstype.SKILT -> no.nav.medlemskap.domene.Sivilstandstype.SKILT
        Sivilstandstype.SEPARERT -> no.nav.medlemskap.domene.Sivilstandstype.SEPARERT
        Sivilstandstype.REGISTRERT_PARTNER -> no.nav.medlemskap.domene.Sivilstandstype.REGISTRERT_PARTNER
        Sivilstandstype.SEPARERT_PARTNER -> no.nav.medlemskap.domene.Sivilstandstype.SEPARERT_PARTNER
        Sivilstandstype.SKILT_PARTNER -> no.nav.medlemskap.domene.Sivilstandstype.SKILT_PARTNER
        Sivilstandstype.GJENLEVENDE_PARTNER -> no.nav.medlemskap.domene.Sivilstandstype.GJENLEVENDE_PARTNER
    }
}

private fun mapFolkeregisterMetadata(folkeregisterMetadata: no.nav.medlemskap.services.pdl.Folkeregistermetadata): Folkeregistermetadata {
    return Folkeregistermetadata(
            ajourholdstidspunkt = folkeregisterMetadata.ajourholdstidspunkt,
            gyldighetstidspunkt = folkeregisterMetadata.gyldighetstidspunkt,
            opphoerstidspunkt = folkeregisterMetadata.opphoerstidspunkt,
            kilde = folkeregisterMetadata.kilde,
            aarsak = folkeregisterMetadata.aarsak,
            sekvens = folkeregisterMetadata.sekvens
    )
}
