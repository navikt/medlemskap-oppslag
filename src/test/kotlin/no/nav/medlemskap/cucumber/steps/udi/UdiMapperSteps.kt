package no.nav.medlemskap.cucumber.steps.udi

import io.cucumber.datatable.DataTable
import io.cucumber.java8.No
import io.kotest.matchers.shouldBe
import no.nav.medlemskap.cucumber.SpraakParserDomene.OppholdstillatelseDomeneSpraakParser
import no.nav.medlemskap.cucumber.mapping.udi.UdiDomeneSpraakParser
import no.nav.medlemskap.domene.Arbeidsadgang
import no.nav.medlemskap.domene.Oppholdstillatelse
import no.nav.medlemskap.services.udi.UdiMapper

class UdiMapperSteps : No {
    private val udiDomenespraakParser = UdiDomeneSpraakParser()
    private val udiArbeidsadgangBuilder = UdiArbeidsadgangBuilder()
    private val udiOppholdstillatelseBuilder = UdiOppholdstillatelseBuilder()
    private var arbeidsadgang: Arbeidsadgang? = null
    private var oppholdstillatelse: Oppholdstillatelse? = null

    init {
        Gitt<DataTable>("følgende harArbeidsadgang fra Arbeidsadgang") { dataTable: DataTable? ->
            udiArbeidsadgangBuilder.harArbeidsadgang = udiDomenespraakParser.mapHarArbeidsadgang(dataTable)
        }

        Gitt<DataTable>("foresporselsfodselsnummer fra HentPersonstatusResultat") { dataTable: DataTable? ->
            udiOppholdstillatelseBuilder.foresporselsfodselsnummer = udiDomenespraakParser.mapforesporselfodselsnummer(dataTable)
        }

        Gitt<DataTable>("uttrekkstidspunkt fra HentPersonstatusResultat") { dataTable: DataTable? ->
            udiOppholdstillatelseBuilder.uttrekkstidspunkt = udiDomenespraakParser.mapUttrekkstidspunkt(dataTable)
        }

        Når("arbeidsadgang mappes") {
            oppholdstillatelse = mapTilOppholdstillatelse()
        }

        Så<DataTable>("skal mappede harArbeidsgang i medlemskap være") { dataTable: DataTable? ->
            val arbeidsadgangForventet = OppholdstillatelseDomeneSpraakParser.mapArbeidstilgang(dataTable)
            oppholdstillatelse?.arbeidsadgang?.harArbeidsadgang.shouldBe(arbeidsadgangForventet)
        }

        Så<DataTable>("foresporselsfodselsnummer i mappet Oppholdsstatus") { dataTable: DataTable? ->
            val foresporselsfodselsnummerForventet = OppholdstillatelseDomeneSpraakParser.mapforesporselfodselsnummer(dataTable)
            oppholdstillatelse?.foresporselsfodselsnummer.shouldBe(foresporselsfodselsnummerForventet)
        }

        Så<DataTable>("uttrekkstidspunkt i mappet Oppholdsstatus") { dataTable: DataTable? ->
            val uttrekkstidspunktDatoForventet = OppholdstillatelseDomeneSpraakParser.mapUttrekkstidspunkt(dataTable)
            oppholdstillatelse?.uttrekkstidspunkt.shouldBe(uttrekkstidspunktDatoForventet)
        }
    }

    private fun mapTilOppholdstillatelse(): Oppholdstillatelse {

        udiOppholdstillatelseBuilder.arbeidsadgang = udiArbeidsadgangBuilder.build()
        val oppholdstillatelse = UdiMapper.mapTilOppholdstillatelser(udiOppholdstillatelseBuilder.build())
        println(oppholdstillatelse.arbeidsadgang.harArbeidsadgang)
        return oppholdstillatelse
    }
}
