package no.nav.medlemskap.cucumber.steps.udi

import no.udi.mt_1067_nav_data.v1.*

class UdiArbeidsadgangBuilder {
    var harArbeidsadgang = JaNeiUavklart.UAVKLART
    var typeArbeidsadgang = ArbeidsadgangType.BESTEMT_ARBEIDSGIVER_ELLER_OPPDRAGSGIVER
    var arbeidsOmfang = ArbeidOmfangKategori.DELTID_SAMT_FERIER_HELTID
    var arbeidsadgangsPeriode = Periode()

    fun build(): Arbeidsadgang {
        val arbeidsadgang = Arbeidsadgang()
        arbeidsadgang.arbeidsOmfang = arbeidsOmfang
        arbeidsadgang.arbeidsadgangsPeriode = arbeidsadgangsPeriode
        arbeidsadgang.harArbeidsadgang = harArbeidsadgang
        arbeidsadgang.typeArbeidsadgang = typeArbeidsadgang
        return arbeidsadgang
    }
}
