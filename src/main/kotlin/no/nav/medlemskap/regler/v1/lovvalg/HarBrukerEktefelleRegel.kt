package no.nav.medlemskap.regler.v1.lovvalg

import no.nav.medlemskap.domene.Datagrunnlag
import no.nav.medlemskap.domene.InputPeriode
import no.nav.medlemskap.domene.PersonhistorikkRelatertPerson
import no.nav.medlemskap.domene.Ytelse
import no.nav.medlemskap.domene.ektefelle.DataOmEktefelle
import no.nav.medlemskap.regler.common.Funksjoner.erIkkeTom
import no.nav.medlemskap.regler.common.RegelId
import no.nav.medlemskap.regler.common.Resultat
import no.nav.medlemskap.regler.common.ja
import no.nav.medlemskap.regler.common.nei
import no.nav.medlemskap.regler.funksjoner.RelasjonFunksjoner.hentRelatertSomFinnesITPS

class HarBrukerEktefelleRegel(
        ytelse: Ytelse,
        private val periode: InputPeriode,
        private val dataOmEktefelle: DataOmEktefelle?,
        private val personhistorikkRelatertPerson: List<PersonhistorikkRelatertPerson>,
        regelId: RegelId = RegelId.REGEL_11_2
) : LovvalgRegel(regelId, ytelse, periode) {

    override fun operasjon(): Resultat {
        val ektefelle = dataOmEktefelle?.personhistorikkEktefelle?.ident
        val ektefelleITps = personhistorikkRelatertPerson.hentRelatertSomFinnesITPS(ektefelle)

        return when {
            ektefelleITps.erIkkeTom() -> ja()
            else -> nei("Bruker har ikke ektefelle i tps")
        }
    }

    companion object {

        fun fraDatagrunnlag(datagrunnlag: Datagrunnlag): HarBrukerEktefelleRegel {
            return HarBrukerEktefelleRegel(
                    ytelse = datagrunnlag.ytelse,
                    periode = datagrunnlag.periode,
                    dataOmEktefelle = datagrunnlag.dataOmEktefelle,
                    personhistorikkRelatertPerson = datagrunnlag.personHistorikkRelatertePersoner
            )
        }
    }
}