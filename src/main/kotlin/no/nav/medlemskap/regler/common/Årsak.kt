package no.nav.medlemskap.regler.common

data class Årsak(val regelId: RegelId?, val avklaring: String, val svar: Svar) {
    val beskrivelse = beskrivelse()

    fun beskrivelse(): String {
        val regelIdStr = if (regelId != null) {
            "Regel ${regelId.identifikator}: "
        } else {
            ""
        }

        return "$regelIdStr $avklaring $svar"
    }

    companion object {
        fun fraResultat(resultat: Resultat): Årsak {
            return Årsak(regelId = resultat.regelId, avklaring = resultat.avklaring, svar = resultat.svar)
        }
    }
}
