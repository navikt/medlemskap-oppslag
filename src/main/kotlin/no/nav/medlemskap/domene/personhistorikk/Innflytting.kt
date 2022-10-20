package no.nav.medlemskap.domene.personhistorikk

data class Innflytting(
    val fraflyttingsland: String?,
    val fraflyttingsstedIUtlandet: String?,
    val folkeregistermetadata: Folkeregistermetadata
)
