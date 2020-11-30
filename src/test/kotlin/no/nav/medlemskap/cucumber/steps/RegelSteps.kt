package no.nav.medlemskap.cucumber.steps

import io.cucumber.datatable.DataTable
import io.cucumber.java8.No
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.matchers.shouldBe
import io.ktor.features.*
import no.nav.medlemskap.common.JsonMapper
import no.nav.medlemskap.common.LokalWebServer
import no.nav.medlemskap.cucumber.DomenespråkParser
import no.nav.medlemskap.cucumber.Medlemskapsparametre
import no.nav.medlemskap.cucumber.SpraakParserDomene.ArbeidsforholdDomeneSpraakParser
import no.nav.medlemskap.cucumber.SpraakParserDomene.PersonhistorikkDomeneSpraakParser
import no.nav.medlemskap.cucumber.steps.pdl.DataOmEktefelleBuilder
import no.nav.medlemskap.cucumber.steps.pdl.PersonhistorikkBuilder
import no.nav.medlemskap.cucumber.steps.pdl.PersonhistorikkEktefelleBuilder
import no.nav.medlemskap.domene.*
import no.nav.medlemskap.domene.barn.DataOmBarn
import no.nav.medlemskap.regler.assertBegrunnelse
import no.nav.medlemskap.regler.assertDelresultat
import no.nav.medlemskap.regler.common.RegelId
import no.nav.medlemskap.regler.common.Resultat
import no.nav.medlemskap.regler.common.Svar
import no.nav.medlemskap.regler.v1.RegelFactory
import no.nav.medlemskap.regler.v1.ReglerService
import org.junit.jupiter.api.Assertions.*
import org.skyscreamer.jsonassert.Customization
import org.skyscreamer.jsonassert.JSONAssert
import org.skyscreamer.jsonassert.JSONCompareMode
import org.skyscreamer.jsonassert.comparator.CustomComparator
import java.io.File

class RegelSteps : No {
    private val ANSATTE_9 = listOf(Ansatte(9, null, null))
    private val VANLIG_NORSK_ARBEIDSGIVER = Arbeidsgiver(
        organisasjonsnummer = "1",
        ansatte = ANSATTE_9,
        konkursStatus = null,
        juridiskeEnheter = null
    )

    private val pdlPersonhistorikkBuilder = PersonhistorikkBuilder()

    private var personhistorikkEktefelleBuilder = PersonhistorikkEktefelleBuilder()
    private var dataOmEktefelleBuilder = DataOmEktefelleBuilder()

    private var medlemskap: List<Medlemskap> = emptyList()

    private var dataOmBarn: List<DataOmBarn> = emptyList()

    private var arbeidsforhold: List<Arbeidsforhold> = emptyList()
    private var arbeidsavtaleMap = hashMapOf<Int, List<Arbeidsavtale>>()
    private var utenlandsoppholdMap = hashMapOf<Int, List<Utenlandsopphold>>()

    private val arbeidsgiverMap = mutableMapOf<Int, Arbeidsgiver>()
    private val ansatteMap = hashMapOf<String?, List<Ansatte>>()
    private var arbeidsavtaleEktefelleMap = hashMapOf<Int, List<Arbeidsavtale>>()

    private var resultat: Resultat? = null
    private var oppgaverFraGosys: List<Oppgave> = emptyList()
    private var journalPosterFraJoArk: List<Journalpost> = emptyList()
    private val domenespråkParser = DomenespråkParser

    private var datagrunnlag: Datagrunnlag? = null

    private var input: String? = null

    var overstyrteRegler: Map<RegelId, Svar> = mapOf()

