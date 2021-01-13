package no.nav.medlemskap.regler.v1.udi

import no.nav.medlemskap.domene.Datagrunnlag
import no.nav.medlemskap.domene.InputPeriode
import no.nav.medlemskap.domene.Oppholdstillatelse
import no.nav.medlemskap.domene.Ytelse
import no.nav.medlemskap.domene.arbeidsforhold.Arbeidsforhold
import no.nav.medlemskap.regler.common.BasisRegel
import no.nav.medlemskap.regler.common.RegelId
import no.nav.medlemskap.regler.common.Resultat
import no.nav.medlemskap.regler.common.Resultat.Companion.ja
import no.nav.medlemskap.regler.common.Resultat.Companion.nei
import java.time.LocalDate

class DekkerOppholdstillatelseArbeidsperiodeRegel(
    ytelse: Ytelse,
    private val periode: InputPeriode,
    private val oppholdstillatelse: Oppholdstillatelse?,
    private val arbeidsforhold: List<Arbeidsforhold>,
    private val førsteDagForYtelse: LocalDate?,
    regelId: RegelId = RegelId.REGEL_16
) : BasisRegel(regelId, ytelse) {

    override fun operasjon(): Resultat {

        for (arbeidsforhold in arbeidsforhold) {
            if (oppholdstillatelse == null ||
                (
                    oppholdstillatelse.harGyldigOppholdOgArbeidstillatelse() &&
                        (
                            !oppholdstillatelse.gjeldendeOppholdsstatus!!.periode.encloses(arbeidsforhold.periode) ||
                                !oppholdstillatelse.arbeidsadgang!!.periode.encloses(arbeidsforhold.periode)
                            )
                    )
            ) {
                return nei(regelId)
            }
        }

        return ja(regelId)
    }

    companion object {

        fun fraDatagrunnlag(datagrunnlag: Datagrunnlag): DekkerOppholdstillatelseArbeidsperiodeRegel {
            return DekkerOppholdstillatelseArbeidsperiodeRegel(
                ytelse = datagrunnlag.ytelse,
                periode = datagrunnlag.periode,
                oppholdstillatelse = datagrunnlag.oppholdstillatelse,
                arbeidsforhold = datagrunnlag.arbeidsforhold,
                førsteDagForYtelse = datagrunnlag.førsteDagForYtelse
            )
        }
    }
}
