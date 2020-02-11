package no.nav.medlemskap.domene

import no.nav.medlemskap.domene.Datagrunnlag
import no.nav.medlemskap.regler.common.Resultat
import java.time.LocalDateTime

data class Response (
        val tidspunkt: LocalDateTime,
        val versjonTjeneste: String,
        val versjonRegler: String,
        val datagrunnlag: Datagrunnlag,
        val resultat: Resultat
)

