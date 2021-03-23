package no.nav.medlemskap.regler.v1.udi

import no.nav.medlemskap.domene.Datagrunnlag
import no.nav.medlemskap.domene.Oppholdstillatelse
import no.nav.medlemskap.domene.Ytelse
import no.nav.medlemskap.domene.ektefelle.DataOmEktefelle
import no.nav.medlemskap.regler.common.BasisRegel
import no.nav.medlemskap.regler.common.RegelId
import no.nav.medlemskap.regler.common.Resultat
import no.nav.medlemskap.regler.common.Resultat.Companion.ja
import no.nav.medlemskap.regler.common.Resultat.Companion.nei

class ErArbeidsadgangUavklartRegel(
    ytelse: Ytelse,
    private val oppholdstillatelse: Oppholdstillatelse?,
    private val dataOmEktefelle: DataOmEktefelle?,
    regelId: RegelId = RegelId.REGEL_19_5
) : BasisRegel(regelId, ytelse) {

    override fun operasjon(): Resultat {
        if (oppholdstillatelse?.gjeldendeOppholdsstatus?.eosellerEFTAOpphold != null &&
            dataOmEktefelle?.personhistorikkEktefelle?.statsborgerskap?.any { it.landkode == "GBR" } == true
        ) {
            return ja(regelId)
        }

        return nei(regelId)
    }

    companion object {

        fun fraDatagrunnlag(datagrunnlag: Datagrunnlag): ErArbeidsadgangUavklartRegel {
            return ErArbeidsadgangUavklartRegel(
                ytelse = datagrunnlag.ytelse,
                dataOmEktefelle = datagrunnlag.dataOmEktefelle,
                oppholdstillatelse = datagrunnlag.oppholdstillatelse
            )
        }
    }
}
