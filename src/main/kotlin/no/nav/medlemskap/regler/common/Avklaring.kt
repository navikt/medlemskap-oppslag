package no.nav.medlemskap.regler.common

data class Avklaring(
        val identifikator: String,
        val avklaring: String,
        val beskrivelse: String,
        val operasjon: (f: Personfakta) -> Resultat
)
