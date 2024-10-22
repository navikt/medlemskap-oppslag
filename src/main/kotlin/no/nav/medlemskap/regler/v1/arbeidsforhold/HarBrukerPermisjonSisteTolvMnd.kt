package no.nav.medlemskap.regler.v1.arbeidsforhold

import mu.KotlinLogging
import net.logstash.logback.argument.StructuredArguments.kv
import no.nav.medlemskap.domene.Datagrunnlag
import no.nav.medlemskap.domene.Periode
import no.nav.medlemskap.domene.Ytelse
import no.nav.medlemskap.domene.arbeidsforhold.Arbeidsforhold
import no.nav.medlemskap.domene.arbeidsforhold.Arbeidsforhold.Companion.finnOverlappendePermisjoner
import no.nav.medlemskap.domene.arbeidsforhold.Arbeidsforhold.Companion.harNoenArbeidsforhold100ProsentPermisjon
import no.nav.medlemskap.domene.arbeidsforhold.Arbeidsforhold.Companion.harPermisjoner
import no.nav.medlemskap.domene.arbeidsforhold.PermisjonPermittering
import no.nav.medlemskap.regler.common.RegelId
import no.nav.medlemskap.regler.common.Resultat
import no.nav.medlemskap.regler.common.Resultat.Companion.ja
import no.nav.medlemskap.regler.common.Resultat.Companion.nei
import java.time.LocalDate

private val secureLogger = KotlinLogging.logger("tjenestekall")

class HarBrukerPermisjonSiste12Måneder(
    ytelse: Ytelse,
    startDatoForYtelse: LocalDate,
    private val arbeidsforhold: List<Arbeidsforhold>,
    private val kontrollperiode: Periode,
    private val fnr: String,
    regelId: RegelId = RegelId.REGEL_32,
) : ArbeidsforholdRegel(regelId, ytelse, startDatoForYtelse) {

    override fun operasjon(): Resultat {
        if (arbeidsforhold.size > 1) {
            //32-d
            if (arbeidsforhold.harPermisjoner()) {
                secureLogger.info(
                    "Regelbrudd 32-d. Bruker har flere arbeidsforhold og permisjon.",
                    kv("fnr", fnr)
                )
                return ja(regelId)
            }
            return nei(regelId)
        }

        if (!arbeidsforhold.harNoenArbeidsforhold100ProsentPermisjon()) {

            return nei(regelId)
        }
        //32-c
        if (arbeidsforhold.finnOverlappendePermisjoner(kontrollperiode).isEmpty()) {
            return nei(regelId)
        }
        secureLogger.info(
            "Regelbrudd 32-c. Bruker har 100% permisjon i arbeidsforhold.",
            kv("fnr", fnr)
        )
        return ja(regelId)
    }


    companion object {

        fun fraDatagrunnlag(datagrunnlag: Datagrunnlag): HarBrukerPermisjonSiste12Måneder {
            return HarBrukerPermisjonSiste12Måneder(
                ytelse = datagrunnlag.ytelse,
                startDatoForYtelse = datagrunnlag.startDatoForYtelse,
                arbeidsforhold = datagrunnlag.arbeidsforhold,
                kontrollperiode = Periode(datagrunnlag.periode.fom, datagrunnlag.periode.tom),
                fnr = datagrunnlag.fnr,
            )
        }
    }
}

fun Arbeidsforhold.harPermisjoner(): Boolean {
    if (permisjonPermittering == null) {
        return false
    }
    return permisjonPermittering.isNotEmpty()
}

