package no.nav.medlemskap.cucumber.steps

import io.cucumber.datatable.DataTable
import io.cucumber.java8.No
import no.nav.medlemskap.cucumber.*
import no.nav.medlemskap.domene.*
import no.nav.medlemskap.regler.assertDelresultat
import no.nav.medlemskap.regler.common.Resultat
import no.nav.medlemskap.regler.common.Svar
import no.nav.medlemskap.regler.v1.*
import no.nav.medlemskap.services.ereg.Ansatte
import org.junit.jupiter.api.Assertions.assertEquals


class RegelSteps : No {
    private val ANSATTE_9 = listOf(Ansatte(9, null, null))
    private val VANLIG_NORSK_ARBEIDSGIVER = Arbeidsgiver(type = "BEDR", identifikator = "1", landkode = "NOR", ansatte = ANSATTE_9, konkursStatus = null)

    private val personhistorikkBuilder = PersonhistorikkBuilder()
    private val pdlPersonhistorikkBuilder = PersonhistorikkBuilder()
    private val personHistorikkRelatertePersoner = mutableListOf<PersonhistorikkRelatertPerson>()

    private var medlemskap: List<Medlemskap> = emptyList()

    private var arbeidsforhold: List<Arbeidsforhold> = emptyList()
    private var arbeidsavtaleMap = hashMapOf<Int, List<Arbeidsavtale>>()
    private var utenlandsoppholdMap = hashMapOf<Int, List<Utenlandsopphold>>()
    private var arbeidsgiverMap = hashMapOf<Int, Arbeidsgiver>()

    private var resultat: Resultat? = null
    private var oppgaverFraGosys: List<Oppgave> = emptyList()
    private var journalPosterFraJoArk: List<Journalpost> = emptyList()
    private val domenespråkParser = DomenespråkParser()

    private var datagrunnlag: Datagrunnlag? = null

