package no.nav.medlemskap.regler.common

import no.nav.medlemskap.common.regelCounter

data class Avklaring(
        val identifikator: String,
        val avklaring: String,
        val beskrivelse: String,
        val operasjon: (f: Personfakta) -> Resultat
) {
    infix fun evaluerMed(personfakta: Personfakta): Resultat {
        val resultat = operasjon.invoke(personfakta).apply { regelCounter.labels(this@Avklaring.avklaring.replace("?", ""), this.resultat.name).inc() }
        return resultat.copy(identifikator = identifikator, avklaring = avklaring)
    }
}
