package no.nav.medlemskap.regler.v2

import no.nav.medlemskap.regler.common.Resultattype

fun ja(begrunnelse: String): Svar = Svar(
        resultat = Resultattype.JA,
        begrunnelse = begrunnelse
)

fun nei(begrunnelse: String): Svar = Svar(
        resultat = Resultattype.NEI,
        begrunnelse = begrunnelse
)

fun uavklart(begrunnelse: String): Svar = Svar(
        resultat = Resultattype.UAVKLART,
        begrunnelse = begrunnelse
)
