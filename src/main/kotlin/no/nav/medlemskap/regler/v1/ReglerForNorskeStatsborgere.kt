package no.nav.medlemskap.regler.v1

import no.nav.medlemskap.domene.Datagrunnlag
import no.nav.medlemskap.domene.InputPeriode
import no.nav.medlemskap.domene.Ytelse
import no.nav.medlemskap.regler.common.*
import no.nav.medlemskap.regler.common.RegelId.REGEL_12
import no.nav.medlemskap.regler.v1.lovvalg.ErBrukerBosattINorgeRegel
import no.nav.medlemskap.regler.v1.lovvalg.HarBrukerJobbet25ProsentEllerMerRegel
import no.nav.medlemskap.regler.v1.lovvalg.HarBrukerJobbetUtenforNorgeRegel

class ReglerForNorskeStatsborgere(
        val periode: InputPeriode,
        ytelse: Ytelse,
        regelMap: Map<RegelId, Regel>
) : Regler(ytelse, regelMap) {

    override fun hentHovedflyt(): Regelflyt {

        val harBrukerJobbetUtenforNorgeFlyt = lagRegelflyt(
                regel = hentRegel(RegelId.REGEL_9),
                hvisJa = konklusjonNei(ytelse),
                hvisNei = regelflytJa(ytelse)
        )

        return harBrukerJobbetUtenforNorgeFlyt
    }

    override fun hentRegelflyter(): List<Regelflyt> {
        val erBrukerBosattINorgeFlyt = lagRegelflyt(
                regel = hentRegel(RegelId.REGEL_10),
                hvisJa = regelflytJa(ytelse),
                hvisNei = konklusjonUavklart(ytelse)
        )

        val harBrukerJobbet25ProsentEllerMerFlyt = lagRegelflyt(
                regel = hentRegel(REGEL_12),
                hvisJa = konklusjonJa(ytelse),
                hvisNei = konklusjonUavklart(ytelse)
        )

        return listOf(hentHovedflyt(), erBrukerBosattINorgeFlyt, harBrukerJobbet25ProsentEllerMerFlyt)
    }

    companion object {
        fun fraDatagrunnlag(datagrunnlag: Datagrunnlag): ReglerForNorskeStatsborgere {
            with(datagrunnlag) {
                return ReglerForNorskeStatsborgere(
                        periode = periode,
                        ytelse = ytelse,
                        regelMap = lagRegelMap(datagrunnlag)
                )
            }
        }

        private fun lagRegelMap(datagrunnlag: Datagrunnlag): Map<RegelId, Regel> {
            val regelListe = listOf(
                    HarBrukerJobbet25ProsentEllerMerRegel.fraDatagrunnlag(datagrunnlag),
                    ErBrukerBosattINorgeRegel.fraDatagrunnlag(datagrunnlag),
                    HarBrukerJobbetUtenforNorgeRegel.fraDatagrunnlag(datagrunnlag)
            )

            return regelListe.map { it.regelId to it.regel }.toMap()
        }
    }
}
