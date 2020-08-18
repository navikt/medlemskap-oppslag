package no.nav.medlemskap.regler.v1

import no.nav.medlemskap.domene.Adresse
import no.nav.medlemskap.domene.Datagrunnlag
import no.nav.medlemskap.domene.InputPeriode
import no.nav.medlemskap.domene.Ytelse
import no.nav.medlemskap.regler.common.*
import no.nav.medlemskap.regler.common.Funksjoner.alleEr
import no.nav.medlemskap.regler.common.Funksjoner.erIkkeTom
import no.nav.medlemskap.regler.common.Funksjoner.erTom
import no.nav.medlemskap.regler.common.RegelId.REGEL_10
import no.nav.medlemskap.regler.funksjoner.AdresseFunksjoner.adresserForKontrollPeriode
import no.nav.medlemskap.regler.funksjoner.AdresseFunksjoner.landkodeTilAdresserForKontrollPeriode

class ErBrukerBosattINorgeRegel(
        val postadresser: List<Adresse>,
        val bostedsadresser: List<Adresse>,
        val midlertidigAdresser: List<Adresse>,
        ytelse: Ytelse,
        val periode: InputPeriode
): BasisRegel(REGEL_10, ytelse) {

    override fun operasjon(): Resultat {
        val datohjelper = Datohjelper(periode, ytelse)
        val kontrollPeriodeForPersonhistorikk = datohjelper.kontrollPeriodeForPersonhistorikk()
        val bostedsadresser = bostedsadresser.adresserForKontrollPeriode(kontrollPeriodeForPersonhistorikk)
        val postadresserLandkoder = postadresser.landkodeTilAdresserForKontrollPeriode(kontrollPeriodeForPersonhistorikk)
        val midlertidigadresserLandkoder = midlertidigAdresser.landkodeTilAdresserForKontrollPeriode(kontrollPeriodeForPersonhistorikk)

        return when {
            bostedsadresser.erIkkeTom()
                    && (postadresserLandkoder alleEr ReglerForLovvalg.NorskLandkode.NOR.name || postadresserLandkoder.erTom())
                    && (midlertidigadresserLandkoder alleEr ReglerForLovvalg.NorskLandkode.NOR.name || midlertidigadresserLandkoder.erTom()) -> ja()
            else -> nei("Ikke alle adressene til bruker er norske, eller bruker mangler bostedsadresse")
        }
    }

    companion object {
        fun fraDatagrunnlag(datagrunnlag: Datagrunnlag): ErBrukerBosattINorgeRegel {
            return ErBrukerBosattINorgeRegel(
                    postadresser = datagrunnlag.personhistorikk.postadresser,
                    bostedsadresser = datagrunnlag.personhistorikk.bostedsadresser,
                    midlertidigAdresser = datagrunnlag.personhistorikk.midlertidigAdresser,
                    ytelse = datagrunnlag.ytelse,
                    periode = datagrunnlag.periode)
        }
    }
}