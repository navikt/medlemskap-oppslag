package no.nav.medlemskap.domene

import no.nav.medlemskap.regler.v2.common.Resultat
import java.time.LocalDateTime

data class Response (
        val tidspunkt: LocalDateTime,
        val versjonTjeneste: String,
        val versjonRegler: String,
        val datagrunnlag: Datagrunnlag,
        val resultat: List<Resultat>
)

