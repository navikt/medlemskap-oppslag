package no.nav.medlemskap.services.pdl

data class HentPdlPersonResponse (
    val adressebeskyttelse: List<String>?,
    val kjoenn: List<String>?,
    val navn: List<Navn>,
    val bostedsadresse: List<Bostedsadresse>?)

data class Navn (
    val fornavn: String,
    val mellomnavn: String?,
    val etternavn: String )

data class Bostedsadresse(val adresse: String)
