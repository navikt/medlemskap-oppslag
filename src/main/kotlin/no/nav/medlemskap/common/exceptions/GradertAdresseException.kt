package no.nav.medlemskap.common.exceptions

import no.nav.medlemskap.domene.Ytelse
import java.lang.Exception

class GradertAdresseException(val system: String, val ytelse: Ytelse) : Exception()
