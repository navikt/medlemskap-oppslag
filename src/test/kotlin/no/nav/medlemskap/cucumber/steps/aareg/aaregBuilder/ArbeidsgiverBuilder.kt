package no.nav.medlemskap.cucumber.steps.aareg.aaregBuilder

import no.nav.medlemskap.clients.aareg.AaRegOpplysningspliktigArbeidsgiver
import no.nav.medlemskap.clients.aareg.AaRegOpplysningspliktigArbeidsgiverType

class ArbeidsgiverBuilder {
    var type = AaRegOpplysningspliktigArbeidsgiverType.Organisasjon
    var organisasjonsnummer = String()
    var aktoerId = String()
    var offentligIdent = String()

    fun build(): AaRegOpplysningspliktigArbeidsgiver {
        return AaRegOpplysningspliktigArbeidsgiver(
            type = type,
            organisasjonsnummer = organisasjonsnummer,
            aktoerId = aktoerId,
            offentligIdent = offentligIdent
        )
    }
}
