package no.nav.medlemskap.cucumber.steps.pdl

import io.cucumber.datatable.DataTable
import io.cucumber.java8.No
import io.kotest.matchers.collections.shouldContainExactly
import io.ktor.features.*
import no.nav.medlemskap.clients.pdl.generated.HentPerson
import no.nav.medlemskap.cucumber.SpraakParserDomene.PersonhistorikkDomeneSpraakParser
import no.nav.medlemskap.cucumber.mapping.pdl.PdlDomenespråkParser
import no.nav.medlemskap.domene.Personhistorikk
import no.nav.medlemskap.domene.barn.PersonhistorikkBarn
import no.nav.medlemskap.domene.ektefelle.PersonhistorikkEktefelle
import no.nav.medlemskap.services.pdl.mapper.PdlMapper
import no.nav.medlemskap.services.pdl.mapper.PdlMapperBarn
import no.nav.medlemskap.services.pdl.mapper.PdlMapperEktefelle
import java.time.LocalDate

class PdlMapperSteps : No {

    private val pdlDomenespråkParser = PdlDomenespråkParser()

    private var pdlPersonBuilder = PdlPersonBuilder()
    private var pdlPeronBuilderBarn = PdlPersonBuilder()
    private var pdlPersonBuilderEktefelle = PdlPersonBuilder()
    private var personhistorikk: Personhistorikk? = null
    private var personhistorikkEktefelle: PersonhistorikkEktefelle? = null
    private var personhistorikkBarn: PersonhistorikkBarn? = null

    init {
        Gitt<DataTable>("følgende statsborgerskap fra PDL:") { dataTable: DataTable? ->
            pdlPersonBuilder.statsborgerskap = pdlDomenespråkParser.mapStatsborgerskap(dataTable)
        }

        Gitt<DataTable>("følgende bostedsadresser fra PDL:") { dataTable: DataTable? ->
            pdlPersonBuilder.bostedsadresser = pdlDomenespråkParser.mapBostedsadresser(dataTable)
        }

        Gitt<DataTable>("følgende kontaktadresser fra PDL:") { dataTable: DataTable? ->
            pdlPersonBuilder.kontaktadresser = pdlDomenespråkParser.mapKontaktadresser(dataTable)
        }

        Gitt<DataTable>("følgende oppholdsadresser fra PDL:") { dataTable: DataTable? ->
            pdlPersonBuilder.oppholdsadresser = pdlDomenespråkParser.mapOppholdsadresser(dataTable)
        }

        Gitt<DataTable>("følgende sivilstander fra PDL:") { dataTable: DataTable? ->
            pdlPersonBuilder.sivilstander = pdlDomenespråkParser.mapSivilstander(dataTable)
        }

        Gitt<DataTable>("følgende familierelasjoner fra PDL:") { dataTable: DataTable? ->
            pdlPersonBuilder.familierelasjoner = pdlDomenespråkParser.mapFamilierelasjoner(dataTable)
        }

        Gitt<DataTable>("følgende opplysninger om doedsfall fra PDL:") { dataTable: DataTable? ->
            pdlPersonBuilder.doedsfall = pdlDomenespråkParser.mapDoedsfall(dataTable)
        }

        Gitt<DataTable>("følgende bostedsadresse i barnets personhistorikk") { dataTable: DataTable? ->
            pdlPeronBuilderBarn.bostedsadresser = pdlDomenespråkParser.mapBostedsadresser(dataTable)
        }

        Gitt<DataTable>("følgende bostedsadresse til ektefelles personhistorikk") { dataTable: DataTable? ->
            pdlPersonBuilderEktefelle.bostedsadresser = pdlDomenespråkParser.mapBostedsadresser(dataTable)
        }

        Når("statsborgerskap mappes") {
            personhistorikk = mapTilPersonhistorikk()
        }

        Når("bostedsadresser mappes") {
            personhistorikk = mapTilPersonhistorikk()
        }

        Når("kontaktadresser mappes") {
            personhistorikk = mapTilPersonhistorikk()
        }

        Når("oppholdsadresser mappes") {
            personhistorikk = mapTilPersonhistorikk()
        }

        Når("sivilstander mappes") {
            personhistorikk = mapTilPersonhistorikk()
        }

        Når("doedsfall mappes") {
            personhistorikk = mapTilPersonhistorikk()
        }

        Når("familierelasjoner mappes") {
            personhistorikk = mapTilPersonhistorikk()
        }

        Når("PDL Person mappes til personhistorikk i datagrunnlaget") {
            personhistorikk = mapTilPersonhistorikk()
        }

        Når<DataTable>("personhistorikken til bruker, ektefelle og barn mappes med følgende parametre") { dataTable: DataTable? ->
            val pdlParametre = pdlDomenespråkParser.mapPdlParametre(dataTable)

            personhistorikk = mapTilPersonhistorikk()
            personhistorikkBarn = mapTilPersonhistorikkBarn()
            personhistorikkEktefelle = mapTilPersonhistorikkEktefelle(pdlParametre.førsteDatoForYtelse)
        }

        Så<DataTable>("skal mappet statsborgerskap være") { dataTable: DataTable? ->
            val statsborgerskapForventet = PersonhistorikkDomeneSpraakParser.mapStatsborgerskap(dataTable)
            personhistorikk!!.statsborgerskap.shouldContainExactly(statsborgerskapForventet)
        }

        Så<DataTable>("skal mappede bostedsadresser være") { dataTable: DataTable? ->
            val bostedsadresserForventet = PersonhistorikkDomeneSpraakParser.mapAdresser(dataTable)
            personhistorikk!!.bostedsadresser.shouldContainExactly(bostedsadresserForventet)
        }

        Så<DataTable>("skal mappede kontaktadresser være") { dataTable: DataTable? ->
            val kontaktadresserForventet = PersonhistorikkDomeneSpraakParser.mapAdresser(dataTable)
            personhistorikk!!.kontaktadresser.shouldContainExactly(kontaktadresserForventet)
        }

        Så<DataTable>("skal mappede oppholdsadresser være") { dataTable: DataTable? ->
            val oppholdsadresserForventet = PersonhistorikkDomeneSpraakParser.mapAdresser(dataTable)
            personhistorikk!!.oppholdsadresser.shouldContainExactly(oppholdsadresserForventet)
        }

        Så<DataTable>("skal mappede sivilstander være") { dataTable: DataTable? ->
            val sivilstanderForventet = PersonhistorikkDomeneSpraakParser.mapSivilstander(dataTable)
            personhistorikk!!.sivilstand.shouldContainExactly(sivilstanderForventet)
        }

        Så<DataTable>("skal mappede familierelasjoner være") { dataTable: DataTable? ->
            val familierelasjonerForventet = PersonhistorikkDomeneSpraakParser.mapFamilierelasjoner(dataTable)
            personhistorikk!!.familierelasjoner.shouldContainExactly(familierelasjonerForventet)
        }

        Så<DataTable>("skal mappede doedsfall være") { dataTable: DataTable? ->
            val doedsfallForventet = PersonhistorikkDomeneSpraakParser.mapDoedsfall(dataTable)
            personhistorikk?.doedsfall.shouldContainExactly(doedsfallForventet)
        }

        Så<DataTable>("mappede bostedadresse til barnet være") { dataTable: DataTable? ->
            val bostedsadresseForventet = PersonhistorikkDomeneSpraakParser.mapAdresser(dataTable)
            personhistorikkBarn?.bostedsadresser.shouldContainExactly(bostedsadresseForventet)
        }

        Så<DataTable>("følgende bostedsadresse til ektefelle være") { dataTable: DataTable? ->
            val bostedsadresseForventet = PersonhistorikkDomeneSpraakParser.mapAdresser(dataTable)
            personhistorikkEktefelle?.bostedsadresser.shouldContainExactly(bostedsadresseForventet)
        }
    }

