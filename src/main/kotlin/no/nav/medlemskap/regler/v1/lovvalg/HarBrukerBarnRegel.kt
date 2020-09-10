package no.nav.medlemskap.regler.v1.lovvalg

import no.nav.medlemskap.domene.*
import no.nav.medlemskap.domene.barn.DataOmBarn
import no.nav.medlemskap.regler.common.Funksjoner.erIkkeTom
import no.nav.medlemskap.regler.common.RegelId
import no.nav.medlemskap.regler.common.Resultat
import no.nav.medlemskap.regler.common.ja
import no.nav.medlemskap.regler.common.nei
import no.nav.medlemskap.regler.funksjoner.RelasjonFunksjoner.hentBarnSomFinnesITPS
import no.nav.medlemskap.regler.funksjoner.RelasjonFunksjoner.hentFnrTilBarnUnder25

class HarBrukerBarnRegel(
        ytelse: Ytelse,
        private val periode: InputPeriode,
        private val dataOmBarn: List<DataOmBarn>?,
        regelId: RegelId = RegelId.REGEL_11_2_1
) : LovvalgRegel(regelId, ytelse, periode) {

    override fun operasjon(): Resultat {
        val barn = dataOmBarn

        return when {
            barn.erIkkeTom() -> ja()
            else -> nei("Bruker har ikke barn i pdl")
        }
    }

    companion object {

        fun fraDatagrunnlag(datagrunnlag: Datagrunnlag, regelId: RegelId): HarBrukerBarnRegel {
            return HarBrukerBarnRegel(
                    ytelse = datagrunnlag.ytelse,
                    periode = datagrunnlag.periode,
                    dataOmBarn= datagrunnlag.dataOmBarn,
                    regelId = regelId
            )
        }
    }
}