package no.nav.medlemskap.regler.common

import no.nav.medlemskap.domene.Statsborgerskap
import java.util.stream.Collectors

object Funksjoner {

    fun antall(liste: List<Any>): Number = liste.size

    infix fun List<Any>.inneholder(objekt: Any?) = this.contains(objekt)

    infix fun Map<String, String>.inneholder(key: String?) = this.containsKey(key)

    infix fun Any?.erDelAv(liste: List<Any>) = liste.contains(this)

    infix fun List<String>.inneholder(string: String) = this.contains(string)

    infix fun List<String>.kunInneholder(string: String) = this.contains(string) && this.size == 1

    infix fun Map<String, String>.harAlle(liste: List<Statsborgerskap>) = this.keys.containsAll(liste.stream().map {it.landkode}.collect(Collectors.toList()))

    infix fun List<String>.inneholderNoe(liste: List<String>) = this.any{it in liste}

    infix fun String?.erDelAv(map: Map<String, String>) = map.containsKey(this)

    infix fun List<Double>.erMerEnn(stillingsprosent: Double) = this.sum() > stillingsprosent

    fun List<Any>.erTom() = this.isNullOrEmpty()

    fun List<Any>.erIkkeTom() = !erTom()
}


