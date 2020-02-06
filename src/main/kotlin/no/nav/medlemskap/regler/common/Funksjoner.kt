package no.nav.medlemskap.regler.common

object Funksjoner {

    fun antall(liste: List<Any>): Number = liste.size

    infix fun List<Any>.inneholder(objekt: Any?) = this.contains(objekt)

}
