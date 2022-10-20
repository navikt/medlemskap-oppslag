package no.nav.medlemskap.domene.personhistorikk

import org.joda.time.DateTime

data class Folkeregistermetadata(
    val ajourholdstidspunkt: DateTime,
    val gyldighetstidspunkt: DateTime,
    val opphoerstidspunkt: DateTime
)
