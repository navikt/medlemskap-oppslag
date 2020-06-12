package no.nav.medlemskap.cucumber.steps

import io.cucumber.datatable.DataTable
import io.cucumber.java8.No
import no.nav.medlemskap.cucumber.AdresseMapper
import no.nav.medlemskap.cucumber.DomenespråkParser
import no.nav.medlemskap.cucumber.MedlemskapsparametreMapper
import no.nav.medlemskap.cucumber.StatsborgerskapMapper
import no.nav.medlemskap.domene.*
import no.nav.medlemskap.regler.common.Personfakta.Companion.initialiserFakta
import no.nav.medlemskap.regler.common.Svar
import no.nav.medlemskap.regler.v1.ReglerForGrunnforordningen
import org.junit.jupiter.api.Assertions.assertEquals


class GrunnforordningSteps : No {
    private var sykemeldingsperiode: InputPeriode? = null
    private var harHattArbeidUtenforNorge: Boolean = false

    private var statsborgerskap: List<Statsborgerskap> = emptyList()
    private var bostedsadresser: List<Adresse> = emptyList()
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

        Når("omfattet av grunnforordningen EØS beregnes med følgende parametre") { dataTable: DataTable? ->
            val medlemskapsparametre = domenespråkParser.mapDataTable(dataTable, MedlemskapsparametreMapper()).get(0)

            sykemeldingsperiode = medlemskapsparametre.inputPeriode
            harHattArbeidUtenforNorge = medlemskapsparametre.harHattArbeidUtenforNorge

            datagrunnlag = Datagrunnlag(
                    periode = sykemeldingsperiode!!,
                    brukerinput = Brukerinput(harHattArbeidUtenforNorge),
                    personhistorikk = Personhistorikk(
                            statsborgerskap = statsborgerskap,
                            personstatuser = emptyList(),
                            bostedsadresser = emptyList(),
                            postadresser = emptyList(),
                            midlertidigAdresser = emptyList()
                    )
            )

            svar = evaluer(datagrunnlag!!)
        }

        Så("skal omfattet av grunnforordningen EØS være {string}") { forventetSvar: String? ->
            assertEquals(domenespråkParser.parseSvar(forventetSvar!!), svar)
        }
    }

    private fun evaluer(datagrunnlag: Datagrunnlag): Svar {
        val regelsett = ReglerForGrunnforordningen(initialiserFakta(datagrunnlag))
        return regelsett.hentHovedRegel().utfør(mutableListOf()).svar
    }
}