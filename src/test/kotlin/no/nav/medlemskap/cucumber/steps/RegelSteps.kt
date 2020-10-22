package no.nav.medlemskap.cucumber.steps

import io.cucumber.datatable.DataTable
import io.cucumber.java8.No
import no.nav.medlemskap.common.JsonMapper
import no.nav.medlemskap.cucumber.DomenespråkParser
import no.nav.medlemskap.cucumber.Medlemskapsparametre
import no.nav.medlemskap.domene.*
import no.nav.medlemskap.domene.barn.DataOmBarn
import no.nav.medlemskap.regler.assertDelresultat
import no.nav.medlemskap.regler.common.Resultat
import no.nav.medlemskap.regler.common.Svar
import no.nav.medlemskap.regler.v1.RegelFactory
import no.nav.medlemskap.regler.v1.ReglerService
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import java.io.File

class RegelSteps : No {
    private val ANSATTE_9 = listOf(Ansatte(9, null, null))
    private val VANLIG_NORSK_ARBEIDSGIVER = Arbeidsgiver(type = "BEDR", organisasjonsnummer = "1", ansatte = ANSATTE_9, konkursStatus = null, juridiskEnhetEnhetstypeMap = null)

    private val pdlPersonhistorikkBuilder = PersonhistorikkBuilder()

    private var personhistorikkEktefelleBuilder = PersonhistorikkEktefelleBuilder()
    private var dataOmEktefelleBuilder = DataOmEktefelleBuilder()

    private val personhistorikkBarn = PersonhistorikkBarnBuilder()
    private val dataOmBarnBuilder = mutableListOf<DataOmBarnBuilder>()
    private var medlemskap: List<Medlemskap> = emptyList()

    private var dataOmBarn: List<DataOmBarn> = emptyList()

    private var arbeidsforhold: List<Arbeidsforhold> = emptyList()
    private var arbeidsavtaleMap = hashMapOf<Int, List<Arbeidsavtale>>()
    private var utenlandsoppholdMap = hashMapOf<Int, List<Utenlandsopphold>>()
    private var arbeidsgiverMap = hashMapOf<Int, Arbeidsgiver>()

    private var arbeidsavtaleEktefelleMap = hashMapOf<Int, List<Arbeidsavtale>>()

    private var resultat: Resultat? = null
    private var oppgaverFraGosys: List<Oppgave> = emptyList()
    private var journalPosterFraJoArk: List<Journalpost> = emptyList()
    private val domenespråkParser = DomenespråkParser

    private var datagrunnlag: Datagrunnlag? = null

