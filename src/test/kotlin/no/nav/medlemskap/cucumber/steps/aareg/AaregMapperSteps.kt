package no.nav.medlemskap.cucumber.steps.aareg
import AaregBuilder
import io.cucumber.datatable.DataTable
import io.cucumber.java8.No
import io.kotest.matchers.collections.shouldContainExactly
import io.kotest.matchers.shouldBe
import no.nav.medlemskap.cucumber.SpraakParserDomene.ArbeidsforholdDomeneSpraakParser
import no.nav.medlemskap.cucumber.mapping.pdl.aareg.AaregDomenespraakParser
import no.nav.medlemskap.cucumber.steps.aareg.aaregBuilder.*
import no.nav.medlemskap.cucumber.steps.aareg.eregBuilder.EnhetstypeBuilder
import no.nav.medlemskap.cucumber.steps.aareg.eregBuilder.OrganisasjonBuilder
import no.nav.medlemskap.cucumber.steps.aareg.eregBuilder.OrganisasjonsDetaljerBuilder
import no.nav.medlemskap.domene.Arbeidsforhold
import no.nav.medlemskap.services.aareg.mapArbeidsforhold

class AaregMapperSteps : No {
    private val aaregDomenespraakParser = AaregDomenespraakParser()
    private var arbeidsforhold = listOf<Arbeidsforhold>()
    private var aaregPeriodeBuilder = AaregPeriodeBuilder()
    private var aaregArbeidsgiverBuilder = AaregOpplysningspliktigArbeidsgiverBuilder()
    private var aaregArbeidsforholdBuilder = AaRegArbeidsforholdBuilder()
    private var organisasjonsDetaljerBuilder = OrganisasjonsDetaljerBuilder()
    private var enhetstypeBuilder = EnhetstypeBuilder()
    private var organisasjonBuilder = OrganisasjonBuilder()
    private var ansettelsesPeriodeBuilder = AaRegAnsettelsesPeriodeBuilder()
    private var arbeidsavtaleBuilder = AaRegArbeidsavtaleBuilder()
    private var utenlandoppholdBuiler = AaRegUtenlandsoppholdBuilder()
    private var permisjonsPermitteringerBuilder = AaRegPermisjonsPermitteringerBuilder()

