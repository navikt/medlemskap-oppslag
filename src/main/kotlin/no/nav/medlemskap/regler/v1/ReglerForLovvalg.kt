package no.nav.medlemskap.regler.v1

import no.nav.medlemskap.domene.Datagrunnlag
import no.nav.medlemskap.domene.InputPeriode
import no.nav.medlemskap.domene.Ytelse
import no.nav.medlemskap.regler.common.*
import no.nav.medlemskap.regler.common.RegelId.*
import no.nav.medlemskap.regler.v1.lovvalg.*

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
                    ErBrukersEktefelleBosattINorgeRegel.fraDatagrunnlag(datagrunnlag, REGEL_11_4),
                    ErBrukersEktefelleBosattINorgeRegel.fraDatagrunnlag(datagrunnlag, REGEL_11_3_1),
                    HarBrukersEktefelleJobbet100ProsentEllerMerRegel.fraDatagrunnlag(datagrunnlag),
                    ErBrukersEktefelleOgBarnasMorSammePersonRegel.fraDatagrunnlag(datagrunnlag),
                    HarBrukerBarnSomErFolkeregistrertRegel.fraDatagrunnlag(datagrunnlag, REGEL_11_2_2),
                    HarBrukerBarnSomErFolkeregistrertRegel.fraDatagrunnlag(datagrunnlag, REGEL_11_4_1),
                    HarBrukerBarnSomErFolkeregistrertRegel.fraDatagrunnlag(datagrunnlag, REGEL_11_5),
                    HarBrukerBarnRegel.fraDatagrunnlag(datagrunnlag, REGEL_11_3),
                    HarBrukerBarnRegel.fraDatagrunnlag(datagrunnlag, REGEL_11_2_1),
                    HarBrukerJobbetUtenforNorgeRegel.fraDatagrunnlag(datagrunnlag),
                    HarBrukerNorskStatsborgerskapRegel.fraDatagrunnlag(datagrunnlag),
                    HarBrukerJobbet25ProsentEllerMerRegel.fraDatagrunnlag(datagrunnlag),
                    HarBrukerEktefelleRegel.fraDatagrunnlag(datagrunnlag),
                    HarBrukerJobbet80ProsentEllerMerRegel.fraDatagrunnlag (datagrunnlag, REGEL_11_6),
                    HarBrukerJobbet80ProsentEllerMerRegel.fraDatagrunnlag (datagrunnlag, REGEL_11_2_3),
                    HarBrukerJobbet100ProsentEllerMerRegel.fraDatagrunnlag(datagrunnlag, REGEL_11_3_1_1),
                    HarBrukerJobbet100ProsentEllerMerRegel.fraDatagrunnlag(datagrunnlag, REGEL_11_2_2_1)
            )

            return regelListe.map { it.regelId to it.regel }.toMap()
        }
    }
}
