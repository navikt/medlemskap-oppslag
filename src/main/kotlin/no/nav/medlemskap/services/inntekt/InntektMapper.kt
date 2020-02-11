package no.nav.medlemskap.services.inntekt

import no.nav.medlemskap.domene.Inntekt
import no.nav.medlemskap.domene.InntektArbeidsforhold

//  Todo Bruker foreløpig ingen data fra inntekstkomponenten
fun mapInntektResultat(@Suppress("UNUSED_PARAMETER") inntekt: InntektskomponentResponse): List<Inntekt> {
    return listOf(Inntekt(arbeidsforhold = listOf(InntektArbeidsforhold("yrke"))))
}
