package no.nav.medlemskap.regler.v1.lovvalg

import io.ktor.features.*
import no.nav.medlemskap.domene.*
import no.nav.medlemskap.regler.common.*
import no.nav.medlemskap.regler.funksjoner.PersonhistorikkFunksjoner.erBrukerDoedEtterPeriode
import java.time.LocalDate

class ErBrukerDoed(
    val doedsfall: List<LocalDate>,
    ytelse: Ytelse,
    val periode: InputPeriode
) : LovvalgRegel(RegelId.REGEL_13, ytelse, periode) {

    override fun operasjon(): Resultat {
        val erBrukerDoed = !doedsfall.isNullOrEmpty()
        val erBrukerDoedEtterInputperiode = doedsfall.erBrukerDoedEtterPeriode(periode)

        if (erBrukerDoed && erBrukerDoedEtterInputperiode) {
            return ja("Bruker har dødsdato etter inputperiode, men det påvirker ikke medlemskapet")
        } else if (erBrukerDoed && !erBrukerDoedEtterInputperiode) {
            throw BadRequestException("Bruker er død, men i eller før inputperiode.")
        }
        return nei()
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
}
