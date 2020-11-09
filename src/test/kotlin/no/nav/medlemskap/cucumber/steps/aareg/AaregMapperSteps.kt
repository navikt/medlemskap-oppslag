package no.nav.medlemskap.cucumber.steps.aareg
import AaregBuilder
import io.cucumber.datatable.DataTable
import io.cucumber.java8.No
import io.kotest.matchers.collections.shouldContainExactly
import io.kotest.matchers.shouldBe
import no.nav.medlemskap.clients.aareg.*
import no.nav.medlemskap.clients.ereg.*
import no.nav.medlemskap.cucumber.DomenespråkParser
import no.nav.medlemskap.cucumber.mapping.pdl.aareg.AaregDomenespraakParser
import no.nav.medlemskap.cucumber.steps.aareg.aaregBuilder.AaregOpplysningspliktigArbeidsgiverBuilder
import no.nav.medlemskap.cucumber.steps.aareg.aaregBuilder.AaregPeriodeBuilder
import no.nav.medlemskap.domene.Arbeidsforhold
import no.nav.medlemskap.services.aareg.mapArbeidsforhold

class AaregMapperSteps : No {
    private val aaregDomenespraakParser = AaregDomenespraakParser()
    private var aaregBuilder = AaregBuilder()
    private var arbeidsforhold = listOf<Arbeidsforhold>()
    private var aaregPeriodeBuilder = AaregPeriodeBuilder()
    private var aaregArbeidsgiverBuilder = AaregOpplysningspliktigArbeidsgiverBuilder()

