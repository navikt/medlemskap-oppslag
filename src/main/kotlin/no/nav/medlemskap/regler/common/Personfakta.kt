package no.nav.medlemskap.regler.common

import no.nav.medlemskap.common.regelCounter
import no.nav.medlemskap.domene.*
import org.threeten.extra.Interval
import java.time.LocalDate
import java.time.ZoneId


class Personfakta(private val datagrunnlag: Datagrunnlag) {

    companion object {
        fun initialiserFakta(datagrunnlag: Datagrunnlag) = Personfakta(datagrunnlag)
    }

    fun personensPerioderIMedl(): List<Medlemskapsunntak> = datagrunnlag.medlemskapsunntak

    fun personensOppgaverIGsak(): List<Oppgave> = datagrunnlag.oppgaver

    fun personensDokumenterIJoark(): List<Journalpost> = datagrunnlag.dokument

    fun personensSisteStatsborgerskap(): String {
        val periodeDatagrunnlag = Interval.of(datagrunnlag.periode.tom.atStartOfDay(ZoneId.systemDefault()).toInstant(), datagrunnlag.periode.fom.atStartOfDay(ZoneId.systemDefault()).toInstant())
        val statsborgerskapIPeriode = datagrunnlag.personhistorikk.statsborgerskap.filter {
            hentOverlappedeIntervaller(it.tom, it.fom, periodeDatagrunnlag) &&
            hentInnlukkedeIntervaller(it.tom, it.fom, periodeDatagrunnlag)
        }


        println(statsborgerskapIPeriode.size)
        statsborgerskapIPeriode.forEach{
            if(it.landkode != "NOR"){
                return it.landkode
            }
        }
        return "NOR"
    }


    fun arbeidsforhold(): List<Arbeidsforhold> = datagrunnlag.arbeidsforhold

    fun sisteArbeidsgiversLand(): String? {

        val arbeidsforholdPeriode = hentArbeidsforholdIPeriode()

        arbeidsforholdPeriode.forEach{
            if(!it.arbeidsgiver.landkode.equals("NOR")){
                return it.arbeidsgiver.landkode
            }
        }

        return "NOR"

    }

    private fun hentInnlukkedeIntervaller(tom: LocalDate?, fom: LocalDate?, periodeDatagrunnlag: Interval?): Boolean {
        val maxTom = LocalDate.MAX
        val minFom = LocalDate.MIN

        return if(tom == null && fom != null){
            Interval.of(fom.atStartOfDay(ZoneId.systemDefault()).toInstant(), maxTom.atStartOfDay(ZoneId.systemDefault()).toInstant()).encloses(periodeDatagrunnlag)
        } else if(tom != null && fom == null){
            Interval.of(minFom.atStartOfDay(ZoneId.systemDefault()).toInstant(), tom.atStartOfDay(ZoneId.systemDefault()).toInstant()).encloses(periodeDatagrunnlag)
        } else if(tom == null && fom == null){
            Interval.of(minFom.atStartOfDay(ZoneId.systemDefault()).toInstant(), maxTom.atStartOfDay(ZoneId.systemDefault()).toInstant()).encloses(periodeDatagrunnlag)
        } else {
            Interval.of(fom?.atStartOfDay(ZoneId.systemDefault())?.toInstant(), tom?.atStartOfDay(ZoneId.systemDefault())?.toInstant()).encloses(periodeDatagrunnlag)
        }
    }

