package no.nav.medlemskap.cucumber.steps

import io.cucumber.datatable.DataTable
import io.cucumber.java8.No

import junit.framework.Assert.assertEquals
import no.nav.medlemskap.cucumber.DomenespråkParser
import no.nav.medlemskap.cucumber.InputPeriodeMapper
import no.nav.medlemskap.domene.InputPeriode
import no.nav.medlemskap.regler.common.Datohjelper
import no.nav.medlemskap.regler.common.Datohjelper.Companion.parseDato
import java.time.LocalDate

class PeriodeSteps: No {
    private var sykemeldingsperiode: InputPeriode? = null
    private var førsteSykedag: LocalDate? = null

    private val domenespråkParser = DomenespråkParser()

    init {
        Gitt("følgende sykemeldingsperiode:") {
            dataTable: DataTable? ->
            sykemeldingsperiode = domenespråkParser.mapDataTable(dataTable, InputPeriodeMapper()).get(0)
        }

        Når("første sykedag beregnes fra sykemeldingsperiode") {
            førsteSykedag = Datohjelper(sykemeldingsperiode!!).førsteSykedag()
        }

        Så("skal første sykedag være {string}") { forventetDato: String? ->
            assertEquals(parseDato(forventetDato!!), førsteSykedag)
        }
    }

}