    private fun mapTilPersonhistorikk(): Personhistorikk {
        return PdlMapper.mapTilPersonHistorikkTilBruker(pdlPersonBuilder.build())
    }

    private fun mapTilPersonhistorikkBarn(): PersonhistorikkBarn {
        val identBarn = personhistorikk?.familierelasjoner?.get(0)?.relatertPersonsIdent
            ?: throw BadRequestException("Ingen fnr registrert på barn i familierelasjoner")
        return PdlMapperBarn.mapPersonhistorikkTilBarn(identBarn, pdlPeronBuilderBarn.build())
    }

    private fun mapTilPersonhistorikkEktefelle(førsteDatoForYtelse: LocalDate): PersonhistorikkEktefelle {
        val identEktefelle = personhistorikk?.sivilstand?.get(0)?.relatertVedSivilstand
            ?: throw BadRequestException("Ingen fnr registrert på ektefelle i sivilstand")
        return PdlMapperEktefelle.mapPersonhistorikkTilEktefelle(identEktefelle, pdlPersonBuilderEktefelle.build(), førsteDatoForYtelse)
    }

    class PdlPersonBuilder {
        var statsborgerskap: List<HentPerson.Statsborgerskap> = emptyList()
        var bostedsadresser: List<HentPerson.Bostedsadresse> = emptyList()
        var kontaktadresser: List<HentPerson.Kontaktadresse> = emptyList()
        var oppholdsadresser: List<HentPerson.Oppholdsadresse> = emptyList()
        var sivilstander: List<HentPerson.Sivilstand> = emptyList()
        var familierelasjoner: List<HentPerson.Familierelasjon> = emptyList()
        var doedsfall: List<HentPerson.Doedsfall> = emptyList()

        fun build(): HentPerson.Person {
            return HentPerson.Person(
                familierelasjoner = familierelasjoner,
                statsborgerskap = statsborgerskap,
                sivilstand = sivilstander,
                bostedsadresse = bostedsadresser,
                kontaktadresse = kontaktadresser,
                oppholdsadresse = oppholdsadresser,
                doedsfall = doedsfall
            )
        }
    }
}
