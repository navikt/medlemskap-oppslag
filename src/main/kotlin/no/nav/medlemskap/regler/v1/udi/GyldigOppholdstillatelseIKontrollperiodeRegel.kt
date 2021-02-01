package no.nav.medlemskap.regler.v1.udi

import no.nav.medlemskap.domene.*
import no.nav.medlemskap.regler.common.BasisRegel
import no.nav.medlemskap.regler.common.RegelId
import no.nav.medlemskap.regler.common.Resultat
import no.nav.medlemskap.regler.common.Resultat.Companion.ja
import no.nav.medlemskap.regler.common.Resultat.Companion.nei
import no.nav.medlemskap.regler.common.Resultat.Companion.uavklart
import java.time.LocalDate

class GyldigOppholdstillatelseIKontrollperiodeRegel(
    ytelse: Ytelse,
    private val oppholdstillatelse: Oppholdstillatelse?,
    private val periode: InputPeriode,
    private val førsteDagForYtelse: LocalDate?,
    regelId: RegelId = RegelId.REGEL_19_2
) : BasisRegel(regelId, ytelse) {

    override fun operasjon(): Resultat {

        if (oppholdstillatelse?.gjeldendeOppholdsstatus == null || oppholdstillatelse.arbeidsadgang == null) {
            return uavklart(regelId)
        }

        val kontrollperiode = Kontrollperiode.kontrollPeriodeForOppholdstillatelse(periode, førsteDagForYtelse)
        if (oppholdstillatelse.harGyldigOppholdstillatelseForPeriode(kontrollperiode.periode)) {
            return ja(regelId)
        }

        return nei(regelId)
    }

    companion object {

        fun fraDatagrunnlag(datagrunnlag: Datagrunnlag): GyldigOppholdstillatelseIKontrollperiodeRegel {
            return GyldigOppholdstillatelseIKontrollperiodeRegel(
                ytelse = datagrunnlag.ytelse,
                periode = datagrunnlag.periode,
                oppholdstillatelse = datagrunnlag.oppholdstillatelse,
                førsteDagForYtelse = datagrunnlag.førsteDagForYtelse
            )
        }
    }
}
