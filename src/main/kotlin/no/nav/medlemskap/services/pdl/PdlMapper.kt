package no.nav.medlemskap.services.pdl

import no.nav.medlemskap.common.exceptions.DetteSkalAldriSkje
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

    val personstatuser: List<FolkeregisterPersonstatus> = emptyList()

    val bostedsadresser: List<Adresse> = person.data.hentPerson.bostedsadresse.map {
        Adresse(
                landkode = "NOR",
                fom = it.folkeregistermetadata.gyldighetstidspunkt?.toLocalDate(),
                tom = it.folkeregistermetadata.opphoerstidspunkt?.toLocalDate()
        )
    }

    val postadresser: List<Adresse> = emptyList()

    val midlertidigAdresser: List<Adresse> = emptyList()

    val sivilstand: List<Sivilstand> = person.data.hentPerson.sivilstand
            .filter { it.type != Sivilstandstype.UGIFT && it.type != Sivilstandstype.UOPPGITT }
            .map {
                Sivilstand(
                        type = mapSivilstandType(it.type),
                        gyldigFraOgMed = it.gyldigFraOgMed,
                        relatertVedSivilstand = it.relatertVedSivilstand!!,
                        folkeregisterMetadata = mapFolkeregisterMetadata(it.folkeregisterMetadata)
                )
            }

    val familierelasjoner: List<Familierelasjon> = person.data.hentPerson.familierelasjoner.filter { it.relatertPersonsRolle == Familierelasjonsrolle.BARN }
            .map {
                Familierelasjon(
                        relatertPersonsIdent = it.relatertPersonsIdent,
                        relatertPersonsRolle = mapFamileRelasjonsrolle(it.relatertPersonsRolle)!!,
                        minRolleForPerson = mapFamileRelasjonsrolle(it.minRolleForPerson),
                        folkeregisterMetadata = mapFolkeregisterMetadata(it.folkeregisterMetadata)
                )
            }

    return Personhistorikk(statsborgerskap, personstatuser, bostedsadresser, postadresser, midlertidigAdresser, sivilstand, familierelasjoner)
}

private fun mapFamileRelasjonsrolle(rolle: Familierelasjonsrolle?): no.nav.medlemskap.domene.Familierelasjonsrolle? {
    return rolle?.let {
        when (it) {
            Familierelasjonsrolle.BARN -> no.nav.medlemskap.domene.Familierelasjonsrolle.BARN
            Familierelasjonsrolle.MOR -> no.nav.medlemskap.domene.Familierelasjonsrolle.MOR
            Familierelasjonsrolle.FAR -> no.nav.medlemskap.domene.Familierelasjonsrolle.FAR
            Familierelasjonsrolle.MEDMOR -> no.nav.medlemskap.domene.Familierelasjonsrolle.MEDMOR
        }
    }
}

private fun mapSivilstandType(type: Sivilstandstype): no.nav.medlemskap.domene.Sivilstandstype {
    return when (type) {
        Sivilstandstype.GIFT -> no.nav.medlemskap.domene.Sivilstandstype.GIFT
        Sivilstandstype.ENKE_ELLER_ENKEMANN -> no.nav.medlemskap.domene.Sivilstandstype.ENKE_ELLER_ENKEMANN
        Sivilstandstype.SKILT -> no.nav.medlemskap.domene.Sivilstandstype.SKILT
        Sivilstandstype.SEPARERT -> no.nav.medlemskap.domene.Sivilstandstype.SEPARERT
        Sivilstandstype.REGISTRERT_PARTNER -> no.nav.medlemskap.domene.Sivilstandstype.REGISTRERT_PARTNER
        Sivilstandstype.SEPARERT_PARTNER -> no.nav.medlemskap.domene.Sivilstandstype.SEPARERT_PARTNER
        Sivilstandstype.SKILT_PARTNER -> no.nav.medlemskap.domene.Sivilstandstype.SKILT_PARTNER
        Sivilstandstype.GJENLEVENDE_PARTNER -> no.nav.medlemskap.domene.Sivilstandstype.GJENLEVENDE_PARTNER
        else -> throw DetteSkalAldriSkje("Denne sivilstandstypen skal være filtrert bort")
    }
}

private fun mapFolkeregisterMetadata(folkeregisterMetadata: no.nav.medlemskap.services.pdl.Folkeregistermetadata?): Folkeregistermetadata? {
    return folkeregisterMetadata?.let {
        Folkeregistermetadata(
                ajourholdstidspunkt = it.ajourholdstidspunkt,
                gyldighetstidspunkt = it.gyldighetstidspunkt,
                opphoerstidspunkt = it.opphoerstidspunkt
        )
    }
}

//Vi velger det høyeste årstallet, da blir personen yngst og det er mest sannsynlig at vi må vurdere bosted
fun mapTilFoedselsaar(response: HentFoedselsaarResponse): Int =
        response.data?.hentPerson?.foedsel?.map { it.foedselsaar }?.sorted()?.last() ?: throw PersonIkkeFunnet("PDL")
