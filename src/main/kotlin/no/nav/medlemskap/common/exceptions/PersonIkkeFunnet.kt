package no.nav.medlemskap.common.exceptions

class PersonIkkeFunnet : Exception {
    private val system: String

    constructor(system: String) : super() {
        this.system = system
    }
    constructor(cause: Throwable, system: String) : super(cause) {
        this.system = system
    }
}
