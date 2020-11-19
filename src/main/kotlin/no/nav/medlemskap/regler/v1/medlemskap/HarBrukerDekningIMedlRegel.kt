package no.nav.medlemskap.regler.v1.medlemskap

import no.nav.medlemskap.common.dekningCounter
import no.nav.medlemskap.domene.*
import no.nav.medlemskap.domene.Dekning.Companion.gjelderForYtelse
import no.nav.medlemskap.domene.Ytelse.Companion.name
import no.nav.medlemskap.regler.common.RegelId
import no.nav.medlemskap.regler.common.Resultat
import no.nav.medlemskap.regler.common.Resultat.Companion.ja
import no.nav.medlemskap.regler.common.Resultat.Companion.nei
import no.nav.medlemskap.regler.funksjoner.MedlFunksjoner.gjeldendeDekning
import java.time.LocalDate

class HarBrukerDekningIMedlRegel(
    ytelse: Ytelse,
    private val periode: InputPeriode,
    førsteDagForYtelse: LocalDate?,
    private val medlemskap: List<Medlemskap>
) : MedlemskapRegel(RegelId.REGEL_1_7, ytelse, periode, førsteDagForYtelse, medlemskap) {

    override fun operasjon(): Resultat {
        val dekning = medlemskap gjeldendeDekning kontrollPeriodeForMedl

        dekningCounter(dekning!!, ytelse.name()) // Dekning er ikke null, regel 1.5 filtrerer ut dekning som er null

        return when {
            Dekning.from(dekning)!!.gjelderForYtelse(ytelse) -> ja("Bruker har dekning", dekning)
            else -> nei("Bruker har ikke dekning", dekning)
        }
    }

    companion object {

        fun fraDatagrunnlag(datagrunnlag: Datagrunnlag): HarBrukerDekningIMedlRegel {
            return HarBrukerDekningIMedlRegel(
                ytelse = datagrunnlag.ytelse,
                periode = datagrunnlag.periode,
                medlemskap = datagrunnlag.medlemskap,
                førsteDagForYtelse = datagrunnlag.førsteDagForYtelse
            )
        }
    }
}
