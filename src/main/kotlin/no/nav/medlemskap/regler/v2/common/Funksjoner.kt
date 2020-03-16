package no.nav.medlemskap.regler.v2.common

fun ja(begrunnelse: String) = Resultat(
        begrunnelse = begrunnelse,
        svar = Svar.JA
)

fun nei(begrunnelse: String) = Resultat(
        begrunnelse = begrunnelse,
        svar = Svar.NEI
)

fun uavklart(begrunnelse: String) = Resultat(
        begrunnelse = begrunnelse,
        svar = Svar.UAVKLART
)

fun sjekkRegelsett(metode: () -> Regler): Regel = metode.invoke().hentHovedRegel()

val uavklartKonklusjon = Regel(
        identifikator = "LOVME",
        avklaring = "Er bruker medlem?",
        beskrivelse = "",
        operasjon = { uavklart("Kan ikke konkludere") }
)

val jaKonklusjon = Regel(
        identifikator = "LOVME",
        avklaring = "Er bruker medlem?",
        beskrivelse = "",
        operasjon = { ja("Bruker er medlem") }
)

val neiKonklusjon = Regel(
        identifikator = "LOVME",
        avklaring = "Er bruker medlem?",
        beskrivelse = "",
        operasjon = { nei("Bruker er ikke medlem") }
)
