package no.nav.medlemskap.regler.v1.arbeidsforhold

import no.nav.medlemskap.domene.Kontrollperiode
import no.nav.medlemskap.domene.Kontrollperiode.Companion.kontrollPeriodeForArbeidsforhold
import no.nav.medlemskap.domene.Kontrollperiode.Companion.kontrollperiodeForBarneBriller
import no.nav.medlemskap.domene.Kontrollperiode.Companion.kontrollperiodeForSykepenger
import no.nav.medlemskap.domene.Ytelse
import no.nav.medlemskap.domene.personhistorikk.Personhistorikk
import no.nav.medlemskap.domene.personhistorikk.Statsborgerskap
import no.nav.medlemskap.regler.common.BasisRegel
import no.nav.medlemskap.regler.common.RegelId
import java.time.LocalDate

abstract class ArbeidsforholdRegel(
    regelId: RegelId,
    ytelse: Ytelse,
    startDatoForYtelse: LocalDate,
    personhistorikk: Personhistorikk? = null
) : BasisRegel(regelId, ytelse) {
    val kontrollPeriodeForArbeidsforhold = kontrollPeriodeForArbeidsforhold(startDatoForYtelse)
    val kontrollperiodeForSykepenger = kontrollperiodeForSykepenger(startDatoForYtelse)
    val kontrollperiodeForBarnbriller = kontrollperiodeForBarneBriller(startDatoForYtelse)
    val statsborgerskapsrelatertStillingsprosent = statsborgerskapsrelatertStillingsprosent(personhistorikk)

    fun finnKOntrollPeriode(ytelse: Ytelse): Kontrollperiode {
        when (ytelse){
            Ytelse.SYKEPENGER -> return kontrollperiodeForSykepenger
            Ytelse.BARNE_BRILLER -> return kontrollperiodeForBarnbriller
            Ytelse.DAGPENGER -> return kontrollperiodeForSykepenger
            Ytelse.ENSLIG_FORSORGER -> return kontrollperiodeForSykepenger
            Ytelse.LOVME -> return kontrollperiodeForSykepenger
            Ytelse.LOVME_GCP -> return kontrollperiodeForSykepenger
            Ytelse.MEDLEMSKAP_BARN -> return kontrollperiodeForBarnbriller
            Ytelse.MIN_VEI -> return kontrollperiodeForSykepenger
        }
    }

    private fun statsborgerskapsrelatertStillingsprosent(personhistorikk: Personhistorikk?): Int {
        val statsborgerskap = personhistorikk?.statsborgerskap?.filter { it.historisk != true }
        return when {
            erNorskBorger(statsborgerskap) -> Statsborgerskapstype.NORSK_BORGER.stillingsprosent
            else -> Statsborgerskapstype.ANDRE_BORGERE.stillingsprosent
        }
    }

    private fun erNorskBorger(statsborgerskap: List<Statsborgerskap>?): Boolean {
        return statsborgerskap?.any{ it.landkode == "NOR" } == true
    }

    enum class Statsborgerskapstype(val stillingsprosent: Int) {
        NORSK_BORGER(25),
        EOS_BORGER(100),
        ANDRE_BORGERE(60)
    }
}
