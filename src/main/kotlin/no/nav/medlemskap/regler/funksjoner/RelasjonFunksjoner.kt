package no.nav.medlemskap.regler.funksjoner

import no.bekk.bekkopen.person.FodselsnummerValidator
import no.nav.medlemskap.domene.*
import java.time.LocalDate
import java.util.*

object RelasjonFunksjoner {

    fun List<Sivilstand>.hentFnrTilEktefellerEllerPartnerForDato(dato: LocalDate): List<String?> =
            this.filter { it.giftEllerRegistrertPartner() && it.overlapper(dato) }.map { it.relatertVedSivilstand }

    fun List<Familierelasjon>.hentFnrTilBarnUnder25(): List<String?> =
            this.filter {
                it.relatertPersonsRolle == Familierelasjonsrolle.BARN
                        && FodselsnummerValidator.isValid(it.relatertPersonsIdent)
                        && it.relatertPersonsIdent.filtrerBarnUnder25Aar()
            }.map { it.relatertPersonsIdent }

    fun List<PersonhistorikkRelatertPerson>.hentRelatertSomFinnesITPS(relatert: List<String?>?):
            List<PersonhistorikkRelatertPerson> =
            this.filter { relatert?.contains(it.ident) ?: false && FodselsnummerValidator.isValid(it.ident) }

    fun List<Personhistorikk?>.hentPersonHistorikkTilAlleBarn(barnITps: List<PersonhistorikkRelatertPerson>): List<Personhistorikk?> =
            this.filter { barn -> barnITps
                    .map { it.ident }.contains(barn?.ident) }

    fun List<Personhistorikk?>.hentFnrTilMorForAlleBarn(): List<String> =
            (this as List<Personhistorikk>).hentFamilieRelasjoner()
                    .filter { it.relatertPersonsRolle == Familierelasjonsrolle.MOR }
                    .map { it.relatertPersonsIdent }
}

fun List<Personhistorikk>.hentFamilieRelasjoner(): List<Familierelasjon> =
        this.flatMap { it.familierelasjoner }

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

