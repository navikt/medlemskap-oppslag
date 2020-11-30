package no.nav.medlemskap.regler.v1

import no.nav.medlemskap.domene.Datagrunnlag
import no.nav.medlemskap.domene.InputPeriode
import no.nav.medlemskap.domene.Ytelse
import no.nav.medlemskap.regler.common.*
import no.nav.medlemskap.regler.common.RegelId.REGEL_12
import no.nav.medlemskap.regler.common.RegelId.REGEL_NORSK
import no.nav.medlemskap.regler.common.Regelflyt.Companion.regelflytJa
import no.nav.medlemskap.regler.common.Regelflyt.Companion.regelflytUavklart
import no.nav.medlemskap.regler.v1.lovvalg.HarBrukerJobbet25ProsentEllerMerRegel
import no.nav.medlemskap.regler.v1.lovvalg.HarBrukerJobbetUtenforNorgeRegel

class ReglerForNorskeStatsborgere(
    val periode: InputPeriode,
    ytelse: Ytelse,
    regelMap: Map<RegelId, Regel>,
    overstyrteRegler: Map<RegelId, Svar>
) : Regler(ytelse, regelMap, overstyrteRegler) {

    override fun hentRegelflyter(): List<Regelflyt> {
        val harBrukerJobbetUtenforNorgeFlyt = lagRegelflyt(
            regel = hentRegel(RegelId.REGEL_9),
            hvisJa = regelflytUavklart(ytelse, REGEL_NORSK),
            hvisNei = regelflytJa(ytelse, REGEL_NORSK)
        )

        val harBrukerJobbet25ProsentEllerMerFlyt = lagRegelflyt(
            regel = hentRegel(REGEL_12),
            hvisJa = regelflytJa(ytelse, REGEL_NORSK),
            hvisNei = harBrukerJobbetUtenforNorgeFlyt
        )

        return listOf(harBrukerJobbet25ProsentEllerMerFlyt)
    }

    companion object {
        fun fraDatagrunnlag(datagrunnlag: Datagrunnlag, overstyrteRegler: Map<RegelId, Svar> = emptyMap()): ReglerForNorskeStatsborgere {

            return ReglerForNorskeStatsborgere(
                periode = datagrunnlag.periode,
                ytelse = datagrunnlag.ytelse,
                regelMap = lagRegelMap(datagrunnlag),
                overstyrteRegler = overstyrteRegler
            )
        }

        private fun lagRegelMap(datagrunnlag: Datagrunnlag): Map<RegelId, Regel> {
            val regelListe = listOf(
                HarBrukerJobbetUtenforNorgeRegel.fraDatagrunnlag(datagrunnlag),
                HarBrukerJobbet25ProsentEllerMerRegel.fraDatagrunnlag(datagrunnlag)
            )
            return regelListe.map { it.regelId to it.regel }.toMap()
        }
    }
}
