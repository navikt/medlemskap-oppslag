package no.nav.medlemskap.regler.v1.lovvalg

import no.nav.medlemskap.domene.*
import no.nav.medlemskap.domene.barn.DataOmBarn
import no.nav.medlemskap.domene.ektefelle.DataOmEktefelle
import no.nav.medlemskap.regler.common.Funksjoner.harAlle
import no.nav.medlemskap.regler.common.RegelId
import no.nav.medlemskap.regler.common.Resultat
import no.nav.medlemskap.regler.common.ja
import no.nav.medlemskap.regler.common.nei

class ErBrukersEktefelleOgBarnasMorSammePersonRegel(
    ytelse: Ytelse,
    private val periode: InputPeriode,
    private val dataOmEktefelle: DataOmEktefelle?,
    private val dataOmBarn: List<DataOmBarn>?,
    regelId: RegelId = RegelId.REGEL_11_5_1
) : LovvalgRegel(regelId, ytelse, periode) {

    override fun operasjon(): Resultat {
        val ektefellesBarn = dataOmEktefelle?.personhistorikkEktefelle?.barn
        val brukersBarn = dataOmBarn

        if (ektefellesBarn != null && brukersBarn != null) {
            return when {
                ektefellesBarn.map { it }.harAlle(brukersBarn.map { it.personhistorikkBarn.ident }) -> ja()
                else -> nei(" Ektefelle er ikke barn/barnas mor")
            }
        }
        return nei()
    }

    companion object {

        fun fraDatagrunnlag(datagrunnlag: Datagrunnlag): ErBrukersEktefelleOgBarnasMorSammePersonRegel {
            return ErBrukersEktefelleOgBarnasMorSammePersonRegel(
                ytelse = datagrunnlag.ytelse,
                periode = datagrunnlag.periode,
                dataOmEktefelle = datagrunnlag.dataOmEktefelle,
                dataOmBarn = datagrunnlag.dataOmBarn
            )
        }
    }
}
