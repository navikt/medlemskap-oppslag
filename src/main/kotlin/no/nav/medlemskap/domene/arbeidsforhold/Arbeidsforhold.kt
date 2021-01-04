package no.nav.medlemskap.domene.arbeidsforhold

import no.nav.medlemskap.domene.*

data class Arbeidsforhold(
    val periode: Periode,
    val utenlandsopphold: List<Utenlandsopphold>?,
    val arbeidsgivertype: OpplysningspliktigArbeidsgiverType,
    val arbeidsgiver: Arbeidsgiver,
    val arbeidsforholdstype: Arbeidsforholdstype,
    var arbeidsavtaler: List<Arbeidsavtale>,
    val permisjonPermittering: List<PermisjonPermittering>?
) : Comparable<Arbeidsforhold> {

    /**
     * Comparator som sorterer arbeidsforhold etter periode.
     * Null-verdier regnes som høyere, slik at aktive arbeidsforhold vil havne sist i listen.
     */
    override fun compareTo(other: Arbeidsforhold): Int {

        if (this.periode.fom != null && other.periode.fom != null && this.periode.fom != other.periode.fom) {
            return this.periode.fom.compareTo(other.periode.fom)
        }

        if (this.periode.tom == null && other.periode.tom == null) {
            return this.periode.fom?.compareTo(other.periode.fom)!! // En gyldig periode har alltid minst én dato
        }

        if (this.periode.tom == null) return 1
        if (other.periode.tom == null) return -1

        return this.periode.tom.compareTo(other.periode.tom)
    }
}
