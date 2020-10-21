package no.nav.medlemskap.regler.v1

import no.nav.medlemskap.domene.Datagrunnlag
import no.nav.medlemskap.domene.Ytelse
import no.nav.medlemskap.regler.common.*
import no.nav.medlemskap.regler.common.RegelId.*
import no.nav.medlemskap.regler.v1.arbeidsforhold.*
import no.nav.medlemskap.regler.v1.lovvalg.HarBrukerJobbetUtenforNorgeRegel

class ReglerForArbeidsforhold(
    ytelse: Ytelse,
    regelMap: Map<RegelId, Regel>,
    overstyrteRegler: Map<RegelId, Svar>
) : Regler(ytelse, regelMap, overstyrteRegler) {

    override fun kjørRegelflyter(): List<Resultat> {
        return listOf(kjørUavhengigeRegelflyterMedEttResultat(REGEL_ARBEIDSFORHOLD))
    }

    override fun hentRegelflyter(): List<Regelflyt> {
        val harBrukerJobbetUtenforNorgeFlyt = lagRegelflyt(
            regel = hentRegel(REGEL_9),
            hvisJa = konklusjonUavklart(ytelse),
            hvisNei = regelflytJa(ytelse)
        )

        val jobberBrukerPaaNorskSkipFlyt = lagRegelflyt(
            regel = hentRegel(REGEL_7_1),
            hvisJa = regelflytJa(ytelse),
            hvisNei = regelflytUavklart(ytelse)
        )

        val erBrukerPilotEllerKabinansattFlyt = lagRegelflyt(
            regel = hentRegel(REGEL_8),
            hvisJa = regelflytUavklart(ytelse),
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
            hvisNei = regelflytUavklart(ytelse)
        )

        val harForetakMerEnn5AnsatteFlyt = lagRegelflyt(
            regel = hentRegel(REGEL_5),
            hvisJa = erForetakAktivtFlyt,
            hvisNei = regelflytUavklart(ytelse)
        )

        val erArbeidsgiverOffentligSektor = lagRegelflyt(
            regel = hentRegel(REGEL_14),
            hvisJa = harBrukerJobbetUtenforNorgeFlyt,
            hvisNei = harForetakMerEnn5AnsatteFlyt
        )

        val erArbeidsgiverOrganisasjonFlyt = lagRegelflyt(
            regel = hentRegel(REGEL_4),
            hvisJa = erArbeidsgiverOffentligSektor,
            hvisNei = regelflytUavklart(ytelse)
        )

        val harBrukerSammenhengendeArbeidsforholdSiste12MndFlyt = lagRegelflyt(
            regel = hentRegel(REGEL_3),
            hvisJa = erArbeidsgiverOrganisasjonFlyt,
            hvisNei = regelflytUavklart(ytelse)
        )

        return listOf(harBrukerSammenhengendeArbeidsforholdSiste12MndFlyt)
    }

    companion object {
        fun fraDatagrunnlag(datagrunnlag: Datagrunnlag): ReglerForArbeidsforhold {
            return ReglerForArbeidsforhold(
                ytelse = datagrunnlag.ytelse,
                regelMap = lagRegelMap(datagrunnlag),
                overstyrteRegler = datagrunnlag.overstyrteRegler
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
                HarBrukerJobbetUtenforNorgeRegel.fraDatagrunnlag(datagrunnlag),
                ErArbeidsforholdetOffentligSektor.fraDatagrunnlag(datagrunnlag)
            )

            return regelListe.map { it.regelId to it.regel }.toMap()
        }
    }
}
