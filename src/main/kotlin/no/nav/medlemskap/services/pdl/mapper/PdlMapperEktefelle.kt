package no.nav.medlemskap.services.pdl.mapper

import no.bekk.bekkopen.person.FodselsnummerValidator
import no.nav.medlemskap.clients.pdl.generated.HentPerson
import no.nav.medlemskap.domene.ektefelle.PersonhistorikkEktefelle
import no.nav.medlemskap.domene.personhistorikk.Adresse
import no.nav.medlemskap.domene.personhistorikk.Familierelasjon.Companion.erBarnUnder25Aar
import no.nav.medlemskap.domene.personhistorikk.Statsborgerskap
import no.nav.medlemskap.services.pdl.mapper.PdlMapper.mapBostedsadresser
import no.nav.medlemskap.services.pdl.mapper.PdlMapper.mapKontaktadresser
import java.time.LocalDate

object PdlMapperEktefelle {

    fun mapPersonhistorikkTilEktefelle(fnr: String, ektefelle: HentPerson.Person, førsteDatoForYtelse: LocalDate): PersonhistorikkEktefelle {
        val barn = mapFnrBarnTilBrukersEktefelle(ektefelle.familierelasjoner, førsteDatoForYtelse)
        val statsborgerskap: List<Statsborgerskap> = PdlMapper.mapStatsborgerskap(ektefelle.statsborgerskap)
        val oppholdsadresse = PdlMapper.mapOppholdsadresser(ektefelle.oppholdsadresse)
        val bostedsadresser: List<Adresse> = mapBostedsadresser(ektefelle.bostedsadresse)
        val kontaktadresser: List<Adresse> = mapKontaktadresser(ektefelle.kontaktadresse)

        return PersonhistorikkEktefelle(
            ident = fnr,
            barn = barn,
            statsborgerskap = statsborgerskap,
            oppholdsadresser = oppholdsadresse,
            bostedsadresser = bostedsadresser,
            kontaktadresser = kontaktadresser
        )
    }

    private fun mapFnrBarnTilBrukersEktefelle(familierelasjoner: List<HentPerson.Familierelasjon>, førsteDatoForYtelse: LocalDate): List<String> {
        return familierelasjoner
            .filter { it.relatertPersonsRolle == HentPerson.Familierelasjonsrolle.BARN }
            .filter { FodselsnummerValidator.isValid(it.relatertPersonsIdent) }
            .filter { it.relatertPersonsIdent.erBarnUnder25Aar(førsteDatoForYtelse) }
            .map { it.relatertPersonsIdent }
    }
}
