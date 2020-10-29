package no.nav.medlemskap.regler.v1.arbeidsforhold

import mu.KotlinLogging
import no.nav.medlemskap.common.enhetstypeCounter
import no.nav.medlemskap.common.enhetstypeForJuridiskEnhet
import no.nav.medlemskap.domene.Arbeidsforhold
import no.nav.medlemskap.domene.Datagrunnlag
import no.nav.medlemskap.domene.InputPeriode
import no.nav.medlemskap.domene.Ytelse
import no.nav.medlemskap.domene.Ytelse.Companion.metricName
import no.nav.medlemskap.regler.common.RegelId
import no.nav.medlemskap.regler.common.Resultat
import no.nav.medlemskap.regler.common.ja
import no.nav.medlemskap.regler.common.nei
import no.nav.medlemskap.regler.funksjoner.ArbeidsforholdFunksjoner.erAlleArbeidsgivereOrganisasjon
import java.time.LocalDate

class ErArbeidsgiverOrganisasjonRegel(
    ytelse: Ytelse,
    private val periode: InputPeriode,
    private val førsteDagForYtelse: LocalDate?,
    private val arbeidsforhold: List<Arbeidsforhold>,
    regelId: RegelId = RegelId.REGEL_4
) : ArbeidsforholdRegel(regelId, ytelse, periode, førsteDagForYtelse) {

    private val logger = KotlinLogging.logger { }

    override fun operasjon(): Resultat {

        registrerArbeidsgiverMetrics()

        return when {
            !arbeidsforhold.erAlleArbeidsgivereOrganisasjon(kontrollPeriodeForArbeidsforhold) -> nei("Ikke alle arbeidsgivere er av typen organisasjon")
            else -> ja()
        }
    }

    private fun registrerArbeidsgiverMetrics() {
        arbeidsforhold.forEach { enhetstypeCounter(it.arbeidsgiver.type ?: "N/A", ytelse.metricName()).increment() }
        arbeidsforhold.forEach { it.arbeidsgiver.juridiskeEnheter?.forEach { p -> enhetstypeForJuridiskEnhet(p?.enhetstype, ytelse.metricName()).increment() } }
    }

    companion object {

        fun fraDatagrunnlag(datagrunnlag: Datagrunnlag): ErArbeidsgiverOrganisasjonRegel {
            return ErArbeidsgiverOrganisasjonRegel(
                ytelse = datagrunnlag.ytelse,
                periode = datagrunnlag.periode,
                førsteDagForYtelse = datagrunnlag.førsteDagForYtelse,
                arbeidsforhold = datagrunnlag.arbeidsforhold
            )
        }
    }
}
