package no.nav.medlemskap.regler.v2.common

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
