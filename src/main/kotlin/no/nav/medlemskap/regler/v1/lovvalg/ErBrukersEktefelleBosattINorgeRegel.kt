package no.nav.medlemskap.regler.v1.lovvalg

import no.nav.medlemskap.domene.Datagrunnlag
import no.nav.medlemskap.domene.InputPeriode
import no.nav.medlemskap.domene.Ytelse
import no.nav.medlemskap.domene.ektefelle.DataOmEktefelle
import no.nav.medlemskap.regler.common.RegelId
import no.nav.medlemskap.regler.common.Resultat
import no.nav.medlemskap.regler.common.Resultat.Companion.ja
import no.nav.medlemskap.regler.common.Resultat.Companion.nei
import no.nav.medlemskap.regler.funksjoner.AdresseFunksjoner.adresserForKontrollPeriode
import no.nav.medlemskap.regler.funksjoner.AdresseFunksjoner.landkodeTilAdresserForKontrollPeriode
import java.time.LocalDate

class ErBrukersEktefelleBosattINorgeRegel(
    ytelse: Ytelse,
    private val periode: InputPeriode,
    førsteDagForYtelse: LocalDate?,
    private val dataOmEktefelle: DataOmEktefelle?,
    regelId: RegelId = RegelId.REGEL_11_3_1
) : LovvalgRegel(regelId, ytelse, periode, førsteDagForYtelse) {

    override fun operasjon(): Resultat {

        if (dataOmEktefelle != null) {
            val ektefelle = dataOmEktefelle.personhistorikkEktefelle

            val bostedsadresserTilEktefelle = ektefelle.bostedsadresser.adresserForKontrollPeriode(kontrollPeriodeForPersonhistorikk)
            val kontaktadresseTilEktefelle = ektefelle.kontaktadresser.landkodeTilAdresserForKontrollPeriode(kontrollPeriodeForPersonhistorikk)
            val oppholdsadresseTilEktefelle = ektefelle.oppholdsadresser.landkodeTilAdresserForKontrollPeriode(kontrollPeriodeForPersonhistorikk)

            return when {
                erPersonBosattINorge(bostedsadresserTilEktefelle, kontaktadresseTilEktefelle, oppholdsadresseTilEktefelle) -> ja(RegelId.REGEL_11_3_1.jaBegrunnelse)
                else -> nei(RegelId.REGEL_11_3_1.neiBegrunnelse)
            }
        }
        // Denne vil ikke skje
        return nei(RegelId.REGEL_11_3_1.neiBegrunnelse)
    }

    companion object {

        fun fraDatagrunnlag(datagrunnlag: Datagrunnlag, regelId: RegelId): ErBrukersEktefelleBosattINorgeRegel {
            return ErBrukersEktefelleBosattINorgeRegel(
                ytelse = datagrunnlag.ytelse,
                periode = datagrunnlag.periode,
                førsteDagForYtelse = datagrunnlag.førsteDagForYtelse,
                dataOmEktefelle = datagrunnlag.dataOmEktefelle,
                regelId = regelId
            )
        }
    }
}
