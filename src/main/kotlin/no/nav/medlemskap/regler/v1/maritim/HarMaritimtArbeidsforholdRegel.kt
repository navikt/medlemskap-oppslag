package no.nav.medlemskap.regler.v1.maritim

import no.nav.medlemskap.domene.Datagrunnlag
import no.nav.medlemskap.domene.Ytelse
import no.nav.medlemskap.domene.arbeidsforhold.Arbeidsforhold
import no.nav.medlemskap.domene.arbeidsforhold.Arbeidsforhold.Companion.arbeidsforholdForYrkestype
import no.nav.medlemskap.domene.arbeidsforhold.Arbeidsforhold.Companion.maritimeArbeidsforholdForKontrollPeriode
import no.nav.medlemskap.domene.arbeidsforhold.Arbeidsforholdstype
import no.nav.medlemskap.regler.common.Funksjoner.alleEr
import no.nav.medlemskap.regler.common.Funksjoner.isNotNullOrEmpty
import no.nav.medlemskap.regler.common.RegelId
import no.nav.medlemskap.regler.common.Resultat
import no.nav.medlemskap.regler.common.Resultat.Companion.ja
import no.nav.medlemskap.regler.common.Resultat.Companion.nei
import no.nav.medlemskap.regler.v1.arbeidsforhold.ArbeidsforholdRegel
import java.time.LocalDate

class HarMaritimtArbeidsforholdRegel(
    ytelse: Ytelse,
    startDatoForYtelse: LocalDate,
    private val arbeidsforhold: List<Arbeidsforhold>,
    regelId: RegelId = RegelId.REGEL_42
) : ArbeidsforholdRegel(regelId, ytelse, startDatoForYtelse) {

    override fun operasjon(): Resultat {
        return when {
            arbeidsforhold.isEmpty() -> nei(regelId)
            arbeidsforhold.maritimeArbeidsforholdForKontrollPeriode(kontrollPeriodeForArbeidsforhold).isNotNullOrEmpty() -> ja(
                regelId
            )
            else -> nei(regelId)
        }
    }

    companion object {

        fun fraDatagrunnlag(datagrunnlag: Datagrunnlag): HarMaritimtArbeidsforholdRegel {
            return HarMaritimtArbeidsforholdRegel(
                ytelse = datagrunnlag.ytelse,
                startDatoForYtelse = datagrunnlag.startDatoForYtelse,
                arbeidsforhold = datagrunnlag.arbeidsforhold
            )
        }
    }
}
