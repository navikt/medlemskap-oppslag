package no.nav.medlemskap.domene.arbeidsforhold

import no.nav.medlemskap.common.antallAnsatteTilUavklart
import no.nav.medlemskap.common.antallTreffPåArbeidsgiver
import no.nav.medlemskap.domene.Ytelse

data class Arbeidsgiver(
    val organisasjonsnummer: String?,
    val ansatte: List<Ansatte>?,
    val konkursStatus: List<String?>?,
    val juridiskeEnheter: List<JuridiskEnhet?>?
) {
    companion object {
        fun List<Arbeidsgiver>.registrereArbeidsgivere(ytelse: Ytelse) {
            this.forEach { antallTreffPåArbeidsgiver(it.organisasjonsnummer, ytelse).increment() }
        }

        fun List<Arbeidsgiver>.registrerAntallAnsatte(ytelse: Ytelse) =
            this.forEach { arbeidsgiver ->
                arbeidsgiver.ansatte?.forEach { ansatte ->
                    antallAnsatteTilUavklart(ansatte.antall.toString(), ytelse).increment()
                }
            }
    }
}
