package no.nav.medlemskap.common.exceptions

import no.nav.medlemskap.common.objectMapper
import java.lang.Exception

data class GraphqlError(val error: Any, val system: String) : Exception() {
    fun errorAsJson() = objectMapper.writeValueAsString(error)
}
