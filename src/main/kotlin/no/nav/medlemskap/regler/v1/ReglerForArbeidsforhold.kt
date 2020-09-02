package no.nav.medlemskap.regler.v1

import no.nav.medlemskap.domene.Datagrunnlag
import no.nav.medlemskap.domene.Ytelse
import no.nav.medlemskap.regler.common.*
import no.nav.medlemskap.regler.common.RegelId.*
import no.nav.medlemskap.regler.v1.arbeidsforhold.*
import no.nav.medlemskap.regler.v1.lovvalg.HarBrukerJobbetUtenforNorgeRegel

class ReglerForArbeidsforhold(
        ytelse: Ytelse,
        regelMap: Map<RegelId, Regel>
) : Regler(ytelse, regelMap) {

    override fun hentHovedflyt(): Regelflyt {
        val harBrukerJobbetUtenforNorgeFlyt = lagRegelflyt(
                regel = hentRegel(REGEL_9),
                hvisJa = konklusjonUavklart(ytelse),
                hvisNei = regelflytJa(ytelse)
        )

        val jobberBrukerPaaNorskSkipFlyt = lagRegelflyt(
                regel = hentRegel(REGEL_7_1),
                hvisJa = harBrukerJobbetUtenforNorgeFlyt,
                hvisNei = konklusjonUavklart(ytelse)
        )

        val erBrukerPilotEllerKabinansattFlyt = lagRegelflyt(
                regel = hentRegel(REGEL_8),
                hvisJa = konklusjonUavklart(ytelse),
                hvisNei = harBrukerJobbetUtenforNorgeFlyt
        )

        val erArbeidsforholdetMaritimtFlyt = lagRegelflyt(
                regel = hentRegel(REGEL_7),
                hvisJa = jobberBrukerPaaNorskSkipFlyt,
                hvisNei = erBrukerPilotEllerKabinansattFlyt
        )

        val erForetakAktivtFlyt = lagRegelflyt(
                regel = hentRegel(REGEL_6),
                hvisJa = erArbeidsforholdetMaritimtFlyt,
                hvisNei = konklusjonUavklart(ytelse)
        )

        val harForetakMerEnn5AnsatteFlyt = lagRegelflyt(
                regel = hentRegel(REGEL_5),
                hvisJa = erForetakAktivtFlyt,
                hvisNei = konklusjonUavklart(ytelse)
        )

        val erArbeidsgiverOrganisasjonFlyt = lagRegelflyt(
                regel = hentRegel(REGEL_4),
                hvisJa = harForetakMerEnn5AnsatteFlyt,
                hvisNei = konklusjonUavklart(ytelse)
        )

        val harBrukerSammenhengendeArbeidsforholdSiste12MndFlyt = lagRegelflyt(
                regel = hentRegel(REGEL_3),
                hvisJa = erArbeidsgiverOrganisasjonFlyt,
                hvisNei = konklusjonUavklart(ytelse)
        )

        return harBrukerSammenhengendeArbeidsforholdSiste12MndFlyt
    }

    override fun hentRegelflyter(): List<Regelflyt> {
        return listOf(hentHovedflyt())
    }


    companion object {
        fun fraDatagrunnlag(datagrunnlag: Datagrunnlag): ReglerForArbeidsforhold {
            return ReglerForArbeidsforhold(
                    ytelse = datagrunnlag.ytelse,
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
                    JobberBrukerPaaNorskSkipRegel.fraDatagrunnlag(datagrunnlag),
                    HarBrukerJobbetUtenforNorgeRegel.fraDatagrunnlag(datagrunnlag)
            )

            return regelListe.map { it.regelId to it.regel }.toMap()
        }
    }
}