    init {
        Gitt("følgende statsborgerskap i personhistorikken") { dataTable: DataTable? ->
            val statsborgerskap = domenespråkParser.mapDataTable(dataTable, StatsborgerskapMapper())
            personhistorikkBuilder.statsborgerskap.addAll(statsborgerskap)
        }

        Gitt("følgende bostedsadresser i personhistorikken") { dataTable: DataTable? ->
            val bostedsadresser = domenespråkParser.mapDataTable(dataTable, AdresseMapper())
            personhistorikkBuilder.bostedsadresser.addAll(bostedsadresser)
        }

        Gitt("følgende postadresser i personhistorikken") { dataTable: DataTable? ->
            val postadresser = domenespråkParser.mapDataTable(dataTable, AdresseMapper())
            personhistorikkBuilder.postadresser.addAll(postadresser)
        }

        Gitt("følgende midlertidige adresser i personhistorikken") { dataTable: DataTable? ->
            val midlertidigAdresser = domenespråkParser.mapDataTable(dataTable, AdresseMapper())
            personhistorikkBuilder.midlertidigAdresser.addAll(midlertidigAdresser)
        }

        Gitt("følgende personstatuser i personhistorikken") { dataTable: DataTable? ->
            val personstatuser = domenespråkParser.mapDataTable(dataTable, PersonstatusMapper())
            personhistorikkBuilder.personstatuser.addAll(personstatuser)
        }

        Gitt<DataTable>("følgende sivilstand i personhistorikk fra TPS\\/PDL") { dataTable: DataTable? ->
            val sivilstand = domenespråkParser.mapDataTable(dataTable, SivilstandMapper())
            pdlPersonhistorikkBuilder.sivilstand.addAll(sivilstand)
        }

        Gitt<DataTable>("følgende familerelasjoner i personhistorikk fra TPS\\/PDL") { dataTable: DataTable? ->
            val familierelasjoner = domenespråkParser.mapDataTable(dataTable, FamilieRelasjonMapper())
            pdlPersonhistorikkBuilder.familierelasjoner.addAll(familierelasjoner)
        }

        Gitt<DataTable>("følgende personhistorikk for relaterte personer fra TPS") { dataTable: DataTable? ->
            val relatertePersoner = domenespråkParser.mapDataTable(dataTable, PersonhistorikkRelatertePersonerMapper())
            personHistorikkRelatertePersoner.addAll(relatertePersoner)
        }

        Gitt("følgende medlemsunntak fra MEDL") { dataTable: DataTable? ->
            medlemskap = domenespråkParser.mapDataTable(dataTable, MedlemskapMapper())
        }

        Gitt("følgende arbeidsforhold fra AAReg") { dataTable: DataTable? ->
            arbeidsforhold = domenespråkParser.mapArbeidsforhold(dataTable)
        }

        Gitt("følgende arbeidsgiver i arbeidsforholdet") { dataTable: DataTable? ->
            val arbeidsgivere = domenespråkParser.mapDataTable(dataTable, ArbeidsgiverMapper())

            arbeidsgiverMap[0] = arbeidsgivere[0]
        }

        Gitt("følgende arbeidsgiver i arbeidsforhold {int}") { arbeidsforholdRad: Int?, dataTable: DataTable? ->
            val arbeidsgivere = domenespråkParser.mapDataTable(dataTable, ArbeidsgiverMapper())
            val arbeidsforholdIndeks = arbeidsforholdIndeks(arbeidsforholdRad)

            arbeidsgiverMap[arbeidsforholdIndeks] = arbeidsgivere[0]
        }

        Gitt("følgende arbeidsavtaler i arbeidsforholdet") { dataTable: DataTable? ->
            arbeidsavtaleMap[0] = domenespråkParser.mapDataTable(dataTable, ArbeidsavtaleMapper())
        }

        Gitt("følgende arbeidsavtaler i arbeidsforhold {int}") { arbeidsforholdRad: Int?, dataTable: DataTable? ->
            val arbeidsavtaler = domenespråkParser.mapDataTable(dataTable, ArbeidsavtaleMapper())
            val arbeidsforholdIndeks = arbeidsforholdIndeks(arbeidsforholdRad)

            arbeidsavtaleMap[arbeidsforholdIndeks] = arbeidsavtaler
        }

        Gitt("følgende utenlandsopphold i arbeidsforholdet") { dataTable: DataTable? ->
            utenlandsoppholdMap[0] = domenespråkParser.mapDataTable(dataTable, UtenlandsoppholdMapper())
        }

        Gitt("følgende utenlandsopphold i arbeidsforhold {int}") { arbeidsforholdRad: Int?, dataTable: DataTable? ->
            val arbeidsforholdIndeks = arbeidsforholdIndeks(arbeidsforholdRad)

            utenlandsoppholdMap[arbeidsforholdIndeks] = domenespråkParser.mapDataTable(dataTable, UtenlandsoppholdMapper())
        }

        Gitt("følgende oppgaver fra Gosys") { dataTable: DataTable? ->
            oppgaverFraGosys = domenespråkParser.mapDataTable(dataTable, OppgaveMapper())
        }

        Gitt("følgende journalposter fra Joark") { dataTable: DataTable? ->
            journalPosterFraJoArk = domenespråkParser.mapDataTable(dataTable, JournalpostMapper())
        }

        Når("medlemskap beregnes med følgende parametre") { dataTable: DataTable? ->
            val medlemskapsparametre = domenespråkParser.mapDataTable(dataTable, MedlemskapsparametreMapper()).get(0)

            datagrunnlag = byggDatagrunnlag(medlemskapsparametre)
            resultat = ReglerService.kjørRegler(datagrunnlag!!)
        }

        Når<String, DataTable>("regel {string} kjøres med følgende parametre") { regelId: String?, dataTable: DataTable? ->
            val medlemskapsparametre = domenespråkParser.mapDataTable(dataTable, MedlemskapsparametreMapper()).get(0)
            datagrunnlag = byggDatagrunnlag(medlemskapsparametre)

            val reglerForLovvalg = ReglerForLovvalg.fraDatagrunnlag(datagrunnlag!!)
            val reglerForMedl = ReglerForMedl.fraDatagrunnlag(datagrunnlag!!)

            val regel = when (regelId!!) {
                "1.1" -> reglerForMedl.erPerioderAvklart
                "1.2" -> reglerForMedl.periodeMedOgUtenMedlemskap
                "1.3" -> reglerForMedl.periodeMedMedlemskap
                "1.3.1" -> reglerForMedl.erPeriodeUtenMedlemskapInnenfor12MndPeriode
                "1.3.2" -> reglerForMedl.erArbeidsforholdUendretForBrukerUtenMedlemskap
                "1.4" -> reglerForMedl.erPeriodeMedMedlemskapInnenfor12MndPeriode
                "1.5" -> reglerForMedl.erArbeidsforholdUendretForBrukerMedMedlemskap
                "1.6" -> reglerForMedl.erDekningUavklart
                "1.7" -> reglerForMedl.harBrukerDekningIMedl
                "9" -> reglerForLovvalg.harBrukerJobbetUtenforNorge
                "10" -> reglerForLovvalg.erBrukerBosattINorge
                "11" -> reglerForLovvalg.harBrukerNorskStatsborgerskap
                "11.2" -> reglerForLovvalg.harBrukerEktefelle
                "11.2.1" -> reglerForLovvalg.harBrukerBarnUtenEktefelle
                "11.2.2" -> reglerForLovvalg.harBrukerUtenEktefelleBarnSomErFolkeregistrert
                "11.2.3" -> reglerForLovvalg.harBrukerMedFolkeregistrerteBarnJobbetMerEnn80Prosent
                "11.3" -> reglerForLovvalg.harBrukerEktefelleOgBarn
                "11.4" -> reglerForLovvalg.erBrukerMedBarnSittEktefelleBosattINorge
                "11.5" -> reglerForLovvalg.erBrukerMedFolkeregistrertEktefelleSittBarnFolkeregistrert
                "11.6" -> reglerForLovvalg.harBrukerMedFolkeregistrerteRelasjonerJobbetMerEnn80Prosent
                "12" -> reglerForLovvalg.harBrukerJobbet25ProsentEllerMer
                else -> throw java.lang.RuntimeException("Ukjent regel")
            }

            resultat = regel.utfør()
        }

        Når("hovedregel med avklaring {string} kjøres med følgende parametre") { avklaring: String, dataTable: DataTable? ->
            val medlemskapsparametre = domenespråkParser.mapDataTable(dataTable, MedlemskapsparametreMapper()).get(0)
            datagrunnlag = byggDatagrunnlag(medlemskapsparametre)

            resultat = when (avklaring) {
                "Finnes det registrerte opplysninger på bruker?" -> evaluerReglerForMedlemsopplysninger(datagrunnlag!!)
                "Er bruker omfattet av grunnforordningen?" -> evaluerGrunnforordningen(datagrunnlag!!)
                else -> throw java.lang.RuntimeException("Fant ikke hovedregel med avklaring = $avklaring")
            }
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
                personhistorikk = personhistorikkBuilder.build(),
                pdlpersonhistorikk = pdlPersonhistorikkBuilder.build(),
                medlemskap = medlemskap,
                arbeidsforhold = byggArbeidsforhold(arbeidsforhold, arbeidsgiverMap, arbeidsavtaleMap, utenlandsoppholdMap),
                oppgaver = oppgaverFraGosys,
                dokument = journalPosterFraJoArk,
                ytelse = ytelse,
                personHistorikkRelatertePersoner = personHistorikkRelatertePersoner
        )
    }

