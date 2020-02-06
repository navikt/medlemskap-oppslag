package no.nav.medlemskap.regler.common

data class Resultat(
        val identifikator: String = "",
        val avklaring: String = "",
        val resultat: Resultattype,
        val beskrivelse: String,
        val delresultat: List<Resultat> = listOf()
) {
    infix fun hvisJa(metode: () -> Resultat?): Resultat {
        if (resultat == Resultattype.JA) {
            metode.invoke()
        }
        return this
    }

    infix fun hvisNei(metode: () -> Resultat?): Resultat {
        if (resultat == Resultattype.NEI) {
            metode.invoke()
        }
        return this
    }

    infix fun hvisUavklart(metode: () -> Resultat?): Resultat {
        if (resultat == Resultattype.UAVKLART) {
            metode.invoke()
        }
        return this
    }

}
