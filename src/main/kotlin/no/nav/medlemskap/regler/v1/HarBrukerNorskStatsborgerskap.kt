package no.nav.medlemskap.regler.v1

import no.nav.medlemskap.domene.InputPeriode
import no.nav.medlemskap.domene.Statsborgerskap
import no.nav.medlemskap.domene.Ytelse
import no.nav.medlemskap.regler.common.*
import no.nav.medlemskap.regler.funksjoner.StatsborgerskapFunksjoner.sjekkStatsborgerskap

class HarBrukerNorskStatsborgerskap(
        regelId: RegelId,
        ytelse: Ytelse,
        private val periode: InputPeriode,
        private val statsborgerskap: List<Statsborgerskap>
) : BasisRegel(regelId, ytelse) {

    override fun operasjon(): Resultat {
        return sjekkOmBrukerErNorskStatsborger()
    }

    private fun sjekkOmBrukerErNorskStatsborger(): Resultat {
        val datohjelper = Datohjelper(periode, ytelse)
        val kontrollPeriodeForPersonhistorikk = datohjelper.kontrollPeriodeForPersonhistorikk()

        return when {
            sjekkStatsborgerskap(statsborgerskap, kontrollPeriodeForPersonhistorikk, {s -> Eøsland.erNorsk(s)}) -> ja()
            else -> nei("Brukeren er ikke norsk statsborger")
        }
    }
}