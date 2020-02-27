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

    fun personensSisteStatsborgerskap(): String {

        //Trenger litt hjelp til å bekrefte at disse sjekker riktig
        val statsborgerskapIPeriode = datagrunnlag.personhistorikk.statsborgerskap.filter {
            it.tom!! >= datagrunnlag.periode.fom &&
            datagrunnlag.periode.tom >= it.fom
        }
        for(stasborgerskap in statsborgerskapIPeriode){
            if(!stasborgerskap.landkode.equals("NOR")){
                return stasborgerskap.landkode
            }
        }
        return "NOR"
    }


    fun arbeidsforhold(): List<Arbeidsforhold> = datagrunnlag.arbeidsforhold

    fun sisteArbeidsgiversLand(): String? {

        //Trenger hjelp på å bekrefte at disse sjekker riktig
        val arbeidsforholdIPeriode = datagrunnlag.arbeidsforhold.filter {
            it.periode.tom!! >= datagrunnlag.periode.fom &&
            datagrunnlag.periode.tom >= it.periode.fom}


        for(arbeidsforhold in arbeidsforholdIPeriode){
            if(!arbeidsforhold.arbeidsgiver.landkode.equals("NOR")){
                return arbeidsforhold.arbeidsgiver.landkode
            }
        }

        return "NOR"

    }


    //Hvis det finnes to arbeidsforholdstyper innenfor en periode hvordan skal vi håndtere dette?
    fun sisteArbeidsforholdtype(): Arbeidsforholdstype = datagrunnlag.arbeidsforhold[0].arbeidsfolholdstype



    fun sisteArbeidsforholdYrkeskode(): String = datagrunnlag.arbeidsforhold[0].arbeidsavtaler[0].yrkeskode

    fun sisteArbeidsforholdSkipsregister(): Skipsregister? = datagrunnlag.arbeidsforhold[0].arbeidsavtaler[0].skipsregister

    fun hentBrukerinputArbeidUtenforNorge(): Boolean = datagrunnlag.brukerinput.arbeidUtenforNorge

    infix fun oppfyller(avklaring: Avklaring): Resultat {
        val resultat = avklaring.operasjon.invoke(this).apply {
            regelCounter.labels(avklaring.avklaring.replace("?", ""), this.resultat.name).inc()
        }
        return resultat.copy(identifikator = avklaring.identifikator, avklaring = avklaring.avklaring)
    }

    infix fun oppfyller(regelsett: Regelsett): Resultat = regelsett.evaluer(this)

}