    init {
        Gitt("følgende statsborgerskap i personhistorikken") { dataTable: DataTable? ->
            val statsborgerskap = PersonhistorikkDomeneSpraakParser.mapStatsborgerskap(dataTable)
            pdlPersonhistorikkBuilder.statsborgerskap.addAll(statsborgerskap)
        }

        Gitt("følgende bostedsadresser i personhistorikken") { dataTable: DataTable? ->
            val bostedsadresser = PersonhistorikkDomeneSpraakParser.mapAdresser(dataTable)
            pdlPersonhistorikkBuilder.bostedsadresser.addAll(bostedsadresser)
        }
        Gitt<DataTable>("følgende opplysninger om dødsfall i personhistorikken:") { dataTable: DataTable? ->
            val doedsfall = PersonhistorikkDomeneSpraakParser.mapDoedsfall(dataTable)
            pdlPersonhistorikkBuilder.doedsfall.addAll(doedsfall)
        }

        Gitt("følgende kontaktadresser i personhistorikken") { dataTable: DataTable? ->
            val kontaktadresser = PersonhistorikkDomeneSpraakParser.mapAdresser(dataTable)
            pdlPersonhistorikkBuilder.kontaktadresse.addAll(kontaktadresser)
        }

        Gitt("følgende oppholdsadresser i personhistorikken") { dataTable: DataTable? ->
            val oppholdsadresse = PersonhistorikkDomeneSpraakParser.mapAdresser(dataTable)
            pdlPersonhistorikkBuilder.oppholdsadresse.addAll(oppholdsadresse)
        }

        Gitt<DataTable>("følgende sivilstand i personhistorikk fra PDL") { dataTable: DataTable? ->
            val sivilstand = PersonhistorikkDomeneSpraakParser.mapSivilstander(dataTable)
            pdlPersonhistorikkBuilder.sivilstand.addAll(sivilstand)
        }

        Gitt<DataTable>("følgende familerelasjoner i personhistorikk fra PDL") { dataTable: DataTable? ->
            val familierelasjoner = PersonhistorikkDomeneSpraakParser.mapFamilierelasjoner(dataTable)
            pdlPersonhistorikkBuilder.familierelasjoner.addAll(familierelasjoner)
        }

        Gitt<DataTable>("følgende personhistorikk for ektefelle fra PDL") { dataTable: DataTable? ->
            val ektefelle = PersonhistorikkDomeneSpraakParser.mapPersonhistorikkEktefelle(dataTable)
            dataOmEktefelleBuilder.personhistorikkEktefelle = ektefelle[0]
        }

        Gitt<DataTable>("følgende barn i personhistorikk for ektefelle fra PDL") { dataTable: DataTable? ->
            val barnTilEktefelle = PersonhistorikkDomeneSpraakParser.mapBarnTilEktefelle(dataTable)
            personhistorikkEktefelleBuilder.barn.addAll(barnTilEktefelle)
        }

        Gitt<DataTable>("følgende personhistorikk for barn fra PDL") { dataTable: DataTable? ->
            val barnTilBruker = PersonhistorikkDomeneSpraakParser.mapPersonhistorikkBarn(dataTable)
            dataOmBarn = barnTilBruker
        }

        Gitt("følgende medlemsunntak fra MEDL") { dataTable: DataTable? ->
            medlemskap = domenespråkParser.mapMedlemskap(dataTable)
        }

        Gitt("følgende arbeidsforhold fra AAReg") { dataTable: DataTable? ->
            arbeidsforhold = ArbeidsforholdDomeneSpraakParser.mapArbeidsforhold(dataTable)
        }

        Gitt("følgende arbeidsgiver i arbeidsforholdet") { dataTable: DataTable? ->
            val arbeidsgivere = ArbeidsforholdDomeneSpraakParser.mapArbeidsgivere(dataTable)
            arbeidsgiverMap[0] = arbeidsgivere[0]
        }

        Gitt("følgende arbeidsgiver i arbeidsforhold {int}") { arbeidsforholdRad: Int?, dataTable: DataTable? ->
            val arbeidsgivere = ArbeidsforholdDomeneSpraakParser.mapArbeidsgivere(dataTable)
            val arbeidsforholdIndeks = arbeidsforholdIndeks(arbeidsforholdRad)
            arbeidsgiverMap[arbeidsforholdIndeks] = arbeidsgivere[0]
        }

        Gitt("følgende detaljer om ansatte for arbeidsgiver med org.nr {string}") { orgnr: String?, dataTable: DataTable? ->
            val ansatte = ArbeidsforholdDomeneSpraakParser.mapAnsatte(dataTable)
            if (ansatte.isNotEmpty()) {
                ansatteMap.put(orgnr, ansatte)
            }
        }

        Gitt("følgende detaljer om ansatte for arbeidsgiver") { dataTable: DataTable? ->
            val ansatte = ArbeidsforholdDomeneSpraakParser.mapAnsatte(dataTable)
            if (ansatte.isNotEmpty()) {
                ansatteMap.put(null, ansatte)
            }
        }

        Gitt("følgende arbeidsavtaler i arbeidsforholdet") { dataTable: DataTable? ->
            arbeidsavtaleMap[0] = ArbeidsforholdDomeneSpraakParser.mapArbeidsavtaler(dataTable)
        }

        Gitt("følgende arbeidsavtaler i arbeidsforhold {int}") { arbeidsforholdRad: Int?, dataTable: DataTable? ->
            val arbeidsavtaler = ArbeidsforholdDomeneSpraakParser.mapArbeidsavtaler(dataTable)
            val arbeidsforholdIndeks = arbeidsforholdIndeks(arbeidsforholdRad)

            arbeidsavtaleMap[arbeidsforholdIndeks] = arbeidsavtaler
        }

        Gitt("følgende utenlandsopphold i arbeidsforholdet") { dataTable: DataTable? ->
            utenlandsoppholdMap[0] = ArbeidsforholdDomeneSpraakParser.mapUtenlandsopphold(dataTable)
        }

        Gitt("følgende utenlandsopphold i arbeidsforhold {int}") { arbeidsforholdRad: Int?, dataTable: DataTable? ->
            val arbeidsforholdIndeks = arbeidsforholdIndeks(arbeidsforholdRad)
            utenlandsoppholdMap[arbeidsforholdIndeks] = ArbeidsforholdDomeneSpraakParser.mapUtenlandsopphold(dataTable)
        }

        Gitt("følgende oppgaver fra Gosys") { dataTable: DataTable? ->
            oppgaverFraGosys = domenespråkParser.mapOppgaverFraGosys(dataTable)
        }

        Gitt("følgende journalposter fra Joark") { dataTable: DataTable? ->
            journalPosterFraJoArk = domenespråkParser.mapJournalposter(dataTable)
        }

        Gitt<DataTable>("følgende arbeidsforhold til ektefelle fra AAReg") { dataTable: DataTable? ->
            val arbeidsforholdEktefelle = ArbeidsforholdDomeneSpraakParser.mapArbeidsforhold(dataTable)
            dataOmEktefelleBuilder.arbeidsforholdEktefelle = arbeidsforholdEktefelle
        }

        Gitt("følgende arbeidsavtaler til ektefelle i arbeidsforholdet") { dataTable: DataTable? ->
            arbeidsavtaleEktefelleMap[0] = ArbeidsforholdDomeneSpraakParser.mapArbeidsavtaler(dataTable)
            dataOmEktefelleBuilder.arbeidsforholdEktefelle.get(0).arbeidsavtaler = arbeidsavtaleEktefelleMap[0]!!
        }

        Gitt<DataTable>("med følgende regeloverstyringer") { dataTable: DataTable? ->
            overstyrteRegler = domenespråkParser.mapOverstyrteRegler(dataTable)
        }

        Når("medlemskap beregnes med følgende parametre") { dataTable: DataTable? ->
            val medlemskapsparametre = domenespråkParser.mapMedlemskapsparametre(dataTable)

            datagrunnlag = byggDatagrunnlag(medlemskapsparametre)
            resultat = ReglerService.kjørRegler(datagrunnlag!!)
        }

        Når<String, String, DataTable>("regel {string} kjøres med følgende parametre, skal valideringsfeil være {string}") { regelId: String?, forventetValideringsfeil: String?, dataTable: DataTable? ->
            val medlemskapsparametre = domenespråkParser.mapMedlemskapsparametre(dataTable)
            datagrunnlag = byggDatagrunnlag(medlemskapsparametre)

            val regelFactory = RegelFactory(datagrunnlag!!)
            val regel = regelFactory.create(regelId!!)

            val exception = shouldThrow<BadRequestException> {
                regel.utfør()
            }

            exception.message shouldBe forventetValideringsfeil
        }

        Når<String, DataTable>("regel {string} kjøres med følgende parametre") { regelId: String?, dataTable: DataTable? ->
            val medlemskapsparametre = domenespråkParser.mapMedlemskapsparametre(dataTable)
            datagrunnlag = byggDatagrunnlag(medlemskapsparametre)

            val regelFactory = RegelFactory(datagrunnlag!!)
            val regel = regelFactory.create(regelId!!)

            resultat = regel.utfør()
        }

        Når("tjenestekall med følgende parametere behandles") { dataTable: DataTable? ->
            val medlemskapsparametre = domenespråkParser.mapMedlemskapsparametre(dataTable)
            input = LokalWebServer.byggInput(medlemskapsparametre)
            LokalWebServer.testDatagrunnlag = byggDatagrunnlag(medlemskapsparametre)
            LokalWebServer.startServer()
        }

        Når("tjenestekall for regler med følgende parametere behandles") { dataTable: DataTable? ->
            val medlemskapsparametre = domenespråkParser.mapMedlemskapsparametre(dataTable)
            input = LokalWebServer.byggDatagrunnlagInput(byggDatagrunnlag(medlemskapsparametre))
            LokalWebServer.startServer()
        }

        Så("skal forventet json respons være {string}") { filnavn: String ->
            val forventetRespons = RegelSteps::class.java
                .getResource("/testpersoner/bakoverkompatibeltest/$filnavn.json").readText()

            val respons = LokalWebServer.respons(input!!)

            JSONAssert.assertEquals(
                forventetRespons, respons,
                CustomComparator(
                    JSONCompareMode.STRICT,
                    Customization("tidspunkt") { _, _ -> true }
                )
            )
        }

        Så("Skal kontrakt være OK") {
            LokalWebServer.kontraktMedlemskap(input!!)
        }

        Så("Skal kontrakt for Regler fra datagrunnlag være OK") {
            LokalWebServer.kontraktRegler(input!!)
        }

        Så("Skal input {string} gi statuskoden {int}") { filnavn: String, statusKode: Int ->
            val input = RegelSteps::class.java.getResource("/testpersoner/testinput/$filnavn.json").readText()
            LokalWebServer.testResponsKode(input, statusKode)
        }

        Så("skal svaret være {string}") { forventetVerdi: String ->
            val forventetSvar = domenespråkParser.parseSvar(forventetVerdi)

            assertEquals(forventetSvar, resultat!!.svar)
        }

        Så("skal begrunnelsen være {string}") { forventetBegrunnelse: String ->
            assertEquals(forventetBegrunnelse, resultat!!.begrunnelse)
        }

        Så("skal avklaringen være som definert i RegelId") {
            assertEquals(resultat!!.regelId!!.avklaring, resultat!!.avklaring)
        }

        Så<String>("skal begrunnelse utfylt være {string}") { begrunnelseUtfyltStr: String? ->
            val begrunnelseUtfylt = domenespråkParser.parseSvar(begrunnelseUtfyltStr!!)

            if (begrunnelseUtfylt == Svar.NEI) {
                assertEquals(resultat!!.regelId!!.neiBegrunnelse, resultat!!.begrunnelse)
            } else {
                assertEquals(resultat!!.regelId!!.jaBegrunnelse, resultat!!.begrunnelse)
            }
        }

        Så("skal årsaken være {string}") { forventetÅrsak: String ->
            assertEquals(forventetÅrsak, resultat!!.årsaksTekst())
        }

        Så("skal svaret være Ja på medlemskap og {string} på harDekning") { forventetVerdi: String ->
            val forventetSvar = domenespråkParser.parseSvar(forventetVerdi)
            assertEquals(Svar.JA, resultat!!.svar)
            assertEquals(forventetSvar, resultat!!.harDekning)
        }

        Så("skal svaret være {string} på medlemskap og {string} på harDekning") { forventetMedlemskap: String, forventetVerdi: String ->
            val forventetSvarMedlemskap = domenespråkParser.parseSvar(forventetMedlemskap)
            assertEquals(forventetSvarMedlemskap, resultat!!.svar)
            val forventetSvar = domenespråkParser.parseSvar(forventetVerdi)
            assertEquals(forventetSvar, resultat!!.harDekning)
        }

        Så("skal regel {string} gi svaret {string}") { regelIdStr: String?, forventetSvar: String? ->
            val regelId = domenespråkParser.parseRegelId(regelIdStr!!)

            assertDelresultat(regelId, domenespråkParser.parseSvar(forventetSvar!!), resultat!!)
        }

        Så<String, String>("skal regel {string} gi begrunnelse {string}") { regelIdStr, forventetBegrunnelse ->
            val regelId = domenespråkParser.parseRegelId(regelIdStr!!)

            assertBegrunnelse(regelId, forventetBegrunnelse, resultat!!)
        }

        Så<String>("skal regel {string} ikke finnes i resultatet") { regelIdStr: String? ->
            val regelId = domenespråkParser.parseRegelId(regelIdStr!!)
            val regelResultat = resultat!!.finnRegelResultat(regelId)

            assertNull(regelResultat)
        }

        Så<String, DataTable>("skal regel {string} inneholde følgende delresultater:") { regelIdStr: String?, dataTable: DataTable? ->
            val regelIdListe = domenespråkParser.mapRegelId(dataTable)
            val regelId = domenespråkParser.parseRegelId(regelIdStr!!)

            val regelResultat = resultat!!.finnRegelResultat(regelId)
            if (regelResultat == null) {
                throw java.lang.RuntimeException("Fant ikke regelresultat for regel {regelIdStr}")
            }

            assertTrue(regelResultat.delresultat.map { it.regelId }.containsAll(regelIdListe))
        }

        Så<DataTable>("skal resultat gi følgende delresultater:") { dataTable: DataTable? ->
            val regelIdListe = domenespråkParser.mapRegelId(dataTable)
            assertTrue(resultat!!.delresultat.map { it.regelId }.containsAll(regelIdListe))
        }

        Så("skal JSON datagrunnlag og resultat genereres i filen {string}") { filnavn: String ->
            val datagrunnlagJson: String = JsonMapper.mapToJson(datagrunnlag!!).trim()
            val resultatJson = JsonMapper.mapToJson(resultat!!)
            val responseJson = datagrunnlagJson.substring(0, datagrunnlagJson.length - 2) + ",\n \"resultat\" : " + resultatJson + "}"

            lagreJson(filnavn, responseJson)
        }

        Så("skal JSON resultat genereres i filen {string}") { filnavn: String ->
            lagreJsonResultat(filnavn)
        }

        Så("skal JSON datagrunnlag genereres i filen {string}") { filnavn: String ->
            lagreJsonDatagrunnlag(filnavn)
        }
    }

