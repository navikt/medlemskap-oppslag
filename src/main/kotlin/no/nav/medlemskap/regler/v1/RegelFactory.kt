package no.nav.medlemskap.regler.v1

import no.nav.medlemskap.domene.Datagrunnlag
import no.nav.medlemskap.regler.common.Regel
import no.nav.medlemskap.regler.common.RegelId
import no.nav.medlemskap.regler.common.RegelId.*
import no.nav.medlemskap.regler.v1.arbeidsforhold.*
import no.nav.medlemskap.regler.v1.grunnforordningen.ErBrukerEøsBorgerRegel
import no.nav.medlemskap.regler.v1.lovvalg.*
import no.nav.medlemskap.regler.v1.medlemskap.*
import no.nav.medlemskap.regler.v1.overstyring.OverstyringRegel
import no.nav.medlemskap.regler.v1.registrerteOpplysninger.HarBrukerRegistrerteOpplysningerRegel
import no.nav.medlemskap.regler.v1.statsborgerskap.ErBrukerBritiskBorgerRegel
import no.nav.medlemskap.regler.v1.udi.*
import no.nav.medlemskap.regler.v1.validering.InputDatoValideringRegel

class RegelFactory(private val datagrunnlag: Datagrunnlag) {

    fun create(regelIdentifikator: String): Regel {
        val regelId = RegelId.fraRegelIdString(regelIdentifikator)
        if (regelId == null) {
            throw java.lang.RuntimeException("Ukjent regel")
        }

        return create(regelId)
    }

    fun create(regelId: RegelId): Regel {
        return when (regelId) {
            REGEL_OPPLYSNINGER -> HarBrukerRegistrerteOpplysningerRegel.fraDatagrunnlag(datagrunnlag).regel
            REGEL_0_1 -> InputDatoValideringRegel.fraDatagrunnlag(datagrunnlag).regel
            REGEL_0_5 -> OverstyringRegel.fraDatagrunnlag(datagrunnlag).regel
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
            REGEL_4 -> ErArbeidsgiverOrganisasjonRegel.fraDatagrunnlag(datagrunnlag).regel
            REGEL_5 -> HarForetaketMerEnn5AnsatteRegel.fraDatagrunnlag(datagrunnlag).regel
            REGEL_6 -> ErForetaketAktivtRegel.fraDatagrunnlag(datagrunnlag).regel
            REGEL_7 -> ErArbeidsforholdetMaritimtRegel.fraDatagrunnlag(datagrunnlag).regel
            REGEL_7_1 -> JobberBrukerPaaNorskSkipRegel.fraDatagrunnlag(datagrunnlag).regel
            REGEL_8 -> ErBrukerPilotEllerKabinansattRegel.fraDatagrunnlag(datagrunnlag).regel
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
            REGEL_13 -> ErBrukerDoedRegel.fraDatagrunnlag(datagrunnlag).regel
            REGEL_14 -> ErArbeidsforholdetOffentligSektor.fraDatagrunnlag(datagrunnlag).regel
            REGEL_17 -> ErArbeidsforholdetFrilanserRegel.fraDatagrunnlag(datagrunnlag).regel
            REGEL_18 -> ErBrukerHovedsakligArbeidstakerRegel.fraDatagrunnlag(datagrunnlag).regel
            REGEL_19_2 -> GyldigOppholdstillatelseIKontrollperiodeRegel.fraDatagrunnlag(datagrunnlag).regel
            REGEL_19_2_1 -> DekkerOppholdstillatelseArbeidsperiodeRegel.fraDatagrunnlag(datagrunnlag).regel
            REGEL_19_3 -> GyldigArbeidstillatelseIKontrollperiodeRegel.fraDatagrunnlag(datagrunnlag).regel
            REGEL_19_3_1 -> DekkerArbeidstillatelsenArbeidsperiodenRegel.fraDatagrunnlag(datagrunnlag).regel
            REGEL_19_4 -> ErBrukerBritiskBorgerRegel.fraDatagrunnlag(datagrunnlag).regel
            REGEL_19_1 -> ErOppholdstillatelseUavklartRegel.fraDatagrunnlag(datagrunnlag).regel
            else -> throw java.lang.RuntimeException("Ukjent regel")
        }
    }
}
