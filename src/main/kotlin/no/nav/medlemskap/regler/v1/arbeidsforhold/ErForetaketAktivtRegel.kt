package no.nav.medlemskap.regler.v1.arbeidsforhold

import no.nav.medlemskap.domene.Arbeidsforhold
import no.nav.medlemskap.domene.Datagrunnlag
import no.nav.medlemskap.domene.InputPeriode
import no.nav.medlemskap.domene.Ytelse
import no.nav.medlemskap.regler.common.Funksjoner.finnes
import no.nav.medlemskap.regler.common.RegelId
import no.nav.medlemskap.regler.common.Resultat
import no.nav.medlemskap.regler.common.ja
import no.nav.medlemskap.regler.common.nei
import no.nav.medlemskap.regler.funksjoner.ArbeidsforholdFunksjoner.konkursStatuserArbeidsgivere

class ErForetaketAktivtRegel(
        ytelse: Ytelse,
        private val periode: InputPeriode,
        private val arbeidsforhold: List<Arbeidsforhold>,
        regelId: RegelId = RegelId.REGEL_6
) : ArbeidsforholdRegel(regelId, ytelse, periode) {

    override fun operasjon(): Resultat {
        return when {
            arbeidsforhold.konkursStatuserArbeidsgivere(kontrollPeriodeForArbeidsforhold).finnes() -> nei("Arbeidstaker har hatt arbeidsforhold til arbeidsgiver som har konkurs-status satt")
            else -> ja()
        }
    }

    companion object {

        fun fraDatagrunnlag(datagrunnlag: Datagrunnlag): ErForetaketAktivtRegel {
            return ErForetaketAktivtRegel(
                    ytelse = datagrunnlag.ytelse,
                    periode = datagrunnlag.periode,
                    arbeidsforhold = datagrunnlag.arbeidsforhold
            )
        }
    }
}