package no.nav.medlemskap.regler.common

import no.nav.medlemskap.common.regelCounter
import no.nav.medlemskap.domene.*

class Personfakta(private val datagrunnlag: Datagrunnlag) {

    companion object {
        fun initialiserFakta(datagrunnlag: Datagrunnlag) = Personfakta(datagrunnlag)
    }

    fun personensPerioderIMedl(): List<Medlemskapsunntak> = datagrunnlag.medlemskapsunntak

    fun personensOppgaverIGsak(): List<Oppgave> = datagrunnlag.oppgaver

    fun personensDokumenterIJoark(): List<Journalpost> = datagrunnlag.dokument

    fun personensSisteStatsborgerskap(): String = datagrunnlag.personhistorikk.statsborgerskap[0].landkode

    fun arbeidsforhold(): List<Arbeidsforhold> = datagrunnlag.arbeidsforhold

    fun sisteArbeidsgiversLand(): String? = datagrunnlag.arbeidsforhold[0].arbeidsgiver.landkode

    fun sisteArbeidsforholdtype(): Arbeidsforholdstype = datagrunnlag.arbeidsforhold[0].arbeidsfolholdstype

    fun sisteArbeidsforholdYrkeskode(): String = datagrunnlag.arbeidsforhold[0].arbeidsavtaler[0].yrkeskode

    fun sisteArbeidsforholdSkipsregister(): Skipsregister? = datagrunnlag.arbeidsforhold[0].arbeidsavtaler[0].skipsregister

    fun hentBrukerinputArbeidUtenforNorge(): Boolean = datagrunnlag.brukerinput.arbeidUtenforNorge

    infix fun oppfyller(avklaring: Avklaring): Resultat {
        val resultat = avklaring.operasjon.invoke(this).apply {
            regelCounter(avklaring.avklaring.replace("?", ""), this.resultat.name).increment()
        }
        return resultat.copy(identifikator = avklaring.identifikator, avklaring = avklaring.avklaring)
    }

    infix fun oppfyller(regelsett: Regelsett): Resultat = regelsett.evaluer(this)

}
