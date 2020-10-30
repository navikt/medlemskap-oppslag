package no.nav.medlemskap.cucumber.steps.aareg
import io.cucumber.datatable.DataTable
import io.cucumber.java8.No
import io.cucumber.java8.PendingException
import io.kotest.matchers.shouldBe
import no.nav.medlemskap.clients.aareg.*
import no.nav.medlemskap.clients.ereg.BestaarAvOrganisasjonsledd
import no.nav.medlemskap.clients.ereg.JuridiskEnhet
import no.nav.medlemskap.clients.ereg.Navn
import no.nav.medlemskap.clients.ereg.Organisasjon
import no.nav.medlemskap.cucumber.DomenespråkParser
import no.nav.medlemskap.cucumber.mapping.pdl.aareg.AaregDomenespraakParser
import no.nav.medlemskap.domene.Arbeidsforhold
import no.nav.medlemskap.services.aareg.ArbeidsforholdOrganisasjon
import no.nav.medlemskap.services.aareg.mapArbeidsforhold
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

        Gitt<DataTable>("følgende om Organisasjon.type fra ereg") { dataTable: DataTable? ->
            aaregBuilder.arbeidsforhold.type = aaregDomenespraakParser.mapType(dataTable)
        }

        Gitt<DataTable>("følgende om Organiasjon.organisasjonsnummer fra ereg") { dataTable: DataTable? ->
            aaregBuilder.organisasjon.organisasjonsnummer = aaregDomenespraakParser.mapOrganisasjonsnummer(dataTable)
        }

        Gitt<DataTable>("følgende om ansatte i Organiasjon.Organisasjonsdetaljer.Ansatte fra ereg") { dataTable: DataTable? ->
            aaregBuilder.organisasjon.organisasjonDetaljer?.ansatte?.get(0)?.antall = aaregDomenespraakParser.mapAntallAnsatte(dataTable)
        }

        Gitt<DataTable>("følgende om konkursstatus organisasjon.organisasjonDetaljer.statuser") { dataTable: DataTable? ->
            aaregBuilder.organisasjon.organisasjonDetaljer?.statuser = aaregDomenespraakParser.mapStatuser(dataTable)
        }

        Så<DataTable>("skal mappet type til arbeidsgiver i arbeidsforholdet være") { dataTable: DataTable? -> throw PendingException() }

        Så<DataTable>("mappet organisasjonsnummer til arbeidsgiver i arbeidsforholdet være") { dataTable: DataTable? -> throw PendingException() }

        Så<DataTable>("mappet ansatte til arbeidsgiver i arbeidsforholdet være") { dataTable: DataTable? -> throw PendingException() }

        Så<DataTable>("mappet konkursstatus til arbeidsgiver i arbeidsforholdet være") { dataTable: DataTable? -> throw PendingException() }

        Når("arbeidsforholdene mappes") {
            arbeidsforhold = mapTilArbeidsforhold()
        }

        Så<DataTable>("skal mappede periode i arbeidsforhold være") { dataTable: DataTable? ->
            val periodeForventet = DomenespråkParser.mapPeriodeIArbeidsforhold(dataTable)
            arbeidsforhold.get(0).periode.shouldBe(periodeForventet)
        }

        Så<DataTable>("skal mappet arbeidsgivertype i arbeidsforholdet være") { dataTable: DataTable? ->
            val arbeidsgiverTypeForventet = DomenespråkParser.mapArbeidsgivertype(dataTable)
            arbeidsforhold.get(0).arbeidsgivertype.shouldBe(arbeidsgiverTypeForventet)
        }

        Så<DataTable>("skal mappet arbeidsforholdstype i arbeidsforholdet være") { dataTable: DataTable? ->
            val arbeidforholdstypeForventet = DomenespråkParser.mapArbeidsforholdstype(dataTable)
            arbeidsforhold[0].arbeidsforholdstype.shouldBe(arbeidforholdstypeForventet)
        }
    }

    private fun mapTilArbeidsforhold(): List<Arbeidsforhold> {
        return mapArbeidsforhold(aaregBuilder.build())
    }

    class AaregBuilder() {
        var ansettelsesperiode = AaRegAnsettelsesperiode(
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
        var antallTimerForTimeloennet = mutableListOf<AaRegAntallTimerForTimeloennet>()
        var arbeidsavtaler = mutableListOf<AaRegArbeidsavtale>()
        var arbeidsforholdId = String()
        var arbeidsgiver = AaRegOpplysningspliktigArbeidsgiver(
            type = AaRegOpplysningspliktigArbeidsgiverType.Organisasjon,
            organisasjonsnummer = String(),
            aktoerId = String(),
            offentligIdent = String()
        )
        val arbeidstaker = AaRegPerson(
            type = AaRegPersonType.Person,
            aktoerId = String(),
            offentligIdent = String()
        )
        val innrapportertEtterAOrdningen = false
        val navArbeidsforholdId = 1
        val opplysningspliktig = AaRegOpplysningspliktigArbeidsgiver(
            type = AaRegOpplysningspliktigArbeidsgiverType.Organisasjon,
            organisasjonsnummer = String(),
            aktoerId = String(),
            offentligIdent = String()
        )
        val permisjonPermitteringer = mutableListOf<AaRegPermisjonPermittering>()
        val registrert = LocalDateTime.now()
        val sistBekreftet = LocalDateTime.now()
        val sporingsinformasjon = AaRegSporingsinformasjon(
            endretAv = String(),
            endretKilde = String(),
            endretKildeReferanse = String(),
            endretTidspunkt = LocalDateTime.now(),
            opprettetAv = String(),
            opprettetKilde = String(),
            opprettetKildereferanse = String(),
            opprettetTidspunkt = LocalDateTime.now()
        )
        val type = String()
        val utenlandsopphold = mutableListOf<AaRegUtenlandsopphold>()
        val arbeidsforholdOrganisasjon = mutableListOf<ArbeidsforholdOrganisasjon>()

        val arbeidsforhold = AaRegArbeidsforhold(
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

        var navn = Navn(null, null, null, null, null, null, null, null)
        val organisasjon = Organisasjon(
            navn = navn,
            organisasjonDetaljer = null,
            organisasjonsnummer = null,
            type = String(),
            bestaarAvOrganisasjonsledd = mutableListOf<BestaarAvOrganisasjonsledd?>(),
            inngaarIJuridiskEnheter = mutableListOf<JuridiskEnhet>()
        )
        val juridiskEnheter = emptyList<Organisasjon>()
        fun build(): List<ArbeidsforholdOrganisasjon> {

            arbeidsforholdOrganisasjon.addAll(listOf(ArbeidsforholdOrganisasjon(arbeidsforhold, organisasjon, juridiskEnheter)))
            return arbeidsforholdOrganisasjon
        }
    }
}
