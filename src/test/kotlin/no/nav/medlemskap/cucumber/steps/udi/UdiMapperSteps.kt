package no.nav.medlemskap.cucumber.steps.udi

import io.cucumber.datatable.DataTable
import io.cucumber.java8.No
import io.kotest.matchers.shouldBe
import no.nav.medlemskap.cucumber.SpraakParserDomene.OppholdstillatelseDomeneSpraakParser
import no.nav.medlemskap.cucumber.mapping.udi.UdiDomeneSpraakParser
import no.nav.medlemskap.domene.Oppholdstillatelse
import no.nav.medlemskap.services.udi.UdiMapper
import no.udi.mt_1067_nav_data.v1.*

class UdiMapperSteps : No {
    private val udiDomenespraakParser = UdiDomeneSpraakParser()
    private var oppholdstillatelse: Oppholdstillatelse? = null
    private val hentPersonstatusResultatMedArbeidsadgang: HentPersonstatusResultat =
        HentPersonstatusResultat()
            .withArbeidsadgang(Arbeidsadgang())
    private val hentPersonstatusResultatMedOppholdsstatus: HentPersonstatusResultat =
        HentPersonstatusResultat().withGjeldendeOppholdsstatus(GjeldendeOppholdsstatus().withOppholdstillatelseEllerOppholdsPaSammeVilkar(OppholdstillatelseEllerOppholdsPaSammeVilkar()))

