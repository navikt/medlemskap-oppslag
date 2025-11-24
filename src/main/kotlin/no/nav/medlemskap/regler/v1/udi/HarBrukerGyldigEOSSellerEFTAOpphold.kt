package no.nav.medlemskap.regler.v1.udi

import no.nav.medlemskap.domene.Datagrunnlag
import no.nav.medlemskap.domene.Kontrollperiode.Companion.kontrollPeriodeForOppholdstillatelse
import no.nav.medlemskap.domene.Oppholdstillatelse
import no.nav.medlemskap.domene.Ytelse
import no.nav.medlemskap.regler.common.RegelId
import no.nav.medlemskap.regler.common.Resultat
import no.nav.medlemskap.regler.common.Resultat.Companion.ja
import no.nav.medlemskap.regler.common.Resultat.Companion.nei
import no.nav.medlemskap.regler.v1.lovvalg.LovvalgRegel
import java.time.LocalDate

class HarBrukerGyldigEOSSellerEFTAOpphold(
    ytelse: Ytelse,
    private val oppholdstillatelse: Oppholdstillatelse?,
    startDatoForYtelse: LocalDate,
    regelId: RegelId = RegelId.REGEL_70
) : LovvalgRegel(regelId, ytelse, startDatoForYtelse) {

    override fun operasjon(): Resultat {
        val kontrollperiode = kontrollPeriodeForOppholdstillatelse(startDatoForYtelse)
        val oppholdsrettPeriode =  oppholdstillatelse?.gjeldendeOppholdsstatus?.eosellerEFTAOpphold?.periode

        if (oppholdsrettPeriode!!.erTom()) {
            return nei(regelId)
        }

        if (oppholdsrettPeriode.encloses(kontrollperiode.periode)) {
            return ja(regelId)
        }

        return nei(regelId)
    }

    companion object {

        fun fraDatagrunnlag(datagrunnlag: Datagrunnlag): HarBrukerGyldigEOSSellerEFTAOpphold {
            return HarBrukerGyldigEOSSellerEFTAOpphold(
                ytelse = datagrunnlag.ytelse,
                oppholdstillatelse = datagrunnlag.oppholdstillatelse,
                startDatoForYtelse = datagrunnlag.startDatoForYtelse
            )
        }
    }
}