    init {
        Gitt<DataTable>("følgende om AaRegPeriode i fra AaRegAnsettelsesperiode fra AaRegArbeidsforhold") { dataTable: DataTable? ->
            aaregPeriodeBuilder.fom = aaregDomenespraakParser.mapPeriode(dataTable).fom
            aaregPeriodeBuilder.tom = aaregDomenespraakParser.mapPeriode(dataTable).tom
        }

        Gitt<DataTable>("følgende om arbeidgivertype fra AaRegOpplysningspliktigArbeidsgiver fra AaRegArbeidsforhold") { dataTable: DataTable? ->
            aaregArbeidsgiverBuilder.type = aaregDomenespraakParser.mapArbeidsgiverType(dataTable)
        }

        Gitt<DataTable>("følgende om type fra AaRegArbeidsforhold") { dataTable: DataTable? ->
            aaregArbeidsforholdBuilder.type = aaregDomenespraakParser.mapArbeidsforholdsType(dataTable)
        }

        Gitt<DataTable>("følgende om enhetstype fra enhetstyper fra organisasjonsdetaljer fra ereg") { dataTable: DataTable? ->
            enhetstypeBuilder.enhetstype = aaregDomenespraakParser.mapEnhetstype(dataTable)
        }

        Gitt<DataTable>("følgende om organisasjonsnummer fra Organiasjon fra ereg") { dataTable: DataTable? ->
            organisasjonBuilder.organisjonsnummer = aaregDomenespraakParser.mapOrganisasjonsnummer(dataTable)
        }

        Gitt<DataTable>("følgende om Ansatte fra  Organisasjonsdetaljer fra Organiasjon fra ereg") { dataTable: DataTable? ->
            organisasjonsDetaljerBuilder.ansatte = aaregDomenespraakParser.mapAntallAnsatte(dataTable).toMutableList()
        }

        Gitt<DataTable>("følgende om konkursstatus fra statuser fra organisasjonDetaljer fra organisasjon") { dataTable: DataTable? ->
            organisasjonsDetaljerBuilder.statuser = aaregDomenespraakParser.mapStatuser(dataTable).toMutableList()
        }

        Gitt<DataTable>("følgende om AaRegPeriode fra AaRegArbeidsavtale fra AaRegBruksperiode") { dataTable: DataTable? ->
            arbeidsavtaleBuilder.bruksperiode = aaregDomenespraakParser.mapBruksPeriode(dataTable)
        }

        Gitt<DataTable>("følgende om gyldighetsperiode fra AaRegArbeidsavtale fra AaRegGyldighetsperiode") { dataTable: DataTable? ->
            arbeidsavtaleBuilder.gyldighetsperiode = aaregDomenespraakParser.mapGyldighetsPeriode(dataTable)
        }

        Gitt<DataTable>("følgende om yrke fra AaRegArbeidsavtale") { dataTable: DataTable? ->
            arbeidsavtaleBuilder.yrke = aaregDomenespraakParser.mapYrkeskode(dataTable)
        }

        Gitt<DataTable>("følgende om skipsregister fra AaRegArbeidsavtale") { dataTable: DataTable? ->
            arbeidsavtaleBuilder.skipsregister = aaregDomenespraakParser.mapSkipsregister(dataTable)
        }

        Gitt<DataTable>("følgende om fartsområde fra AaRegArbeidsavtale") { dataTable: DataTable? ->
            arbeidsavtaleBuilder.fartsomraade = aaregDomenespraakParser.mapFartsomraade(dataTable)
        }

        Gitt<DataTable>("følgende om stillingsprosent fra AaRegArbeidsavtale") { dataTable: DataTable? ->
            arbeidsavtaleBuilder.stillingsprosent = aaregDomenespraakParser.mapStillingsprosent(dataTable)
        }

        Gitt<DataTable>("følgende om beregnetAntallTimerPrUke") { dataTable: DataTable? ->
            arbeidsavtaleBuilder.beregnetAntallTimerPrUke = aaregDomenespraakParser.mapBeregnetAntallTimerPerUke(dataTable)
        }

        Gitt<DataTable>("følgende om landkode fra AaRegUtenlandsopphold fra AaRegArbeidsforhold") { dataTable: DataTable? ->
            utenlandoppholdBuiler.landkode = aaregDomenespraakParser.mapLandkode(dataTable)
        }

        Gitt<DataTable>("følgende om AaRegPeriode i fra AaRegUtenlandsopphold fra AaRegArbeidsforhold") { dataTable: DataTable? ->
            utenlandoppholdBuiler.periode = aaregDomenespraakParser.mapPeriode(dataTable)
        }

        Gitt<DataTable>("følgende rapporteringsperiode fra AaRegUtenlandsopphold fra AaRegArbeidsforhold") { dataTable: DataTable? ->
            utenlandoppholdBuiler.rapporteringsperiode = aaregDomenespraakParser.mapRapporteringsperiode(dataTable)
        }

        Gitt<DataTable>("følgende om AaRegPeriode fra AaRegPermisjonPermittering fra AaRegArbeidsforhold") { dataTable: DataTable? ->
            permisjonsPermitteringerBuilder.periode = aaregDomenespraakParser.mapPeriode(dataTable)
        }

        Gitt<DataTable>("følgende om permisjonPermitteringId i fra AaRegPermisjonPermittering fra AaRegArbeidsforhold") { dataTable: DataTable? ->
            permisjonsPermitteringerBuilder.permisjonsPermitteringId = aaregDomenespraakParser.mapPermitteringsId(dataTable)
        }

        Gitt<DataTable>("følgende prosent fra fra AaRegPermisjonPermittering fra AaRegArbeidsforhold") { dataTable: DataTable? ->
            permisjonsPermitteringerBuilder.prosent = aaregDomenespraakParser.mapProsent(dataTable)
        }

        Gitt<DataTable>("følgende type fra fra AaRegPermisjonPermittering fra AaRegArbeidsforhold") { dataTable: DataTable? ->
            permisjonsPermitteringerBuilder.type = aaregDomenespraakParser.mapType(dataTable)
        }

        Gitt<DataTable>("følgende varslingskode fra fra AaRegPermisjonPermittering fra AaRegArbeidsforhold") { dataTable: DataTable? ->
            permisjonsPermitteringerBuilder.varslingkode = aaregDomenespraakParser.mapVarslingskode(dataTable)
        }

        Når("arbeidsforholdene mappes") {
            arbeidsforhold = mapTilArbeidsforhold()
        }

        Så<DataTable>("skal mappet landkode i utenlandsoppholdet være") { dataTable: DataTable? ->
            val landkodeForventet = ArbeidsforholdDomeneSpraakParser.mapLandkode(dataTable)
            arbeidsforhold[0].utenlandsopphold?.get(0)?.landkode.shouldBe(landkodeForventet)
        }

        Så<DataTable>("mappet periode være i utenlandsoppholdet være") { dataTable: DataTable? ->
            val periodeForventet = ArbeidsforholdDomeneSpraakParser.mapPeriodeIUtenlandsopphold(dataTable)
            arbeidsforhold[0].utenlandsopphold?.get(0)?.periode.shouldBe(periodeForventet)
        }

        Så<DataTable>("skal mappet periode i arbeidsforholdet være") { dataTable: DataTable? ->
            val periodeForventet = ArbeidsforholdDomeneSpraakParser.mapPeriodeForPermittering(dataTable)
            arbeidsforhold[0].permisjonPermittering?.get(0)?.periode.shouldBe(periodeForventet)
        }

        Så<DataTable>("mappet permisjonPermitteringId skal være") { dataTable: DataTable? ->
            val permisjonPermitteringIdForventet = ArbeidsforholdDomeneSpraakParser.mapPermitteringId(dataTable)
            arbeidsforhold[0].permisjonPermittering?.get(0)?.permisjonPermitteringId.shouldBe(permisjonPermitteringIdForventet)
        }

        Så<DataTable>("mappet rapporteringsperiode i utenlandsoppholdet være") { dataTable: DataTable? ->
            val rapporteringsPeriodeForventet = ArbeidsforholdDomeneSpraakParser.mapRapporteringsperiode(dataTable)
            arbeidsforhold[0].utenlandsopphold?.get(0)?.rapporteringsperiode.shouldBe(rapporteringsPeriodeForventet)
        }

        Så<DataTable>("skal mappet periode i arbeidsavtale være") { dataTable: DataTable? ->
            val periodeForventet = ArbeidsforholdDomeneSpraakParser.mapPeriodeIArbeidsavtale(dataTable)
            arbeidsforhold[0].arbeidsavtaler[0].periode.shouldBe(periodeForventet)
        }

        Så<DataTable>("mappet gyldighetsperiode i arbeidsavtale være") { dataTable: DataTable? ->
            val periodeForventet = ArbeidsforholdDomeneSpraakParser.mapPeriodeIArbeidsavtale(dataTable)
            arbeidsforhold[0].arbeidsavtaler[0].periode.shouldBe(periodeForventet)
        }

        Så<DataTable>("mappet organisasjonsnummer til arbeidsgiver i arbeidsforholdet være") { dataTable: DataTable? ->
            val organisasjonsnummerForventet = ArbeidsforholdDomeneSpraakParser.mapOrganisasjonsnummer(dataTable)
            arbeidsforhold[0].arbeidsgiver.organisasjonsnummer.shouldBe(organisasjonsnummerForventet)
        }

        Så<DataTable>("mappet ansatte til arbeidsgiver i arbeidsforholdet være") { dataTable: DataTable? ->
            val ansatteForventet = ArbeidsforholdDomeneSpraakParser.mapAnsatte(dataTable)
            arbeidsforhold[0].arbeidsgiver.ansatte.shouldContainExactly(ansatteForventet)
        }

        Så<DataTable>("mappet konkursstatus til arbeidsgiver i arbeidsforholdet være") { dataTable: DataTable? ->
            val statusForventet = ArbeidsforholdDomeneSpraakParser.mapKonkurStatuser(dataTable)
            arbeidsforhold[0].arbeidsgiver.konkursStatus.shouldContainExactly(statusForventet)
        }

        Så<DataTable>("mappet skipsregister være") { dataTable: DataTable? ->
            val skipsregisterForventet = ArbeidsforholdDomeneSpraakParser.mapSkipsregister(dataTable)
            arbeidsforhold[0].arbeidsavtaler[0].skipsregister.shouldBe(skipsregisterForventet)
        }

        Så<DataTable>("mappet fartsområde skal være") { dataTable: DataTable? ->
            val fartsomraadeForventet = ArbeidsforholdDomeneSpraakParser.mapFartsomraade(dataTable)
            arbeidsforhold[0].arbeidsavtaler[0].fartsomraade.shouldBe(fartsomraadeForventet)
        }

        Så<DataTable>("mappet prosent skal være") { dataTable: DataTable? ->
            val prosentForventet = ArbeidsforholdDomeneSpraakParser.mapProsent(dataTable)
            arbeidsforhold[0].permisjonPermittering?.get(0)?.prosent.shouldBe(prosentForventet)
        }

        Så<DataTable>("mappet type skal være") { dataTable: DataTable? ->
            val permisjonstypeForventet = ArbeidsforholdDomeneSpraakParser.mapType(dataTable)
            arbeidsforhold[0].permisjonPermittering?.get(0)?.type.shouldBe(permisjonstypeForventet)
        }

        Så<DataTable>("mappet varslingkode skal være") { dataTable: DataTable? ->
            val varslingsKodeForventet = ArbeidsforholdDomeneSpraakParser.mapVarslingskode(dataTable)
            arbeidsforhold[0].permisjonPermittering?.get(0)?.varslingskode.shouldBe(varslingsKodeForventet)
        }

        Så<DataTable>("skal mappede periode i arbeidsforhold være") { dataTable: DataTable? ->
            val periodeForventet = ArbeidsforholdDomeneSpraakParser.mapPeriodeIArbeidsforhold(dataTable)
            arbeidsforhold.get(0).periode.shouldBe(periodeForventet)
        }

        Så<DataTable>("mappet stillingsprosent være") { dataTable: DataTable? ->
            val stillinprosentForventet = ArbeidsforholdDomeneSpraakParser.mapStillingsprosent(dataTable)
            arbeidsforhold[0].arbeidsavtaler[0].stillingsprosent.shouldBe(stillinprosentForventet)
        }

        Så<DataTable>("skal mappet arbeidsgivertype i arbeidsforholdet være") { dataTable: DataTable? ->
            val arbeidsgiverTypeForventet = ArbeidsforholdDomeneSpraakParser.mapArbeidsgivertype(dataTable)
            arbeidsforhold.get(0).arbeidsgivertype.shouldBe(arbeidsgiverTypeForventet)
        }

        Så<DataTable>("skal mappet arbeidsforholdstype i arbeidsforholdet være") { dataTable: DataTable? ->
            val arbeidforholdstypeForventet = ArbeidsforholdDomeneSpraakParser.mapArbeidsforholdstype(dataTable)
            arbeidsforhold[0].arbeidsforholdstype.shouldBe(arbeidforholdstypeForventet)
        }

        Så<DataTable>("mappet beregnetAntallTimerPrUke være") { dataTable: DataTable? ->
            val beregnetAntallTimerPerUkeForventet = ArbeidsforholdDomeneSpraakParser.mapBeregnetAntallTimerUke(dataTable)
            arbeidsforhold[0].arbeidsavtaler[0].beregnetAntallTimerPrUke.shouldBe(beregnetAntallTimerPerUkeForventet)
        }

        Så<DataTable>("mappet yrkeskode være") { dataTable: DataTable? ->
            val yrkeskodeForventet = ArbeidsforholdDomeneSpraakParser.mapYrkeskode(dataTable)
            arbeidsforhold[0].arbeidsavtaler[0].yrkeskode.shouldBe(yrkeskodeForventet)
        }
    }

