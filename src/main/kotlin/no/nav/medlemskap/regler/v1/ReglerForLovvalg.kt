package no.nav.medlemskap.regler.v1

import no.nav.medlemskap.domene.Datagrunnlag
import no.nav.medlemskap.domene.InputPeriode
import no.nav.medlemskap.domene.Ytelse
import no.nav.medlemskap.regler.common.*
import no.nav.medlemskap.regler.common.RegelId.*
import no.nav.medlemskap.regler.v1.lovvalg.ErBrukerBosattINorgeRegel
import no.nav.medlemskap.regler.v1.lovvalg.HarBrukerJobbet25ProsentEllerMerRegel
import no.nav.medlemskap.regler.v1.lovvalg.HarBrukerJobbetUtenforNorgeRegel
import no.nav.medlemskap.regler.v1.lovvalg.HarBrukerNorskStatsborgerskapRegel

class ReglerForLovvalg(
        val periode: InputPeriode,
        ytelse: Ytelse,
        regelMap: Map<RegelId, Regel>,
        private val reglerForEøsBorgere: ReglerForEøsBorgere
) : Regler(ytelse, regelMap) {

    override fun hentRegelflyt(): Regelflyt {

        val harBrukerJobbet25ProsentEllerMerFlyt = lagRegelflyt(
                regel = hentRegel(REGEL_12),
                hvisJa = konklusjonJa(ytelse),
                hvisNei = konklusjonUavklart(ytelse)
        )

        val harBrukerNorskStatsborgerskapFlyt = lagRegelflyt(
                regel = hentRegel(REGEL_11),
                hvisJa = harBrukerJobbet25ProsentEllerMerFlyt,
                hvisNei = reglerForEøsBorgere.hentRegelflyt()
        )

        val erBrukerBosattINorgeFlyt = lagRegelflyt(
                regel = hentRegel(REGEL_10),
                hvisJa = harBrukerNorskStatsborgerskapFlyt,
                hvisNei = konklusjonUavklart(ytelse)
        )

        val harBrukerJobbetUtenforNorgeFlyt = lagRegelflyt(
                regel = hentRegel(REGEL_9),
                hvisJa = konklusjonNei(ytelse),
                hvisNei = erBrukerBosattINorgeFlyt
        )

        return harBrukerJobbetUtenforNorgeFlyt
    }


    companion object {
        fun fraDatagrunnlag(datagrunnlag: Datagrunnlag): ReglerForLovvalg {
            with(datagrunnlag) {
                return ReglerForLovvalg(
                        periode = periode,
                        ytelse = ytelse,
                        regelMap = lagRegelMap(datagrunnlag),
                        reglerForEøsBorgere = ReglerForEøsBorgere.fraDatagrunnlag(datagrunnlag)
                )
            }
        }

        private fun lagRegelMap(datagrunnlag: Datagrunnlag): Map<RegelId, Regel> {
            val regelListe = listOf(
                    ErBrukerBosattINorgeRegel.fraDatagrunnlag(datagrunnlag),
                    HarBrukerJobbetUtenforNorgeRegel.fraDatagrunnlag(datagrunnlag),
                    HarBrukerNorskStatsborgerskapRegel.fraDatagrunnlag(datagrunnlag),
                    HarBrukerJobbet25ProsentEllerMerRegel.fraDatagrunnlag(datagrunnlag)
            )

            return regelListe.map { it.regelId to it.regel }.toMap()
        }
    }
}
