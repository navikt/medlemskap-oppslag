package no.nav.medlemskap.regler.v1.validering

import no.nav.medlemskap.domene.Datagrunnlag
import no.nav.medlemskap.domene.InputPeriode
import no.nav.medlemskap.domene.Statsborgerskap
import no.nav.medlemskap.domene.Ytelse
import no.nav.medlemskap.regler.common.RegelId
import no.nav.medlemskap.regler.common.Resultat
import no.nav.medlemskap.regler.common.ja
import no.nav.medlemskap.regler.common.nei
import no.nav.medlemskap.regler.v1.lovvalg.LovvalgRegel
import java.time.LocalDate

class InputDatoValideringRegel(
    ytelse: Ytelse,
    val periode: InputPeriode,
    val førsteDagForYtelse: LocalDate?,
    val statsborgerskap: List<Statsborgerskap>
) : LovvalgRegel(RegelId.REGEL_0_1, ytelse, periode, førsteDagForYtelse) {

    override fun operasjon(): Resultat {
        val førsteGyldigeDato = bestemFørsteGyldigeDato()

        if (periode.fom.isBefore(førsteGyldigeDato)) {
            return nei("Periode fom kan ikke være før $førsteGyldigeDato")
        }

        if (førsteDagForYtelse?.isBefore(førsteGyldigeDato) ?: false) {
            return nei("Første dag for ytelse kan ikke være før $førsteGyldigeDato")
        }

        val sisteGyldigeDato = bestemSisteGyldigeDato()
        if (periode.fom.isAfter(sisteGyldigeDato)) {
            return nei("Periode fom kan ikke være etter $sisteGyldigeDato")
        }

        if (førsteDagForYtelse?.isAfter(sisteGyldigeDato) ?: false) {
            return nei("Første dag for ytelse kan ikke være etter $sisteGyldigeDato")
        }

        return ja()
    }

    private fun bestemFørsteGyldigeDato(): LocalDate {
        val erBrukerSveitsiskStatsborgerUtenAnnetEøsStatsborgerskap = erBrukerSveitsiskBorgerUtenAnnetEøsStatsborgerskap(statsborgerskap)

        if (erBrukerSveitsiskStatsborgerUtenAnnetEøsStatsborgerskap) {
            return LocalDate.of(2017, 1, 1)
        } else {
            return LocalDate.of(2016, 1, 1)
        }
    }

    private fun bestemSisteGyldigeDato(): LocalDate {
        if (erBrukerBritiskBorgerUtenAnnetEøsStatsborgerskap(statsborgerskap)) {
            return LocalDate.of(2020, 12, 31)
        } else {
            return LocalDate.MAX
        }
    }

    companion object {
        fun fraDatagrunnlag(datagrunnlag: Datagrunnlag): InputDatoValideringRegel {
            return InputDatoValideringRegel(
                ytelse = datagrunnlag.ytelse,
                periode = datagrunnlag.periode,
                førsteDagForYtelse = datagrunnlag.førsteDagForYtelse,
                statsborgerskap = datagrunnlag.pdlpersonhistorikk.statsborgerskap
            )
        }
    }
}
