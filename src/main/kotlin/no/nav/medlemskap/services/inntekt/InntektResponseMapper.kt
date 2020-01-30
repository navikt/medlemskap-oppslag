package no.nav.medlemskap.services.inntekt

import no.nav.medlemskap.domene.Inntekt
import no.nav.medlemskap.domene.InntektArbeidsforhold
import no.nav.medlemskap.modell.inntekt.ArbeidsinntektMaaned
import no.nav.medlemskap.modell.inntekt.InntektskomponentResponse

//  Todo Bruker foreløpig ingen data fra inntekstkomponenten
fun mapInntektResultat(@Suppress("UNUSED_PARAMETER") inntekt: InntektskomponentResponse): List<Inntekt> {
    return listOf(Inntekt(arbeidsforhold = listOf(InntektArbeidsforhold("yrke"))))
}
