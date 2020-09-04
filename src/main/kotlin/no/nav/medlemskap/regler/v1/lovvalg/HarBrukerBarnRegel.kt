package no.nav.medlemskap.regler.v1.lovvalg

import no.nav.medlemskap.domene.*
import no.nav.medlemskap.regler.common.Funksjoner.erIkkeTom
import no.nav.medlemskap.regler.common.RegelId
import no.nav.medlemskap.regler.common.Resultat
import no.nav.medlemskap.regler.common.ja
import no.nav.medlemskap.regler.common.nei
import no.nav.medlemskap.regler.funksjoner.RelasjonFunksjoner.hentBarnSomFinnesITPS
import no.nav.medlemskap.regler.funksjoner.RelasjonFunksjoner.hentFnrTilBarnUnder25

class HarBrukerBarnRegel(
    ytelse: Ytelse,
    private val periode: InputPeriode,
    private val pdlPersonhistorikk: Personhistorikk?,
    private val personhistorikkRelatertPerson: List<PersonhistorikkRelatertPerson>,
    regelId: RegelId = RegelId.REGEL_11_2_1
) : LovvalgRegel(regelId, ytelse, periode) {

    override fun operasjon(): Resultat {
        val familierelasjon = pdlPersonhistorikk?.familierelasjoner
        val barn = familierelasjon?.hentFnrTilBarnUnder25()
        val barnITps = personhistorikkRelatertPerson.hentBarnSomFinnesITPS(barn)

        return when {
            barnITps.erIkkeTom() -> ja()
            else -> nei("Bruker har ikke barn i tps")
        }
    }

    companion object {

        fun fraDatagrunnlag(datagrunnlag: Datagrunnlag, regelId: RegelId): HarBrukerBarnRegel {
            return HarBrukerBarnRegel(
                ytelse = datagrunnlag.ytelse,
                periode = datagrunnlag.periode,
                pdlPersonhistorikk = datagrunnlag.pdlpersonhistorikk,
                personhistorikkRelatertPerson = datagrunnlag.personHistorikkRelatertePersoner,
                regelId = regelId
            )
        }
    }
}