    init {
        Gitt<DataTable>("følgende om AaRegPeriode i fra AaRegArbeidsforhold.AaRegAnsettelsesperiode") { dataTable: DataTable? ->
            aaregPeriodeBuilder.fom = aaregDomenespraakParser.mapPeriode(dataTable).fom
            aaregPeriodeBuilder.tom = aaregDomenespraakParser.mapPeriode(dataTable).tom
        }

        Gitt<DataTable>("følgende om arbeidgivertype fra AaRegArbeidsforhold.AaRegOpplysningspliktigArbeidsgiver") { dataTable: DataTable? ->
            aaregBuilder.arbeidsforhold.arbeidsgiver.type = aaregDomenespraakParser.mapArbeidsgiverType(dataTable)
        }

        Gitt<DataTable>("følgende om type fra AaRegArbeidsforhold") { dataTable: DataTable? ->
            aaregBuilder.arbeidsforhold.type = aaregDomenespraakParser.mapArbeidsforholdsType(dataTable)
        }

        Gitt<DataTable>("følgende om landkode i AaRegArbeidsforhold.AaRegUtenlandsopphold") { dataTable: DataTable? ->
            aaregBuilder.arbeidsforhold.utenlandsopphold?.get(0)?.landkode = aaregDomenespraakParser.mapLandkode(dataTable)
        }

        Gitt<DataTable>("følgende om Organisasjon.organisasjonsdetaljer.enhetstyper.enhetstype fra ereg") { dataTable: DataTable? ->
            aaregBuilder.first().organisasjon.organisasjonDetaljer?.enhetstyper?.first()?.enhetstype = aaregDomenespraakParser.mapEnhetstype(dataTable)
        }

        Gitt<DataTable>("følgende om Organiasjon.organisasjonsnummer fra ereg") { dataTable: DataTable? ->
            aaregBuilder.first().organisasjon.organisasjonsnummer = aaregDomenespraakParser.mapOrganisasjonsnummer(dataTable)
        }

        Gitt<DataTable>("følgende om ansatte i Organiasjon.Organisasjonsdetaljer.Ansatte fra ereg") { dataTable: DataTable? ->
            aaregBuilder.first().organisasjon.organisasjonDetaljer?.ansatte = aaregDomenespraakParser.mapAntallAnsatte(dataTable)
        }

        Gitt<DataTable>("følgende om konkursstatus organisasjon.organisasjonDetaljer.statuser") { dataTable: DataTable? ->
            aaregBuilder.first().organisasjon.organisasjonDetaljer?.statuser = aaregDomenespraakParser.mapStatuser(dataTable)
        }

        Gitt<DataTable>("følgende om AaRegPeriode i fra AaRegArbeidsavtale AaRegBruksperiode") { dataTable: DataTable? ->
            aaregBuilder.first().arbeidsforhold.arbeidsavtaler[0].bruksperiode = aaregDomenespraakParser.mapBruksPeriode(dataTable)
        }

        Gitt<DataTable>("følgende om gyldighetsperiode fra AaRegArbeidsavtale AaRegGyldighetsperiode") { dataTable: DataTable? ->
            aaregBuilder.first().arbeidsforhold.arbeidsavtaler[0].gyldighetsperiode = aaregDomenespraakParser.mapGyldighetsPeriode(dataTable)
        }

        Gitt<DataTable>("følgende om yrke fra AaRegArbeidsavtale") { dataTable: DataTable? ->
            aaregBuilder.first().arbeidsforhold.arbeidsavtaler.arbeidsavtaler[0].yrke = aaregDomenespraakParser.mapYrkeskode(dataTable)
        }

        Gitt<DataTable>("følgende om skipsregister fra AaRegArbeidsavtale") { dataTable: DataTable? ->
            aaregBuilder.first().arbeidsforhold.arbeidsavtaler[0].skipsregister = aaregDomenespraakParser.mapSkipsregister(dataTable)
        }

        Gitt<DataTable>("følgende om stillingsprosent fra AaRegArbeidsavtale") { dataTable: DataTable? ->
            aaregBuilder.first().arbeidsforhold.arbeidsavtaler[0].stillingsprosent = aaregDomenespraakParser.mapStillingsprosent(dataTable)
        }

        Gitt<DataTable>("følgende om beregnetAntallTimerPrUke") { dataTable: DataTable? ->
            aaregBuilder.first().arbeidsforhold.arbeidsavtaler[0].beregnetAntallTimerPrUke = aaregDomenespraakParser.mapBeregnetAntallTimer(dataTable)
        }

        Gitt<DataTable>("følgende om landkode fra AaRegArbeidsforhold.AaRegUtenlandsopphold") { dataTable: DataTable? ->
            aaregBuilder.first().arbeidsforhold.utenlandsopphold?.get(0)?.landkode = aaregDomenespraakParser.mapLandkode(dataTable)
        }

        Gitt<DataTable>("følgende om AaRegPeriode i fra AaRegArbeidsforhold.AaRegUtenlandsopphold") { dataTable: DataTable? ->
            aaregBuilder.first().arbeidsforhold.utenlandsopphold?.get(0)?.periode = aaregDomenespraakParser.mapPeriode(dataTable)
        }

        Gitt<DataTable>("følgende rapporteringsperiode i AaRegArbeidsforhold.AaRegUtenlandsopphold") { dataTable: DataTable? ->
            aaregBuilder.first().arbeidsforhold.utenlandsopphold?.get(0)?.rapporteringsperiode = aaregDomenespraakParser.mapRapporteringsperiode(dataTable)
        }

        Så<DataTable>("skal mappet landkode i utenlandsoppholdet være") { dataTable: DataTable? ->
            val landkodeForventet = DomenespråkParser.mapLandkode(dataTable)
            arbeidsforhold[0].utenlandsopphold?.get(0)?.landkode.shouldBe(landkodeForventet)
        }

        Så<DataTable>("mappet periode være i utenlandsoppholdet være") { dataTable: DataTable? ->
            val periodeForventet = DomenespråkParser.mapPeriodeIUtenlandsopphold(dataTable)
            arbeidsforhold[0].utenlandsopphold?.get(0)?.periode.shouldBe(periodeForventet)
        }

        Så<DataTable>("mappet rapporteringsperiode i utenlandsoppholdet være") { dataTable: DataTable? ->
            val rapporteringsPeriodeForventet = DomenespråkParser.mapRapporteringsperiode(dataTable)
            arbeidsforhold[0].utenlandsopphold?.get(0)?.rapporteringsperiode.shouldBe(rapporteringsPeriodeForventet)
        }

        Så<DataTable>("skal mappet type til arbeidsgiver i arbeidsforholdet være") { dataTable: DataTable? ->
            val typeForventet = DomenespråkParser.mapTypeIArbeidsforhold(dataTable)
            arbeidsforhold[0].arbeidsgiver.type.shouldBe(typeForventet)
        }

        Så<DataTable>("skal mappet periode i arbeidsavtale være") { dataTable: DataTable? ->
            val periodeForventet = DomenespråkParser.mapPeriodeIArbeidsavtale(dataTable)
            arbeidsforhold[0].arbeidsavtaler[0].periode.shouldBe(periodeForventet)
        }

        Så<DataTable>("mappet gyldighetsperiode i arbeidsavtale være") { dataTable: DataTable? ->
            val periodeForventet = DomenespråkParser.mapPeriodeIArbeidsavtale(dataTable)
            arbeidsforhold[0].arbeidsavtaler[0].periode.shouldBe(periodeForventet)
        }

        Så<DataTable>("mappet organisasjonsnummer til arbeidsgiver i arbeidsforholdet være") { dataTable: DataTable? ->
            val organisasjonsnummerForventet = DomenespråkParser.mapOrganisasjonsnummer(dataTable)
            arbeidsforhold[0].arbeidsgiver.organisasjonsnummer.shouldBe(organisasjonsnummerForventet)
        }

        Så<DataTable>("mappet ansatte til arbeidsgiver i arbeidsforholdet være") { dataTable: DataTable? ->
            val ansatteForventet = DomenespråkParser.mapAnsatte(dataTable)
            arbeidsforhold[0].arbeidsgiver.ansatte.shouldContainExactly(ansatteForventet)
        }

        Så<DataTable>("mappet konkursstatus til arbeidsgiver i arbeidsforholdet være") { dataTable: DataTable? ->
            val statusForventet = DomenespråkParser.mapStatuser(dataTable)
            arbeidsforhold[0].arbeidsgiver.konkursStatus.shouldContainExactly(statusForventet)
        }

        Så<DataTable>("mappet skipsregister være") { dataTable: DataTable? ->
            val skipsregisterForventet = DomenespråkParser.mapSkipsregister(dataTable)
            arbeidsforhold[0].arbeidsavtaler[0].skipsregister.shouldBe(skipsregisterForventet)
        }

        Når("arbeidsforholdene mappes") {
            arbeidsforhold = mapTilArbeidsforhold()
        }

        Så<DataTable>("skal mappede periode i arbeidsforhold være") { dataTable: DataTable? ->
            val periodeForventet = DomenespråkParser.mapPeriodeIArbeidsforhold(dataTable)
            arbeidsforhold.get(0).periode.shouldBe(periodeForventet)
        }

        Så<DataTable>("mappet stillingsprosent være") { dataTable: DataTable? ->
            val stillinprosentForventet = DomenespråkParser.mapStillingsprosent(dataTable)
            arbeidsforhold[0].arbeidsavtaler[0].stillingsprosent.shouldBe(stillinprosentForventet)
        }

        Så<DataTable>("skal mappet arbeidsgivertype i arbeidsforholdet være") { dataTable: DataTable? ->
            val arbeidsgiverTypeForventet = DomenespråkParser.mapArbeidsgivertype(dataTable)
            arbeidsforhold.get(0).arbeidsgivertype.shouldBe(arbeidsgiverTypeForventet)
        }

        Så<DataTable>("skal mappet arbeidsforholdstype i arbeidsforholdet være") { dataTable: DataTable? ->
            val arbeidforholdstypeForventet = DomenespråkParser.mapArbeidsforholdstype(dataTable)
            arbeidsforhold[0].arbeidsforholdstype.shouldBe(arbeidforholdstypeForventet)
        }

        Så<DataTable>("mappet beregnetAntallTimerPrUke være") { dataTable: DataTable? ->
            val beregnetAntallTimerPerUkeForventet = DomenespråkParser.mapBeregnetAntallTimerUke(dataTable)
            arbeidsforhold[0].arbeidsavtaler[0].beregnetAntallTimerPrUke.shouldBe(beregnetAntallTimerPerUkeForventet)
        }

        Så<DataTable>("mappet yrkeskode være") { dataTable: DataTable? ->
            val yrkeskodeForventet = DomenespråkParser.mapYrkeskode(dataTable)
            arbeidsforhold[0].arbeidsavtaler[0].yrkeskode.shouldBe(yrkeskodeForventet)
        }
    }

    private fun mapTilArbeidsforhold(): List<Arbeidsforhold> {
        return mapArbeidsforhold(aaregBuilder)
    }
}
