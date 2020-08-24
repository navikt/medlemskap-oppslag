package no.nav.medlemskap.regler.v1.lovvalg

import no.nav.medlemskap.domene.Datagrunnlag
import no.nav.medlemskap.domene.InputPeriode
import no.nav.medlemskap.domene.PersonhistorikkRelatertPerson
import no.nav.medlemskap.domene.Ytelse
import no.nav.medlemskap.domene.ektefelle.DataOmEktefelle
import no.nav.medlemskap.regler.common.RegelId
import no.nav.medlemskap.regler.common.Resultat
import no.nav.medlemskap.regler.common.ja
import no.nav.medlemskap.regler.common.nei
import no.nav.medlemskap.regler.funksjoner.AdresseFunksjoner.adresserForKontrollPeriode
import no.nav.medlemskap.regler.funksjoner.AdresseFunksjoner.landkodeTilAdresserForKontrollPeriode
import no.nav.medlemskap.regler.funksjoner.RelasjonFunksjoner.hentRelatertSomFinnesITPS

class ErBrukersEktefelleBosattINorgeRegel(
        ytelse: Ytelse,
        private val periode: InputPeriode,
        private val dataOmEktefelle: DataOmEktefelle?,
        private val personhistorikkRelatertPerson: List<PersonhistorikkRelatertPerson>,
        regelId: RegelId = RegelId.REGEL_11_3_1
) : LovvalgRegel(regelId, ytelse, periode) {

    override fun operasjon(): Resultat {
        val ektefelle = dataOmEktefelle?.personhistorikkEktefelle?.ident
        val ektefelleITps = personhistorikkRelatertPerson.hentRelatertSomFinnesITPS(ektefelle)

        val bostedsadresserTilEktefelle = ektefelleITps.flatMap { it.bostedsadresser.adresserForKontrollPeriode(kontrollPeriodeForPersonhistorikk) }
        val postAdresseTilEktefelle = ektefelleITps.flatMap { it.postadresser.landkodeTilAdresserForKontrollPeriode(kontrollPeriodeForPersonhistorikk) }
        val midlertidigPostadresseTilEktefelle = ektefelleITps.flatMap { it.midlertidigAdresser.landkodeTilAdresserForKontrollPeriode(kontrollPeriodeForPersonhistorikk) }
        return when {
            erPersonBosattINorge(bostedsadresserTilEktefelle, postAdresseTilEktefelle, midlertidigPostadresseTilEktefelle) -> ja()
            else -> nei("Ikke alle adressene til ektefelle er norske, eller ektefelle mangler bostedsadresse")
        }
    }

    companion object {

        fun fraDatagrunnlag(datagrunnlag: Datagrunnlag, regelId: RegelId): ErBrukersEktefelleBosattINorgeRegel {
            return ErBrukersEktefelleBosattINorgeRegel(
                    ytelse = datagrunnlag.ytelse,
                    periode = datagrunnlag.periode,
                    dataOmEktefelle = datagrunnlag.dataOmEktefelle,
                    personhistorikkRelatertPerson = datagrunnlag.personHistorikkRelatertePersoner,
                    regelId = regelId
            )
        }
    }
}