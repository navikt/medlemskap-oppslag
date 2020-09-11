package no.nav.medlemskap.regler.v1.lovvalg

import no.nav.medlemskap.domene.*
import no.nav.medlemskap.domene.barn.DataOmBarn
import no.nav.medlemskap.regler.common.*
import no.nav.medlemskap.regler.funksjoner.AdresseFunksjoner.adresserForKontrollPeriode
import no.nav.medlemskap.regler.funksjoner.AdresseFunksjoner.landkodeTilAdresserForKontrollPeriode

class HarBrukerBarnSomErFolkeregistrertRegel(
        ytelse: Ytelse,
        private val periode: InputPeriode,
        private val dataOmbarn: List<DataOmBarn>?,
        regelId: RegelId = RegelId.REGEL_11_2_2
) : LovvalgRegel(regelId, ytelse, periode) {

    override fun operasjon(): Resultat {
        val barn = dataOmbarn

        val harBarnBosattINorge = barn?.any {
            erPersonBosattINorge(
                    it.personhistorikkBarn.bostedsadresser.adresserForKontrollPeriode(kontrollPeriodeForPersonhistorikk),
                    it.personhistorikkBarn.kontaktadresser.landkodeTilAdresserForKontrollPeriode(kontrollPeriodeForPersonhistorikk),
                    it.personhistorikkBarn.oppholdsadresser.landkodeTilAdresserForKontrollPeriode(kontrollPeriodeForPersonhistorikk))
        }

        val harBarnSomIkkeErBosattINorge = barn?.filterNot {
            erPersonBosattINorge(
                    it.personhistorikkBarn.bostedsadresser.adresserForKontrollPeriode(kontrollPeriodeForPersonhistorikk),
                    it.personhistorikkBarn.kontaktadresser.landkodeTilAdresserForKontrollPeriode(kontrollPeriodeForPersonhistorikk),
                    it.personhistorikkBarn.oppholdsadresser.landkodeTilAdresserForKontrollPeriode(kontrollPeriodeForPersonhistorikk))
        }?.any()

        if (harBarnBosattINorge!! && harBarnSomIkkeErBosattINorge!!) {
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
                    dataOmbarn = datagrunnlag.dataOmBarn,
                    regelId = regelId
            )
        }
    }
}