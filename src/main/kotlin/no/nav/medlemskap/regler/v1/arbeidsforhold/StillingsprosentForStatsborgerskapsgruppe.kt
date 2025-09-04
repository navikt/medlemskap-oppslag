package no.nav.medlemskap.regler.v1.arbeidsforhold

import no.nav.medlemskap.domene.personhistorikk.Personhistorikk
import no.nav.medlemskap.domene.personhistorikk.Statsborgerskap

fun hentStillingsprosentForStatsborgerskapsgruppe(personhistorikk: Personhistorikk?): Int {
    val statsborgerskap = personhistorikk?.statsborgerskap?.filter { it.historisk != true }
    return when {
        erNorskBorger(statsborgerskap) -> Statsborgerskapstype.NORSK_BORGER.stillingsprosent
        else -> Statsborgerskapstype.ANDRE_BORGERE.stillingsprosent
    }
}

fun erNorskBorger(statsborgerskap: List<Statsborgerskap>?): Boolean {
    return statsborgerskap?.any{ it.landkode == "NOR" } == true
}

enum class Statsborgerskapstype(val stillingsprosent: Int) {
    NORSK_BORGER(25),
    EOS_BORGER(100),
    ANDRE_BORGERE(60)
}
