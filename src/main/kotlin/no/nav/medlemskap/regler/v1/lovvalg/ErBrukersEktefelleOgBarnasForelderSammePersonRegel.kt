package no.nav.medlemskap.regler.v1.lovvalg

import no.nav.medlemskap.domene.Datagrunnlag
import no.nav.medlemskap.domene.InputPeriode
import no.nav.medlemskap.domene.Ytelse
import no.nav.medlemskap.domene.barn.DataOmBarn
import no.nav.medlemskap.domene.ektefelle.DataOmEktefelle
import no.nav.medlemskap.regler.common.Funksjoner.harAlle
import no.nav.medlemskap.regler.common.RegelId
import no.nav.medlemskap.regler.common.Resultat
import no.nav.medlemskap.regler.common.ja
import no.nav.medlemskap.regler.common.nei
import java.time.LocalDate

class ErBrukersEktefelleOgBarnasForelderSammePersonRegel(
    ytelse: Ytelse,
    private val periode: InputPeriode,
    førsteDagForYtelse: LocalDate?,
    private val dataOmEktefelle: DataOmEktefelle?,
    private val dataOmBarn: List<DataOmBarn>?,
    regelId: RegelId = RegelId.REGEL_11_5_1
) : LovvalgRegel(regelId, ytelse, periode, førsteDagForYtelse) {

    override fun operasjon(): Resultat {
        val ektefellesBarn = dataOmEktefelle?.personhistorikkEktefelle?.barn
        val brukersBarn = dataOmBarn

        if (ektefellesBarn != null && brukersBarn != null) {
            return when {
                ektefellesBarn.map { it }.harAlle(brukersBarn.map { it.personhistorikkBarn.ident }) -> ja()
                else -> nei(" Ektefelle er ikke barn/barnas foreldre")
            }
        }
        return nei()
    }

    companion object {

        fun fraDatagrunnlag(datagrunnlag: Datagrunnlag): ErBrukersEktefelleOgBarnasForelderSammePersonRegel {
            return ErBrukersEktefelleOgBarnasForelderSammePersonRegel(
                ytelse = datagrunnlag.ytelse,
                periode = datagrunnlag.periode,
                førsteDagForYtelse = datagrunnlag.førsteDagForYtelse,
                dataOmEktefelle = datagrunnlag.dataOmEktefelle,
                dataOmBarn = datagrunnlag.dataOmBarn
            )
        }
    }
}
