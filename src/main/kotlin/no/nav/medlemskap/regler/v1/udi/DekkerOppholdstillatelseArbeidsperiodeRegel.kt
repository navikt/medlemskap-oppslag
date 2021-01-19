package no.nav.medlemskap.regler.v1.udi

import no.nav.medlemskap.domene.Datagrunnlag
import no.nav.medlemskap.domene.Oppholdstillatelse
import no.nav.medlemskap.domene.Periode
import no.nav.medlemskap.domene.Ytelse
import no.nav.medlemskap.domene.arbeidsforhold.Arbeidsforhold
import no.nav.medlemskap.regler.common.BasisRegel
import no.nav.medlemskap.regler.common.RegelId
import no.nav.medlemskap.regler.common.Resultat
import no.nav.medlemskap.regler.common.Resultat.Companion.ja
import no.nav.medlemskap.regler.common.Resultat.Companion.nei
import no.nav.medlemskap.regler.common.Resultat.Companion.uavklart

class DekkerOppholdstillatelseArbeidsperiodeRegel(
    ytelse: Ytelse,
    private val oppholdstillatelse: Oppholdstillatelse?,
    private val arbeidsforhold: List<Arbeidsforhold>,
    regelId: RegelId = RegelId.REGEL_19_2
) : BasisRegel(regelId, ytelse) {

    override fun operasjon(): Resultat {

        if (oppholdstillatelse?.gjeldendeOppholdsstatus == null) {
            return uavklart(regelId)
        }

        val oppholdstillatelse = oppholdstillatelse.gjeldendeOppholdsstatus.oppholdstillatelsePaSammeVilkar

        for (arbeidsforhold in arbeidsforhold) {
            val arbeidsforholdTilOgMedDato = arbeidsforhold.periode.tom ?: oppholdstillatelse?.periode?.tom
            val arbeidsforholdPeriode = Periode(arbeidsforhold.periode.fom, arbeidsforholdTilOgMedDato)

            if (oppholdstillatelse?.periode?.encloses(arbeidsforholdPeriode) == true) {
                return ja(regelId)
            }
        }
        return nei(regelId)
    }

    companion object {

        fun fraDatagrunnlag(datagrunnlag: Datagrunnlag): DekkerOppholdstillatelseArbeidsperiodeRegel {
            return DekkerOppholdstillatelseArbeidsperiodeRegel(
                ytelse = datagrunnlag.ytelse,
                oppholdstillatelse = datagrunnlag.oppholdstillatelse,
                arbeidsforhold = datagrunnlag.arbeidsforhold
            )
        }
    }
}
