package no.nav.medlemskap.regler.v1

import no.nav.medlemskap.domene.Datagrunnlag
import no.nav.medlemskap.domene.Ytelse
import no.nav.medlemskap.regler.common.*
import no.nav.medlemskap.regler.common.Konklusjonstype.MEDLEM
import no.nav.medlemskap.regler.common.Konklusjonstype.REGELFLYT
import no.nav.medlemskap.regler.common.RegelId.*
import no.nav.medlemskap.regler.common.Regelflyt.Companion.regelflytJa
import no.nav.medlemskap.regler.common.Regelflyt.Companion.regelflytUavklart
import no.nav.medlemskap.regler.v1.medlemskap.*
import no.nav.medlemskap.regler.v1.registrerteOpplysninger.FinnesOpplysningerIGosysRegel
import no.nav.medlemskap.regler.v1.registrerteOpplysninger.FinnesOpplysningerIJoarkRegel
import no.nav.medlemskap.regler.v1.registrerteOpplysninger.FinnesOpplysningerIMedlRegel
import no.nav.medlemskap.regler.v1.registrerteOpplysninger.HarBrukerRegistrerteOpplysningerRegel

class ReglerForMedl(
    ytelse: Ytelse,
    regelMap: Map<RegelId, Regel> = emptyMap(),
    overstyrteRegler: Map<RegelId, Svar>
) : Regler(ytelse, regelMap, overstyrteRegler) {

    override fun hentHovedflyt(): Regelflyt {
        val harBrukerDekningIMedlFlyt = lagRegelflyt(
            regel = hentRegel(REGEL_1_7),
            hvisJa = regelflytJa(ytelse, REGEL_MEDL, MEDLEM),
            hvisNei = regelflytUavklart(ytelse, REGEL_MEDL, MEDLEM)
        )

        val erDekningUavklartFlyt = lagRegelflyt(
            regel = hentRegel(REGEL_1_6),
            hvisJa = regelflytUavklart(ytelse, REGEL_MEDL, MEDLEM),
            hvisNei = harBrukerDekningIMedlFlyt
        )

        val erArbeidsforholdUendretForBrukerMedMedlemskapFlyt = lagRegelflyt(
            regel = hentRegel(REGEL_1_5),
            hvisJa = erDekningUavklartFlyt,
            hvisNei = regelflytUavklart(ytelse, REGEL_MEDL, MEDLEM)
        )

        val erPeriodeMedMedlemskapInnenfor12MndPeriodeFlyt = lagRegelflyt(
            regel = hentRegel(REGEL_1_4),
            hvisJa = erArbeidsforholdUendretForBrukerMedMedlemskapFlyt,
            hvisNei = regelflytUavklart(ytelse, REGEL_MEDL, MEDLEM)
        )

        val erArbeidsforholdUendretForBrukerUtenMedlemskapFlyt = lagRegelflyt(
            regel = hentRegel(REGEL_1_3_2),
            hvisJa = regelflytUavklart(ytelse, REGEL_MEDL, MEDLEM),
            hvisNei = regelflytUavklart(ytelse, REGEL_MEDL, MEDLEM)
        )

        val erPeriodeUtenMedlemskapInnenfor12MndPeriodeFlyt = lagRegelflyt(
            regel = hentRegel(REGEL_1_3_1),
            hvisJa = erArbeidsforholdUendretForBrukerUtenMedlemskapFlyt,
            hvisNei = regelflytUavklart(ytelse, REGEL_MEDL, MEDLEM)
        )

        val periodeMedMedlemskapFlyt = lagRegelflyt(
            regel = hentRegel(REGEL_1_3),
            hvisJa = erPeriodeMedMedlemskapInnenfor12MndPeriodeFlyt,
            hvisNei = erPeriodeUtenMedlemskapInnenfor12MndPeriodeFlyt
        )

        val periodeMedOgUtenMedlemskapFlyt = lagRegelflyt(
            regel = hentRegel(REGEL_1_2),
            hvisJa = regelflytUavklart(ytelse, REGEL_MEDL, MEDLEM),
            hvisNei = periodeMedMedlemskapFlyt
        )

        val erPerioderAvklartFlyt = lagRegelflyt(
            regel = hentRegel(REGEL_1_1),
            hvisJa = periodeMedOgUtenMedlemskapFlyt,
            hvisNei = regelflytUavklart(ytelse, REGEL_MEDL, MEDLEM)
        )

        val harBrukerRegistrerteOpplysningerFlyt = lagRegelflyt(
            regel = hentRegel(REGEL_OPPLYSNINGER),
            hvisJa = erPerioderAvklartFlyt,
            hvisNei = regelflytJa(ytelse, REGEL_MEDL, REGELFLYT),
            hvisUavklart = regelflytUavklart(ytelse, REGEL_MEDL, MEDLEM)
        )

        return harBrukerRegistrerteOpplysningerFlyt
    }

    companion object {
        fun fraDatagrunnlag(datagrunnlag: Datagrunnlag): ReglerForMedl {
            with(datagrunnlag) {
                return ReglerForMedl(
                    ytelse = ytelse,
                    regelMap = lagRegelMap(datagrunnlag),
                    overstyrteRegler = datagrunnlag.overstyrteRegler
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
                PeriodeMedOgUtenMedlemskapRegel.fraDatagrunnlag(datagrunnlag),
                FinnesOpplysningerIGosysRegel.fraDatagrunnlag(datagrunnlag),
                FinnesOpplysningerIJoarkRegel.fraDatagrunnlag(datagrunnlag),
                FinnesOpplysningerIMedlRegel.fraDatagrunnlag(datagrunnlag),
                HarBrukerRegistrerteOpplysningerRegel.fraDatagrunnlag(datagrunnlag)
            )

            return regelListe.map { it.regelId to it.regel }.toMap()
        }
    }
}
