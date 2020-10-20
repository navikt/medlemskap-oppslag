package no.nav.medlemskap.regler.v1.lovvalg

import no.nav.medlemskap.domene.*
import no.nav.medlemskap.regler.common.*
import no.nav.medlemskap.regler.funksjoner.PersonhistorikkFunksjoner.erBrukerDoedEtterPeriode
import no.nav.medlemskap.regler.common.Funksjoner.erIkkeTom
import no.nav.medlemskap.regler.common.Funksjoner.erTom
import no.nav.medlemskap.regler.funksjoner.AdresseFunksjoner.adresserForKontrollPeriode
import no.nav.medlemskap.regler.funksjoner.AdresseFunksjoner.landkodeTilAdresserForKontrollPeriode
import java.time.LocalDate

class ErBrukerDoed (
        val doedsfall: List<LocalDate?>,
        ytelse: Ytelse,
        val periode: InputPeriode
) : LovvalgRegel(RegelId.REGEL_13, ytelse, periode) {

    override fun operasjon(): Resultat {
        val erBrukerDoed = doedsfall.isNotEmpty()
        val erBrukerDoedEtterInputperiode = doedsfall.erBrukerDoedEtterPeriode(periode)

       if(erBrukerDoed && erBrukerDoedEtterInputperiode) {
           return ja("Bruker har dødsdato etter inputperiode")
       }

       else if (erBrukerDoed && !erBrukerDoedEtterInputperiode){
           return  uavklart("Bruker er død, men i eller før inputperiode")
       }
        return nei("Bruker er ikke død")
    }

    companion object {
        fun fraDatagrunnlag(datagrunnlag: Datagrunnlag): ErBrukerDoed {
            return ErBrukerDoed(
                    doedsfall = datagrunnlag.pdlpersonhistorikk.doedsfall,
                    ytelse = datagrunnlag.ytelse,
                    periode = datagrunnlag.periode

            )
        }
    }

    {
