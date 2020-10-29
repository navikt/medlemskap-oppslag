package no.nav.medlemskap.cucumber.steps.aareg

import no.nav.medlemskap.domene.*

class AaregBuilder {
    var periode = Periode(fom = null, tom = null)
    val utenlandsopphold = mutableListOf<Utenlandsopphold>()
    val arbeidsgiverType = OpplysningspliktigArbeidsgiverType.Organisasjon
    val arbeidsgiver = Arbeidsgiver(
        type = String(),
        organisasjonsnummer = String(),
        ansatte = mutableListOf<Ansatte>(),
        konkursStatus = mutableListOf<String>(),
        juridiskeEnheter = mutableListOf<JuridiskEnhet>()
    )
    val arbeidsforholdstype = Arbeidsforholdstype.NORMALT
    val arbeidavtaler = mutableListOf<Arbeidsavtale>()

    fun build(): Arbeidsforhold {
        return Arbeidsforhold(
            periode = periode,
            utenlandsopphold = utenlandsopphold,
            arbeidsavtaler = arbeidavtaler,
            arbeidsforholdstype = arbeidsforholdstype,
            arbeidsgiver = arbeidsgiver,
            arbeidsgivertype = arbeidsgiverType

        )
    }
}
