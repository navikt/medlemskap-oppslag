package no.nav.medlemskap.cucumber.steps.udi

import io.cucumber.datatable.DataTable
import io.cucumber.java8.No
import io.kotest.matchers.shouldBe
import no.nav.medlemskap.cucumber.SpraakParserDomene.OppholdstillatelseDomeneSpraakParser
import no.nav.medlemskap.cucumber.mapping.udi.UdiDomeneSpraakParser
import no.nav.medlemskap.domene.Oppholdstillatelse
import no.nav.medlemskap.services.udi.UdiMapper

class UdiMapperSteps : No {
    private val udiDomenespraakParser = UdiDomeneSpraakParser()
    private val udiArbeidsadgangBuilder = UdiArbeidsadgangBuilder()
    private val udiGjeldendeOppholdsstatus = UdiGjeldendeOppholdstatusBuilder()
    private val udiOppholdstillatelseBuilder = UdiOppholdstillatelseBuilder()
    private var oppholdstillatelse: Oppholdstillatelse? = null

    init {
        Gitt<DataTable>("følgende harArbeidsadgang fra Arbeidsadgang") { dataTable: DataTable? ->
            udiArbeidsadgangBuilder.harArbeidsadgang = udiDomenespraakParser.mapHarArbeidsadgang(dataTable)
        }

        Gitt<DataTable>(
            "følgende periode fra OppholdstillatelseEllerOppholdsPaSammeVilkar"
        ) { dataTable: DataTable? ->
            udiGjeldendeOppholdsstatus.oppholdstillatelseEllerOppholdsPaSammeVilkar?.oppholdstillatelsePeriode = udiDomenespraakParser.mapPeriode(dataTable)
        }

        Gitt<DataTable>(
            "effektueringsdato fra OppholdstillatelseEllerOppholdsPaSammeVilkar"
        ) { dataTable: DataTable? ->
            udiGjeldendeOppholdsstatus.oppholdstillatelseEllerOppholdsPaSammeVilkar?.effektueringsdato = udiDomenespraakParser.mapEffektueringsdato(dataTable)
        }

        Gitt<DataTable>(
            "følgende Oppholdstillatelse fra OppholdstillatelseEllerOppholdsPaSammeVilkar"
        ) { dataTable: DataTable? ->
            udiGjeldendeOppholdsstatus.oppholdstillatelseEllerOppholdsPaSammeVilkar?.oppholdstillatelse = udiDomenespraakParser.mapOppholdstillatelse(dataTable)
        }

        Gitt<DataTable>("følgende uavklart fra EOSellerEFTAOpphold") { dataTable: DataTable? ->
        }

        Gitt<DataTable>("foresporselsfodselsnummer fra HentPersonstatusResultat") { dataTable: DataTable? ->
            udiOppholdstillatelseBuilder.foresporselsfodselsnummer = udiDomenespraakParser.mapforesporselfodselsnummer(dataTable)
        }

        Gitt<DataTable>("følgende arbeidomfangKategori fra Arbeidsadgang") { dataTable: DataTable? ->
            udiArbeidsadgangBuilder.arbeidsOmfang = udiDomenespraakParser.mapArbeidsomfang(dataTable)
        }

        Gitt<DataTable>("følgende arbeidsadgangType fra Arbeidsadgang") { dataTable: DataTable? ->
            udiArbeidsadgangBuilder.typeArbeidsadgang = udiDomenespraakParser.mapArbeidsadgangType(dataTable)
        }

        Gitt<DataTable>("uttrekkstidspunkt fra HentPersonstatusResultat") { dataTable: DataTable? ->
            udiOppholdstillatelseBuilder.uttrekkstidspunkt = udiDomenespraakParser.mapUttrekkstidspunkt(dataTable)
        }

        Gitt<DataTable>("følgende om periode i Arbeidsadgang") { dataTable: DataTable? ->
            udiArbeidsadgangBuilder.arbeidsadgangsPeriode = udiDomenespraakParser.mapPeriode(dataTable)
        }

        Når("GjeldendeOppholdsstatus mappes") {
            oppholdstillatelse = mapTilOppholdstillatelse()
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

        Så<DataTable>("skal mappede ArbeidomfangKategori i medlemskap være") { dataTable: DataTable? ->
            val arbeidomfangKategoriForventet = OppholdstillatelseDomeneSpraakParser.mapArbeidsomfangKategori(dataTable)
            oppholdstillatelse?.arbeidsadgang?.arbeidsomfang.shouldBe(arbeidomfangKategoriForventet)
        }

        Så<DataTable>("skal mappede arbeidsadgangtype i medlemskap være") { dataTable: DataTable? ->
            val arbeidsadgangTypeForventet = OppholdstillatelseDomeneSpraakParser.mapArbeidsadgangType(dataTable)
            oppholdstillatelse?.arbeidsadgang?.arbeidsadgangType.shouldBe(arbeidsadgangTypeForventet)
        }

        Så<DataTable>("mappet periode i medlemskap være") { dataTable: DataTable? ->
            val periodeForventet = OppholdstillatelseDomeneSpraakParser.mapPeriode(dataTable)
            oppholdstillatelse?.arbeidsadgang?.periode.shouldBe(periodeForventet)
        }

        Så("skal mappede OppholdstillatelsePaSammeVilkar være") { dataTable: DataTable? ->
            val forventet = OppholdstillatelseDomeneSpraakParser.mapOppholdstillatelsePaSammeVilkar(dataTable)
        }
    }

    private fun mapTilOppholdstillatelse(): Oppholdstillatelse {
        udiOppholdstillatelseBuilder.gjeldendeOppholdsstatus = udiGjeldendeOppholdsstatus.build()
        udiOppholdstillatelseBuilder.arbeidsadgang = udiArbeidsadgangBuilder.build()
        return UdiMapper.mapTilOppholdstillatelser(udiOppholdstillatelseBuilder.build())
    }
}
