package no.nav.medlemskap.modell

enum class Resultattype {
    JA, NEI, UAVKLART
}

data class Resultat(val resultattype: Resultattype, val beskrivelse: String)
