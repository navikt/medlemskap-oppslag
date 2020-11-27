package no.nav.medlemskap.regler.v1.lovvalg

import no.nav.medlemskap.domene.*
import no.nav.medlemskap.regler.common.Funksjoner.alleEr
import no.nav.medlemskap.regler.common.Funksjoner.erIkkeTom
import no.nav.medlemskap.regler.common.Funksjoner.erTom
import no.nav.medlemskap.regler.common.RegelId.REGEL_10
import no.nav.medlemskap.regler.common.Resultat
import no.nav.medlemskap.regler.common.Resultat.Companion.ja
import no.nav.medlemskap.regler.common.Resultat.Companion.nei
import no.nav.medlemskap.regler.funksjoner.AdresseFunksjoner.adresserForKontrollPeriode
import no.nav.medlemskap.regler.funksjoner.AdresseFunksjoner.landkodeTilAdresserForKontrollPeriode
import java.time.LocalDate

class ErBrukerBosattINorgeRegel(
    val kontaktadresser: List<Adresse>,
    val bostedsadresser: List<Adresse>,
    val oppholdsadresser: List<Adresse>,
    ytelse: Ytelse,
    val periode: InputPeriode,
    førsteDagForYtelse: LocalDate?
) : LovvalgRegel(REGEL_10, ytelse, periode, førsteDagForYtelse) {

    override fun operasjon(): Resultat {
        val bostedsadresser = bostedsadresser.adresserForKontrollPeriode(kontrollPeriodeForPersonhistorikk)
        val landkoderBostedsadresse = bostedsadresser.map { it.landkode }
        val kontaktadresserLandkoder = kontaktadresser.landkodeTilAdresserForKontrollPeriode(kontrollPeriodeForPersonhistorikk)
        val oppholsadresserLandkoder = oppholdsadresser.landkodeTilAdresserForKontrollPeriode(kontrollPeriodeForPersonhistorikk)

        return when {
            bostedsadresser.erIkkeTom() && landkoderBostedsadresse alleEr "NOR" &&
                (kontaktadresserLandkoder.all { Eøsland.erNorsk(it) } || kontaktadresserLandkoder.erTom())
                && (oppholsadresserLandkoder.all { Eøsland.erNorsk(it) } || oppholsadresserLandkoder.erTom()) -> ja(regelId)
            else -> nei(regelId)
        }
    }

    companion object {
        fun fraDatagrunnlag(datagrunnlag: Datagrunnlag): ErBrukerBosattINorgeRegel {
            return ErBrukerBosattINorgeRegel(
                kontaktadresser = datagrunnlag.pdlpersonhistorikk.kontaktadresser,
                bostedsadresser = datagrunnlag.pdlpersonhistorikk.bostedsadresser,
                oppholdsadresser = datagrunnlag.pdlpersonhistorikk.oppholdsadresser,
                ytelse = datagrunnlag.ytelse,
                periode = datagrunnlag.periode,
                førsteDagForYtelse = datagrunnlag.førsteDagForYtelse
            )
        }
    }
}
