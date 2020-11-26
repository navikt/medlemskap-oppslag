package no.nav.medlemskap.regler.v1.grunnforordningen

import no.nav.medlemskap.domene.Datagrunnlag
import no.nav.medlemskap.domene.InputPeriode
import no.nav.medlemskap.domene.Statsborgerskap
import no.nav.medlemskap.domene.Ytelse
import no.nav.medlemskap.regler.common.RegelId
import no.nav.medlemskap.regler.common.Resultat
import no.nav.medlemskap.regler.common.Resultat.Companion.ja
import no.nav.medlemskap.regler.common.Resultat.Companion.nei
import no.nav.medlemskap.regler.funksjoner.StatsborgerskapFunksjoner.registrerStatsborgerskapGrafana
import no.nav.medlemskap.regler.v1.lovvalg.LovvalgRegel
import java.time.LocalDate

class ErBrukerEøsBorgerRegel(
    ytelse: Ytelse,
    val periode: InputPeriode,
    val førsteDagForYtelse: LocalDate?,
    val statsborgerskap: List<Statsborgerskap>
) : LovvalgRegel(RegelId.REGEL_2, ytelse, periode, førsteDagForYtelse) {

    override fun operasjon(): Resultat {
        if (erBrukerEøsBorger(statsborgerskap)) {
            return ja(RegelId.REGEL_2.jaBegrunnelse)
        } else {
            statsborgerskap.registrerStatsborgerskapGrafana(kontrollPeriodeForPersonhistorikk, ytelse, RegelId.REGEL_2)
            return nei(RegelId.REGEL_2.neiBegrunnelse)
        }
    }

    companion object {
        fun fraDatagrunnlag(datagrunnlag: Datagrunnlag): ErBrukerEøsBorgerRegel {
            return ErBrukerEøsBorgerRegel(
                ytelse = datagrunnlag.ytelse,
                periode = datagrunnlag.periode,
                førsteDagForYtelse = datagrunnlag.førsteDagForYtelse,
                statsborgerskap = datagrunnlag.pdlpersonhistorikk.statsborgerskap
            )
        }
    }
}