    init {
        Gitt("følgende statsborgerskap i personhistorikken") { dataTable: DataTable? ->
            val statsborgerskap = domenespråkParser.mapStatsborgerskap(dataTable)
            pdlPersonhistorikkBuilder.statsborgerskap.addAll(statsborgerskap)
        }

        Gitt("følgende bostedsadresser i personhistorikken") { dataTable: DataTable? ->
            val bostedsadresser = domenespråkParser.mapAdresser(dataTable)
            pdlPersonhistorikkBuilder.bostedsadresser.addAll(bostedsadresser)
        }
        Gitt<DataTable>("følgende opplysninger om doedsfall i personhistorikken:") { dataTable: DataTable? ->
            val doedsfall = domenespråkParser.mapDoedsfall(dataTable)
            pdlPersonhistorikkBuilder.doedsfall.addAll(doedsfall)
        }

        Gitt("følgende kontaktadresser i personhistorikken") { dataTable: DataTable? ->
            val kontaktadresser = domenespråkParser.mapAdresser(dataTable)
            pdlPersonhistorikkBuilder.kontaktadresse.addAll(kontaktadresser)
        }

        Gitt("følgende oppholdsadresser i personhistorikken") { dataTable: DataTable? ->
            val oppholdsadresse = domenespråkParser.mapAdresser(dataTable)
            pdlPersonhistorikkBuilder.oppholdsadresse.addAll(oppholdsadresse)
        }

        Gitt<DataTable>("følgende sivilstand i personhistorikk fra PDL") { dataTable: DataTable? ->
            val sivilstand = domenespråkParser.mapSivilstander(dataTable)
            pdlPersonhistorikkBuilder.sivilstand.addAll(sivilstand)
        }

        Gitt<DataTable>("følgende familerelasjoner i personhistorikk fra PDL") { dataTable: DataTable? ->
            val familierelasjoner = domenespråkParser.mapFamilierelasjoner(dataTable)
            pdlPersonhistorikkBuilder.familierelasjoner.addAll(familierelasjoner)
        }

        Gitt<DataTable>("følgende personhistorikk for ektefelle fra PDL") { dataTable: DataTable? ->
            val ektefelle = domenespråkParser.mapPersonhistorikkEktefelle(dataTable)
            dataOmEktefelleBuilder.personhistorikkEktefelle = ektefelle[0]
        }

        Gitt<DataTable>("følgende barn i personhistorikk for ektefelle fra PDL") { dataTable: DataTable? ->
            val barnTilEktefelle = domenespråkParser.mapBarnTilEktefelle(dataTable)
            personhistorikkEktefelleBuilder.barn.addAll(barnTilEktefelle)
        }

        Gitt<DataTable>("følgende personhistorikk for barn fra PDL") { dataTable: DataTable? ->
            val barnTilBruker = domenespråkParser.mapPersonhistorikkBarn(dataTable)
            dataOmBarn = barnTilBruker
        }

        Gitt("følgende medlemsunntak fra MEDL") { dataTable: DataTable? ->
            medlemskap = domenespråkParser.mapMedlemskap(dataTable)
        }

        Gitt("følgende arbeidsforhold fra AAReg") { dataTable: DataTable? ->
            arbeidsforhold = domenespråkParser.mapArbeidsforhold(dataTable)
        }

        Gitt("følgende arbeidsgiver i arbeidsforholdet") { dataTable: DataTable? ->
            val arbeidsgivere = domenespråkParser.mapArbeidsgivere(dataTable)

            arbeidsgiverMap[0] = arbeidsgivere[0]
        }

        Gitt("følgende arbeidsgiver i arbeidsforhold {int}") { arbeidsforholdRad: Int?, dataTable: DataTable? ->
            val arbeidsgivere = domenespråkParser.mapArbeidsgivere(dataTable)
            val arbeidsforholdIndeks = arbeidsforholdIndeks(arbeidsforholdRad)

            arbeidsgiverMap[arbeidsforholdIndeks] = arbeidsgivere[0]
        }

        Gitt("følgende arbeidsavtaler i arbeidsforholdet") { dataTable: DataTable? ->
            arbeidsavtaleMap[0] = domenespråkParser.mapArbeidsavtaler(dataTable)
        }

        Gitt("følgende arbeidsavtaler i arbeidsforhold {int}") { arbeidsforholdRad: Int?, dataTable: DataTable? ->
            val arbeidsavtaler = domenespråkParser.mapArbeidsavtaler(dataTable)
            val arbeidsforholdIndeks = arbeidsforholdIndeks(arbeidsforholdRad)

            arbeidsavtaleMap[arbeidsforholdIndeks] = arbeidsavtaler
        }

        Gitt("følgende utenlandsopphold i arbeidsforholdet") { dataTable: DataTable? ->
            utenlandsoppholdMap[0] = domenespråkParser.mapUtenlandsopphold(dataTable)
        }

        Gitt("følgende utenlandsopphold i arbeidsforhold {int}") { arbeidsforholdRad: Int?, dataTable: DataTable? ->
            val arbeidsforholdIndeks = arbeidsforholdIndeks(arbeidsforholdRad)

            utenlandsoppholdMap[arbeidsforholdIndeks] = domenespråkParser.mapUtenlandsopphold(dataTable)
        }

        Gitt("følgende oppgaver fra Gosys") { dataTable: DataTable? ->
            oppgaverFraGosys = domenespråkParser.mapOppgaverFraGosys(dataTable)
        }

        Gitt("følgende journalposter fra Joark") { dataTable: DataTable? ->
            journalPosterFraJoArk = domenespråkParser.mapJournalposter(dataTable)
        }

        Gitt<DataTable>("følgende arbeidsforhold til ektefelle fra AAReg") { dataTable: DataTable? ->
            val arbeidsforholdEktefelle = domenespråkParser.mapArbeidsforhold(dataTable)
            dataOmEktefelleBuilder.arbeidsforholdEktefelle = arbeidsforholdEktefelle
        }

        Gitt("følgende arbeidsavtaler til ektefelle i arbeidsforholdet") { dataTable: DataTable? ->
            arbeidsavtaleEktefelleMap[0] = domenespråkParser.mapArbeidsavtaler(dataTable)
            dataOmEktefelleBuilder.arbeidsforholdEktefelle.get(0).arbeidsavtaler = arbeidsavtaleEktefelleMap[0]!!
        }

        Når("medlemskap beregnes med følgende parametre") { dataTable: DataTable? ->
            val medlemskapsparametre = domenespråkParser.mapMedlemskapsparametre(dataTable)

            datagrunnlag = byggDatagrunnlag(medlemskapsparametre)
            resultat = ReglerService.kjørRegler(datagrunnlag!!)
        }

        Når<String, DataTable>("regel {string} kjøres med følgende parametre") { regelId: String?, dataTable: DataTable? ->
            val medlemskapsparametre = domenespråkParser.mapMedlemskapsparametre(dataTable)
            datagrunnlag = byggDatagrunnlag(medlemskapsparametre)

            val regelFactory = RegelFactory(datagrunnlag!!)
            val regel = regelFactory.create(regelId!!)

            resultat = regel.utfør()
        }

        Så("skal svaret være {string}") { forventetVerdi: String ->
            val forventetSvar = domenespråkParser.parseSvar(forventetVerdi)

            assertEquals(forventetSvar, resultat!!.svar)
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

        val sykemeldingsperiode = medlemskapsparametre.inputPeriode
        val harHattArbeidUtenforNorge = medlemskapsparametre.harHattArbeidUtenforNorge
        val ytelse = medlemskapsparametre.ytelse ?: Ytelse.SYKEPENGER

        return Datagrunnlag(
            periode = sykemeldingsperiode,
            brukerinput = Brukerinput(harHattArbeidUtenforNorge),
            pdlpersonhistorikk = pdlPersonhistorikkBuilder.build(),
            medlemskap = medlemskap,
            arbeidsforhold = byggArbeidsforhold(arbeidsforhold, arbeidsgiverMap, arbeidsavtaleMap, utenlandsoppholdMap),
            oppgaver = oppgaverFraGosys,
            dokument = journalPosterFraJoArk,
            ytelse = ytelse,
            dataOmBarn = dataOmBarn,
            dataOmEktefelle = dataOmEktefelleBuilder.build()
        )
    }

    private fun byggArbeidsforhold(
        arbeidsforholdListe: List<Arbeidsforhold>,
        arbeidsgiverMap: Map<Int, Arbeidsgiver>,
        arbeidsavtaleMap: Map<Int, List<Arbeidsavtale>>,
        utenlandsoppholdMap: Map<Int, List<Utenlandsopphold>>
    ): List<Arbeidsforhold> {
        return arbeidsforholdListe
            .mapIndexed { index, arbeidsforhold ->
                arbeidsforhold.copy(
                    utenlandsopphold = utenlandsoppholdMap[index] ?: emptyList(),
                    arbeidsgiver = arbeidsgiverMap[index] ?: VANLIG_NORSK_ARBEIDSGIVER,
                    arbeidsavtaler = arbeidsavtaleMap[index] ?: emptyList()
                )
            }
    }
}
