package no.nav.medlemskap.regler.v2.common

import no.nav.medlemskap.regler.common.Resultattype

data class Element(
        val spørsmål: () -> Spørsmål,
        val resultatSomGirUavklart: Resultattype? = null,
        val begrunnelseVedUavklart: String? = null
)

class Spørsmålsrekke(val liste: List<Element>) {

    companion object {
        fun spørsmålsrekke(vararg spørsmål: Element) = Spørsmålsrekke(spørsmål.asList())
    }

    fun evaluerMedSvarHvisVellykket(metode: () -> Svar): Svar {
        val utførteSpørsmål = mutableListOf<Spørsmål>()

        for (spørsmålIRekke in liste) {
            val spm = spørsmålIRekke.spørsmål.invoke()
            utførteSpørsmål.add(spm)
            if (spørsmålIRekke.resultatSomGirUavklart == spm.svar.resultat) {
                return Svar(
                        resultat = Resultattype.UAVKLART,
                        begrunnelse = spørsmålIRekke.begrunnelseVedUavklart ?: "",
                        underspørsmål = utførteSpørsmål
                )
            }
        }
        return metode.invoke().copy(underspørsmål = utførteSpørsmål)
    }

}
