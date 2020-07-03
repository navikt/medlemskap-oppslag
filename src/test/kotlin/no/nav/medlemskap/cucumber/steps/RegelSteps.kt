package no.nav.medlemskap.cucumber.steps

import io.cucumber.datatable.DataTable
import io.cucumber.java8.No
import no.nav.medlemskap.cucumber.*
import no.nav.medlemskap.domene.*
import no.nav.medlemskap.regler.assertDelresultat
import no.nav.medlemskap.regler.common.Resultat
import no.nav.medlemskap.regler.v1.ReglerForGrunnforordningen
import no.nav.medlemskap.regler.v1.ReglerForLovvalg
import no.nav.medlemskap.regler.v1.ReglerForRegistrerteOpplysninger
import no.nav.medlemskap.regler.v1.ReglerService
import no.nav.medlemskap.services.ereg.Ansatte
import org.junit.jupiter.api.Assertions.assertEquals


class RegelSteps : No {
    private val ANSATTE_9 = listOf(Ansatte(9, null, null))
    private val VANLIG_NORSK_ARBEIDSGIVER = Arbeidsgiver(type = "BEDR", identifikator = "1", landkode = "NOR", ansatte = ANSATTE_9, konkursStatus = null)

    private var statsborgerskap: List<Statsborgerskap> = emptyList()
    private var bostedsadresser: List<Adresse> = emptyList()
    private var postadresser: List<Adresse> = emptyList()
    private var midlertidigAdresser: List<Adresse> = emptyList()
    private var personstatuser: List<FolkeregisterPersonstatus> = emptyList()

    private var medlemskap: List<Medlemskap> = emptyList()

    private var arbeidsgivere: List<Arbeidsgiver> = emptyList()
    private var arbeidsforhold: List<Arbeidsforhold> = emptyList()
    private var arbeidsavtaler: List<Arbeidsavtale> = emptyList()
    private var utenlandsopphold: List<Utenlandsopphold> = emptyList()
    private var resultat: Resultat? = null
    private var oppgaverFraGosys: List<Oppgave> = emptyList()
    private var journalPosterFraJoArk: List<Journalpost> = emptyList()

    private val domenespråkParser = DomenespråkParser()

    private var datagrunnlag: Datagrunnlag? = null

    init {
        Gitt("følgende statsborgerskap i personhistorikken") { dataTable: DataTable? ->
            statsborgerskap = domenespråkParser.mapDataTable(dataTable, StatsborgerskapMapper())
        }

        Gitt("følgende bostedsadresser i personhistorikken") { dataTable: DataTable? ->
            bostedsadresser = domenespråkParser.mapDataTable(dataTable, AdresseMapper())
        }

        Gitt("følgende postadresser i personhistorikken") { dataTable: DataTable? ->
            postadresser = domenespråkParser.mapDataTable(dataTable, AdresseMapper())
        }

        Gitt("følgende midlertidige adresser i personhistorikken") { dataTable: DataTable? ->
            midlertidigAdresser = domenespråkParser.mapDataTable(dataTable, AdresseMapper())
        }

        Gitt("følgende personstatuser i personhistorikken") { dataTable: DataTable? ->
            personstatuser = domenespråkParser.mapDataTable(dataTable, PersonstatusMapper())
        }

        Gitt("følgende medlemsunntak fra MEDL") { dataTable: DataTable? ->
            medlemskap = domenespråkParser.mapDataTable(dataTable, MedlemskapMapper())
        }

        Gitt("følgende arbeidsforhold fra AAReg") { dataTable: DataTable? ->
            arbeidsforhold = domenespråkParser.mapArbeidsforhold(dataTable, utenlandsopphold, arbeidsgivere)
        }

        Gitt("følgende arbeidsgiver i arbeidsforholdet") { dataTable: DataTable? ->
            arbeidsgivere = domenespråkParser.mapDataTable(dataTable, ArbeidsgiverMapper())
        }

        Gitt("følgende arbeidsavtaler i arbeidsforholdet") { dataTable: DataTable? ->
            arbeidsavtaler = domenespråkParser.mapDataTable(dataTable, ArbeidsavtaleMapper())
        }

        Gitt("følgende utenlandsopphold i arbeidsforholdet") { dataTable: DataTable? ->
            utenlandsopphold = domenespråkParser.mapDataTable(dataTable, UtenlandsoppholdMapper())
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

            val regel = when(regelId!!) {
                "9" -> reglerForLovvalg.harBrukerJobbetUtenforNorge
                "10" -> reglerForLovvalg.erBrukerBosattINorge
                "11" -> reglerForLovvalg.harBrukerNorskStatsborgerskap
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

        Så("skal medlemskap være {string}") { forventetVerdi: String ->
            val forventetSvar = domenespråkParser.parseSvar(forventetVerdi)

            assertEquals(forventetSvar, resultat!!.svar)
        }

        Så("skal svaret på hovedregelen være {string}") { forventetVerdi: String ->
            val forventetSvar = domenespråkParser.parseSvar(forventetVerdi)

            assertEquals(forventetSvar, resultat!!.svar)
        }

        Så("skal svaret være {string}") { forventetVerdi: String ->
            val forventetSvar = domenespråkParser.parseSvar(forventetVerdi)

            assertEquals(forventetSvar, resultat!!.svar)
        }

        Så("skal regelen gi svaret {string}") { forventetVerdi: String ->
            val forventetSvar = domenespråkParser.parseSvar(forventetVerdi)

            assertEquals(forventetSvar, resultat!!.svar)
        }

        Så("skal regel {string} gi svaret {string}") { regelIdentifikator: String?, forventetSvar: String? ->
            assertDelresultat(regelIdentifikator!!, domenespråkParser.parseSvar(forventetSvar!!), resultat!!)
        }
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
                personhistorikk = Personhistorikk(
                        statsborgerskap = statsborgerskap,
                        personstatuser = personstatuser,
                        bostedsadresser = bostedsadresser,

                        postadresser = postadresser,
                        midlertidigAdresser = midlertidigAdresser,
                        familierelasjoner = emptyList(),
                        sivilstand = emptyList()
                ),
                pdlpersonhistorikk = Personhistorikk(
                        statsborgerskap = statsborgerskap,
                        personstatuser = emptyList(),
                        bostedsadresser = bostedsadresser,
                        postadresser = emptyList(),
                        midlertidigAdresser = emptyList(),
                        familierelasjoner = emptyList(),
                        sivilstand = emptyList()
                ),
                medlemskap = medlemskap,
                arbeidsforhold = byggArbeidsforhold(arbeidsforhold, arbeidsgivere, arbeidsavtaler, utenlandsopphold),
                oppgaver = oppgaverFraGosys,
                dokument = journalPosterFraJoArk,
                ytelse = ytelse,
                personHistorikkRelatertePersoner = emptyList()
        )
    }

    private fun byggArbeidsforhold(arbeidsforhold: List<Arbeidsforhold>, arbeidsgivere: List<Arbeidsgiver>, arbeidsavtaler: List<Arbeidsavtale>, utenlandsopphold: List<Utenlandsopphold>): List<Arbeidsforhold> {
        val arbeidsgiver = if (arbeidsgivere.isEmpty()) {
            VANLIG_NORSK_ARBEIDSGIVER
        } else {
            arbeidsgivere[0]
        }

        return arbeidsforhold.map { it.copy(utenlandsopphold = utenlandsopphold, arbeidsgiver = arbeidsgiver, arbeidsavtaler = arbeidsavtaler) }
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