    private fun hentOverlappedeIntervaller(tom: LocalDate?, fom: LocalDate?, periodeDatagrunnlag: Interval?): Boolean {
    val maxTom = LocalDate.MAX
    val minFom = LocalDate.MIN

        return if(tom == null && fom != null){
            Interval.of(fom.atStartOfDay(ZoneId.systemDefault()).toInstant(), maxTom.atStartOfDay(ZoneId.systemDefault()).toInstant()).overlaps(periodeDatagrunnlag)
        } else if(tom != null && fom == null){
            Interval.of(minFom.atStartOfDay(ZoneId.systemDefault()).toInstant(), tom.atStartOfDay(ZoneId.systemDefault()).toInstant()).overlaps(periodeDatagrunnlag)
        } else if(tom == null && fom == null){
            Interval.of(minFom.atStartOfDay(ZoneId.systemDefault()).toInstant(), maxTom.atStartOfDay(ZoneId.systemDefault()).toInstant()).overlaps(periodeDatagrunnlag)
        } else {
            Interval.of(fom?.atStartOfDay(ZoneId.systemDefault())?.toInstant(), tom?.atStartOfDay(ZoneId.systemDefault())?.toInstant()).overlaps(periodeDatagrunnlag)
        }
    }


    fun sisteArbeidsforholdtype(): Arbeidsforholdstype {
        val arbeidsforholdPeriode = hentArbeidsforholdIPeriode()
        arbeidsforholdPeriode.forEach{
            if(!it.arbeidsgiver.landkode.equals("NORMALT")){
                return it.arbeidsfolholdstype
            }
        }

        return Arbeidsforholdstype.NORMALT
    }



    fun sisteArbeidsforholdYrkeskode(): String {
        val yrkeskoderLuftfart = listOf("3143107", "5111105", "5111117")


        val arbeidsforholdPeriode = hentArbeidsforholdIPeriode()

        arbeidsforholdPeriode.forEach{ it ->
            it.arbeidsavtaler.forEach{
                if(yrkeskoderLuftfart.contains(it.yrkeskode)){
                    return it.yrkeskode
                }
            }
        }
        return arbeidsforholdPeriode.last().arbeidsavtaler.last().yrkeskode

    }

    private fun hentArbeidsforholdIPeriode(): List<Arbeidsforhold> {
        val periodeDatagrunnlag = Interval.of(datagrunnlag.periode.tom.atStartOfDay(ZoneId.systemDefault()).toInstant(), datagrunnlag.periode.fom.atStartOfDay(ZoneId.systemDefault()).toInstant())

        val arbeidsforholdPeriode = datagrunnlag.arbeidsforhold.filter {
            hentOverlappedeIntervaller(it.periode.tom, it.periode.fom, periodeDatagrunnlag) &&
                    hentInnlukkedeIntervaller(it.periode.tom, it.periode.fom, periodeDatagrunnlag)
        }
        return arbeidsforholdPeriode
    }

    fun sisteArbeidsforholdSkipsregister(): Skipsregister? {

        val periodeDatagrunnlag = Interval.of(datagrunnlag.periode.tom.atStartOfDay(ZoneId.systemDefault()).toInstant(), datagrunnlag.periode.fom.atStartOfDay(ZoneId.systemDefault()).toInstant())
        val arbeidsforholdPeriode = datagrunnlag.arbeidsforhold.filter {
            hentOverlappedeIntervaller(it.periode.tom, it.periode.fom, periodeDatagrunnlag)
        }

        arbeidsforholdPeriode.forEach{ it ->
            it.arbeidsavtaler.forEach{
                if(!it.skipsregister?.name?.equals(Skipsregister.nor.toString())!!){
                    return it.skipsregister
                }
            }
        }
        return Skipsregister.nor
    }


    fun hentBrukerinputArbeidUtenforNorge(): Boolean = datagrunnlag.brukerinput.arbeidUtenforNorge

    infix fun oppfyller(avklaring: Avklaring): Resultat {
        val resultat = avklaring.operasjon.invoke(this).apply {
            regelCounter.labels(avklaring.avklaring.replace("?", ""), this.resultat.name).inc()
        }
        return resultat.copy(identifikator = avklaring.identifikator, avklaring = avklaring.avklaring)
    }

    infix fun oppfyller(regelsett: Regelsett): Resultat = regelsett.evaluer(this)

}