    private fun byggArbeidsforhold(
            arbeidsforholdListe: List<Arbeidsforhold>,
            arbeidsgiverMap: Map<Int, Arbeidsgiver>,
            arbeidsavtaleMap: Map<Int, List<Arbeidsavtale>>,
            utenlandsoppholdMap: Map<Int, List<Utenlandsopphold>>): List<Arbeidsforhold> {
        return arbeidsforholdListe
                .mapIndexed { index, arbeidsforhold ->
                    arbeidsforhold.copy(
                            utenlandsopphold = utenlandsoppholdMap[index] ?: emptyList(),
                            arbeidsgiver = arbeidsgiverMap[index] ?: VANLIG_NORSK_ARBEIDSGIVER,
                            arbeidsavtaler = arbeidsavtaleMap[index] ?: emptyList()
                    )
                }
    }

    private fun evaluerGrunnforordningen(datagrunnlag: Datagrunnlag): Resultat {
        val regelsett = ReglerForGrunnforordningen.fraDatagrunnlag(datagrunnlag)
        return regelsett.hentHovedRegel().utfør(mutableListOf())
    }

    private fun evaluerReglerForMedlemsopplysninger(datagrunnlag: Datagrunnlag): Resultat {
        val regelsett = ReglerForRegistrerteOpplysninger.fraDatagrunnlag(datagrunnlag)
        return regelsett.hentHovedRegel().utfør(mutableListOf())
    }

}