package no.nav.medlemskap.modell.inntekt

data class HentInntektListeRequest(

        val ident: Ident,
        val maanedFom: String?,
        val maanedTil: String?

)
