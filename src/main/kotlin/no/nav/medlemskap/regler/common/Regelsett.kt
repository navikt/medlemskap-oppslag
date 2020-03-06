package no.nav.medlemskap.regler.common

import no.nav.medlemskap.common.regelCounter

abstract class Regelsett(val navn: String) {

    private var konklusjon: Resultat? = null
    private val delresultat: MutableList<Resultat> = mutableListOf()

    protected abstract val KONKLUSJON_IDENTIFIKATOR: String
    protected abstract val KONKLUSJON_AVKLARING: String

    abstract fun evaluer(personfakta: Personfakta): Resultat

    protected fun avklar(metode: () -> Resultat): Resultat {
        val resultat = metode.invoke()
        delresultat.add(resultat)
        return resultat
    }

    protected fun konkluderMed(resultat: Resultat): Resultat? {
        konklusjon = resultat.copy(
                identifikator = KONKLUSJON_IDENTIFIKATOR,
                avklaring = KONKLUSJON_AVKLARING,
                delresultat = delresultat
        )
        return null
    }

    protected fun hentUtKonklusjon(underResultat: Resultat): Resultat {
        return (konklusjon
                ?: uavklart("Kom ikke til noen konklusjon")).apply { regelCounter(navn.replace("?", ""), this.resultat.name).increment() }
    }

    protected fun ja(begrunnelse: String): Resultat = Resultat(
            resultat = Resultattype.JA,
            beskrivelse = begrunnelse
    )

    protected fun nei(begrunnelse: String): Resultat = Resultat(
            resultat = Resultattype.NEI,
            beskrivelse = begrunnelse
    )

    protected fun uavklart(begrunnelse: String): Resultat = Resultat(
            resultat = Resultattype.UAVKLART,
            beskrivelse = begrunnelse
    )

}
