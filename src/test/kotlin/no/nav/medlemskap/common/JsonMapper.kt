package no.nav.medlemskap.common

import no.nav.medlemskap.domene.Datagrunnlag
import no.nav.medlemskap.regler.common.Resultat

object JsonMapper {

    fun mapToJson(datagrunnlag: Datagrunnlag): String {
        return objectMapper.writeValueAsString(datagrunnlag)
    }

    fun mapToJson(resultat: Resultat): String {
        return objectMapper.writeValueAsString(resultat)
    }
}
