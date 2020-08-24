package no.nav.medlemskap.regler.v1.lovvalg

import no.nav.medlemskap.domene.*
import no.nav.medlemskap.regler.common.*
import no.nav.medlemskap.regler.funksjoner.AdresseFunksjoner.adresserForKontrollPeriode
import no.nav.medlemskap.regler.funksjoner.AdresseFunksjoner.landkodeTilAdresserForKontrollPeriode
import no.nav.medlemskap.regler.funksjoner.RelasjonFunksjoner.hentBarnSomFinnesITPS
import no.nav.medlemskap.regler.funksjoner.RelasjonFunksjoner.hentFnrTilBarnUnder25

class HarBrukerBarnSomErFolkeregistrertRegel(
        ytelse: Ytelse,
        private val periode: InputPeriode,
        private val pdlPersonhistorikk: Personhistorikk?,
        private val personhistorikkRelatertPerson: List<PersonhistorikkRelatertPerson>,
        regelId: RegelId = RegelId.REGEL_11_2_2
) : LovvalgRegel(regelId, ytelse, periode) {

    override fun operasjon(): Resultat {
        val familierelasjon = pdlPersonhistorikk?.familierelasjoner
        val barn = familierelasjon?.hentFnrTilBarnUnder25()
        val barnITps = personhistorikkRelatertPerson.hentBarnSomFinnesITPS(barn)

        val harBarnBosattINorge = barnITps.any {
            erPersonBosattINorge(
                    it.bostedsadresser.adresserForKontrollPeriode(kontrollPeriodeForPersonhistorikk),
                    it.postadresser.landkodeTilAdresserForKontrollPeriode(kontrollPeriodeForPersonhistorikk),
                    it.midlertidigAdresser.landkodeTilAdresserForKontrollPeriode(kontrollPeriodeForPersonhistorikk))
        }

        val harBarnSomIkkeErBosattINorge = barnITps.filterNot {
            erPersonBosattINorge(
                    it.bostedsadresser.adresserForKontrollPeriode(kontrollPeriodeForPersonhistorikk),
                    it.postadresser.landkodeTilAdresserForKontrollPeriode(kontrollPeriodeForPersonhistorikk),
                    it.midlertidigAdresser.landkodeTilAdresserForKontrollPeriode(kontrollPeriodeForPersonhistorikk))
        }.any()

        if (harBarnBosattINorge && harBarnSomIkkeErBosattINorge) {
            return uavklart("Noen barn med norsk adresse og noen barn med utenlandsk adresse")
        }

        if (harBarnBosattINorge) {
            return ja()
        } else {
            return nei("Ikke alle adressene til barna er norske, eller barn som mangler bostedsadresse")
        }
    }

    companion object {

        fun fraDatagrunnlag(datagrunnlag: Datagrunnlag, regelId: RegelId): HarBrukerBarnSomErFolkeregistrertRegel {
            return HarBrukerBarnSomErFolkeregistrertRegel(
                    ytelse = datagrunnlag.ytelse,
                    periode = datagrunnlag.periode,
                    pdlPersonhistorikk = datagrunnlag.pdlpersonhistorikk,
                    personhistorikkRelatertPerson = datagrunnlag.personHistorikkRelatertePersoner,
                    regelId = regelId
            )
        }
    }
}