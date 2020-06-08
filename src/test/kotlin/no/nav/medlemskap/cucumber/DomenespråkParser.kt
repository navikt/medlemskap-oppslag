package no.nav.medlemskap.cucumber

import io.cucumber.datatable.DataTable
import no.nav.medlemskap.domene.Adresse
import no.nav.medlemskap.domene.InputPeriode
import no.nav.medlemskap.domene.Statsborgerskap
import no.nav.medlemskap.regler.common.Datohjelper
import no.nav.medlemskap.regler.common.Svar
import java.time.LocalDate

class DomenespråkParser {
    fun parseDato(nøkkel: String, rad: Map<String, String>): LocalDate {

        return Datohjelper.parseDato(verdi(nøkkel, rad))
    }

    fun parseValgfriDato(nøkkel: String, rad: Map<String, String?>): LocalDate? {
        if (rad.get(nøkkel) == null || rad.get(nøkkel) == "") {
            return null
        }

        return Datohjelper.parseDato(rad.get(nøkkel)!!)
    }

    fun parseString(nøkkel: String, rad: Map<String, String>): String {

        return verdi(nøkkel, rad)
    }

    fun parseBoolean(nøkkel: String, rad: Map<String, String>): Boolean {
        val verdi = verdi(nøkkel, rad)

        return when(verdi) {
            "Ja" -> true
            else -> false
        }
    }

    fun parseSvar(nøkkel: String, rad: Map<String, String>): Svar {
        return parseSvar(verdi(nøkkel, rad))
    }

    fun parseSvar(verdi: String): Svar {
        return when(verdi) {
            "Ja" -> Svar.JA
            "Nei" -> Svar.NEI
            else -> Svar.UAVKLART
        }
    }

    private fun verdi(nøkkel: String, rad: Map<String, String>): String {
        val verdi = rad.get(nøkkel)

        if (verdi == null || verdi == "") {
            throw java.lang.RuntimeException("Fant ingen verdi for $nøkkel")
        }

        return verdi
    }

    fun <T>mapDataTable(dataTable: DataTable?, radMapper: RadMapper<T>): List<T> {
        if (dataTable == null) {
            return emptyList()
        }

        return dataTable!!.asMaps().map{ radMapper.mapRad(this, it) }
    }
}

interface RadMapper<T> {
    fun mapRad(domenespråkParser: DomenespråkParser, rad: Map<String, String>): T
}

class StatsborgerskapMapper: RadMapper<Statsborgerskap> {
    override fun mapRad(domenespråkParser: DomenespråkParser, rad: Map<String, String>): Statsborgerskap {
        return Statsborgerskap(
                domenespråkParser.parseString("Landkode", rad),
                domenespråkParser.parseDato("Fra og med dato", rad),
                domenespråkParser.parseValgfriDato("Til og med dato", rad)
        )
    }
}

class InputPeriodeMapper: RadMapper<InputPeriode> {
    override fun mapRad(domenespråkParser: DomenespråkParser, rad: Map<String, String>): InputPeriode {
        return InputPeriode(
                domenespråkParser.parseDato("Fra og med dato", rad),
                domenespråkParser.parseDato("Til og med dato", rad)
        )
    }
}

class MedlemskapsparametreMapper: RadMapper<Medlemskapsparametre> {
    override fun mapRad(domenespråkParser: DomenespråkParser, rad: Map<String, String>): Medlemskapsparametre {
        return Medlemskapsparametre(
                InputPeriode(
                        domenespråkParser.parseDato("Fra og med dato", rad),
                        domenespråkParser.parseDato("Til og med dato", rad)
                ),
                domenespråkParser.parseBoolean("Har hatt arbeid utenfor Norge", rad)
        )
    }
}

class AdresseMapper: RadMapper<Adresse> {
    override fun mapRad(domenespråkParser: DomenespråkParser, rad: Map<String, String>): Adresse {
        return Adresse(
                        domenespråkParser.parseString("Adressse", rad),
                        domenespråkParser.parseString("Landkode", rad),
                        domenespråkParser.parseValgfriDato("Fra og med dato", rad),
                        domenespråkParser.parseValgfriDato("Til og med dato", rad)
                )
    }
}

data class Medlemskapsparametre(val inputPeriode: InputPeriode, val harHattArbeidUtenforNorge: Boolean)


