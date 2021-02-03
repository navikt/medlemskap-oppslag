package no.nav.medlemskap.regler.v1

import no.nav.medlemskap.domene.Datagrunnlag
import no.nav.medlemskap.domene.Ytelse
import no.nav.medlemskap.regler.common.*
import no.nav.medlemskap.regler.common.RegelId.*
import no.nav.medlemskap.regler.common.Regelflyt.Companion.regelflytJa
import no.nav.medlemskap.regler.common.Regelflyt.Companion.regelflytUavklart
import no.nav.medlemskap.regler.v1.arbeidsforhold.*

class ReglerForArbeidsforhold(
    ytelse: Ytelse,
    regelMap: Map<RegelId, Regel>,
    overstyrteRegler: Map<RegelId, Svar>
) : Regler(ytelse, regelMap, overstyrteRegler) {

    override fun hentHovedflyt(): Regelflyt {
        val jobberBrukerPaaNorskSkipFlyt = lagRegelflyt(
            regel = hentRegel(REGEL_7_1),
            hvisJa = regelflytJa(ytelse, REGEL_ARBEIDSFORHOLD),
            hvisNei = regelflytUavklart(ytelse, REGEL_ARBEIDSFORHOLD)
        )

        val erBrukerPilotEllerKabinansattFlyt = lagRegelflyt(
            regel = hentRegel(REGEL_8),
            hvisJa = regelflytUavklart(ytelse, REGEL_ARBEIDSFORHOLD),
            hvisNei = regelflytJa(ytelse, REGEL_ARBEIDSFORHOLD)
        )

        val erArbeidsforholdetMaritimtFlyt = lagRegelflyt(
            regel = hentRegel(REGEL_7),
            hvisJa = jobberBrukerPaaNorskSkipFlyt,
            hvisNei = erBrukerPilotEllerKabinansattFlyt
        )

        val erArbeidsforholdetFrilanserFlyt = lagRegelflyt(
            regel = hentRegel(REGEL_17),
            hvisJa = regelflytUavklart(ytelse, REGEL_ARBEIDSFORHOLD),
            hvisNei = erArbeidsforholdetMaritimtFlyt
        )

        val erForetakAktivtFlyt = lagRegelflyt(
            regel = hentRegel(REGEL_6),
            hvisJa = erArbeidsforholdetFrilanserFlyt,
            hvisNei = regelflytUavklart(ytelse, REGEL_ARBEIDSFORHOLD)
        )

        val harForetakMerEnn5AnsatteFlyt = lagRegelflyt(
            regel = hentRegel(REGEL_5),
            hvisJa = erForetakAktivtFlyt,
            hvisNei = regelflytUavklart(ytelse, REGEL_ARBEIDSFORHOLD)
        )

        val erArbeidsgiverOffentligSektor = lagRegelflyt(
            regel = hentRegel(REGEL_14),
            hvisJa = regelflytJa(ytelse, REGEL_ARBEIDSFORHOLD),
            hvisNei = harForetakMerEnn5AnsatteFlyt
        )

        val erArbeidsgiverOrganisasjonFlyt = lagRegelflyt(
            regel = hentRegel(REGEL_4),
            hvisJa = erArbeidsgiverOffentligSektor,
            hvisNei = regelflytUavklart(ytelse, REGEL_ARBEIDSFORHOLD)
        )

        val harBrukerSammenhengendeArbeidsforholdSiste12MndFlyt = lagRegelflyt(
            regel = hentRegel(REGEL_3),
            hvisJa = erArbeidsgiverOrganisasjonFlyt,
            hvisNei = regelflytUavklart(ytelse, REGEL_ARBEIDSFORHOLD)
        )

        return harBrukerSammenhengendeArbeidsforholdSiste12MndFlyt
    }

    companion object {
        fun fraDatagrunnlag(datagrunnlag: Datagrunnlag, overstyrteRegler: Map<RegelId, Svar> = emptyMap()): ReglerForArbeidsforhold {
            return ReglerForArbeidsforhold(
                ytelse = datagrunnlag.ytelse,
                regelMap = lagRegelMap(datagrunnlag),
                overstyrteRegler = overstyrteRegler
            )
        }

        private fun lagRegelMap(datagrunnlag: Datagrunnlag): Map<RegelId, Regel> {
            val regelListe = listOf(
                ErArbeidsforholdetMaritimtRegel.fraDatagrunnlag(datagrunnlag),
                ErArbeidsgiverOrganisasjonRegel.fraDatagrunnlag(datagrunnlag),
                ErBrukerPilotEllerKabinansattRegel.fraDatagrunnlag(datagrunnlag),
                ErForetaketAktivtRegel.fraDatagrunnlag(datagrunnlag),
                HarBrukerSammenhengendeArbeidsforholdRegel.fraDatagrunnlag(datagrunnlag),
                HarForetaketMerEnn5AnsatteRegel.fraDatagrunnlag(datagrunnlag),
                JobberBrukerPaaNorskSkipRegel.fraDatagrunnlag(datagrunnlag),
                ErArbeidsforholdetOffentligSektor.fraDatagrunnlag(datagrunnlag),
                ErArbeidsforholdetFrilanserRegel.fraDatagrunnlag(datagrunnlag)
            )

            return regelListe.map { it.regelId to it.regel }.toMap()
        }
    }
}
