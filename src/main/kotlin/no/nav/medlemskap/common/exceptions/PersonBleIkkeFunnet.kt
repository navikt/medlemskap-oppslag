package no.nav.medlemskap.common.exceptions

import java.lang.Exception

class PersonBleIkkeFunnet(cause: Throwable, val system: String) : Exception(cause)
