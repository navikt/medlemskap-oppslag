package no.nav.medlemskap.common.exceptions

class PersonIkkeFunnet(cause: Throwable, val system: String) : Exception(cause) {
    constructor(system: String)
}
