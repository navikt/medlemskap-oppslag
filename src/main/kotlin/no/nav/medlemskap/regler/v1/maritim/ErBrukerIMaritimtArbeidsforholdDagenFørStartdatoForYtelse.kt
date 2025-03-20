package no.nav.medlemskap.regler.v1.maritim

import no.nav.medlemskap.domene.Datagrunnlag
import no.nav.medlemskap.domene.Ytelse
import no.nav.medlemskap.domene.arbeidsforhold.Arbeidsforhold
import no.nav.medlemskap.domene.arbeidsforhold.Arbeidsforhold.Companion.harMaritimtArbeidsforholdDagenFørStartDatoForYtelse
import no.nav.medlemskap.regler.common.RegelId
import no.nav.medlemskap.regler.common.Resultat
import no.nav.medlemskap.regler.common.Resultat.Companion.ja
import no.nav.medlemskap.regler.common.Resultat.Companion.nei
import no.nav.medlemskap.regler.v1.arbeidsforhold.ArbeidsforholdRegel
import java.time.LocalDate

class ErBrukerIMaritimtArbeidsforholdDagenFørStartdatoForYtelse(
    ytelse: Ytelse,
    private val startDatoForYtelse: LocalDate,
    private val arbeidsforhold: List<Arbeidsforhold>,
    regelId: RegelId = RegelId.REGEL_35
) : ArbeidsforholdRegel(regelId, ytelse, startDatoForYtelse) {

    override fun operasjon(): Resultat {
        return when {
            arbeidsforhold.harMaritimtArbeidsforholdDagenFørStartDatoForYtelse(startDatoForYtelse) -> ja(regelId)
            else -> nei(regelId)
        }
    }

    companion object {
        fun fraDatagrunnlag(datagrunnlag: Datagrunnlag): ErBrukerIMaritimtArbeidsforholdDagenFørStartdatoForYtelse {
            return ErBrukerIMaritimtArbeidsforholdDagenFørStartdatoForYtelse(
                ytelse = datagrunnlag.ytelse,
                startDatoForYtelse = datagrunnlag.startDatoForYtelse,
                arbeidsforhold = datagrunnlag.arbeidsforhold
            )
        }
    }
}
