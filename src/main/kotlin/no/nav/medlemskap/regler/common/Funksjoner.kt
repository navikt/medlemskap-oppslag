package no.nav.medlemskap.regler.common

import no.nav.medlemskap.domene.Periode
import no.nav.medlemskap.domene.Ytelse
import no.nav.medlemskap.regler.common.RegelId.REGEL_MEDLEM_KONKLUSJON
import org.threeten.extra.Interval

object Funksjoner {

    val List<Any>.antall: Number
        get() = size

    infix fun List<Any>.inneholder(objekt: Any?): Boolean = this.contains(objekt)

    infix fun Map<String, String>.inneholder(key: String?) = this.containsKey(key)

    infix fun Any?.erDelAv(liste: List<Any>) = liste.contains(this)

    infix fun List<String>.inneholder(string: String) = this.contains(string)

    infix fun List<String>.alleEr(string: String) = this.stream().allMatch { m -> m.equals(string) }

    infix fun List<String>.harAlle(strings: List<String>) = this.stream().allMatch { strings.contains(it) }

    infix fun List<String>.kunInneholder(string: String) = this.contains(string) && this.size == 1

    infix fun String?.er(string: String) = this != null && this == string

    infix fun List<String>.inneholderNoe(liste: List<String>) = this.any { it in liste }

    infix fun Map<String, String>.finnesI(liste: List<String>) = this.keys.intersect(liste).isNotEmpty()

    fun List<Any>?.erTom() = this == null || this.isNullOrEmpty()

    fun List<Any>?.erIkkeTom() = !erTom()

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

fun ja(begrunnelse: String, dekning: String) = Resultat(
    begrunnelse = begrunnelse,
    svar = Svar.JA,
    harDekning = Svar.JA,
    dekning = dekning
)

fun nei(begrunnelse: String, dekning: String) = Resultat(
    begrunnelse = begrunnelse,
    svar = Svar.NEI,
    harDekning = Svar.NEI,
    dekning = dekning
)

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

fun uavklartKonklusjon(ytelse: Ytelse) = Regel(
    regelId = REGEL_MEDLEM_KONKLUSJON,
    ytelse = ytelse,
    operasjon = { uavklart("Kan ikke konkludere med medlemskap") }
)

fun jaKonklusjon(ytelse: Ytelse) = Regel(
    regelId = REGEL_MEDLEM_KONKLUSJON,
    ytelse = ytelse,
    operasjon = { ja("Bruker er medlem") }
)

fun neiKonklusjon(ytelse: Ytelse) = Regel(
    regelId = REGEL_MEDLEM_KONKLUSJON,
    ytelse = ytelse,
    operasjon = { nei("Bruker er ikke medlem") }
)

fun regelflytUavklartKonklusjon(ytelse: Ytelse, regelId: RegelId) = Regel(
    regelId = regelId,
    ytelse = ytelse,
    operasjon = { uavklart("Regelflyt konkluderer med UAVKLART") }
)

fun regelflytJaKonklusjon(ytelse: Ytelse, regelId: RegelId) = Regel(
    regelId = regelId,
    ytelse = ytelse,
    operasjon = { ja("Regelflyt konkluderer med JA") }
)

fun regelflytNeiKonklusjon(ytelse: Ytelse, regelId: RegelId) = Regel(
    regelId = regelId,
    ytelse = ytelse,
    operasjon = { nei("Regelflyt konkluderer med NEI") }
)

fun konklusjonJa(ytelse: Ytelse): Regelflyt {
    return Regelflyt(jaKonklusjon(ytelse), ytelse)
}

fun konklusjonNei(ytelse: Ytelse): Regelflyt {
    return Regelflyt(neiKonklusjon(ytelse), ytelse)
}

fun konklusjonUavklart(ytelse: Ytelse): Regelflyt {
    return Regelflyt(uavklartKonklusjon(ytelse), ytelse)
}

fun regelflytJa(ytelse: Ytelse, regelId: RegelId = RegelId.REGEL_FLYT_KONKLUSJON): Regelflyt {
    return Regelflyt(regelflytJaKonklusjon(ytelse, regelId), ytelse)
}

fun regelflytNei(ytelse: Ytelse, regelId: RegelId = RegelId.REGEL_FLYT_KONKLUSJON): Regelflyt {
    return Regelflyt(regelflytNeiKonklusjon(ytelse, regelId), ytelse)
}

fun regelflytUavklart(ytelse: Ytelse, regelId: RegelId = RegelId.REGEL_FLYT_KONKLUSJON): Regelflyt {
    return Regelflyt(regelflytUavklartKonklusjon(ytelse, regelId), ytelse)
}

fun List<Resultat>.utenKonklusjon(): List<Resultat> {
    return this.filterNot { it.erKonklusjon() }
}
