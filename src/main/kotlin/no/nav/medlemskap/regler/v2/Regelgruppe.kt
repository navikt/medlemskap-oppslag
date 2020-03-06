package no.nav.medlemskap.regler.v2

import no.nav.medlemskap.regler.common.Resultattype

abstract class Regelgruppe {

    abstract fun evaluer(): Spørsmål

    protected fun minstEnAvDisse(vararg spørsmål: Spørsmål): Svar {
        val svarListe = spørsmål.map { it.utfør() }
        val svar = utledResultat(svarListe)
        return Svar(
                resultat = svar,
                begrunnelse = "Minst en av spørsmålene ble ${svar.name}",
                underspørsmål = svarListe
        )
    }

    private fun utledResultat(liste: List<Spørsmål>): Resultattype {
        val resultatliste = liste.map { it.svar.resultat }
        return when {
            resultatliste.any { it == Resultattype.JA } -> Resultattype.JA
            resultatliste.any { it == Resultattype.UAVKLART } -> Resultattype.UAVKLART
            else -> Resultattype.NEI
        }
    }
}
