package no.nav.medlemskap.regler.common

import no.nav.medlemskap.domene.*

class Fakta(private val datagrunnlag: Datagrunnlag) {

    companion object {
        fun initialiserFakta(datagrunnlag: Datagrunnlag) = Fakta(datagrunnlag)
    }

    fun personensPerioderIMedl(): List<Medlemskapsunntak> = datagrunnlag.medlemskapsunntak

    fun personensOppgaverIGsak(): List<Oppgave> = datagrunnlag.oppgaver

    fun personensDokumenterIJoark(): List<Journalpost> = datagrunnlag.dokument

    fun personensSisteStatsborgerskap(): String = datagrunnlag.personhistorikk.statsborgerskap[0].landkode

    fun sisteArbeidsgiversLand(): String? = datagrunnlag.arbeidsforhold[0].arbeidsgiver.landkode

    fun sisteArbeidsforholdtype(): Arbeidsforholdstype = datagrunnlag.arbeidsforhold[0].arbeidsfolholdstype

    fun sisteArbeidsforholdYrkeskode(): String = datagrunnlag.arbeidsforhold[0].arbeidsavtaler[0].yrkeskode

    fun sisteArbeidsforholdSkipsregister(): Skipsregister? = datagrunnlag.arbeidsforhold[0].arbeidsavtaler[0].skipsregister

    fun hentBrukerinputArbeidUtenforNorge(): Boolean = datagrunnlag.brukerinput.arbeidUtenforNorge

}
