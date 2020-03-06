package no.nav.medlemskap.regler.v2.arbeidsforhold

import no.nav.medlemskap.domene.Arbeidsforholdstype
import no.nav.medlemskap.domene.Skipsregister
import no.nav.medlemskap.regler.common.Funksjoner.erDelAv
import no.nav.medlemskap.regler.common.Funksjoner.erIkkeTom
import no.nav.medlemskap.regler.common.Personfakta
import no.nav.medlemskap.regler.common.Resultattype.*
import no.nav.medlemskap.regler.v2.*

class RegelgruppeForArbeidsforhold(val personfakta: Personfakta) : Regelgruppe() {

    override fun evaluer() =
            Spørsmål(
                    identifikator = "1.5",
                    spørsmål = "Oppfyller arbeidsforholdet kravet?",
                    beskrivelse = "",
                    operasjon = { kjørRegler() }
            ).utfør()


    private fun kjørRegler(): Svar {
        val arbeidsforhold = harArbeidsforhold.utfør()

        if (arbeidsforhold.svar.resultat == NEI) {
            return Svar(
                    resultat = UAVKLART,
                    begrunnelse = "Brukeren mangler registrerte arbeidsforhold",
                    underspørsmål = listOf(arbeidsforhold)
            )
        }

        val norskArbeidsgiver = erArbeidsgiverNorsk.utfør()

        if (norskArbeidsgiver.svar.resultat == NEI) {
            return Svar(
                    resultat = UAVKLART,
                    begrunnelse = "Brukeren har ikke norsk arbeidsgiver",
                    underspørsmål = listOf(arbeidsforhold, norskArbeidsgiver)
            )
        }

        val maritimt = erArbeidsforholdetMaritimt.utfør()

        if (maritimt.svar.resultat == NEI) {
            val kabinansatt = erPersonenPilotEllerKabinansatt.utfør()

            if (kabinansatt.svar.resultat == JA) {
                return Svar(
                        resultat = UAVKLART,
                        begrunnelse = "Brukeren er pilot eller kabinansatt og må utredes nærmere",
                        underspørsmål = listOf(arbeidsforhold, norskArbeidsgiver, maritimt, kabinansatt)
                )
            }

            return Svar(
                    resultat = JA,
                    begrunnelse = "Krav oppfylt",
                    underspørsmål = listOf(arbeidsforhold, norskArbeidsgiver, maritimt, kabinansatt)
            )
        } else {
            val norskSkip = jobberPersonenPåEtNorskregistrertSkip.utfør()

            if (norskSkip.svar.resultat == NEI) {
                return Svar(
                        resultat = UAVKLART,
                        begrunnelse = "Brukeren jobber ikke på et norskregistrert skip",
                        underspørsmål = listOf(arbeidsforhold, norskArbeidsgiver, maritimt, norskSkip)
                )
            }
            return Svar(
                    resultat = JA,
                    begrunnelse = "Krav oppfylt",
                    underspørsmål = listOf(arbeidsforhold, norskArbeidsgiver, maritimt, norskSkip)
            )
        }
    }

    private val yrkeskoderLuftfart = listOf("3143107", "5111105", "5111117")
    private val norskeSkipsregister = listOf(Skipsregister.nor)

    private val harArbeidsforhold = Spørsmål(
            identifikator = "1.5.1",
            spørsmål = "Har personen et registrert arbeidsforhold?",
            beskrivelse = "",
            operasjon = { sjekkArbeidsforhold() }
    )

    private val erArbeidsgiverNorsk = Spørsmål(
            identifikator = "1.5.2",
            spørsmål = "Jobber personen for en norsk arbeidsgiver?",
            beskrivelse = "",
            operasjon = { sjekkArbeidsgiver() }
    )

    private val erArbeidsforholdetMaritimt = Spørsmål(
            identifikator = "1.5.3",
            spørsmål = "Er arbeidsforholdet maritimt?",
            beskrivelse = "Maritime arbeidshorhold har egne regler for medlemskap",
            operasjon = { sjekkMaritim() }
    )

    private val jobberPersonenPåEtNorskregistrertSkip = Spørsmål(
            identifikator = "1.5.3.1",
            spørsmål = "Jobber bruker på et norskregistrert skip?",
            beskrivelse = "Kun norskregistrerte skip har pliktig medlemskap",
            operasjon = { sjekkSkipsregister() }
    )

    private val erPersonenPilotEllerKabinansatt = Spørsmål(
            identifikator = "1.5.4",
            spørsmål = "Er brukeren pilot eller kabinansatt?",
            beskrivelse = "Spesielle regler gjelder for piloter og kabinansatte",
            operasjon = { sjekkYrkeskodeLuftfart() }
    )

    private fun sjekkArbeidsforhold(): Svar =
            when {
                personfakta.arbeidsforhold().erIkkeTom() -> ja("Personen har et registrert arbeidsforhold")
                else -> nei("Personen har ikke et registrert arbeidsforhold")
            }

    private fun sjekkArbeidsgiver(): Svar =
            when {
                personfakta.sisteArbeidsgiversLand() == "NOR" -> ja("Arbeidsgiver er norsk")
                else -> nei("Arbeidsgiver er ikke norsk. Land: ${personfakta.sisteArbeidsgiversLand()}")
            }

    private fun sjekkMaritim(): Svar =
            when (Arbeidsforholdstype.MARITIM) {
                personfakta.sisteArbeidsforholdtype() -> ja("Personen er ansatt i det maritime")
                else -> nei("Personen jobber ikke i det maritime")
            }

    private fun sjekkSkipsregister(): Svar =
            when {
                personfakta.sisteArbeidsforholdSkipsregister() erDelAv norskeSkipsregister -> ja("Personen jobber på et norskregistrert skip")
                else -> nei("Personen jobber ikke på et norskregistrert skip")
            }

    private fun sjekkYrkeskodeLuftfart(): Svar =
            when {
                personfakta.sisteArbeidsforholdYrkeskode() erDelAv yrkeskoderLuftfart -> ja("Personen er pilot eller kabinansatt")
                else -> nei("Personen er ikke pilot eller kabinansatt")
            }

}
