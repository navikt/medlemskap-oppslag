package no.nav.medlemskap.regler.v1.lovvalg

import no.nav.medlemskap.domene.Datagrunnlag
import no.nav.medlemskap.domene.InputPeriode
import no.nav.medlemskap.domene.Ytelse
import no.nav.medlemskap.regler.common.RegelId
import no.nav.medlemskap.regler.common.Resultat
import no.nav.medlemskap.regler.common.ja
import no.nav.medlemskap.regler.common.nei

class HarBrukerJobbetUtenforNorgeRegel(
        ytelse: Ytelse,
        private val periode: InputPeriode,
        private val arbeidUtenforNorge: Boolean,
        regelId: RegelId = RegelId.REGEL_9
) : LovvalgRegel(regelId, ytelse, periode) {

    override fun operasjon(): Resultat {
        return when {
            arbeidUtenforNorge -> ja()
            else -> nei()
        }
    }

    companion object {

        fun fraDatagrunnlag(datagrunnlag: Datagrunnlag): HarBrukerJobbetUtenforNorgeRegel {
            return HarBrukerJobbetUtenforNorgeRegel(
                    ytelse = datagrunnlag.ytelse,
                    periode = datagrunnlag.periode,
                    arbeidUtenforNorge = datagrunnlag.brukerinput.arbeidUtenforNorge
            )
        }
    }
}