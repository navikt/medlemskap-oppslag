package no.nav.medlemskap.regler.v2.common

enum class Svar {
    JA, NEI, UAVKLART
}

data class Resultat(
        val identifikator: String = "ukjent",
        val avklaring: String = "uavklart",
        val begrunnelse: String = "ukjent",
        val svar: Svar,
        val delresultat: List<Resultat> = listOf()
)
