package no.nav.medlemskap.regler.v1

import no.nav.medlemskap.domene.Datagrunnlag
import no.nav.medlemskap.domene.Ytelse
import no.nav.medlemskap.regler.common.*
import no.nav.medlemskap.regler.common.RegelId.*
import no.nav.medlemskap.regler.v1.medlemskap.*

class ReglerForMedl(
        ytelse: Ytelse,
        regelMap: Map<RegelId, Regel> = emptyMap()
) : Regler(ytelse, regelMap) {

    override fun hentHovedflyt(): Regelflyt {
        val harBrukerDekningIMedlFlyt = lagRegelflyt(
                regel = hentRegel(REGEL_1_7),
                hvisJa = konklusjonJa(ytelse),
                hvisNei = konklusjonUavklart(ytelse)
        )

        val erDekningUavklartFlyt = lagRegelflyt(
                regel = hentRegel(REGEL_1_6),
                hvisJa = konklusjonUavklart(ytelse),
                hvisNei = harBrukerDekningIMedlFlyt
        )

        val erArbeidsforholdUendretForBrukerMedMedlemskapFlyt = lagRegelflyt(
                regel = hentRegel(REGEL_1_5),
                hvisJa = erDekningUavklartFlyt,
                hvisNei = konklusjonUavklart(ytelse)
        )

        val erPeriodeMedMedlemskapInnenfor12MndPeriodeFlyt = lagRegelflyt(
                regel = hentRegel(REGEL_1_4),
                hvisJa = erArbeidsforholdUendretForBrukerMedMedlemskapFlyt,
                hvisNei = konklusjonUavklart(ytelse)
        )

        val erArbeidsforholdUendretForBrukerUtenMedlemskapFlyt = lagRegelflyt(
                regel = hentRegel(REGEL_1_3_2),
                hvisJa = konklusjonUavklart(ytelse),
                hvisNei = konklusjonUavklart(ytelse)
        )

        val erPeriodeUtenMedlemskapInnenfor12MndPeriodeFlyt = lagRegelflyt(
                regel = hentRegel(REGEL_1_3_1),
                hvisJa = erArbeidsforholdUendretForBrukerUtenMedlemskapFlyt,
                hvisNei = konklusjonUavklart(ytelse)
        )

        val periodeMedMedlemskapFlyt = lagRegelflyt(
                regel = hentRegel(REGEL_1_3),
                hvisJa = erPeriodeMedMedlemskapInnenfor12MndPeriodeFlyt,
                hvisNei = erPeriodeUtenMedlemskapInnenfor12MndPeriodeFlyt
        )

        val periodeMedOgUtenMedlemskapFlyt = lagRegelflyt(
                regel = hentRegel(REGEL_1_2),
                hvisJa = konklusjonUavklart(ytelse),
                hvisNei = periodeMedMedlemskapFlyt
        )

        val erPerioderAvklartFlyt = lagRegelflyt(
                regel = hentRegel(REGEL_1_1),
                hvisJa = periodeMedOgUtenMedlemskapFlyt,
                hvisNei = konklusjonUavklart(ytelse)
        )

        return erPerioderAvklartFlyt
    }


    companion object {
        fun fraDatagrunnlag(datagrunnlag: Datagrunnlag): ReglerForMedl {
            with(datagrunnlag) {
                return ReglerForMedl(
                        ytelse = ytelse,
                        regelMap = lagRegelMap(datagrunnlag)
                )
            }
        }

        private fun lagRegelMap(datagrunnlag: Datagrunnlag): Map<RegelId, Regel> {
            val regelListe = listOf(
                    ErArbeidsforholdUendretRegel.fraDatagrunnlag(datagrunnlag, REGEL_1_3_2),
                    ErArbeidsforholdUendretRegel.fraDatagrunnlag(datagrunnlag, REGEL_1_5),
                    ErBrukersDekningUavklartRegel.fraDatagrunnlag(datagrunnlag),
                    ErPeriodeMedMedlemskapInnenfor12MndPeriodeRegel.fraDatagrunnlag(datagrunnlag),
                    ErPerioderAvklartRegel.fraDatagrunnlag(datagrunnlag),
                    ErPeriodeUtenMedlemskapInnenfor12MndPeriodeRegel.fraDatagrunnlag(datagrunnlag),
                    HarBrukerDekningIMedlRegel.fraDatagrunnlag(datagrunnlag),
                    PeriodeMedMedlemskapRegel.fraDatagrunnlag(datagrunnlag),
                    PeriodeMedOgUtenMedlemskapRegel.fraDatagrunnlag(datagrunnlag)
            )

            return regelListe.map { it.regelId to it.regel }.toMap()
        }

    }
}