    private fun lagreJsonResultat(filnavn: String) {
        lagreJson(filnavn, JsonMapper.mapToJson(resultat!!))
    }

    private fun lagreJsonDatagrunnlag(filnavn: String) {
        lagreJson(filnavn, JsonMapper.mapToJson(datagrunnlag!!))
    }

    private fun lagreJson(filnavn: String, json: String) {
        val katalog = File("build", "cucumberjson")
        katalog.mkdir()

        val myfile = File(katalog, "$filnavn.json")
        myfile.writeText(json)
    }

    private fun arbeidsforholdIndeks(radnummer: Int?): Int {
        return (radnummer ?: 1) - 1
    }

    private fun byggDatagrunnlag(medlemskapsparametre: Medlemskapsparametre? = null): Datagrunnlag {
        if (medlemskapsparametre == null) {
            throw RuntimeException("medlemskapsparametre må være satt for å bygge Datagrunnlag")
        }

        return Datagrunnlag(
            periode = medlemskapsparametre.inputPeriode,
            førsteDagForYtelse = medlemskapsparametre.førsteDagForYtelse,
            brukerinput = Brukerinput(medlemskapsparametre.harHattArbeidUtenforNorge),
            pdlpersonhistorikk = pdlPersonhistorikkBuilder.build(),
            medlemskap = medlemskap,
            arbeidsforhold = byggArbeidsforhold(arbeidsforhold, arbeidsgiverMap, arbeidsavtaleMap, utenlandsoppholdMap, ansatteMap),
            oppgaver = oppgaverFraGosys,
            dokument = journalPosterFraJoArk,
            ytelse = medlemskapsparametre.ytelse ?: Ytelse.SYKEPENGER,
            dataOmBarn = dataOmBarn,
            dataOmEktefelle = dataOmEktefelleBuilder.build(),
            overstyrteRegler = overstyrteRegler
        )
    }

