package no.nav.medlemskap.regler.v1

import no.nav.medlemskap.domene.Datagrunnlag
import no.nav.medlemskap.regler.common.Regel
import no.nav.medlemskap.regler.common.RegelId
import no.nav.medlemskap.regler.common.RegelId.*

class RegelFactory(private val datagrunnlag: Datagrunnlag) {
    private val reglerForLovvalg = ReglerForLovvalg.fraDatagrunnlag(datagrunnlag)
    private val reglerForArbeidsforhold = ReglerForArbeidsforhold.fraDatagrunnlag(datagrunnlag)

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

            REGEL_3 -> reglerForArbeidsforhold.harBrukerSammenhengendeArbeidsforholdSiste12Mnd
            REGEL_5 -> reglerForArbeidsforhold.harForetakMerEnn5Ansatte
            REGEL_9 -> reglerForLovvalg.harBrukerJobbetUtenforNorge

            REGEL_10 -> ErBrukerBosattINorgeRegel.fraDatagrunnlag(datagrunnlag).regel

            REGEL_11 -> reglerForLovvalg.harBrukerNorskStatsborgerskap
            REGEL_11_2 -> reglerForLovvalg.harBrukerEktefelle
            REGEL_11_2_1 -> reglerForLovvalg.harBrukerBarnUtenEktefelle
            REGEL_11_2_2 -> reglerForLovvalg.harBrukerUtenEktefelleBarnSomErFolkeregistrert
            REGEL_11_2_2_1 -> reglerForLovvalg.harBrukerUtenEktefelleOgBarnJobbetMerEnn100Prosent
            REGEL_11_2_3 -> reglerForLovvalg.harBrukerMedFolkeregistrerteBarnJobbetMerEnn80Prosent
            REGEL_11_3 -> reglerForLovvalg.harBrukerEktefelleOgBarn
            REGEL_11_3_1 -> reglerForLovvalg.erBarnloesBrukersEktefelleBosattINorge
            REGEL_11_3_1_1 -> reglerForLovvalg.harBarnloesBrukerMedFolkeregistrertEktefelleJobbetMerEnn100Prosent
            REGEL_11_4 -> reglerForLovvalg.erBrukerMedBarnSittEktefelleBosattINorge
            REGEL_11_4_1 -> reglerForLovvalg.erBrukerUtenFolkeregistrertEktefelleSittBarnFolkeregistrert
            REGEL_11_5 -> reglerForLovvalg.erBrukerMedFolkeregistrertEktefelleSittBarnFolkeregistrert
            REGEL_11_6 -> reglerForLovvalg.harBrukerMedFolkeregistrerteRelasjonerJobbetMerEnn80Prosent
            REGEL_11_6_1 -> reglerForLovvalg.harBrukersEktefelleJobbetMinst100ProsentSiste12Mnd
            REGEL_12 -> reglerForLovvalg.harBrukerJobbet25ProsentEllerMer

            else -> throw java.lang.RuntimeException("Ukjent regel")
        }
    }
}