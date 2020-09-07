package no.nav.medlemskap.regler.v1.registrerteOpplysninger

import no.nav.medlemskap.domene.Datagrunnlag
import no.nav.medlemskap.domene.Oppgave
import no.nav.medlemskap.domene.Ytelse
import no.nav.medlemskap.regler.common.*
import no.nav.medlemskap.regler.funksjoner.GsakFunksjoner.finnesAapneOppgaver

class FinnesOpplysningerIGosysRegel(
    ytelse: Ytelse,
    val oppgaver: List<Oppgave> = emptyList()
) : BasisRegel(RegelId.REGEL_B, ytelse) {
    override fun operasjon(): Resultat {
        return when {
            oppgaver.finnesAapneOppgaver() -> ja()
            else -> nei()
        }
    }

    companion object {

        fun fraDatagrunnlag(datagrunnlag: Datagrunnlag): FinnesOpplysningerIGosysRegel {
            return FinnesOpplysningerIGosysRegel(
                ytelse = datagrunnlag.ytelse,
                oppgaver = datagrunnlag.oppgaver
            )
        }
    }
}
