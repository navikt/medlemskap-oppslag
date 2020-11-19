package no.nav.medlemskap.regler.v1.lovvalg

import no.nav.medlemskap.domene.Datagrunnlag
import no.nav.medlemskap.domene.InputPeriode
import no.nav.medlemskap.domene.Ytelse
import no.nav.medlemskap.domene.barn.DataOmBarn
import no.nav.medlemskap.regler.common.RegelId
import no.nav.medlemskap.regler.common.Resultat
import no.nav.medlemskap.regler.common.Resultat.Companion.ja
import no.nav.medlemskap.regler.common.Resultat.Companion.nei
import no.nav.medlemskap.regler.common.Resultat.Companion.uavklart
import no.nav.medlemskap.regler.funksjoner.AdresseFunksjoner.adresserForKontrollPeriode
import no.nav.medlemskap.regler.funksjoner.AdresseFunksjoner.landkodeTilAdresserForKontrollPeriode
import java.time.LocalDate

class HarBrukerBarnSomErFolkeregistrertRegel(
    ytelse: Ytelse,
    private val periode: InputPeriode,
    førsteDagForYtelse: LocalDate?,
    private val dataOmbarn: List<DataOmBarn>?,
    regelId: RegelId = RegelId.REGEL_11_2_2
) : LovvalgRegel(regelId, ytelse, periode, førsteDagForYtelse) {

    override fun operasjon(): Resultat {
        val barn = dataOmbarn

        val harBarnBosattINorge = barn?.any {
            erPersonBosattINorge(
                it.personhistorikkBarn.bostedsadresser.adresserForKontrollPeriode(kontrollPeriodeForPersonhistorikk),
                it.personhistorikkBarn.kontaktadresser.landkodeTilAdresserForKontrollPeriode(kontrollPeriodeForPersonhistorikk),
                it.personhistorikkBarn.oppholdsadresser.landkodeTilAdresserForKontrollPeriode(kontrollPeriodeForPersonhistorikk)
            )
        }

        val harBarnSomIkkeErBosattINorge = barn?.filterNot {
            erPersonBosattINorge(
                it.personhistorikkBarn.bostedsadresser.adresserForKontrollPeriode(kontrollPeriodeForPersonhistorikk),
                it.personhistorikkBarn.kontaktadresser.landkodeTilAdresserForKontrollPeriode(kontrollPeriodeForPersonhistorikk),
                it.personhistorikkBarn.oppholdsadresser.landkodeTilAdresserForKontrollPeriode(kontrollPeriodeForPersonhistorikk)
            )
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
                førsteDagForYtelse = datagrunnlag.førsteDagForYtelse,
                dataOmbarn = datagrunnlag.dataOmBarn,
                regelId = regelId
            )
        }
    }
}
