package no.nav.medlemskap.common.exceptions

import java.lang.Exception

class PersonIkkeFunnet(cause: Throwable, val system: String) : Exception(cause)
