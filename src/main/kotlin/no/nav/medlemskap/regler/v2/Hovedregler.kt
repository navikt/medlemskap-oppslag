package no.nav.medlemskap.regler.v2

import no.nav.medlemskap.regler.common.Personfakta
import no.nav.medlemskap.regler.common.Resultattype
import no.nav.medlemskap.regler.v2.arbeidsforhold.RegelgruppeForArbeidsforhold
import no.nav.medlemskap.regler.v2.common.Element
import no.nav.medlemskap.regler.v2.common.Regelgruppe
import no.nav.medlemskap.regler.v2.common.Spørsmål
import no.nav.medlemskap.regler.v2.common.Spørsmålsrekke.Companion.spørsmålsrekke
import no.nav.medlemskap.regler.v2.common.Svar
import no.nav.medlemskap.regler.v2.grunnforordningen.RegelgruppeForGrunnforordningen
import no.nav.medlemskap.regler.v2.registrerte_opplysninger.RegelgruppeForRegistrerteOpplysninger

class Hovedregler(personfakta: Personfakta) : Regelgruppe() {

    private val regelgruppeForRegistrerteOpplysninger = RegelgruppeForRegistrerteOpplysninger(personfakta)
    private val regelgruppeForGrunnforordningen = RegelgruppeForGrunnforordningen(personfakta)
    private var reglerForArbeidsforhold = RegelgruppeForArbeidsforhold(personfakta)

    override fun evaluer() =
            Spørsmål(
                    identifikator = "LOVME",
                    spørsmål = "Er brukeren omfattet av norsk lovvalg og medlem av folketrygden?",
                    beskrivelse = "",
                    operasjon = { kjørRegler() }
            ).utfør()


    private fun kjørRegler() = spørsmålsrekke(
            Element(
                    spørsmål = { regelgruppeForRegistrerteOpplysninger.evaluer() },
                    resultatSomGirUavklart = Resultattype.JA,
                    begrunnelseVedUavklart = "Bruker har registrerte opplysninger i NAV som må kontrolleres nærmere"
            ),
            Element(
                    spørsmål = { regelgruppeForGrunnforordningen.evaluer() },
                    resultatSomGirUavklart = Resultattype.NEI,
                    begrunnelseVedUavklart = "Bruker er ikke omfattet av grunnforordningen"
            ),
            Element(
                    spørsmål = { reglerForArbeidsforhold.evaluer() },
                    resultatSomGirUavklart = Resultattype.UAVKLART
            )

    ).evaluerMedSvarHvisVellykket {
        Svar(
                resultat = Resultattype.JA,
                begrunnelse = "Kravet er oppfylt, bruker er medlem"
        )
    }

}
