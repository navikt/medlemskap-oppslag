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
    private val hentResultatBuilder = HentResultatBuilder()
    private val hentPersonstatusResultatMedArbeidsadgang = hentResultatBuilder.hentPersonstatusResultatMedArbeidsadgang
    private val hentPersonstatusResultatMedOppholdsstatus = hentResultatBuilder.hentPersonstatusResultatMedOppholdsstatus
    private val hentPersonstatusResultatMedGjeldendeOppholdsstatusUavklart = hentResultatBuilder.hentPersonstatusResultatMedGjeldendeOppholdsstatusUavklart
    private val hentPersonstatusResultatIkkeOppholdstillatelseIkkeOppholdsPaSammeVilkarIkkeVisum = hentResultatBuilder.hentPersonstatusResultatIkkeOppholdstillatelseIkkeOppholdsPaSammeVilkarIkkeVisum
    private val hentPersonstatusResultatEOSellerEFTABeslutningOmOppholdsrett = hentResultatBuilder.hentPersonstatusResultatEOSellerEFTABeslutningOmOppholdsrett
    private val hentPersonstatusResultatEOSellerEFTAOppholdstillatelse = hentResultatBuilder.hentPersonstatusResultatEOSellerEFTAOppholdstillatelse
    private val hentPersonstatusResultatEOSellerEFTAVarigOppholdsrett = hentResultatBuilder.hentPersonstatusResultatEOSellerEFTAVarigOppholdsrett

    init {
        Gitt<DataTable>("følgende harArbeidsadgang fra Arbeidsadgang") { dataTable: DataTable ->
            hentPersonstatusResultatMedArbeidsadgang.arbeidsadgang.harArbeidsadgang = udiDomenespraakParser.mapHarArbeidsadgang(dataTable)
        }

        Gitt<DataTable>(
            "følgende periode fra OppholdstillatelseEllerOppholdsPaSammeVilkar"
        ) { dataTable: DataTable ->
            hentPersonstatusResultatMedOppholdsstatus.gjeldendeOppholdsstatus.oppholdstillatelseEllerOppholdsPaSammeVilkar?.oppholdstillatelsePeriode = udiDomenespraakParser.mapPeriode(dataTable)
        }

        Gitt<DataTable>("effektueringsdato fra OppholdstillatelseEllerOppholdsPaSammeVilkar") { dataTable: DataTable ->
            hentPersonstatusResultatMedOppholdsstatus.gjeldendeOppholdsstatus.oppholdstillatelseEllerOppholdsPaSammeVilkar?.effektueringsdato = udiDomenespraakParser.mapEffektueringsdato(dataTable)
        }

        Gitt<DataTable>("følgende Uavklart") { dataTable: DataTable ->
            hentPersonstatusResultatMedGjeldendeOppholdsstatusUavklart.gjeldendeOppholdsstatus.uavklart =
                udiDomenespraakParser.mapUavklart(dataTable)
        }

        Gitt<DataTable>("følgende Oppholdstillatelse fra OppholdstillatelseEllerOppholdsPaSammeVilkar") { dataTable: DataTable ->
            hentPersonstatusResultatMedOppholdsstatus.gjeldendeOppholdsstatus.oppholdstillatelseEllerOppholdsPaSammeVilkar?.oppholdstillatelse = udiDomenespraakParser.mapOppholdstillatelse(dataTable)
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

        Gitt<DataTable>("følgende JaNeiUavklart fra UtvistMedInnreiseForbud") { dataTable: DataTable ->
            hentPersonstatusResultatIkkeOppholdstillatelseIkkeOppholdsPaSammeVilkarIkkeVisum
                .gjeldendeOppholdsstatus
                .ikkeOppholdstillatelseIkkeOppholdsPaSammeVilkarIkkeVisum.utvistMedInnreiseForbud.innreiseForbud = udiDomenespraakParser.mapJaNeiUavklart(dataTable)
        }

        Gitt<DataTable>("avgjorelsesDato fra AvslagEllerBortfallAvPOBOSellerTilbakekallEllerFormeltVedtak") { dataTable: DataTable ->
            hentPersonstatusResultatIkkeOppholdstillatelseIkkeOppholdsPaSammeVilkarIkkeVisum
                .gjeldendeOppholdsstatus
                .ikkeOppholdstillatelseIkkeOppholdsPaSammeVilkarIkkeVisum
                .avslagEllerBortfallAvPOBOSellerTilbakekallEllerFormeltVedtak
                .avgjorelsesDato = udiDomenespraakParser.mapAvgjorelsesdato(dataTable)
        }

        Gitt<DataTable>("følgende OvrigIkkeOppholdsKategori fra OvrigIkkeOpphold") { dataTable: DataTable ->
            hentPersonstatusResultatIkkeOppholdstillatelseIkkeOppholdsPaSammeVilkarIkkeVisum.gjeldendeOppholdsstatus
                .ikkeOppholdstillatelseIkkeOppholdsPaSammeVilkarIkkeVisum
                .ovrigIkkeOpphold.arsak = udiDomenespraakParser.mapOvrigIkkeOpphold(dataTable)
        }
        Gitt<DataTable>("følgende periode fra EOSellerEFTABeslutningOmOppholdsrett fra EOSEllerEFTAOpphold med følgende type") { dataTable: DataTable ->
            hentPersonstatusResultatEOSellerEFTABeslutningOmOppholdsrett.gjeldendeOppholdsstatus.eoSellerEFTAOpphold.eoSellerEFTABeslutningOmOppholdsrett.oppholdsrettsPeriode =
                udiDomenespraakParser.mapPeriode(dataTable)
        }

        Gitt<DataTable>("følgende periode fra EOSellerEFTAOppholdstillatelse fra EOSEllerEFTAOpphold med følgende type") { dataTable: DataTable ->
            hentPersonstatusResultatEOSellerEFTAOppholdstillatelse.gjeldendeOppholdsstatus.eoSellerEFTAOpphold.eoSellerEFTAOppholdstillatelse.oppholdstillatelsePeriode =
                udiDomenespraakParser.mapPeriode(dataTable)
        }

        Gitt<DataTable>("følgende oppholdsrettsPeriode fra EOSellerEFTAVedtakOmVarigOppholdsrett fra EOSEllerEFTAOpphold med følgende type") { dataTable: DataTable ->
            hentPersonstatusResultatEOSellerEFTAVarigOppholdsrett.gjeldendeOppholdsstatus.eoSellerEFTAOpphold.eoSellerEFTAVedtakOmVarigOppholdsrett.oppholdsrettsPeriode =
                udiDomenespraakParser.mapPeriode(dataTable)
        }

        Gitt<DataTable>("følgende EOSellerEFTAGrunnlagskategoriOppholdsrett fra EOSellerEFTABeslutningOmOppholdsrett") { dataTable: DataTable ->
            hentPersonstatusResultatEOSellerEFTABeslutningOmOppholdsrett.gjeldendeOppholdsstatus.eoSellerEFTAOpphold.eoSellerEFTABeslutningOmOppholdsrett.eosOppholdsgrunnlag =
                udiDomenespraakParser.mapEOsOpppholdgrunnlagOppholdsrett(dataTable)
        }

        Gitt<DataTable>(
            "følgende EOSellerEFTAGrunnlagskategoriOppholdsrett fra EOSellerEFTAVedtakOmVarigOppholdsrett"
        ) { dataTable: DataTable ->
            hentPersonstatusResultatEOSellerEFTAVarigOppholdsrett.gjeldendeOppholdsstatus.eoSellerEFTAOpphold.eoSellerEFTAVedtakOmVarigOppholdsrett.eosOppholdsgrunnlag =
                udiDomenespraakParser.mapEOsOpppholdgrunnlagOppholdsrett(dataTable)
        }

        Gitt<DataTable>(
            "følgende EOSellerEFTAGrunnlagskategoriOppholdsrett fra EOSellerEFTAOppholdstillatelse"
        ) { dataTable: DataTable ->
            hentPersonstatusResultatEOSellerEFTAOppholdstillatelse.gjeldendeOppholdsstatus.eoSellerEFTAOpphold.eoSellerEFTAOppholdstillatelse.eosOppholdsgrunnlag =
                udiDomenespraakParser.mapEOsOpppholdgrunnlagOppholdstillatelse(dataTable)
        }

        Gitt<DataTable>("følgende OppholdPaSammeVilkar fra OppholdstillatelseEllerOppholdsPaSammeVilkar") { dataTable: DataTable ->
            hentPersonstatusResultatMedOppholdsstatus.gjeldendeOppholdsstatus.oppholdstillatelseEllerOppholdsPaSammeVilkar.oppholdPaSammeVilkar =
                udiDomenespraakParser.mapOppholdPaSammeVilkar(dataTable)
        }

        Gitt<DataTable>("følgende oppholdsrettsPeriode fra EOSellerEFTAVedtakOmVarigOppholdsrett  fra EOSEllerEFTAOpphold med følgende type") {
            oppholdstillatelse = mapTilOppholdstillatelse(hentPersonstatusResultatEOSellerEFTAVarigOppholdsrett)
        }

        Når("GjeldendeOppholdsstatus mappes") {
            oppholdstillatelse = mapTilOppholdstillatelse(hentPersonstatusResultatMedOppholdsstatus)
        }

        Når("arbeidsadgang mappes") {
            oppholdstillatelse = mapTilOppholdstillatelse(hentPersonstatusResultatMedArbeidsadgang)
        }

        Når("GjeldendeOppholdsstatus med IkkeOppholdstillatelseIkkeOppholdsPaSammeVilkarIkkeVisum mappes") {
            oppholdstillatelse = mapTilOppholdstillatelse(hentPersonstatusResultatIkkeOppholdstillatelseIkkeOppholdsPaSammeVilkarIkkeVisum)
        }

        Når("GjeldendeOppholdsstatus med EOSEllerEFTAOpphold med EOSellerEFTABeslutningOmOppholdsrett mappes") {
            oppholdstillatelse = mapTilOppholdstillatelse(hentPersonstatusResultatEOSellerEFTABeslutningOmOppholdsrett)
        }

        Når("GjeldendeOppholdsstatus med EOSEllerEFTAOpphold med EOSellerEFTAOppholdstillatelse mappes") {
            oppholdstillatelse = mapTilOppholdstillatelse(hentPersonstatusResultatEOSellerEFTAOppholdstillatelse)
        }

        Når("GjeldendeOppholdsstatus med EOSellerEFTAVedtakOmVarigOppholdsrett med EOSEllerEFTAOpphold mappes") {
            oppholdstillatelse = mapTilOppholdstillatelse(hentPersonstatusResultatEOSellerEFTAVarigOppholdsrett)
        }

        Når("Gjeldende opphold på samme vilkår") {
            oppholdstillatelse = mapTilOppholdstillatelse(hentPersonstatusResultatMedOppholdsstatus)
        }

        Så<DataTable>("skal mappede EOSEllerEFTAOpphold være") { dataTable: DataTable ->
            val forventetEOSEllerEFTAOpphold = OppholdstillatelseDomeneSpraakParser.mapEOSElllerEFTAOppholdKlasse(dataTable)
            oppholdstillatelse?.gjeldendeOppholdsstatus?.eosellerEFTAOpphold.shouldBe(forventetEOSEllerEFTAOpphold)
        }

        Så<DataTable>(
            "skal mappede oppholdstillatelse med EOS_ELLER_EFTA_BESLUTNING_OM_OPPHOLDSRETT"
        ) { dataTable: DataTable ->
            val forventetEOSEllerEFTAOpphold = OppholdstillatelseDomeneSpraakParser.mapEOSElllerEFTAOppholdKlasse(dataTable)
            oppholdstillatelse?.gjeldendeOppholdsstatus?.eosellerEFTAOpphold.shouldBe(forventetEOSEllerEFTAOpphold)
        }

        Når("GjeldendeOppholdsstatus med Uavklart mappes") {
            oppholdstillatelse = mapTilOppholdstillatelse(hentPersonstatusResultatMedGjeldendeOppholdsstatusUavklart)
        }

        Så<DataTable>("skal mappede UtvistMedInnreiseForbud i IkkeOppholdstillatelseIkkeOppholdsPaSammeVilkarIkkeVisum i Oppholdstillatelse være") { dataTable: DataTable ->
            val forventetInnreiseForbud = OppholdstillatelseDomeneSpraakParser.mapJaNeiUavklart(dataTable)
            oppholdstillatelse?.gjeldendeOppholdsstatus?.ikkeOppholdstillatelseIkkeOppholdsPaSammeVilkarIkkeVisum?.utvistMedInnreiseForbud?.innreiseForbud.shouldBe(
                forventetInnreiseForbud
            )
        }

        Så<DataTable>("skal mappede harArbeidsgang i medlemskap være") { dataTable: DataTable ->
            val arbeidsadgangForventet = OppholdstillatelseDomeneSpraakParser.mapHarArbeidstilgang(dataTable)
            oppholdstillatelse?.arbeidsadgang?.harArbeidsadgang.shouldBe(arbeidsadgangForventet)
        }

        Så<DataTable>("foresporselsfodselsnummer i mappet Oppholdsstatus") { dataTable: DataTable ->
            val foresporselsfodselsnummerForventet =
                OppholdstillatelseDomeneSpraakParser.mapforesporselfodselsnummer(dataTable)
            oppholdstillatelse?.foresporselsfodselsnummer.shouldBe(foresporselsfodselsnummerForventet)
        }

        Så<DataTable>("uttrekkstidspunkt i mappet Oppholdsstatus") { dataTable: DataTable ->
            val uttrekkstidspunktDatoForventet =
                OppholdstillatelseDomeneSpraakParser.mapUttrekkstidspunkt(dataTable)
            oppholdstillatelse?.uttrekkstidspunkt.shouldBe(uttrekkstidspunktDatoForventet)
        }

        Så<DataTable>("skal mappede ArbeidomfangKategori i medlemskap være") { dataTable: DataTable ->
            val arbeidomfangKategoriForventet =
                OppholdstillatelseDomeneSpraakParser.mapArbeidsomfangKategori(dataTable)
            oppholdstillatelse?.arbeidsadgang?.arbeidsomfang.shouldBe(arbeidomfangKategoriForventet)
        }

        Så<DataTable>("skal mappede arbeidsadgangtype i medlemskap være") { dataTable: DataTable ->
            val arbeidsadgangTypeForventet = OppholdstillatelseDomeneSpraakParser.mapArbeidsadgangType(dataTable)
            oppholdstillatelse?.arbeidsadgang?.arbeidsadgangType.shouldBe(arbeidsadgangTypeForventet)
        }

        Så<DataTable>(
            "mappede AvslagEllerBortfallAvPOBOSellerTilbakekallEllerFormeltVedtak i IkkeOppholdstillatelseIkkeOppholdsPaSammeVilkarIkkeVisum i Oppholdstillatelse være"
        ) { dataTable: DataTable ->
            val forventetAvgjorelsesDato = OppholdstillatelseDomeneSpraakParser.mapAvgjorelseDato(dataTable)
            oppholdstillatelse?.gjeldendeOppholdsstatus?.ikkeOppholdstillatelseIkkeOppholdsPaSammeVilkarIkkeVisum?.avslagEllerBortfallAvPOBOSellerTilbakekallEllerFormeltVedtak?.avgjorelsesDato.shouldBe(
                forventetAvgjorelsesDato
            )
        }

        Så<DataTable>("mappet periode i medlemskap være") { dataTable: DataTable ->
            val periodeForventet = OppholdstillatelseDomeneSpraakParser.mapPeriode(dataTable)
            oppholdstillatelse?.arbeidsadgang?.periode.shouldBe(periodeForventet)
        }

        Så<DataTable>("mappede OvrigIkkeOppholdsKategori i IkkeOppholdstillatelseIkkeOppholdsPaSammeVilkarIkkeVisum i Oppholdstillatelse være") { dataTable: DataTable ->
            val ovrigIkkeOppholdsKategoriForventet =
                OppholdstillatelseDomeneSpraakParser.mapOvrigIkkeOppholdsKategori(dataTable)
            oppholdstillatelse?.gjeldendeOppholdsstatus?.ikkeOppholdstillatelseIkkeOppholdsPaSammeVilkarIkkeVisum?.ovrigIkkeOpphold?.ovrigIkkeOppholdsKategori.shouldBe(
                ovrigIkkeOppholdsKategoriForventet
            )
        }

        Så("skal mappede OppholdstillatelsePaSammeVilkar være") { dataTable: DataTable ->
            val forventetOppholdstillatelsePaSammeVilkar = OppholdstillatelseDomeneSpraakParser.mapOppholdstillatelsePaSammeVilkar(dataTable)
            oppholdstillatelse?.gjeldendeOppholdsstatus?.oppholdstillatelsePaSammeVilkar.shouldBe(
                forventetOppholdstillatelsePaSammeVilkar
            )
        }

        Så<DataTable>("skal mappede Uavklart være") { dataTable: DataTable ->
            val forventetUavklart = OppholdstillatelseDomeneSpraakParser.mapUavklart(dataTable)
            oppholdstillatelse?.gjeldendeOppholdsstatus?.uavklart.shouldBe(forventetUavklart)
        }
    }

    private fun mapTilOppholdstillatelse(hentPersonstatusResultat: HentPersonstatusResultat): Oppholdstillatelse {
        return UdiMapper.mapTilOppholdstillatelse(hentPersonstatusResultat)
    }
}
