package no.nav.medlemskap.regler.v1.arbeidsforhold

import mu.KotlinLogging
import no.nav.medlemskap.domene.Datagrunnlag
import no.nav.medlemskap.domene.InputPeriode
import no.nav.medlemskap.domene.Ytelse
import no.nav.medlemskap.domene.arbeidsforhold.Arbeidsforhold
import no.nav.medlemskap.domene.arbeidsforhold.Arbeidsforhold.Companion.erArbeidsforholdetOffentligSektor
import no.nav.medlemskap.regler.common.RegelId
import no.nav.medlemskap.regler.common.Resultat
import no.nav.medlemskap.regler.common.Resultat.Companion.ja
import no.nav.medlemskap.regler.common.Resultat.Companion.nei
import no.nav.medlemskap.regler.v1.lovvalg.LovvalgRegel
import java.time.LocalDate

private val logger = KotlinLogging.logger { }

class ErArbeidsforholdetOffentligSektor(
    ytelse: Ytelse,
    private val periode: InputPeriode,
    førsteDagForYtelse: LocalDate?,
    private val arbeidsforhold: List<Arbeidsforhold>,
    regelId: RegelId = RegelId.REGEL_14
) : LovvalgRegel(regelId, ytelse, periode, førsteDagForYtelse) {

    override fun operasjon(): Resultat {

        arbeidsforhold.forEach { arbeidsforhold ->
            arbeidsforhold.arbeidsgiver.juridiskeEnheter
                ?.filter { juridiskEnhetstype -> juridiskEnhetstype?.enhetstype.equals("SÆR") }
                ?.forEach { logger.info("orgnr med enhetstype SÆR: ${arbeidsforhold.arbeidsgiver.organisasjonsnummer}") }
        }

        return when {
            erArbeidsforholdetOffentligSektor(arbeidsforhold, kontrollPeriodeForArbeidsforhold, ytelse) -> ja(regelId)
            else -> nei(regelId)
        }
    }

    companion object {

        fun fraDatagrunnlag(datagrunnlag: Datagrunnlag): ErArbeidsforholdetOffentligSektor {
            return ErArbeidsforholdetOffentligSektor(
                ytelse = datagrunnlag.ytelse,
                periode = datagrunnlag.periode,
                førsteDagForYtelse = datagrunnlag.førsteDagForYtelse,
                arbeidsforhold = datagrunnlag.arbeidsforhold
            )
        }
    }
}
