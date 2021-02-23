package no.nav.medlemskap.regler.v1.lovvalg

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
    regelId: RegelId = RegelId.REGEL_9
) : LovvalgRegel(regelId, ytelse, startDatoForYtelse) {

    override fun operasjon(): Resultat {
        return when {
            arbeidUtenforNorge -> ja(regelId)
            else -> nei(regelId)
        }
    }

    companion object {

        fun fraDatagrunnlag(datagrunnlag: Datagrunnlag): HarBrukerJobbetUtenforNorgeRegel {
            return HarBrukerJobbetUtenforNorgeRegel(
                ytelse = datagrunnlag.ytelse,
                startDatoForYtelse = datagrunnlag.startDatoForYtelse,
                arbeidUtenforNorge = datagrunnlag.brukerinput.arbeidUtenforNorge
            )
        }
    }
}
