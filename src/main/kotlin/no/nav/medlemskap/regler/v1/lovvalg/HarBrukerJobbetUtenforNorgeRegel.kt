package no.nav.medlemskap.regler.v1.lovvalg

import no.nav.medlemskap.domene.Brukerinput
import no.nav.medlemskap.domene.Datagrunnlag
import no.nav.medlemskap.domene.Ytelse
import no.nav.medlemskap.regler.common.RegelId
import no.nav.medlemskap.regler.common.Resultat
import no.nav.medlemskap.regler.common.Resultat.Companion.ja
import no.nav.medlemskap.regler.common.Resultat.Companion.nei
import java.time.LocalDate

class HarBrukerJobbetUtenforNorgeRegel(
    ytelse: Ytelse,
    startDatoForYtelse: LocalDate,
    private val arbeidUtenforNorge: Boolean,
    private val brukerInput: Brukerinput,
    regelId: RegelId = RegelId.REGEL_9
) : LovvalgRegel(regelId, ytelse, startDatoForYtelse) {

    override fun operasjon(): Resultat {
        if(brukerInput.oppholdstilatelse?.equals(null) == false ||
                brukerInput.oppholdUtenforNorge?.equals(null) == false ||
                brukerInput.oppholdUtenforEos?.equals(null) == false ||
                brukerInput.utfortAarbeidUtenforNorge?.equals(null) == false)
        return when {
            arbeidUtenforNorge -> ja(regelId)
            else -> nei(regelId)
        }
        return nei(regelId)
    }

    companion object {

        fun fraDatagrunnlag(datagrunnlag: Datagrunnlag): HarBrukerJobbetUtenforNorgeRegel {
            return HarBrukerJobbetUtenforNorgeRegel(
                ytelse = datagrunnlag.ytelse,
                startDatoForYtelse = datagrunnlag.startDatoForYtelse,
                arbeidUtenforNorge = datagrunnlag.brukerinput.arbeidUtenforNorge,
                    brukerInput = datagrunnlag.brukerinput
            )
        }
    }
}
