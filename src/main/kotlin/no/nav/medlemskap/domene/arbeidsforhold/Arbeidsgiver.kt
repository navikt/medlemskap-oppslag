package no.nav.medlemskap.domene.arbeidsforhold

data class Arbeidsgiver(
    val organisasjonsnummer: String?,
    val ansatte: List<Ansatte>?,
    val konkursStatus: List<String?>?,
    val juridiskeEnheter: List<JuridiskEnhet?>?
)