    private fun byggArbeidsforhold(
        arbeidsforholdListe: List<Arbeidsforhold>,
        arbeidsgiverMap: Map<Int, Arbeidsgiver>,
        arbeidsavtaleMap: Map<Int, List<Arbeidsavtale>>,
        utenlandsoppholdMap: Map<Int, List<Utenlandsopphold>>,
        ansatteMap: Map<String?, List<Ansatte>>
    ): List<Arbeidsforhold> {
        return arbeidsforholdListe
            .mapIndexed { index, arbeidsforhold ->
                arbeidsforhold.copy(
                    utenlandsopphold = utenlandsoppholdMap[index] ?: emptyList(),
                    arbeidsgiver = byggArbeidsgiver(arbeidsgiverMap[index], ansatteMap) ?: VANLIG_NORSK_ARBEIDSGIVER,
                    arbeidsavtaler = arbeidsavtaleMap[index] ?: emptyList()
                )
            }
    }

    private fun byggArbeidsgiver(arbeidsgiver: Arbeidsgiver?, ansatteMap: Map<String?, List<Ansatte>>): Arbeidsgiver? {
        if (arbeidsgiver == null) {
            return null
        }

        val ansatte = ansatteMap[arbeidsgiver.organisasjonsnummer] ?: ansatteMap[null]
        if (ansatte != null) {
            return arbeidsgiver.copy(ansatte = ansatte)
        }

        return arbeidsgiver
    }
}
