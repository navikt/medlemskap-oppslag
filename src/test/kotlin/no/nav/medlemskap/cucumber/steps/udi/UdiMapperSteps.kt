package no.nav.medlemskap.cucumber.steps.udi

import io.cucumber.datatable.DataTable
import io.cucumber.java8.No
import io.kotest.matchers.shouldBe
import no.nav.medlemskap.cucumber.SpraakParserDomene.OppholdstillatelseDomeneSpraakParser
import no.nav.medlemskap.cucumber.mapping.udi.UdiDomeneSpraakParser
import no.nav.medlemskap.domene.Oppholdstillatelse
import no.nav.medlemskap.services.udi.UdiMapper
import no.udi.mt_1067_nav_data.v1.Arbeidsadgang
import no.udi.mt_1067_nav_data.v1.HentPersonstatusResultat

class UdiMapperSteps : No {
    private val udiDomenespraakParser = UdiDomeneSpraakParser()
    private val hentPersonstatusResultat: HentPersonstatusResultat = HentPersonstatusResultat().withArbeidsadgang(Arbeidsadgang())
    private var oppholdstillatelse: Oppholdstillatelse? = null

    init {
        Gitt<DataTable>("følgende harArbeidsadgang fra Arbeidsadgang") { dataTable: DataTable ->
            hentPersonstatusResultat.arbeidsadgang.harArbeidsadgang = udiDomenespraakParser.mapHarArbeidsadgang(dataTable)
        }

        Gitt<DataTable>("foresporselsfodselsnummer fra HentPersonstatusResultat") { dataTable: DataTable ->
            hentPersonstatusResultat.foresporselsfodselsnummer = udiDomenespraakParser.mapforesporselfodselsnummer(dataTable)
        }

        Gitt<DataTable>("følgende arbeidomfangKategori fra Arbeidsadgang") { dataTable: DataTable ->
            hentPersonstatusResultat.arbeidsadgang.arbeidsOmfang = udiDomenespraakParser.mapArbeidsomfang(dataTable)
        }

        Gitt<DataTable>("følgende arbeidsadgangType fra Arbeidsadgang") { dataTable: DataTable ->
            hentPersonstatusResultat.arbeidsadgang.typeArbeidsadgang = udiDomenespraakParser.mapArbeidsadgangType(dataTable)
        }

        Gitt<DataTable>("uttrekkstidspunkt fra HentPersonstatusResultat") { dataTable: DataTable ->
            hentPersonstatusResultat.uttrekkstidspunkt = udiDomenespraakParser.mapUttrekkstidspunkt(dataTable)
        }

        Gitt<DataTable>("følgende om periode i Arbeidsadgang") { dataTable: DataTable ->
            hentPersonstatusResultat.arbeidsadgang.arbeidsadgangsPeriode = udiDomenespraakParser.mapPeriode(dataTable)
        }

        Når("arbeidsadgang mappes") {
            oppholdstillatelse = mapTilOppholdstillatelse()
        }

        Så<DataTable>("skal mappede harArbeidsgang i medlemskap være") { dataTable: DataTable ->
            val arbeidsadgangForventet = OppholdstillatelseDomeneSpraakParser.mapArbeidstilgang(dataTable)
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
    }

    private fun mapTilOppholdstillatelse(): Oppholdstillatelse {
        return UdiMapper.mapTilOppholdstillatelse(hentPersonstatusResultat)
    }
}
