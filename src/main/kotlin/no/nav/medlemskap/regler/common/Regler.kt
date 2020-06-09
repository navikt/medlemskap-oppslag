package no.nav.medlemskap.regler.common

abstract class Regler {

    abstract fun hentHovedRegel(): Regel

    protected fun finnDelResultat(vararg regler: Regel): Resultat {
        val delresultat = regler.map { it.utfør() }

        return utledResultat(delresultat).copy(
                delresultat = delresultat
        )
    }

    protected fun sjekkRegel(metode: () -> Regel) = metode.invoke()

    private fun utledResultat(resultater: List<Resultat>): Resultat {
        return when {

            //gresultater.any{medlNEI(it) && gsakNEI(it) && joarkNEI(it)} -> nei("Alle de følgende ble nei")
            resultater.any{medlNEI(it) && gsakNEI(it) && joarkJA(it)} -> uavklart("Medl og gsak nei, Joark ja")
            resultater.any{medlNEI(it) && gsakJA(it) && joarkNEI(it)} -> uavklart("Medl og Joark nei, gsak ja")
            resultater.any{medlNEI(it) && gsakJA(it) && joarkNEI(it)} -> uavklart("Gsak og joark er ja, medl nei")
            resultater.any{medlJA(it) && gsakNEI(it) && joarkNEI(it)} -> ja("Medl ja, og joark og gsak nei")
            resultater.any{medlJA(it) && gsakNEI(it) && joarkJA(it)} -> ja("medl og joark gir ja, gsak nei")
            resultater.any{medlJA(it) && gsakJA(it) && joarkNEI(it)} -> uavklart("Medl og gsak gir ja, joark gir nei")
            resultater.any{medlJA(it) && gsakJA(it) && joarkJA(it)} -> uavklart("Alle gir ja")
            else -> nei("Alle de følgende ble NEI")
        }
    }

    private fun medlJA(it: Resultat) = it.identifikator.equals("OPPLYSNINGER-MEDL") && it.svar == Svar.JA
    private fun gsakJA(it: Resultat) = it.identifikator.equals("OPPLYSNINGER-GOSYS") && it.svar == Svar.JA
    private fun joarkJA(it: Resultat) = it.identifikator.equals("OPPLYSNINGER-JOARK") && it.svar == Svar.JA

    private fun medlNEI(it: Resultat) = it.identifikator.equals("OPPLYSNINGER-MEDL") && it.svar == Svar.NEI
    private fun gsakNEI(it: Resultat) = it.identifikator.equals("OPPLYSNINGER-GOSYS") && it.svar == Svar.NEI
    private fun joarkNEI(it: Resultat) = it.identifikator.equals("OPPLYSNINGER-JOARK") && it.svar == Svar.NEI
}
