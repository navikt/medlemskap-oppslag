package no.nav.medlemskap.regler.funksjoner

import no.bekk.bekkopen.person.FodselsnummerValidator
import no.nav.medlemskap.domene.*
import java.util.*

object RelasjonFunksjoner {



    fun String.filtrerBarnUnder25Aar() =
        (Calendar.getInstance().get(Calendar.YEAR) - this.hentBursdagsAar().toInt()) <= 25

    fun String.hentBursdagsAar(): String {
        return this.hentAarHundre() + this.hent2DigitBursdagsAar()
    }

    fun String.hentAarHundre(): String? {
        val individnummer: Int = getIndividnummer().toInt()
        val birthYear: Int = hent2DigitBursdagsAar().toInt()

        return when {
            individnummer <= 499 -> "19"
            individnummer >= 500 && birthYear < 40 -> "20"
            individnummer in 500..749 && birthYear >= 54 -> "18"
            individnummer >= 900 && birthYear > 39 -> "19"
            else -> null
        }
    }

    fun String.hent2DigitBursdagsAar() = this.substring(4, 6)

    fun String.getIndividnummer() = this.substring(6, 9)

    fun hentFnrTilEktefelle(personHistorikkFraPdl: Personhistorikk?): String? {
        val fnrTilEktefelle =
                personHistorikkFraPdl?.sivilstand
                        ?.filter { it.relatertVedSivilstand != null }
                        ?.filter { FodselsnummerValidator.isValid(it.relatertVedSivilstand) }
                        ?.filter { it.type == Sivilstandstype.GIFT || it.type == Sivilstandstype.REGISTRERT_PARTNER }
                        ?.map { it.relatertVedSivilstand }
                        ?.lastOrNull()
        return fnrTilEktefelle
    }

    fun hentFnrTilBarn(familierelasjoner: List<Familierelasjon>): List<String> {
        return familierelasjoner
                .filter { FodselsnummerValidator.isValid(it.relatertPersonsIdent) }
                .filter { it.erBarn() }
                .filter { it.relatertPersonsIdent.filtrerBarnUnder25Aar()}
                .map { it.relatertPersonsIdent }
    }

    fun Familierelasjon.erBarn() = this.relatertPersonsRolle == Familierelasjonsrolle.BARN




}
