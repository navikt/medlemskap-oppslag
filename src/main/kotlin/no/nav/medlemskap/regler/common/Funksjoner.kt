package no.nav.medlemskap.regler.common

object Funksjoner {

    fun antall(liste: List<Any>): Number = liste.size

    infix fun List<Any>.inneholder(objekt: Any?) = this.contains(objekt)

    infix fun Map<String, String>.inneholder(key: String?) = this.containsKey(key)

    infix fun Any?.erDelAv(liste: List<Any>) = liste.contains(this)

    infix fun String?.erDelAv(map: Map<String, String>) = map.containsKey(this)

}
