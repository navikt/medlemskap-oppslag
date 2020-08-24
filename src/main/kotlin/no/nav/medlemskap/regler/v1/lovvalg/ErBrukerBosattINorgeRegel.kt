package no.nav.medlemskap.regler.v1.lovvalg

import no.nav.medlemskap.domene.*
import no.nav.medlemskap.regler.common.Funksjoner.erIkkeTom
import no.nav.medlemskap.regler.common.Funksjoner.erTom
import no.nav.medlemskap.regler.common.RegelId.REGEL_10
import no.nav.medlemskap.regler.common.Resultat
import no.nav.medlemskap.regler.common.ja
import no.nav.medlemskap.regler.common.nei
import no.nav.medlemskap.regler.funksjoner.AdresseFunksjoner.adresserForKontrollPeriode
import no.nav.medlemskap.regler.funksjoner.AdresseFunksjoner.landkodeTilAdresserForKontrollPeriode

class ErBrukerBosattINorgeRegel(
        val postadresser: List<Adresse>,
        val bostedsadresser: List<Adresse>,
        val midlertidigAdresser: List<Adresse>,
        ytelse: Ytelse,
        val periode: InputPeriode
): LovvalgRegel(REGEL_10, ytelse, periode) {

    override fun operasjon(): Resultat {
        val bostedsadresser = bostedsadresser.adresserForKontrollPeriode(kontrollPeriodeForPersonhistorikk)
        val postadresserLandkoder = postadresser.landkodeTilAdresserForKontrollPeriode(kontrollPeriodeForPersonhistorikk)
        val midlertidigadresserLandkoder = midlertidigAdresser.landkodeTilAdresserForKontrollPeriode(kontrollPeriodeForPersonhistorikk)

        return when {
            bostedsadresser.erIkkeTom()
                    && (postadresserLandkoder.all { Eøsland.erNorsk(it)} || postadresserLandkoder.erTom())
                    && (midlertidigadresserLandkoder.all { Eøsland.erNorsk(it)} || midlertidigadresserLandkoder.erTom()) -> ja()
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