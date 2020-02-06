package no.nav.medlemskap.regler.common

class HvisUttrykk (private val resultat: Boolean) {

    private var svar: Resultat? = null

    companion object {
        fun hvis(uttrykk: () -> Boolean): HvisUttrykk = HvisUttrykk(uttrykk.invoke())
    }

    infix fun så(metode: () -> Resultat): HvisUttrykk {
        if (resultat) {
            svar = metode.invoke()
        }
        return this
    }

    infix fun ellers(metode: () -> Resultat): Resultat {
        if (resultat) {
            return svar!!
        }
        return metode.invoke()
    }


}
