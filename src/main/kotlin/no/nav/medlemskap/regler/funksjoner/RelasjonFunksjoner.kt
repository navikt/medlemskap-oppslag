package no.nav.medlemskap.regler.funksjoner

import no.bekk.bekkopen.person.FodselsnummerValidator
import no.nav.medlemskap.domene.*
import no.nav.medlemskap.regler.common.Funksjoner
import no.nav.medlemskap.regler.common.lagInterval
import java.util.*

object RelasjonFunksjoner {

    fun List<Sivilstand>.hentFnrTilEktefellerEllerPartnerIPeriode(kontrollPeriode: Periode): List<String?> =
            this.sivilstandSiste12Mnd(kontrollPeriode).filter {
             it.type == Sivilstandstype.GIFT || it.type == Sivilstandstype.REGISTRERT_PARTNER}.map { it.relatertVedSivilstand }

    fun List<Familierelasjon>.hentFnrTilBarnUnder25(): List<String?> =
            this.filter {
                         it.relatertPersonsRolle == Familierelasjonsrolle.BARN
                        && FodselsnummerValidator.isValid(it.relatertPersonsIdent)
                        && it.relatertPersonsIdent.filtrerBarnUnder25Aar()}
                        .map { it.relatertPersonsIdent}

    fun List<PersonhistorikkRelatertPerson>.hentRelatertSomFinnesITPS(relatert: List<String?>?):
            List<PersonhistorikkRelatertPerson> =
            this.filter { relatert?.contains(it.ident) ?: false && FodselsnummerValidator.isValid(it.ident)}}

    fun String.filtrerBarnUnder25Aar() =
            (Calendar.getInstance().get(Calendar.YEAR) - this.hentBursdagsAar().toInt()) <= 25

    fun String.hentBursdagsAar(): String {
      return this.hentAarHundre() + this.hent2DigitBursdagsAar()
    }

    fun String.hentAarHundre(): String? {
      var resultat: String? = null
      val individnummer: Int = getIndividnummer().toInt()
      val birthYear: Int = hent2DigitBursdagsAar().toInt()

        if (individnummer <= 499) {
            resultat = "19"
        } else if (individnummer >= 500 && birthYear < 40) {
            resultat = "20"
        } else if (individnummer >= 500 && individnummer <= 749 && birthYear >= 54) {
            resultat = "18"
        } else if (individnummer >= 900 && birthYear > 39) {
            resultat = "19"
        }
          return resultat
    }

    fun String.hent2DigitBursdagsAar() = this.substring(4, 6)

    fun String.getIndividnummer() = this.substring(6, 9)

    //Todo sjekke tom på sivilstand
    fun Sivilstand.sivilstandPeriodeOverlapperKontrollPerioden(kontrollPeriode: Periode) =
        Funksjoner.periodefilter(lagInterval(Periode(this.gyldigFraOgMed, kontrollPeriode.tom)), kontrollPeriode)

    fun List<Sivilstand>.sivilstandSiste12Mnd(kontrollPeriode: Periode): List<Sivilstand> =
        this.filter {it.sivilstandPeriodeOverlapperKontrollPerioden(kontrollPeriode) }





