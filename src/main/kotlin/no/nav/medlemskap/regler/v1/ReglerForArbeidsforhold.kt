package no.nav.medlemskap.regler.v1

import no.nav.medlemskap.domene.Datagrunnlag
import no.nav.medlemskap.domene.Ytelse
import no.nav.medlemskap.regler.common.*
import no.nav.medlemskap.regler.common.RegelId.*
import no.nav.medlemskap.regler.v1.arbeidsforhold.*

class ReglerForArbeidsforhold(
        ytelse: Ytelse,
        val reglerForLovvalg: ReglerForLovvalg,
        regelMap: Map<RegelId, Regel>
) : Regler(ytelse, regelMap) {

    override fun hentRegelflyt(): Regelflyt {
        val jobberBrukerPaaNorskSkipFlyt = lagRegelflyt(
                regel = hentRegel(REGEL_7_1),
                hvisJa = reglerForLovvalg.hentRegelflyt(),
                hvisNei = regelFlytUavklart(ytelse)
        )

        val erBrukerPilotEllerKabinansattFlyt = lagRegelflyt(
                regel = hentRegel(REGEL_8),
                hvisJa = regelFlytUavklart(ytelse),
                hvisNei = reglerForLovvalg.hentRegelflyt()
        )

        val erArbeidsforholdetMaritimtFlyt = lagRegelflyt(
                regel = hentRegel(REGEL_7),
                hvisJa = jobberBrukerPaaNorskSkipFlyt,
                hvisNei = erBrukerPilotEllerKabinansattFlyt
        )

        val erForetakAktivtFlyt = lagRegelflyt(
                regel = hentRegel(REGEL_6),
                hvisJa = erArbeidsforholdetMaritimtFlyt,
                hvisNei = regelFlytUavklart(ytelse)
        )

        val harForetakMerEnn5AnsatteFlyt = lagRegelflyt(
                regel = hentRegel(REGEL_5),
                hvisJa = erForetakAktivtFlyt,
                hvisNei = regelFlytUavklart(ytelse)
        )

        val erArbeidsgiverOrganisasjonFlyt = lagRegelflyt(
                regel = hentRegel(REGEL_4),
                hvisJa = harForetakMerEnn5AnsatteFlyt,
                hvisNei = regelFlytUavklart(ytelse)
        )

        val harBrukerSammenhengendeArbeidsforholdSiste12MndFlyt = lagRegelflyt(
                regel = hentRegel(REGEL_3),
                hvisJa = erArbeidsgiverOrganisasjonFlyt,
                hvisNei = regelFlytUavklart(ytelse)
        )

        return harBrukerSammenhengendeArbeidsforholdSiste12MndFlyt
    }


    companion object {
        fun fraDatagrunnlag(datagrunnlag: Datagrunnlag): ReglerForArbeidsforhold {
            val reglerForLovvalg = ReglerForLovvalg.fraDatagrunnlag(datagrunnlag)

            return ReglerForArbeidsforhold(
                    ytelse = datagrunnlag.ytelse,
                    reglerForLovvalg = reglerForLovvalg,
                    regelMap = lagRegelMap(datagrunnlag)
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
                    JobberBrukerPaaNorskSkipRegel.fraDatagrunnlag(datagrunnlag)
            )

            return regelListe.map { it.regelId to it.regel }.toMap()
        }
    }
}