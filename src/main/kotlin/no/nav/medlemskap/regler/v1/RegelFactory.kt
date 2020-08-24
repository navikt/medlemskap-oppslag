package no.nav.medlemskap.regler.v1

import no.nav.medlemskap.domene.Datagrunnlag
import no.nav.medlemskap.regler.common.Regel
import no.nav.medlemskap.regler.common.RegelId
import no.nav.medlemskap.regler.common.RegelId.*
import no.nav.medlemskap.regler.v1.arbeidsforhold.HarBrukerSammenhengendeArbeidsforholdRegel
import no.nav.medlemskap.regler.v1.arbeidsforhold.HarForetaketMerEnn5AnsatteRegel
import no.nav.medlemskap.regler.v1.lovvalg.*

class RegelFactory(private val datagrunnlag: Datagrunnlag) {

    fun create(regelIdentifikator: String) : Regel{
        val regelId = RegelId.fraRegelIdString(regelIdentifikator)
        if (regelId == null) {
            throw java.lang.RuntimeException("Ukjent regel")
        }

        return create(regelId)
    }

    fun create(regelId: RegelId) : Regel {
        return when(regelId) {
            REGEL_OPPLYSNINGER -> HarBrukerRegistrerteOpplysningerRegel.fraDatagrunnlag(datagrunnlag).regel
            REGEL_1_1 -> ErPerioderAvklartRegel.fraDatagrunnlag(datagrunnlag).regel
            REGEL_1_2 -> PeriodeMedOgUtenMedlemskapRegel.fraDatagrunnlag(datagrunnlag).regel
            REGEL_1_3 -> PeriodeMedMedlemskapRegel.fraDatagrunnlag(datagrunnlag).regel
            REGEL_1_3_1 -> ErPeriodeUtenMedlemskapInnenfor12MndPeriodeRegel.fraDatagrunnlag(datagrunnlag).regel
            REGEL_1_3_2 -> ErArbeidsforholdUendretRegel.fraDatagrunnlag(datagrunnlag, REGEL_1_3_2).regel
            REGEL_1_4 -> ErPeriodeMedMedlemskapInnenfor12MndPeriodeRegel.fraDatagrunnlag(datagrunnlag).regel
            REGEL_1_5 -> ErArbeidsforholdUendretRegel.fraDatagrunnlag(datagrunnlag, REGEL_1_5).regel
            REGEL_1_6 -> ErBrukersDekningUavklartRegel.fraDatagrunnlag(datagrunnlag).regel
            REGEL_1_7 -> HarBrukerDekningIMedlRegel.fraDatagrunnlag(datagrunnlag).regel

            REGEL_2 -> ErBrukerEøsBorgerRegel.fraDatagrunnlag(datagrunnlag).regel

            REGEL_3 -> HarBrukerSammenhengendeArbeidsforholdRegel.fraDatagrunnlag(datagrunnlag).regel
            REGEL_5 -> HarForetaketMerEnn5AnsatteRegel.fraDatagrunnlag(datagrunnlag).regel
            REGEL_9 -> HarBrukerJobbetUtenforNorgeRegel.fraDatagrunnlag(datagrunnlag).regel

            REGEL_10 -> ErBrukerBosattINorgeRegel.fraDatagrunnlag(datagrunnlag).regel

            REGEL_11 -> HarBrukerNorskStatsborgerskapRegel.fraDatagrunnlag(datagrunnlag).regel
            REGEL_11_2 -> HarBrukerEktefelleRegel.fraDatagrunnlag(datagrunnlag).regel
            REGEL_11_2_1 -> HarBrukerBarnRegel.fraDatagrunnlag(datagrunnlag, REGEL_11_2_1).regel
            REGEL_11_2_2 -> HarBrukerBarnSomErFolkeregistrertRegel.fraDatagrunnlag(datagrunnlag, REGEL_11_2_2).regel
            REGEL_11_2_2_1 -> HarBrukerJobbet100ProsentEllerMerRegel.fraDatagrunnlag(datagrunnlag, REGEL_11_2_2_1).regel
            REGEL_11_2_3 -> HarBrukerJobbet80ProsentEllerMerRegel.fraDatagrunnlag(datagrunnlag, REGEL_11_2_3).regel
            REGEL_11_3 -> HarBrukerBarnRegel.fraDatagrunnlag(datagrunnlag, REGEL_11_3).regel
            REGEL_11_3_1 -> ErBrukersEktefelleBosattINorgeRegel.fraDatagrunnlag(datagrunnlag, REGEL_11_3_1).regel
            REGEL_11_3_1_1 -> HarBrukerJobbet100ProsentEllerMerRegel.fraDatagrunnlag(datagrunnlag, REGEL_11_3_1_1).regel
            REGEL_11_4 -> ErBrukersEktefelleBosattINorgeRegel.fraDatagrunnlag(datagrunnlag, REGEL_11_4).regel
            REGEL_11_4_1 -> HarBrukerBarnSomErFolkeregistrertRegel.fraDatagrunnlag(datagrunnlag, REGEL_11_4_1).regel
            REGEL_11_5 -> HarBrukerBarnSomErFolkeregistrertRegel.fraDatagrunnlag(datagrunnlag, REGEL_11_5).regel
            REGEL_11_6 -> HarBrukerJobbet80ProsentEllerMerRegel.fraDatagrunnlag(datagrunnlag, REGEL_11_6).regel
            REGEL_11_6_1 -> HarBrukersEktefelleJobbet100ProsentEllerMerRegel.fraDatagrunnlag(datagrunnlag).regel
            REGEL_12 -> HarBrukerJobbet25ProsentEllerMerRegel.fraDatagrunnlag(datagrunnlag).regel

            else -> throw java.lang.RuntimeException("Ukjent regel")
        }
    }
}