    private fun mapTilArbeidsforhold(): List<Arbeidsforhold> {
        return mapArbeidsforhold(byggAaregBuilder().build())
    }

    private fun byggAaregBuilder(): AaregBuilder {
        val aaregBuilder = AaregBuilder()
        organisasjonsDetaljerBuilder.enhetstyper = mutableListOf(enhetstypeBuilder.build())
        organisasjonBuilder.originasjonsDetaljer = organisasjonsDetaljerBuilder.build()
        ansettelsesPeriodeBuilder.periode = aaregPeriodeBuilder.build()
        aaregArbeidsforholdBuilder.ansettelsesperiode = ansettelsesPeriodeBuilder.build()
        aaregArbeidsforholdBuilder.utenlandsopphold = mutableListOf(utenlandoppholdBuiler.build())
        aaregArbeidsforholdBuilder.arbeidsgiver = aaregArbeidsgiverBuilder.build()
        aaregArbeidsforholdBuilder.arbeidsavtaler = mutableListOf(arbeidsavtaleBuilder.build())
        aaregArbeidsforholdBuilder.permisjonPermitteringer = mutableListOf(permisjonsPermitteringerBuilder.build())
        aaregBuilder.arbeidsforhold = aaregArbeidsforholdBuilder.build()
        aaregBuilder.organisasjon = organisasjonBuilder.build()
        return aaregBuilder
    }
}