    init {
        Gitt<DataTable>("følgende harArbeidsadgang fra Arbeidsadgang") { dataTable: DataTable ->
            hentPersonstatusResultatMedArbeidsadgang.arbeidsadgang.harArbeidsadgang = udiDomenespraakParser.mapHarArbeidsadgang(dataTable)
        }

        Gitt<DataTable>(
            "følgende periode fra OppholdstillatelseEllerOppholdsPaSammeVilkar"
        ) { dataTable: DataTable ->
            hentPersonstatusResultatMedOppholdsstatus.gjeldendeOppholdsstatus.oppholdstillatelseEllerOppholdsPaSammeVilkar?.oppholdstillatelsePeriode = udiDomenespraakParser.mapPeriode(dataTable)
        }

        Gitt<DataTable>(
            "effektueringsdato fra OppholdstillatelseEllerOppholdsPaSammeVilkar"
        ) { dataTable: DataTable ->
            hentPersonstatusResultatMedOppholdsstatus.gjeldendeOppholdsstatus.oppholdstillatelseEllerOppholdsPaSammeVilkar?.effektueringsdato = udiDomenespraakParser.mapEffektueringsdato(dataTable)
        }

        Gitt<DataTable>(
            "følgende Oppholdstillatelse fra OppholdstillatelseEllerOppholdsPaSammeVilkar"
        ) { dataTable: DataTable ->
            hentPersonstatusResultatMedOppholdsstatus.gjeldendeOppholdsstatus.oppholdstillatelseEllerOppholdsPaSammeVilkar?.oppholdstillatelse = udiDomenespraakParser.mapOppholdstillatelse(dataTable)
        }

        Gitt<DataTable>("følgende uavklart fra EOSellerEFTAOpphold") { dataTable: DataTable ->
        }

        Gitt<DataTable>("foresporselsfodselsnummer fra HentPersonstatusResultat") { dataTable: DataTable ->
            hentPersonstatusResultatMedArbeidsadgang.foresporselsfodselsnummer = udiDomenespraakParser.mapforesporselfodselsnummer(dataTable)
            hentPersonstatusResultatMedOppholdsstatus.foresporselsfodselsnummer = udiDomenespraakParser.mapforesporselfodselsnummer(dataTable)
        }

        Gitt<DataTable>("følgende arbeidomfangKategori fra Arbeidsadgang") { dataTable: DataTable ->
            hentPersonstatusResultatMedArbeidsadgang.arbeidsadgang.arbeidsOmfang = udiDomenespraakParser.mapArbeidsomfang(dataTable)
        }

        Gitt<DataTable>("følgende arbeidsadgangType fra Arbeidsadgang") { dataTable: DataTable ->
            hentPersonstatusResultatMedArbeidsadgang.arbeidsadgang.typeArbeidsadgang = udiDomenespraakParser.mapArbeidsadgangType(dataTable)
        }

        Gitt<DataTable>("uttrekkstidspunkt fra HentPersonstatusResultat") { dataTable: DataTable ->
            hentPersonstatusResultatMedArbeidsadgang.uttrekkstidspunkt = udiDomenespraakParser.mapUttrekkstidspunkt(dataTable)
        }

        Gitt<DataTable>("følgende om periode i Arbeidsadgang") { dataTable: DataTable ->
            hentPersonstatusResultatMedArbeidsadgang.arbeidsadgang.arbeidsadgangsPeriode = udiDomenespraakParser.mapPeriode(dataTable)
        }

        Når("GjeldendeOppholdsstatus mappes") {
            oppholdstillatelse = mapTilOppholdstillatelse(hentPersonstatusResultatMedOppholdsstatus)
        }

        Når("arbeidsadgang mappes") {
            oppholdstillatelse = mapTilOppholdstillatelse(hentPersonstatusResultatMedArbeidsadgang)
        }

        Så<DataTable>("skal mappede harArbeidsgang i medlemskap være") { dataTable: DataTable ->
            val arbeidsadgangForventet = OppholdstillatelseDomeneSpraakParser.mapHarArbeidstilgang(dataTable)
            oppholdstillatelse?.arbeidsadgang?.harArbeidsadgang.shouldBe(arbeidsadgangForventet)
        }

        Så<DataTable>("foresporselsfodselsnummer i mappet Oppholdsstatus") { dataTable: DataTable ->
            val foresporselsfodselsnummerForventet = OppholdstillatelseDomeneSpraakParser.mapforesporselfodselsnummer(dataTable)
            oppholdstillatelse?.foresporselsfodselsnummer.shouldBe(foresporselsfodselsnummerForventet)
        }

        Så<DataTable>("uttrekkstidspunkt i mappet Oppholdsstatus") { dataTable: DataTable ->
            val uttrekkstidspunktDatoForventet = OppholdstillatelseDomeneSpraakParser.mapUttrekkstidspunkt(dataTable)
            oppholdstillatelse?.uttrekkstidspunkt.shouldBe(uttrekkstidspunktDatoForventet)
        }

        Så<DataTable>("skal mappede ArbeidomfangKategori i medlemskap være") { dataTable: DataTable ->
            val arbeidomfangKategoriForventet = OppholdstillatelseDomeneSpraakParser.mapArbeidsomfangKategori(dataTable)
            oppholdstillatelse?.arbeidsadgang?.arbeidsomfang.shouldBe(arbeidomfangKategoriForventet)
        }

        Så<DataTable>("skal mappede arbeidsadgangtype i medlemskap være") { dataTable: DataTable ->
            val arbeidsadgangTypeForventet = OppholdstillatelseDomeneSpraakParser.mapArbeidsadgangType(dataTable)
            oppholdstillatelse?.arbeidsadgang?.arbeidsadgangType.shouldBe(arbeidsadgangTypeForventet)
        }

        Så<DataTable>("mappet periode i medlemskap være") { dataTable: DataTable ->
            val periodeForventet = OppholdstillatelseDomeneSpraakParser.mapPeriode(dataTable)
            oppholdstillatelse?.arbeidsadgang?.periode.shouldBe(periodeForventet)
        }

        Så("skal mappede OppholdstillatelsePaSammeVilkar være") { dataTable: DataTable ->
            val forventetOppholdstillatelsePaSammeVilkar = OppholdstillatelseDomeneSpraakParser.mapOppholdstillatelsePaSammeVilkar(dataTable)
            oppholdstillatelse?.gjeldendeOppholdsstatus?.oppholdstillatelsePaSammeVilkar.shouldBe(forventetOppholdstillatelsePaSammeVilkar)
        }
    }

    private fun mapTilOppholdstillatelse(hentPersonstatusResultat: HentPersonstatusResultat): Oppholdstillatelse {
        return UdiMapper.mapTilOppholdstillatelse(hentPersonstatusResultat)
    }
}
