package no.nav.medlemskap.regler.v2

import no.nav.medlemskap.regler.common.Resultattype

data class Svar(
        val resultat: Resultattype,
        val begrunnelse: String,
        val underspørsmål: List<Spørsmål> = listOf()
)
