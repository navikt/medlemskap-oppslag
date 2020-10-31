package no.nav.medlemskap.cucumber.steps

import assertk.assertThat
import assertk.assertions.isEqualTo
import io.cucumber.datatable.DataTable
import io.cucumber.java8.No
import junit.framework.TestCase.assertEquals
import no.nav.medlemskap.cucumber.DomenespråkParser
import no.nav.medlemskap.cucumber.ytelse.YtelseDomenespråkParser
import no.nav.medlemskap.domene.InputPeriode
import no.nav.medlemskap.domene.Kontrollperiode
import no.nav.medlemskap.domene.Ytelse
import no.nav.medlemskap.regler.common.Datohjelper
import no.nav.medlemskap.regler.common.Datohjelper.Companion.parseDato
import java.time.LocalDate

class YtelseSteps : No {
    private val domenespråkParser = DomenespråkParser
    private val ytelseDomenespråkParser = YtelseDomenespråkParser()

    private var sykemeldingsperiode: InputPeriode? = null
    private var førsteSykedag: LocalDate? = null
    private var kontrollperiode: Kontrollperiode? = null

    init {
        Gitt("følgende sykemeldingsperiode:") { dataTable: DataTable? ->
            sykemeldingsperiode = DomenespråkParser.mapInputPeriode(dataTable)
        }

        Når("første sykedag beregnes fra sykemeldingsperiode") {
            førsteSykedag = Datohjelper(sykemeldingsperiode!!, null, Ytelse.SYKEPENGER).førsteSykedag()
        }

        Når<DataTable>("kontrollperiode for arbeidsforhold beregnes med følgende parametre:") { dataTable: DataTable? ->
            val parametreKontrollperiode = ytelseDomenespråkParser.mapParametreKontrollperiode(dataTable!!)
            sykemeldingsperiode = parametreKontrollperiode.iputPeriode

            kontrollperiode = Datohjelper(sykemeldingsperiode!!, parametreKontrollperiode.førsteDagForYtelse, Ytelse.SYKEPENGER).kontrollPeriodeForArbeidsforhold()
        }

        Så("skal første sykedag være {string}") { forventetDato: String? ->
            assertEquals(parseDato(forventetDato!!), førsteSykedag)
        }

        Så<DataTable>("skal kontrollperioden være:") { dataTable: DataTable? ->
            val forventetKontrollperiode = ytelseDomenespråkParser.mapKontrollperiode(dataTable!!)
            assertThat(kontrollperiode).isEqualTo(forventetKontrollperiode)
        }
    }
}
