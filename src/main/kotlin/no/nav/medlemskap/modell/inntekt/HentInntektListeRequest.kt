package no.nav.medlemskap.modell.inntekt

data class HentInntektListeRequest(

        val ident: Ident,
        val ainntektsfilter: String,
        val maanedFom: String?,
        val maanedTom: String?,
        val formaal: String

)
