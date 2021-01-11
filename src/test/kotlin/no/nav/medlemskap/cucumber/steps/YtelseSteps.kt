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
import no.nav.medlemskap.domene.Kontrollperiode.Companion.kontrollPeriodeForArbeidsforhold
import no.nav.medlemskap.domene.Kontrollperiode.Companion.kontrollPeriodeForMedl
import no.nav.medlemskap.domene.Kontrollperiode.Companion.startDatoForYtelse
import no.nav.medlemskap.regler.common.Datohjelper.parseDato
import java.time.LocalDate

class YtelseSteps : No {
    private val ytelseDomenespråkParser = YtelseDomenespråkParser

    private var ytelsePeriode: InputPeriode? = null
    private var førsteDagForYtelse: LocalDate? = null
    private var kontrollperiode: Kontrollperiode? = null

    init {
        Gitt("følgende sykemeldingsperiode:") { dataTable: DataTable? ->
            ytelsePeriode = DomenespråkParser.mapInputPeriode(dataTable)
        }

        Når("første sykedag beregnes fra sykemeldingsperiode") {
            førsteDagForYtelse = Kontrollperiode.startDatoForYtelse(ytelsePeriode!!, null)
        }

        Når<DataTable>("kontrollperiode for arbeidsforhold beregnes med følgende parametre:") { dataTable: DataTable? ->
            val parametreKontrollperiode = ytelseDomenespråkParser.mapParametreKontrollperiode(dataTable!!)

            ytelsePeriode = parametreKontrollperiode.iputPeriode
            førsteDagForYtelse = parametreKontrollperiode.førsteDagForYtelse

            kontrollperiode = kontrollPeriodeForArbeidsforhold(startDatoForYtelse(ytelsePeriode!!, førsteDagForYtelse))
        }

        Når<DataTable>("kontrollperiode for medl beregnes med følgende parametre:") { dataTable: DataTable? ->
            val parametreKontrollperiode = ytelseDomenespråkParser.mapParametreKontrollperiode(dataTable!!)

            ytelsePeriode = parametreKontrollperiode.iputPeriode

            kontrollperiode = kontrollPeriodeForMedl(startDatoForYtelse(ytelsePeriode!!, parametreKontrollperiode.førsteDagForYtelse))
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
