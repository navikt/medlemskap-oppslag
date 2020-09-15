package no.nav.medlemskap.regler.v1

import no.nav.medlemskap.domene.Datagrunnlag
import no.nav.medlemskap.domene.InputPeriode
import no.nav.medlemskap.domene.Ytelse
import no.nav.medlemskap.regler.common.*
import no.nav.medlemskap.regler.common.RegelId.*
import no.nav.medlemskap.regler.v1.lovvalg.*

class ReglerForEøsBorgere(
    val periode: InputPeriode,
    ytelse: Ytelse,
    regelMap: Map<RegelId, Regel>
) : Regler(ytelse, regelMap) {

    override fun hentRegelflyter(): List<Regelflyt> {
        val harBrukerMedBarn80ProsenStillingEllerMerRegelFlyt = lagRegelflyt(
            regel = hentRegel(REGEL_11_2_2_2),
            hvisJa = regelflytUavklart(ytelse, REGEL_EØS_BOSATT),
            hvisNei = regelflytUavklart(ytelse, REGEL_EØS_BOSATT)
        )

        val harBrukerMedBarnOgEktefelle80ProsentStillingEllerMerRegelFlyt = lagRegelflyt(
            regel = hentRegel(REGEL_11_5_2),
            hvisJa = regelflytUavklart(ytelse, REGEL_EØS_BOSATT),
            hvisNei = regelflytUavklart(ytelse, REGEL_EØS_BOSATT)
        )

        val harBrukerUtenFolkeregistrertEktefelleStillingsprosent100EllerMerRegelFlyt = lagRegelflyt(
            regel = hentRegel(REGEL_11_4_2),
            hvisJa = regelflytUavklart(ytelse, REGEL_EØS_BOSATT),
            hvisNei = regelflytUavklart(ytelse, REGEL_EØS_BOSATT)
        )

        val harBrukerMedFolkeregistrertEktefelleStillingsprosent100EllerMerRegelFlyt = lagRegelflyt(
            regel = hentRegel(REGEL_11_5_3),
            hvisJa = regelflytUavklart(ytelse, REGEL_EØS_BOSATT),
            hvisNei = regelflytUavklart(ytelse, REGEL_EØS_BOSATT)
        )

        val harBrukerMedFolkeregistrerteBarnJobbetMerEnn80ProsentFlyt = lagRegelflyt(
            regel = hentRegel(REGEL_11_2_3),
            hvisJa = regelflytJa(ytelse, REGEL_EØS_BOSATT),
            hvisNei = regelflytUavklart(ytelse, REGEL_EØS_BOSATT)
        )

        val harBrukerJobbetMerEnn100ProsentFlyt = lagRegelflyt(
            regel = hentRegel(REGEL_11_2_2_1),
            hvisJa = regelflytJa(ytelse, REGEL_EØS_BOSATT),
            hvisNei = regelflytUavklart(ytelse, REGEL_EØS_BOSATT)
        )

        val harBrukerUtenEktefelleBarnSomErFolkeregistrertFlyt = lagRegelflyt(
            regel = hentRegel(REGEL_11_2_2),
            hvisJa = harBrukerMedFolkeregistrerteBarnJobbetMerEnn80ProsentFlyt,
            hvisNei = harBrukerJobbetMerEnn100ProsentFlyt,
            hvisUavklart = harBrukerMedBarn80ProsenStillingEllerMerRegelFlyt
        )

        val harBrukerUtenEktefelleBarnFlyt = lagRegelflyt(
            regel = hentRegel(REGEL_11_2_1),
            hvisJa = harBrukerUtenEktefelleBarnSomErFolkeregistrertFlyt,
            hvisNei = harBrukerJobbetMerEnn100ProsentFlyt
        )

        val harBarnloesBrukerMedFolkeregistrertEktefelleJobbetMerEnn100ProsentFlyt = lagRegelflyt(
            regel = hentRegel(REGEL_11_3_1_1),
            hvisJa = regelflytJa(ytelse, REGEL_EØS_BOSATT),
            hvisNei = regelflytUavklart(ytelse, REGEL_EØS_BOSATT)
        )

        val harBrukersEktefelleJobbetMinst100ProsentSiste12MndFlyt = lagRegelflyt(
            regel = hentRegel(REGEL_11_6_1),
            hvisJa = regelflytJa(ytelse, REGEL_EØS_BOSATT),
            hvisNei = regelflytUavklart(ytelse, REGEL_EØS_BOSATT)
        )

        val harBrukerJobbetMerEnn80ProsentFlyt = lagRegelflyt(
            regel = hentRegel(REGEL_11_6),
            hvisJa = regelflytJa(ytelse, REGEL_EØS_BOSATT),
            hvisNei = harBrukersEktefelleJobbetMinst100ProsentSiste12MndFlyt
        )

        val erBarnloesBrukersEktefelleBosattINorgeFlyt = lagRegelflyt(
            regel = hentRegel(REGEL_11_3_1),
            hvisJa = harBrukerJobbetMerEnn80ProsentFlyt,
            hvisNei = harBarnloesBrukerMedFolkeregistrertEktefelleJobbetMerEnn100ProsentFlyt
        )

        val erBrukersEktefelleOgBarnasMorSammePersonFlyt = lagRegelflyt(
            regel = hentRegel(REGEL_11_5_1),
            hvisJa = regelflytUavklart(ytelse, REGEL_EØS_BOSATT),
            hvisNei = harBrukerJobbetMerEnn100ProsentFlyt,
            hvisUavklart = harBrukerMedFolkeregistrertEktefelleStillingsprosent100EllerMerRegelFlyt
        )

        val erBrukerUtenFolkeregistrertEktefelleSittBarnFolkeregistrertFlyt = lagRegelflyt(
            regel = hentRegel(REGEL_11_4_1),
            hvisJa = erBrukersEktefelleOgBarnasMorSammePersonFlyt,
            hvisNei = harBrukerJobbetMerEnn100ProsentFlyt,
            hvisUavklart = harBrukerUtenFolkeregistrertEktefelleStillingsprosent100EllerMerRegelFlyt
        )

        val erBrukerMedFolkeregistrertEktefelleSittBarnFolkeregistrertFlyt = lagRegelflyt(
            regel = hentRegel(REGEL_11_5),
            hvisJa = harBrukerJobbetMerEnn80ProsentFlyt,
            hvisNei = regelflytUavklart(ytelse, REGEL_EØS_BOSATT),
            hvisUavklart = harBrukerMedBarnOgEktefelle80ProsentStillingEllerMerRegelFlyt
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

        return listOf(
            harBrukerEktefelleFlyt
        )
    }

    companion object {
        fun fraDatagrunnlag(datagrunnlag: Datagrunnlag): ReglerForEøsBorgere {
            with(datagrunnlag) {
                return ReglerForEøsBorgere(
                    periode = periode,
                    ytelse = ytelse,
                    regelMap = lagRegelMap(datagrunnlag)
                )
            }
        }

        private fun lagRegelMap(datagrunnlag: Datagrunnlag): Map<RegelId, Regel> {
            val regelListe = listOf(
                ErBrukersEktefelleBosattINorgeRegel.fraDatagrunnlag(datagrunnlag, REGEL_11_4),
                ErBrukersEktefelleBosattINorgeRegel.fraDatagrunnlag(datagrunnlag, REGEL_11_3_1),
                HarBrukersEktefelleJobbet100ProsentEllerMerRegel.fraDatagrunnlag(datagrunnlag),
                ErBrukersEktefelleOgBarnasMorSammePersonRegel.fraDatagrunnlag(datagrunnlag),
                HarBrukerBarnSomErFolkeregistrertRegel.fraDatagrunnlag(datagrunnlag, REGEL_11_2_2),
                HarBrukerBarnSomErFolkeregistrertRegel.fraDatagrunnlag(datagrunnlag, REGEL_11_4_1),
                HarBrukerBarnSomErFolkeregistrertRegel.fraDatagrunnlag(datagrunnlag, REGEL_11_5),
                HarBrukerBarnRegel.fraDatagrunnlag(datagrunnlag, REGEL_11_3),
                HarBrukerBarnRegel.fraDatagrunnlag(datagrunnlag, REGEL_11_2_1),
                HarBrukerEktefelleRegel.fraDatagrunnlag(datagrunnlag),
                HarBrukerJobbet80ProsentEllerMerRegel.fraDatagrunnlag(datagrunnlag, REGEL_11_6),
                HarBrukerJobbet80ProsentEllerMerRegel.fraDatagrunnlag(datagrunnlag, REGEL_11_2_3),
                HarBrukerJobbet100ProsentEllerMerRegel.fraDatagrunnlag(datagrunnlag, REGEL_11_3_1_1),
                HarBrukerJobbet100ProsentEllerMerRegel.fraDatagrunnlag(datagrunnlag, REGEL_11_2_2_1),
                HarBrukerJobbet100ProsentEllerMerRegel.fraDatagrunnlag(datagrunnlag, REGEL_11_4_2),
                HarBrukerJobbet100ProsentEllerMerRegel.fraDatagrunnlag(datagrunnlag, REGEL_11_5_3),
                HarBrukerJobbet80ProsentEllerMerRegel.fraDatagrunnlag(datagrunnlag, REGEL_11_2_2_2),
                HarBrukerJobbet80ProsentEllerMerRegel.fraDatagrunnlag(datagrunnlag, REGEL_11_5_2)
            )

            return regelListe.map { it.regelId to it.regel }.toMap()
        }
    }
}
