package no.nav.medlemskap.regler.v2

import no.nav.medlemskap.regler.common.Personfakta
import no.nav.medlemskap.regler.common.Resultattype
import no.nav.medlemskap.regler.v2.arbeidsforhold.RegelgruppeForArbeidsforhold
import no.nav.medlemskap.regler.v2.common.Spørsmål
import no.nav.medlemskap.regler.v2.common.Svar
import no.nav.medlemskap.regler.v2.grunnforordningen.RegelgruppeForGrunnforordningen
import no.nav.medlemskap.regler.v2.registrerte_opplysninger.RegelgruppeForRegistrerteOpplysninger

class Hovedregler(personfakta: Personfakta) {

    private val regelgruppeForRegistrerteOpplysninger = RegelgruppeForRegistrerteOpplysninger(personfakta)
    private val regelgruppeForGrunnforordningen = RegelgruppeForGrunnforordningen(personfakta)
    private var reglerForArbeidsforhold = RegelgruppeForArbeidsforhold(personfakta)

    fun evaluer() =
            Spørsmål(
                    identifikator = "LOVME",
                    spørsmål = "Er brukeren omfattet av norsk lovvalg og medlem av folketrygden?",
                    beskrivelse = "",
                    operasjon = { kjørRegler() }
            ).utfør()


    private fun kjørRegler(): Svar {
        val oppl = regelgruppeForRegistrerteOpplysninger.evaluer()

        if (oppl.svar.resultat == Resultattype.JA) {
            return Svar(
                    resultat = Resultattype.UAVKLART,
                    begrunnelse = "Bruker har registrerte opplysninger i NAV som må kontrolleres nærmere",
                    underspørsmål = listOf(oppl)
            )
        }

        val grunn = regelgruppeForGrunnforordningen.evaluer()

        if (grunn.svar.resultat == Resultattype.NEI) {
            return Svar(
                    resultat = Resultattype.UAVKLART,
                    begrunnelse = "Bruker er ikke omfattet av grunnforordningen",
                    underspørsmål = listOf(oppl, grunn)
            )
        }

        val arbeidsforhold = reglerForArbeidsforhold.evaluer()

        if (arbeidsforhold.svar.resultat == Resultattype.UAVKLART) {
            return Svar(
                    resultat = Resultattype.UAVKLART,
                    begrunnelse = "Arbeidsforholdet oppfyller ikke kravet",
                    underspørsmål = listOf(oppl, grunn, arbeidsforhold)
            )
        }

        return Svar(
                resultat = Resultattype.JA,
                begrunnelse = "Kravet er oppfylt, bruker er medlem",
                underspørsmål = listOf(oppl, grunn, arbeidsforhold)
        )
    }
}
