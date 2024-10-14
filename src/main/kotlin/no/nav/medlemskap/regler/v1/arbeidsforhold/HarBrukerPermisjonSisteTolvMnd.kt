package no.nav.medlemskap.regler.v1.arbeidsforhold

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

class HarBrukerPermisjonSiste12Måneder(
    ytelse: Ytelse,
    startDatoForYtelse: LocalDate,
    private val arbeidsforhold: List<Arbeidsforhold>,
    private val kontrollperiode: Periode,
    regelId: RegelId = RegelId.REGEL_32
) : ArbeidsforholdRegel(regelId, ytelse, startDatoForYtelse) {

    override fun operasjon(): Resultat {
        if (arbeidsforhold.size > 1) {
            //32-d
            // TODO verifiser med Helle at dette er korrekt
            if (arbeidsforhold.harPermisjoner()) {
                return nei(regelId)
            }
            return ja(regelId)
        }
        //val permisjoner = arbeidsforhold.flatMap { it.permisjonPermittering!! }
        if (!arbeidsforhold.harNoenArbeidsforhold100ProsentPermisjon()) {

            return nei(regelId)
        }
        //32-c
        if (arbeidsforhold.finnOverlappendePermisjoner(kontrollperiode).isEmpty()) {
            return nei(regelId)
        }

        return ja(regelId)
    }


    companion object {

        fun fraDatagrunnlag(datagrunnlag: Datagrunnlag): HarBrukerPermisjonSiste12Måneder {
            return HarBrukerPermisjonSiste12Måneder(
                ytelse = datagrunnlag.ytelse,
                startDatoForYtelse = datagrunnlag.startDatoForYtelse,
                arbeidsforhold = datagrunnlag.arbeidsforhold,
                kontrollperiode = Periode(datagrunnlag.periode.fom, datagrunnlag.periode.tom),
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

fun finnOverlappendePerioder(
    permisjoner: MutableList<PermisjonPermittering>,
    kontrollperiode: Periode
): List<PermisjonPermittering> {
    return permisjoner.filter { it.periode.overlapper(kontrollperiode) }
}