package no.nav.medlemskap.regler.funksjoner

import no.bekk.bekkopen.person.FodselsnummerValidator
import no.nav.medlemskap.domene.*
import no.nav.medlemskap.regler.common.Funksjoner
import no.nav.medlemskap.regler.common.lagInterval

object RelasjonFunksjoner {

    fun List<Sivilstand>.hentFnrTilEktefellerEllerPartnerIPeriode(kontrollPeriode: Periode): List<String?> =
            this.sivilstandSiste12Mnd(kontrollPeriode).filter {
             it.type == Sivilstandstype.GIFT || it.type == Sivilstandstype.REGISTRERT_PARTNER}.map { it.relatertVedSivilstand }

    //Todo --> Barn hele perioden, er adoptivbarn registrert med annen type
    fun List<Familierelasjon>.hentFnrTilBarnUnder25(): List<String?> =
            this.filter { it.relatertPersonsRolle == Familierelasjonsrolle.BARN && FodselsnummerValidator.isValid(it.relatertPersonsIdent)}.map {
                it.relatertPersonsIdent.filtrerBarnUnder25Aar() }

    fun List<PersonhistorikkRelatertPerson>.hentRelatertSomFinnesITPS(relatert: List<String?>?):
            List<PersonhistorikkRelatertPerson> =
            this.filter { relatert?.contains(it.ident) ?: false && FodselsnummerValidator.isValid(it.ident)}


}

//Todo regne ut om barnet er under 25 eller er ikke disse registrert da?
fun String.filtrerBarnUnder25Aar() = this

//Todo sjekke tom på sivilstand
fun Sivilstand.sivilstandPeriodeOverlapperKontrollPerioden(kontrollPeriode: Periode) =
        Funksjoner.periodefilter(lagInterval(Periode(this.gyldigFraOgMed, kontrollPeriode.tom)), kontrollPeriode)

fun List<Sivilstand>.sivilstandSiste12Mnd(kontrollPeriode: Periode): List<Sivilstand> =
        this.filter {it.sivilstandPeriodeOverlapperKontrollPerioden(kontrollPeriode) }





