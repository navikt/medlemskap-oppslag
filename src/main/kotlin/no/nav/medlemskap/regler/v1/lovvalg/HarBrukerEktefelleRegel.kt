package no.nav.medlemskap.regler.v1.lovvalg

import no.nav.medlemskap.domene.Datagrunnlag
import no.nav.medlemskap.domene.InputPeriode
import no.nav.medlemskap.domene.Ytelse
import no.nav.medlemskap.domene.ektefelle.DataOmEktefelle
import no.nav.medlemskap.regler.common.RegelId
import no.nav.medlemskap.regler.common.Resultat
import no.nav.medlemskap.regler.common.ja
import no.nav.medlemskap.regler.common.nei

class HarBrukerEktefelleRegel(
        ytelse: Ytelse,
        private val periode: InputPeriode,
        private val dataOmEktefelle: DataOmEktefelle?,
        regelId: RegelId = RegelId.REGEL_11_2
) : LovvalgRegel(regelId, ytelse, periode) {

    override fun operasjon(): Resultat {
        val ektefelle = dataOmEktefelle?.personhistorikkEktefelle

        if (ektefelle != null) {
            return when {
                !ektefelle.ident.isEmpty()  -> ja()
                else -> nei("Bruker har ikke ektefelle i PDL")
            }
        }
        return nei("Bruker har ikke ektefelle i PDL")
    }

    companion object {

        fun fraDatagrunnlag(datagrunnlag: Datagrunnlag): HarBrukerEktefelleRegel {
            return HarBrukerEktefelleRegel(
                    ytelse = datagrunnlag.ytelse,
                    periode = datagrunnlag.periode,
                    dataOmEktefelle = datagrunnlag.dataOmEktefelle
            )
        }
    }
}