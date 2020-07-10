package no.nav.medlemskap.regler.v1

import no.nav.medlemskap.domene.Adresse
import no.nav.medlemskap.domene.Datagrunnlag
import no.nav.medlemskap.domene.InputPeriode
import no.nav.medlemskap.domene.Ytelse
import no.nav.medlemskap.regler.common.*
import no.nav.medlemskap.regler.common.Funksjoner.alleEr
import no.nav.medlemskap.regler.common.Funksjoner.erIkkeTom
import no.nav.medlemskap.regler.common.Funksjoner.erTom
import no.nav.medlemskap.regler.funksjoner.AdresseFunksjoner.adresserForKontrollPeriode
import no.nav.medlemskap.regler.funksjoner.AdresseFunksjoner.landkodeTilAdresserForKontrollPeriode

class ErBrukerBosattINorgeRegel(
        val postadresser: List<Adresse>,
        val bostedsadresser: List<Adresse>,
        val midlertidigAdresser: List<Adresse>,
        val ytelse: Ytelse,
        val periode: InputPeriode
) {
    val regel = Regel(
            identifikator = "10",
            avklaring = "Er bruker folkeregistrert som bosatt i Norge og har vært det i 12 mnd?",
            beskrivelse = "",
            ytelse = ytelse,
            operasjon = { sjekkLandkode() }
    )

    private fun sjekkLandkode(): Resultat {
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