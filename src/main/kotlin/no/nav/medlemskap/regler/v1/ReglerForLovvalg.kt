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
        regelMap: Map<RegelId, Regel>
) : Regler(ytelse, regelMap) {

    override fun hentRegelflyt(): Regelflyt {
        val harBrukerMedFolkeregistrerteBarnJobbetMerEnn80ProsentFlyt = lagRegelflyt(
                regel = hentRegel(REGEL_11_2_3),
                hvisJa = regelFlytJa(ytelse),
                hvisNei = regelFlytUavklart(ytelse)
        )

        val harBrukerJobbetMerEnn100ProsentFlyt = lagRegelflyt(
                regel = hentRegel(REGEL_11_2_2_1),
                hvisJa = regelFlytJa(ytelse),
                hvisNei = regelFlytUavklart(ytelse)
        )

        val harBrukerUtenEktefelleBarnSomErFolkeregistrertFlyt = lagRegelflyt(
                regel = hentRegel(REGEL_11_2_2),
                hvisJa = harBrukerMedFolkeregistrerteBarnJobbetMerEnn80ProsentFlyt,
                hvisNei = harBrukerJobbetMerEnn100ProsentFlyt
        )

        val harBrukerUtenEktefelleBarnFlyt = lagRegelflyt(
                regel = hentRegel(REGEL_11_2_1),
                hvisJa = harBrukerUtenEktefelleBarnSomErFolkeregistrertFlyt,
                hvisNei = harBrukerJobbetMerEnn100ProsentFlyt
        )

        val harBarnloesBrukerMedFolkeregistrertEktefelleJobbetMerEnn100ProsentFlyt = lagRegelflyt(
                regel = hentRegel(REGEL_11_3_1_1),
                hvisJa = regelFlytJa(ytelse),
                hvisNei = regelFlytUavklart(ytelse)
        )

        val harBrukersEktefelleJobbetMinst100ProsentSiste12MndFlyt = lagRegelflyt(
                regel = hentRegel(REGEL_11_6_1),
                hvisJa = regelFlytJa(ytelse),
                hvisNei = regelFlytUavklart(ytelse)
        )

        val harBrukerJobbetMerEnn80ProsentFlyt = lagRegelflyt(
                regel = hentRegel(REGEL_11_6),
                hvisJa = regelFlytJa(ytelse),
                hvisNei = harBrukersEktefelleJobbetMinst100ProsentSiste12MndFlyt
        )

        val erBarnloesBrukersEktefelleBosattINorgeFlyt = lagRegelflyt(
                regel = hentRegel(REGEL_11_3_1),
                hvisJa = harBrukerJobbetMerEnn80ProsentFlyt,
                hvisNei = harBarnloesBrukerMedFolkeregistrertEktefelleJobbetMerEnn100ProsentFlyt
        )

        val erBrukersEktefelleOgBarnasMorSammePersonFlyt = lagRegelflyt(
                regel = hentRegel(REGEL_11_5_1),
                hvisJa = regelFlytUavklart(ytelse),
                hvisNei = harBrukerJobbetMerEnn100ProsentFlyt
        )

        val erBrukerUtenFolkeregistrertEktefelleSittBarnFolkeregistrertFlyt = lagRegelflyt(
                regel = hentRegel(REGEL_11_4_1),
                hvisJa = erBrukersEktefelleOgBarnasMorSammePersonFlyt,
                hvisNei = harBrukerJobbetMerEnn100ProsentFlyt
        )


        val erBrukerMedFolkeregistrertEktefelleSittBarnFolkeregistrertFlyt = lagRegelflyt(
                regel = hentRegel(REGEL_11_5),
                hvisJa = harBrukerJobbetMerEnn80ProsentFlyt,
                hvisNei = regelFlytUavklart(ytelse)
        )

        val erBrukerMedBarnSittEktefelleBosattINorgeFlyt = lagRegelflyt(
                regel = hentRegel(REGEL_11_4),
                hvisJa = erBrukerMedFolkeregistrertEktefelleSittBarnFolkeregistrertFlyt,
                hvisNei = erBrukerUtenFolkeregistrertEktefelleSittBarnFolkeregistrertFlyt
        )

        val harBrukerEktefelleOgBarnFlyt = lagRegelflyt(
                regel = hentRegel(REGEL_11_3),
                hvisJa = erBrukerMedBarnSittEktefelleBosattINorgeFlyt,
                hvisNei = erBarnloesBrukersEktefelleBosattINorgeFlyt
        )

        val harBrukerEktefelleFlyt = lagRegelflyt(
                regel = hentRegel(REGEL_11_2),
                hvisJa = harBrukerEktefelleOgBarnFlyt,
                hvisNei = harBrukerUtenEktefelleBarnFlyt
        )

        val harBrukerJobbet25ProsentEllerMerFlyt = lagRegelflyt(
                regel = hentRegel(REGEL_12),
                hvisJa = regelFlytJa(ytelse),
                hvisNei = regelFlytUavklart(ytelse)
        )

        val harBrukerNorskStatsborgerskapFlyt = lagRegelflyt(
                regel = hentRegel(REGEL_11),
                hvisJa = harBrukerJobbet25ProsentEllerMerFlyt,
                hvisNei = harBrukerEktefelleFlyt
        )

        val erBrukerBosattINorgeFlyt = lagRegelflyt(
                regel = hentRegel(REGEL_10),
                hvisJa = harBrukerNorskStatsborgerskapFlyt,
                hvisNei = regelFlytUavklart(ytelse)
        )

        val harBrukerJobbetUtenforNorgeFlyt = lagRegelflyt(
                regel = hentRegel(REGEL_9),
                hvisJa = regelFlytNei(ytelse),
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
                        regelMap = lagRegelMap(datagrunnlag)
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
