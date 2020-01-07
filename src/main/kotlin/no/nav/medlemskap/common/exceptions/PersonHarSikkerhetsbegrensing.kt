package no.nav.medlemskap.common.exceptions

import java.lang.Exception

class PersonHarSikkerhetsbegrensing(cause: Throwable, val system: String) : Exception(cause)
