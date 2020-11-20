package no.nav.medlemskap.regler.v1.lovvalg

import io.ktor.features.*
import no.nav.medlemskap.domene.Datagrunnlag
import no.nav.medlemskap.domene.InputPeriode
import no.nav.medlemskap.domene.Ytelse
import no.nav.medlemskap.regler.common.RegelId
import no.nav.medlemskap.regler.common.Resultat
import no.nav.medlemskap.regler.common.Resultat.Companion.ja
import no.nav.medlemskap.regler.common.Resultat.Companion.nei
import no.nav.medlemskap.regler.funksjoner.PersonhistorikkFunksjoner.erBrukerDoedEtterPeriode
import java.time.LocalDate

class ErBrukerDoedRegel(
    val doedsfall: List<LocalDate>,
    ytelse: Ytelse,
    val periode: InputPeriode,
    førsteDagForYtelse: LocalDate?
) : LovvalgRegel(RegelId.REGEL_13, ytelse, periode, førsteDagForYtelse) {

    override fun operasjon(): Resultat {
        val erBrukerDoed = !doedsfall.isNullOrEmpty()
        val erBrukerDoedEtterInputperiode = doedsfall.erBrukerDoedEtterPeriode(periode)

        if (erBrukerDoed && erBrukerDoedEtterInputperiode) {
            return ja(RegelId.REGEL_13.begrunnelse)
        } else if (erBrukerDoed && !erBrukerDoedEtterInputperiode) {
            throw BadRequestException("Bruker er død, men i eller før inputperiode.")
        }
        return nei()
    }

    companion object {
        fun fraDatagrunnlag(datagrunnlag: Datagrunnlag): ErBrukerDoedRegel {
            return ErBrukerDoedRegel(
                doedsfall = datagrunnlag.pdlpersonhistorikk.doedsfall,
                ytelse = datagrunnlag.ytelse,
                periode = datagrunnlag.periode,
                førsteDagForYtelse = datagrunnlag.førsteDagForYtelse
            )
        }
    }
}
