package no.nav.medlemskap.cucumber.steps

import io.cucumber.datatable.DataTable
import io.cucumber.java8.No
import junit.framework.TestCase.assertEquals
import no.nav.medlemskap.cucumber.AdresseMapper
import no.nav.medlemskap.cucumber.DomenespråkParser
import no.nav.medlemskap.cucumber.MedlemskapsparametreMapper
import no.nav.medlemskap.cucumber.StatsborgerskapMapper
import no.nav.medlemskap.domene.*
import no.nav.medlemskap.regler.assertDelresultat
import no.nav.medlemskap.regler.common.Resultat
import no.nav.medlemskap.regler.personer.Personleser
import no.nav.medlemskap.regler.v1.ReglerService

class RegelSteps: No {
    private var sykemeldingsperiode: InputPeriode? = null
    private var statsborgerskap: List<Statsborgerskap> = emptyList()
    private var bostedsadresser: List<Adresse> = emptyList()
    private var harHattArbeidUtenforNorge: Boolean = false
    private var resultat: Resultat? = null

    private var datagrunnlag: Datagrunnlag? = null

    private val domenespråkParser = DomenespråkParser()

    init {
        Gitt("følgende datagrunnlag json") { docString: String? ->
            datagrunnlag = Personleser().dataGrunnlagFraJson(docString!!)
        }


        Gitt("følgende statsborgerskap i personhistorikken") { dataTable: DataTable? ->
            statsborgerskap = domenespråkParser.mapDataTable(dataTable, StatsborgerskapMapper())
        }

        Gitt("følgende bostedsadresser i personhistorikken") { dataTable: DataTable? ->
            bostedsadresser = domenespråkParser.mapDataTable(dataTable, AdresseMapper())
        }

        Når("lovvalg og medlemskap beregnes med følgende parametre") { dataTable: DataTable? ->
            val medlemskapsparametre = domenespråkParser.mapDataTable(dataTable, MedlemskapsparametreMapper()).get(0)

            sykemeldingsperiode = medlemskapsparametre.inputPeriode
            harHattArbeidUtenforNorge = medlemskapsparametre.harHattArbeidUtenforNorge

            datagrunnlag = Datagrunnlag(
                    periode = sykemeldingsperiode!!,
                    brukerinput = Brukerinput(harHattArbeidUtenforNorge),
                    personhistorikk = Personhistorikk(
                            statsborgerskap = statsborgerskap,
                            personstatuser = emptyList(),
                            bostedsadresser = bostedsadresser,
                            postadresser = emptyList(),
                            midlertidigAdresser = emptyList()
                    )
            )

            resultat = ReglerService.kjørRegler(datagrunnlag!!)
        }

        Når("lovvalg og medlemskap beregnes fra datagrunnlag json") {
            resultat = ReglerService.kjørRegler(datagrunnlag!!)
        }

        Så("skal delresultat {string} være {string}") { regelIdentifikator: String?, forventetSvar: String? ->
            assertDelresultat(regelIdentifikator!!, domenespråkParser.parseSvar(forventetSvar!!), resultat!!)
        }

        Og("skal medlemskap i Folketrygden være {string}") {
            forventetVerdi: String ->
            val forventetSvar = domenespråkParser.parseSvar(forventetVerdi)

            assertEquals(forventetSvar, resultat!!.svar)
        }
    }

}