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
    private val ytelseDomenespråkParser = YtelseDomenespråkParser

    private var ytelsePeriode: InputPeriode? = null
    private var førsteDagForYtelse: LocalDate? = null
    private var kontrollperiode: Kontrollperiode? = null

    init {
        Gitt("følgende sykemeldingsperiode:") { dataTable: DataTable? ->
            ytelsePeriode = DomenespråkParser.mapInputPeriode(dataTable)
        }

        Når("første sykedag beregnes fra sykemeldingsperiode") {
            førsteDagForYtelse = Datohjelper(ytelsePeriode!!, null, Ytelse.SYKEPENGER).førsteDatoForYtelse()
        }

        Når<DataTable>("kontrollperiode for arbeidsforhold beregnes med følgende parametre:") { dataTable: DataTable? ->
            val parametreKontrollperiode = ytelseDomenespråkParser.mapParametreKontrollperiode(dataTable!!)

            ytelsePeriode = parametreKontrollperiode.iputPeriode
            val datohjelper = Datohjelper(
                periode = ytelsePeriode!!,
                førsteDagForYtelse = parametreKontrollperiode.førsteDagForYtelse,
                ytelse = parametreKontrollperiode.ytelse
            )

            kontrollperiode = datohjelper.kontrollPeriodeForArbeidsforhold()
        }

        Når<DataTable>("kontrollperiode for medl beregnes med følgende parametre:") { dataTable: DataTable? ->
            val parametreKontrollperiode = ytelseDomenespråkParser.mapParametreKontrollperiode(dataTable!!)

            ytelsePeriode = parametreKontrollperiode.iputPeriode
            val datohjelper = Datohjelper(
                periode = ytelsePeriode!!,
                førsteDagForYtelse = parametreKontrollperiode.førsteDagForYtelse,
                ytelse = parametreKontrollperiode.ytelse
            )

            kontrollperiode = datohjelper.kontrollPeriodeForMedl()
        }

        Så("skal første sykedag være {string}") { forventetDato: String? ->
            assertEquals(parseDato(forventetDato!!), førsteDagForYtelse)
        }

        Så<DataTable>("skal kontrollperioden være:") { dataTable: DataTable? ->
            val forventetKontrollperiode = ytelseDomenespråkParser.mapKontrollperiode(dataTable!!)
            assertThat(kontrollperiode).isEqualTo(forventetKontrollperiode)
        }
    }
}
