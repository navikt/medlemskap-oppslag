package no.nav.medlemskap.common.exceptions

import io.ktor.features.*
import no.nav.medlemskap.domene.Ytelse

class UgyldigRequestException(message: String, val ytelse: Ytelse) : BadRequestException(message)
