package no.nav.medlemskap.cucumber.steps.aareg
import io.cucumber.datatable.DataTable
import io.cucumber.java8.No
import io.kotest.matchers.collections.shouldContainExactly
import io.kotest.matchers.shouldBe
import no.nav.medlemskap.clients.aareg.*
import no.nav.medlemskap.clients.ereg.*
import no.nav.medlemskap.cucumber.DomenespråkParser
import no.nav.medlemskap.cucumber.mapping.pdl.aareg.AaregDomenespraakParser
import no.nav.medlemskap.domene.Arbeidsforhold
import no.nav.medlemskap.services.aareg.ArbeidsforholdOrganisasjon
import no.nav.medlemskap.services.aareg.mapArbeidsforhold
import java.time.LocalDate
import java.time.LocalDateTime

class AaregMapperSteps : No {
    private val aaregDomenespraakParser = AaregDomenespraakParser()
    private val aaregBuilder = AaregBuilder()
    private var arbeidsforhold = listOf<Arbeidsforhold>()

    init {
        Gitt<DataTable>("følgende om AaRegPeriode i fra AaRegArbeidsforhold.AaRegAnsettelsesperiode") { dataTable: DataTable? ->
            aaregBuilder.arbeidsforhold.ansettelsesperiode.periode = aaregDomenespraakParser.mapPeriode(dataTable)
        }

        Gitt<DataTable>("følgende om arbeidgivertype fra AaRegArbeidsforhold.AaRegOpplysningspliktigArbeidsgiver") { dataTable: DataTable? ->
            aaregBuilder.arbeidsforhold.arbeidsgiver.type = aaregDomenespraakParser.mapArbeidsgiverType(dataTable)
        }

        Gitt<DataTable>("følgende om type fra AaRegArbeidsforhold") { dataTable: DataTable? ->
            aaregBuilder.arbeidsforhold.type = aaregDomenespraakParser.mapArbeidsforholdsType(dataTable)
        }

        Gitt<DataTable>("følgende om Organisasjon.organisasjonsdetaljer.enhetstyper.enhetstype fra ereg") { dataTable: DataTable? ->
            aaregBuilder.organisasjon.organisasjonDetaljer?.enhetstyper?.first()?.enhetstype = aaregDomenespraakParser.mapEnhetstype(dataTable)
        }

        Gitt<DataTable>("følgende om Organiasjon.organisasjonsnummer fra ereg") { dataTable: DataTable? ->
            aaregBuilder.organisasjon.organisasjonsnummer = aaregDomenespraakParser.mapOrganisasjonsnummer(dataTable)
        }

        Gitt<DataTable>("følgende om ansatte i Organiasjon.Organisasjonsdetaljer.Ansatte fra ereg") { dataTable: DataTable? ->
            aaregBuilder.organisasjon.organisasjonDetaljer?.ansatte = aaregDomenespraakParser.mapAntallAnsatte(dataTable)
        }

        Gitt<DataTable>("følgende om konkursstatus organisasjon.organisasjonDetaljer.statuser") { dataTable: DataTable? ->
            aaregBuilder.organisasjon.organisasjonDetaljer?.statuser = aaregDomenespraakParser.mapStatuser(dataTable)
        }

        Gitt<DataTable>("følgende om AaRegPeriode i fra AaRegArbeidsavtale AaRegBruksperiode") { dataTable: DataTable? ->
            aaregBuilder.arbeidsavtaler[0].bruksperiode = aaregDomenespraakParser.mapBruksPeriode(dataTable)
        }

        Gitt<DataTable>("følgende om gyldighetsperiode fra AaRegArbeidsavtale AaRegGyldighetsperiode") { dataTable: DataTable? ->
            aaregBuilder.arbeidsavtaler[0].gyldighetsperiode = aaregDomenespraakParser.mapGyldighetsPeriode(dataTable)
        }

        Gitt<DataTable>("følgende om yrke fra AaRegArbeidsavtale") { dataTable: DataTable? ->
            aaregBuilder.arbeidsavtaler[0].yrke = aaregDomenespraakParser.mapYrkeskode(dataTable)
        }

        Gitt<DataTable>("følgende om skipsregister fra AaRegArbeidsavtale") { dataTable: DataTable? ->
            aaregBuilder.arbeidsavtaler[0].skipsregister = aaregDomenespraakParser.mapSkipsregister(dataTable)
        }

        Gitt<DataTable>("følgende om stillingsprosent fra AaRegArbeidsavtale") { dataTable: DataTable? ->
            aaregBuilder.arbeidsavtaler[0].stillingsprosent = aaregDomenespraakParser.mapStillingsprosent(dataTable)
        }

        Gitt<DataTable>("følgende om beregnetAntallTimerPrUke") { dataTable: DataTable? ->
            aaregBuilder.arbeidsavtaler[0].beregnetAntallTimerPrUke = aaregDomenespraakParser.mapBeregnetAntallTimer(dataTable)
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
        return mapArbeidsforhold(aaregBuilder.build())
    }

    class AaregBuilder() {
        var ansettelsesperiode = hentAnsettelsesPeriode()
        var antallTimerForTimeloennet = mutableListOf<AaRegAntallTimerForTimeloennet>()
        var arbeidsavtaler: List<AaRegArbeidsavtale> = listOf(hentArbeidsavtale())
        var arbeidsforholdId = String()
        var arbeidsgiver = hentArbeidsgiver()
        val arbeidstaker = hentArbeidstaker()
        val innrapportertEtterAOrdningen = false
        val navArbeidsforholdId = 1
        val opplysningspliktig = hentOpplysningspliktigArbeidsgiver()
        val permisjonPermitteringer = mutableListOf<AaRegPermisjonPermittering>()
        val registrert = LocalDateTime.now()
        val sistBekreftet = LocalDateTime.now()
        val sporingsinformasjon = hentAaregSporingsinformasjon()
        val type = String()
        val utenlandsopphold = mutableListOf<AaRegUtenlandsopphold>()
        val arbeidsforholdOrganisasjon = mutableListOf<ArbeidsforholdOrganisasjon>()
        val arbeidsforhold = hentAaregArbeidsforhold()
        var navn = null
        var organisasjon = hentOrganisasjon()
        val juridiskEnheter = emptyList<Organisasjon>()
        fun build(): List<ArbeidsforholdOrganisasjon> {
            arbeidsforholdOrganisasjon
                .addAll(
                    listOf
                    (
                        ArbeidsforholdOrganisasjon(
                            arbeidsforhold,
                            organisasjon,
                            juridiskEnheter
                        )
                    )
                )
            return arbeidsforholdOrganisasjon
        }

        fun hentAnsettelsesPeriode(): AaRegAnsettelsesperiode =
            AaRegAnsettelsesperiode(
                bruksperiode = AaRegBruksperiode(fom = LocalDateTime.now(), tom = null),
                periode = AaRegPeriode(fom = null, tom = null),
                varslingskode = String(),
                sporingsinformasjon = AaRegSporingsinformasjon(
                    endretAv = String(),
                    endretKilde = String(),
                    endretKildeReferanse = String(),
                    endretTidspunkt = LocalDateTime.now(),
                    opprettetAv = String(),
                    opprettetKilde = String(),
                    opprettetKildereferanse = String(),
                    opprettetTidspunkt = LocalDateTime.now()
                )
            )

        fun hentArbeidsgiver(): AaRegOpplysningspliktigArbeidsgiver =
            AaRegOpplysningspliktigArbeidsgiver(
                type = AaRegOpplysningspliktigArbeidsgiverType.Organisasjon,
                organisasjonsnummer = String(),
                aktoerId = String(),
                offentligIdent = String()
            )

        fun hentArbeidstaker(): AaRegPerson =
            AaRegPerson(
                type = AaRegPersonType.Person,
                aktoerId = String(),
                offentligIdent = String()
            )

        fun hentOpplysningspliktigArbeidsgiver(): AaRegOpplysningspliktigArbeidsgiver =
            AaRegOpplysningspliktigArbeidsgiver(
                type = AaRegOpplysningspliktigArbeidsgiverType.Organisasjon,
                organisasjonsnummer = String(),
                aktoerId = String(),
                offentligIdent = String()
            )

        fun hentAaregSporingsinformasjon(): AaRegSporingsinformasjon =
            AaRegSporingsinformasjon(
                endretAv = String(),
                endretKilde = String(),
                endretKildeReferanse = String(),
                endretTidspunkt = LocalDateTime.now(),
                opprettetAv = String(),
                opprettetKilde = String(),
                opprettetKildereferanse = String(),
                opprettetTidspunkt = LocalDateTime.now()
            )

        fun hentArbeidsavtale(): AaRegArbeidsavtale =
            AaRegArbeidsavtale(
                antallTimerPrUke = null,
                arbeidstidsordning = String(),
                beregnetAntallTimerPrUke = null,
                bruksperiode = AaRegBruksperiode(LocalDateTime.MIN, null),
                gyldighetsperiode = AaRegGyldighetsperiode(LocalDate.now(), LocalDate.MIN),
                sistStillingsendring = null,
                skipsregister = null,
                skipstype = String(),
                fartsomraade = String(),
                yrke = String(),
                sporingsinformasjon = AaRegSporingsinformasjon(
                    endretAv = null,
                    endretKildeReferanse = null,
                    endretKilde = null,
                    endretTidspunkt = null,
                    opprettetTidspunkt = LocalDateTime.MIN,
                    opprettetAv = String(),
                    opprettetKilde = null,
                    opprettetKildereferanse = String()
                ),
                stillingsprosent = null,
                sistLoennsendring = null
            )

        fun hentAaregArbeidsforhold(): AaRegArbeidsforhold =
            AaRegArbeidsforhold(
                ansettelsesperiode,
                antallTimerForTimeloennet,
                arbeidsavtaler,
                arbeidsforholdId,
                arbeidsgiver,
                arbeidstaker,
                innrapportertEtterAOrdningen,
                navArbeidsforholdId,
                opplysningspliktig,
                permisjonPermitteringer,
                registrert,
                sistBekreftet,
                sporingsinformasjon,
                type,
                utenlandsopphold
            )

        fun hentOrganisasjon(): Organisasjon =
            Organisasjon(
                navn = navn,
                organisasjonsnummer = null,
                type = String(),
                bestaarAvOrganisasjonsledd = mutableListOf<BestaarAvOrganisasjonsledd?>(),
                inngaarIJuridiskEnheter = mutableListOf<JuridiskEnhet>(),
                organisasjonDetaljer = Organisasjonsdetaljer(
                    mutableListOf<Ansatte>(Ansatte(null, null, null)),
                    null,
                    null,
                    mutableListOf<Enhetstyper>(Enhetstyper(null, String(), null)),
                    null,
                    null,
                    null,
                    mutableListOf<Hjemlandregistre>(),
                    null,
                    null,
                    null,
                    null,
                    null,
                    null,
                    null,
                    null,
                    null,
                    null,
                    null,
                    mutableListOf<Status>(),
                    null,
                    null,
                    null,
                    null,
                    mutableListOf<JuridiskEnhet>()
                )
            )
    }
}
