package no.nav.medlemskap.regler.v1.lovvalg

import no.nav.medlemskap.domene.Datagrunnlag
import no.nav.medlemskap.domene.InputPeriode
import no.nav.medlemskap.domene.Ytelse
import no.nav.medlemskap.domene.ektefelle.DataOmEktefelle
import no.nav.medlemskap.regler.common.RegelId
import no.nav.medlemskap.regler.common.Resultat
import no.nav.medlemskap.regler.common.ja
import no.nav.medlemskap.regler.common.nei
import no.nav.medlemskap.regler.funksjoner.AdresseFunksjoner.adresserForKontrollPeriode
import no.nav.medlemskap.regler.funksjoner.AdresseFunksjoner.landkodeTilAdresserForKontrollPeriode

class ErBrukersEktefelleBosattINorgeRegel(
        ytelse: Ytelse,
        private val periode: InputPeriode,
        private val dataOmEktefelle: DataOmEktefelle?,
        regelId: RegelId = RegelId.REGEL_11_3_1
) : LovvalgRegel(regelId, ytelse, periode) {

    override fun operasjon(): Resultat {

        if(dataOmEktefelle != null) {
            val ektefelle = dataOmEktefelle.personhistorikkEktefelle

            val bostedsadresserTilEktefelle = ektefelle.bostedsadresser.adresserForKontrollPeriode(kontrollPeriodeForPersonhistorikk)
            val kontaktadresseTilEktefelle = ektefelle.kontaktadresser.landkodeTilAdresserForKontrollPeriode(kontrollPeriodeForPersonhistorikk)
            val oppholdsadresseTilEktefelle = ektefelle.oppholdsadresser.landkodeTilAdresserForKontrollPeriode(kontrollPeriodeForPersonhistorikk)

            return when {
                erPersonBosattINorge(bostedsadresserTilEktefelle, kontaktadresseTilEktefelle, oppholdsadresseTilEktefelle) -> ja()
                else -> nei("Ikke alle adressene til ektefelle er norske, eller ektefelle mangler bostedsadresse")
            }
        }
        // Denne vil ikke skje
        return nei("Ikke alle adressene til ektefelle er norske, eller ektefelle mangler bostedsadresse")
    }

    companion object {

        fun fraDatagrunnlag(datagrunnlag: Datagrunnlag, regelId: RegelId): ErBrukersEktefelleBosattINorgeRegel {
            return ErBrukersEktefelleBosattINorgeRegel(
                    ytelse = datagrunnlag.ytelse,
                    periode = datagrunnlag.periode,
                    dataOmEktefelle = datagrunnlag.dataOmEktefelle,
                    regelId = regelId
            )
        }
    }
}