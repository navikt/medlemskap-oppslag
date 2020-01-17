package no.nav.medlemskap.modell.inntekt

data class HentInntektListeRequest(

        val ident: Ident,
        val maanedFom: String?,
        val maanedTom: String?,
        val ainntekstfilter: String,
        val formaal: String

)

