package no.nav.medlemskap.regler.v1

import no.nav.medlemskap.domene.Datagrunnlag
import no.nav.medlemskap.domene.Ytelse
import no.nav.medlemskap.regler.common.*
import no.nav.medlemskap.regler.common.RegelId.*

class ReglerForMedl(
        ytelse: Ytelse,
        regelMap: Map<RegelId, Regel> = emptyMap()
) : Regler(ytelse, regelMap) {

    override fun hentRegelflyt(): Regelflyt {
        val harBrukerDekningIMedlFlyt = lagRegelflyt(
                regel = hentRegel(REGEL_1_7),
                hvisJa = regelFlytJa(ytelse),
                hvisNei = regelFlytUavklart(ytelse)
        )

        val erDekningUavklartFlyt = lagRegelflyt(
                regel = hentRegel(REGEL_1_6),
                hvisJa = regelFlytUavklart(ytelse),
                hvisNei = harBrukerDekningIMedlFlyt
        )

        val erArbeidsforholdUendretForBrukerMedMedlemskapFlyt = lagRegelflyt(
                regel = hentRegel(REGEL_1_5),
                hvisJa = erDekningUavklartFlyt,
                hvisNei = regelFlytUavklart(ytelse)
        )

        val erPeriodeMedMedlemskapInnenfor12MndPeriodeFlyt = lagRegelflyt(
                regel = hentRegel(REGEL_1_4),
                hvisJa = erArbeidsforholdUendretForBrukerMedMedlemskapFlyt,
                hvisNei = regelFlytUavklart(ytelse)
        )

        val erArbeidsforholdUendretForBrukerUtenMedlemskapFlyt = lagRegelflyt(
                regel = hentRegel(REGEL_1_3_2),
                hvisJa = regelFlytUavklart(ytelse),
                hvisNei = regelFlytUavklart(ytelse)
        )

        val erPeriodeUtenMedlemskapInnenfor12MndPeriodeFlyt = lagRegelflyt(
                regel = hentRegel(REGEL_1_3_1),
                hvisJa = erArbeidsforholdUendretForBrukerUtenMedlemskapFlyt,
                hvisNei = regelFlytUavklart(ytelse)
        )

        val periodeMedMedlemskapFlyt = lagRegelflyt(
                regel = hentRegel(REGEL_1_3),
                hvisJa = erPeriodeMedMedlemskapInnenfor12MndPeriodeFlyt,
                hvisNei = erPeriodeUtenMedlemskapInnenfor12MndPeriodeFlyt
        )

        val periodeMedOgUtenMedlemskapFlyt = lagRegelflyt(
                regel = hentRegel(REGEL_1_2),
                hvisJa = regelFlytUavklart(ytelse),
                hvisNei = periodeMedMedlemskapFlyt
        )

        val erPerioderAvklartFlyt = lagRegelflyt(
                regel = hentRegel(REGEL_1_1),
                hvisJa = periodeMedOgUtenMedlemskapFlyt,
                hvisNei = regelFlytUavklart(ytelse)
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