package no.nav.medlemskap.regler.v1.lovvalg

import no.nav.medlemskap.domene.Brukerinput
import no.nav.medlemskap.domene.Datagrunnlag
import no.nav.medlemskap.domene.Ytelse
import no.nav.medlemskap.regler.common.RegelId
import no.nav.medlemskap.regler.common.Resultat
import no.nav.medlemskap.regler.common.Resultat.Companion.ja
import no.nav.medlemskap.regler.common.Resultat.Companion.nei
import java.time.LocalDate

class HarBrukerFaattNyeBrukerSpoersmaalRegel(
    ytelse: Ytelse,
    startDatoForYtelse: LocalDate,
    val brukerInput: Brukerinput,
    regelId: RegelId = RegelId.REGEL_0_2
) : LovvalgRegel(regelId, ytelse, startDatoForYtelse) {

    override fun operasjon(): Resultat {
        return if (brukerInput.oppholdstilatelse?.equals(null) == false ||
            brukerInput.oppholdUtenforNorge?.equals(null) == false ||
            brukerInput.oppholdUtenforEos?.equals(null) == false ||
            brukerInput.utfortAarbeidUtenforNorge?.equals(null) == false
        ) {
            ja(regelId)
        } else nei(regelId)
    }

    companion object {

        fun fraDatagrunnlag(datagrunnlag: Datagrunnlag): HarBrukerFaattNyeBrukerSpoersmaalRegel {
            return HarBrukerFaattNyeBrukerSpoersmaalRegel(
                ytelse = datagrunnlag.ytelse,
                startDatoForYtelse = datagrunnlag.startDatoForYtelse,
                brukerInput = datagrunnlag.brukerinput
            )
        }
    }
}
