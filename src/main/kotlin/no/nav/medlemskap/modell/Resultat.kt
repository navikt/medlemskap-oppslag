package no.nav.medlemskap.modell

enum class Resultattype {
    JA, NEI, KANSKJE
}

data class Resultat(val resultattype: Resultattype, val beskrivelse: String)
