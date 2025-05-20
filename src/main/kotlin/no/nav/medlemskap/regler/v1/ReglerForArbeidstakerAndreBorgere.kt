package no.nav.medlemskap.regler.v1

import no.nav.medlemskap.domene.Datagrunnlag
import no.nav.medlemskap.domene.InputPeriode
import no.nav.medlemskap.domene.Ytelse
import no.nav.medlemskap.regler.common.RegelId
import no.nav.medlemskap.regler.common.RegelId.*
import no.nav.medlemskap.regler.common.Regelflyt
import no.nav.medlemskap.regler.common.Regelflyt.Companion.regelflytJa
import no.nav.medlemskap.regler.common.Regelflyt.Companion.regelflytUavklart
import no.nav.medlemskap.regler.common.Regler
import no.nav.medlemskap.regler.common.Svar

class ReglerForArbeidstakerAndreBorgere(
    val periode: InputPeriode,
    ytelse: Ytelse,
    regelFactory: RegelFactory,
    overstyrteRegler: Map<RegelId, Svar>
) : Regler(ytelse, regelFactory, overstyrteRegler) {

    override fun hentHovedflyt(): Regelflyt {


        val HarBrukerVaertIMinst60ProsentStillingIEnArbeidsavtaleSiste12Mnd = lagRegelflyt (
            regel = hentRegel(REGEL_66),
            hvisJa = regelflytJa(ytelse, REGEL_ARBEIDSTAKERE_ANDRE_BORGERE),
            hvisNei = regelflytUavklart(ytelse, REGEL_ARBEIDSTAKERE_ANDRE_BORGERE)
        )

        val harBrukerVærtIMinst60ProsentStillingIHeleKontrollperioden = lagRegelflyt(
            regel = hentRegel(REGEL_64),
            hvisJa = regelflytJa(ytelse, REGEL_ARBEIDSTAKERE_ANDRE_BORGERE),
            hvisNei = regelflytUavklart(ytelse, REGEL_ARBEIDSTAKERE_ANDRE_BORGERE)
        )

        val HarArbeidsavtalenVartHeleKontrollPeriodenRegel = lagRegelflyt(
            regel = hentRegel(REGEL_61),
            hvisJa = harBrukerVærtIMinst60ProsentStillingIHeleKontrollperioden,
            hvisNei = regelflytUavklart(ytelse, REGEL_ARBEIDSTAKERE_ANDRE_BORGERE)
        )

        val HarBrukerFlereArbeidsavtalerSiste12Mnd = lagRegelflyt(
            regel = hentRegel(REGEL_65),
            hvisJa = HarBrukerVaertIMinst60ProsentStillingIEnArbeidsavtaleSiste12Mnd,
            hvisNei = HarArbeidsavtalenVartHeleKontrollPeriodenRegel
        )

        val harBrukerBareEttArbeidisforhold = lagRegelflyt(
            regel = hentRegel(REGEL_60),
            hvisJa = HarBrukerFlereArbeidsavtalerSiste12Mnd,
            hvisNei = regelflytUavklart(ytelse, REGEL_ARBEIDSTAKERE_ANDRE_BORGERE)
        )

        return harBrukerBareEttArbeidisforhold
    }

    companion object {
        fun fraDatagrunnlag(datagrunnlag: Datagrunnlag, overstyrteRegler: Map<RegelId, Svar> = emptyMap()): ReglerForArbeidstakerAndreBorgere {

            return ReglerForArbeidstakerAndreBorgere(
                periode = datagrunnlag.periode,
                ytelse = datagrunnlag.ytelse,
                regelFactory = RegelFactory(datagrunnlag),
                overstyrteRegler = overstyrteRegler
            )
        }
    }
}
