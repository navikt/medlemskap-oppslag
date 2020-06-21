package no.nav.medlemskap.cucumber.steps

import io.cucumber.datatable.DataTable
import io.cucumber.java8.No
import no.nav.medlemskap.cucumber.*
import no.nav.medlemskap.domene.*
import no.nav.medlemskap.regler.common.Resultat
import no.nav.medlemskap.regler.common.Svar
import no.nav.medlemskap.regler.v1.ReglerForGrunnforordningen
import no.nav.medlemskap.regler.v1.ReglerService
import no.nav.medlemskap.services.ereg.Ansatte
import org.junit.jupiter.api.Assertions.assertEquals


class RegelSteps : No {
    private val ANSATTE_9 = listOf(Ansatte(9, null, null))
    private val VANLIG_NORSK_ARBEIDSGIVER = Arbeidsgiver(type = "BEDR", identifikator = "1", landkode = "NOR", ansatte = ANSATTE_9, konkursStatus = null)

    private var sykemeldingsperiode: InputPeriode? = null
    private var harHattArbeidUtenforNorge: Boolean = false

    private var statsborgerskap: List<Statsborgerskap> = emptyList()
    private var bostedsadresser: List<Adresse> = emptyList()
    private var medlemskap: List<Medlemskap> = emptyList()

    private var arbeidsgivere: List<Arbeidsgiver> = emptyList()
    private var arbeidsforhold: List<Arbeidsforhold> = emptyList()
    private var utenlandsopphold: List<Utenlandsopphold> = emptyList()
    private var resultat: Resultat? = null

    private val domenespråkParser = DomenespråkParser()

    private var svar: Svar? = null
    private var datagrunnlag: Datagrunnlag? = null

    init {
        Gitt("følgende statsborgerskap i personhistorikken") { dataTable: DataTable? ->
            statsborgerskap = domenespråkParser.mapDataTable(dataTable, StatsborgerskapMapper())
        }

        Gitt("følgende bostedsadresser i personhistorikken") { dataTable: DataTable? ->
            bostedsadresser = domenespråkParser.mapDataTable(dataTable, AdresseMapper())
        }

        Gitt("følgende medlemsunntak fra MEDL") { dataTable: DataTable? ->
            medlemskap = domenespråkParser.mapDataTable(dataTable, MedlemskapMapper())
        }

        Gitt("følgende arbeidsforhold fra AAReg") { dataTable: DataTable? ->
            arbeidsforhold = domenespråkParser.mapArbeidsforhold(dataTable, utenlandsopphold, VANLIG_NORSK_ARBEIDSGIVER)
        }

        Gitt("følgende arbeidsgiver i arbeidsforholdet") { dataTable: DataTable? ->
            arbeidsgivere = domenespråkParser.mapDataTable(dataTable, ArbeidsgiverMapper())
        }

        Gitt("følgende utenlandsopphold i arbeidsforholdet") { dataTable: DataTable? ->
            utenlandsopphold = domenespråkParser.mapDataTable(dataTable, UtenlandsoppholdMapper())
        }

        Når("omfattet av grunnforordningen beregnes med følgende parametre") { dataTable: DataTable? ->
            val medlemskapsparametre = domenespråkParser.mapDataTable(dataTable, MedlemskapsparametreMapper()).get(0)

            sykemeldingsperiode = medlemskapsparametre.inputPeriode
            harHattArbeidUtenforNorge = medlemskapsparametre.harHattArbeidUtenforNorge

            datagrunnlag = byggDatagrunnlag()
            svar = evaluerGrunnforordningen(datagrunnlag!!)
        }

        Når("medlemskap beregnes med følgende parametre") { dataTable: DataTable? ->
            val medlemskapsparametre = domenespråkParser.mapDataTable(dataTable, MedlemskapsparametreMapper()).get(0)

            sykemeldingsperiode = medlemskapsparametre.inputPeriode
            harHattArbeidUtenforNorge = medlemskapsparametre.harHattArbeidUtenforNorge

            datagrunnlag = byggDatagrunnlag()

            resultat = ReglerService.kjørRegler(datagrunnlag!!)
        }

        Så("skal omfattet av grunnforordningen være {string}") { forventetSvar: String? ->
            assertEquals(domenespråkParser.parseSvar(forventetSvar!!), svar)
        }

        Så("skal medlemskap være {string}") { forventetVerdi: String ->
            val forventetSvar = domenespråkParser.parseSvar(forventetVerdi)

            assertEquals(forventetSvar, resultat!!.svar)
        }
    }

    private fun evaluerGrunnforordningen(datagrunnlag: Datagrunnlag): Svar {
        val regelsett = ReglerForGrunnforordningen(datagrunnlag)
        return regelsett.hentHovedRegel().utfør(mutableListOf()).svar
    }

    private fun byggDatagrunnlag(): Datagrunnlag {
        return Datagrunnlag(
                periode = sykemeldingsperiode!!,
                brukerinput = Brukerinput(harHattArbeidUtenforNorge),
                personhistorikk = Personhistorikk(
                        statsborgerskap = statsborgerskap,
                        personstatuser = emptyList(),
                        bostedsadresser = bostedsadresser,
                        postadresser = emptyList(),
                        midlertidigAdresser = emptyList()
                ),
                medlemskap = medlemskap,
                arbeidsforhold = byggArbeidsforhold(arbeidsforhold, arbeidsgivere, utenlandsopphold)
        )
    }

    private fun byggArbeidsforhold(arbeidsforhold: List<Arbeidsforhold>, arbeidsgivere: List<Arbeidsgiver>, utenlandsopphold: List<Utenlandsopphold>): List<Arbeidsforhold> {
        val arbeidsgiver = if (arbeidsgivere.isEmpty()) {
            VANLIG_NORSK_ARBEIDSGIVER
        } else {
            arbeidsgivere[0]
        }

        return arbeidsforhold.map { it.copy(utenlandsopphold = utenlandsopphold, arbeidsgiver = arbeidsgiver) }
    }
}