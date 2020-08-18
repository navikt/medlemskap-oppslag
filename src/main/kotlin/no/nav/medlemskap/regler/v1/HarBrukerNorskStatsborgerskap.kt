package no.nav.medlemskap.regler.v1

import no.nav.medlemskap.domene.InputPeriode
import no.nav.medlemskap.domene.Statsborgerskap
import no.nav.medlemskap.domene.Ytelse
import no.nav.medlemskap.regler.common.*
import no.nav.medlemskap.regler.common.Funksjoner.inneholder
import no.nav.medlemskap.regler.funksjoner.StatsborgerskapFunksjoner.hentStatsborgerskapVedSluttAvKontrollperiode
import no.nav.medlemskap.regler.funksjoner.StatsborgerskapFunksjoner.hentStatsborgerskapVedStartAvKontrollperiode

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
        val førsteStatsborgerskap = statsborgerskap.hentStatsborgerskapVedStartAvKontrollperiode(kontrollPeriodeForPersonhistorikk)
        val sisteStatsborgerskap = statsborgerskap.hentStatsborgerskapVedSluttAvKontrollperiode(kontrollPeriodeForPersonhistorikk)

        return when {
            førsteStatsborgerskap inneholder ReglerForLovvalg.NorskLandkode.NOR.name
                    && sisteStatsborgerskap inneholder ReglerForLovvalg.NorskLandkode.NOR.name -> ja()
            else -> nei("Brukeren er ikke norsk statsborger")
        }
    }
}