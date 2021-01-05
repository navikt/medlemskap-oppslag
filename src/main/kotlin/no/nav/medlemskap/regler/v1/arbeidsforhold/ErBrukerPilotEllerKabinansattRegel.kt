package no.nav.medlemskap.regler.v1.arbeidsforhold

import no.nav.medlemskap.domene.*
import no.nav.medlemskap.domene.Datagrunnlag
import no.nav.medlemskap.domene.arbeidsforhold.Arbeidsforhold
import no.nav.medlemskap.domene.arbeidsforhold.YrkeskoderForLuftFart
import no.nav.medlemskap.regler.common.Funksjoner.inneholderNoe
import no.nav.medlemskap.regler.common.RegelId
import no.nav.medlemskap.regler.common.Resultat
import no.nav.medlemskap.regler.common.Resultat.Companion.ja
import no.nav.medlemskap.regler.common.Resultat.Companion.nei
import no.nav.medlemskap.regler.funksjoner.ArbeidsforholdFunksjoner.sisteArbeidsforholdYrkeskode
import java.time.LocalDate

class ErBrukerPilotEllerKabinansattRegel(
    ytelse: Ytelse,
    private val periode: InputPeriode,
    private val førsteDagForYtelse: LocalDate?,
    private val arbeidsforhold: List<Arbeidsforhold>,
    regelId: RegelId = RegelId.REGEL_8
) : ArbeidsforholdRegel(regelId, ytelse, periode, førsteDagForYtelse) {

    override fun operasjon(): Resultat {
        return when {
            arbeidsforhold.isEmpty() -> nei(regelId)
            arbeidsforhold sisteArbeidsforholdYrkeskode kontrollPeriodeForArbeidsforhold inneholderNoe yrkeskoderLuftfart -> ja(regelId)
            else -> nei(regelId)
        }
    }

    companion object {
        private val yrkeskoderLuftfart = YrkeskoderForLuftFart.values().map { it.styrk }

        fun fraDatagrunnlag(datagrunnlag: Datagrunnlag): ErBrukerPilotEllerKabinansattRegel {
            return ErBrukerPilotEllerKabinansattRegel(
                ytelse = datagrunnlag.ytelse,
                periode = datagrunnlag.periode,
                førsteDagForYtelse = datagrunnlag.førsteDagForYtelse,
                arbeidsforhold = datagrunnlag.arbeidsforhold
            )
        }
    }
}
