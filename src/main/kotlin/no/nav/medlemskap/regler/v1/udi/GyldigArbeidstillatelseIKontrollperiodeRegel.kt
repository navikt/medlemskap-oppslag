package no.nav.medlemskap.regler.v1.udi

import no.nav.medlemskap.domene.*
import no.nav.medlemskap.regler.common.BasisRegel
import no.nav.medlemskap.regler.common.RegelId
import no.nav.medlemskap.regler.common.Resultat
import no.nav.medlemskap.regler.common.Resultat.Companion.ja
import no.nav.medlemskap.regler.common.Resultat.Companion.nei
import java.time.LocalDate

class GyldigArbeidstillatelseIKontrollperiodeRegel(
    ytelse: Ytelse,
    private val periode: InputPeriode,
    private val oppholdstillatelse: Oppholdstillatelse?,
    private val førsteDagForYtelse: LocalDate?,
    regelId: RegelId = RegelId.REGEL_19_3
) : BasisRegel(regelId, ytelse) {

    override fun operasjon(): Resultat {

        val kontrollperiode = Kontrollperiode.kontrollPeriodeForOppholdstillatelse(periode, førsteDagForYtelse)
        if (oppholdstillatelse != null &&
            oppholdstillatelse.harGyldigArbeidstillatelseForPeriode(kontrollperiode.periode)
        ) {
            return ja(regelId)
        }

        return nei(regelId)
    }

    companion object {

        fun fraDatagrunnlag(datagrunnlag: Datagrunnlag): GyldigArbeidstillatelseIKontrollperiodeRegel {
            return GyldigArbeidstillatelseIKontrollperiodeRegel(
                ytelse = datagrunnlag.ytelse,
                periode = datagrunnlag.periode,
                oppholdstillatelse = datagrunnlag.oppholdstillatelse,
                førsteDagForYtelse = datagrunnlag.førsteDagForYtelse
            )
        }
    }
}
