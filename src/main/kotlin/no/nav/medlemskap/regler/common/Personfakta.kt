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
        val periodeDatagrunnlag = Interval.of(datagrunnlag.periode.fom.atStartOfDay(ZoneId.systemDefault()).toInstant(), datagrunnlag.periode.tom.atStartOfDay(ZoneId.systemDefault()).toInstant())
        val statsborgerskapIPeriode = datagrunnlag.personhistorikk.statsborgerskap.filter {
            periodeDatagrunnlag.overlaps(lagInterval(Periode(it.fom, it.tom))) ||
            periodeDatagrunnlag.encloses(lagInterval(Periode(it.fom, it.tom)))
        }

        statsborgerskapIPeriode.forEach{
            if(!eøsLand.containsKey(it.landkode)){
                return it.landkode
            }
        }
        return statsborgerskapIPeriode.last().landkode
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

    private fun lagInterval(periode: Periode): Interval {
        val fom = periode.fom ?: LocalDate.MIN
        val tom = periode.tom ?: LocalDate.MAX
        return Interval.of(fom.atStartOfDay(ZoneId.systemDefault()).toInstant(), tom.atStartOfDay(ZoneId.systemDefault()).toInstant())
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
        val yrkeskoderLuftfart = listOf(LuftfartYrkeskoder.KABINPERSONALE.beskrivelse,
                                        LuftfartYrkeskoder.KABINSJEF.beskrivelse,
                                        LuftfartYrkeskoder.PILOT.beskrivelse)

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
        val periodeDatagrunnlag = Interval.of(datagrunnlag.periode.fom.atStartOfDay(ZoneId.systemDefault()).toInstant(), datagrunnlag.periode.tom.atStartOfDay(ZoneId.systemDefault()).toInstant())

        return datagrunnlag.arbeidsforhold.filter {
            periodeDatagrunnlag.overlaps(lagInterval(Periode(it.periode.fom, it.periode.tom))) ||
            periodeDatagrunnlag.encloses(lagInterval(Periode(it.periode.fom, it.periode.tom)))
        }
    }

    fun sisteArbeidsforholdSkipsregister(): Skipsregister? {

        val periodeDatagrunnlag = Interval.of(datagrunnlag.periode.fom.atStartOfDay(ZoneId.systemDefault()).toInstant(), datagrunnlag.periode.tom.atStartOfDay(ZoneId.systemDefault()).toInstant())
        val arbeidsforholdPeriode = datagrunnlag.arbeidsforhold.filter {
            lagInterval(Periode(it.periode.tom, it.periode.fom)).overlaps(periodeDatagrunnlag) &&
            lagInterval(Periode(it.periode.tom, it.periode.fom)).encloses(periodeDatagrunnlag)
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

    //flytte ut denne
    val eøsLand = mapOf(
            "BEL" to "BELGIA",
            "BGR" to "BULGARIA",
            "DNK" to "DANMARK",
            "EST" to "ESTLAND",
            "FIN" to "FINLAND",
            "FRA" to "FRANKRIKE",
            "GRC" to "HELLAS",
            "IRL" to "IRLAND",
            "ISL" to "ISLAND",
            "ITA" to "ITALIA",
            "HRV" to "KROATIA",
            "CYP" to "KYPROS",
            "LVA" to "LATVIA",
            "LIE" to "LIECHTENSTEIN",
            "LTU" to "LITAUEN",
            "LUX" to "LUXENBURG",
            "MLT" to "MALTA",
            "NLD" to "NEDERLAND",
            "NOR" to "NORGE",
            "POL" to "POLEN",
            "PRT" to "PORTUGAL",
            "ROU" to "ROMANIA",
            "SVK" to "SLOVAKIA",
            "SVN" to "SLOVENIA",
            "ESP" to "SPANIA",
            "SWE" to "SVERIGE",
            "CZE" to "TSJEKKIA",
            "DEU" to "TYSKAND",
            "HUN" to "UNGARN",
            "AUT" to "ØSTERRIKE"
    )

}
