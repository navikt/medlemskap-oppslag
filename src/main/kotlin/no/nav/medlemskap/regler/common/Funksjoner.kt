package no.nav.medlemskap.regler.common

import no.nav.medlemskap.domene.Periode
import org.threeten.extra.Interval

object Funksjoner {

    val List<Any>.antall: Number
        get() = size

    infix fun List<Any>.inneholder(objekt: Any?) = this.contains(objekt)

    infix fun Map<String, String>.inneholder(key: String?) = this.containsKey(key)

    infix fun Any?.erDelAv(liste: List<Any>) = liste.contains(this)

    infix fun List<String>.inneholder(string: String) = this.contains(string)

    infix fun List<String>.alleEr(string: String) = this.stream().allMatch { m -> m.equals(string) }

    infix fun List<String>.kunInneholder(string: String) = this.contains(string) && this.size == 1

    infix fun String?.er(string: String) = this != null && this == string

    infix fun List<String>.inneholderNoe(liste: List<String>) = this.any { it in liste }

    infix fun Map<String, String>.finnesI(liste: List<String>) = this.keys.intersect(liste).isNotEmpty()

    fun List<Any>?.erTom() = this == null || this.isNullOrEmpty()

    fun List<String?>?.finnes() = this != null && this.isNotEmpty()

    infix fun List<Int?>.finnesMindreEnn(tall: Int) = this.stream().anyMatch { p -> p == null || p < tall }

    fun periodefilter(periodeDatagrunnlag: Interval, periode: Periode): Boolean {
        return periodeDatagrunnlag.overlaps(lagInterval(periode)) || periodeDatagrunnlag.encloses(lagInterval(periode))
    }
}

fun ja(begrunnelse: String) = Resultat(
        begrunnelse = begrunnelse,
        svar = Svar.JA
)

fun ja() = Resultat(svar = Svar.JA)

fun nei(begrunnelse: String) = Resultat(
        begrunnelse = begrunnelse,
        svar = Svar.NEI
)

fun nei() = Resultat(svar = Svar.NEI)

fun uavklart(begrunnelse: String) = Resultat(
        begrunnelse = begrunnelse,
        svar = Svar.UAVKLART
)

fun uavklart() = Resultat(svar = Svar.UAVKLART)

fun sjekkRegelsett(metode: () -> Regler): Regel = metode.invoke().hentHovedRegel()

const val konklusjonIdentifikator = "LOVME"

val uavklartKonklusjon = Regel(
        identifikator = konklusjonIdentifikator,
        avklaring = "Er bruker medlem?",
        beskrivelse = "",
        operasjon = { uavklart("Kan ikke konkludere med medlemskap") }
)

val jaKonklusjon = Regel(
        identifikator = konklusjonIdentifikator,
        avklaring = "Er bruker medlem?",
        beskrivelse = "",
        operasjon = { ja("Bruker er medlem") }
)

val neiKonklusjon = Regel(
        identifikator = konklusjonIdentifikator,
        avklaring = "Er bruker medlem?",
        beskrivelse = "",
        operasjon = { nei("Bruker er ikke medlem") }
)

