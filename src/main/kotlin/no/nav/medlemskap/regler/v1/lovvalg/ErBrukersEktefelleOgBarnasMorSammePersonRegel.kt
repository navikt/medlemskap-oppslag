package no.nav.medlemskap.regler.v1.lovvalg

import no.nav.medlemskap.domene.*
import no.nav.medlemskap.domene.ektefelle.DataOmEktefelle
import no.nav.medlemskap.regler.common.Funksjoner.harAlle
import no.nav.medlemskap.regler.common.RegelId
import no.nav.medlemskap.regler.common.Resultat
import no.nav.medlemskap.regler.common.ja
import no.nav.medlemskap.regler.common.nei
import no.nav.medlemskap.regler.funksjoner.RelasjonFunksjoner.hentBarnSomFinnesITPS
import no.nav.medlemskap.regler.funksjoner.RelasjonFunksjoner.hentFnrTilBarnUnder25

class ErBrukersEktefelleOgBarnasMorSammePersonRegel(
        ytelse: Ytelse,
        private val periode: InputPeriode,
        private val dataOmEktefelle: DataOmEktefelle?,
        private val pdlPersonhistorikk: Personhistorikk?,
        private val personhistorikkRelatertPerson: List<PersonhistorikkRelatertPerson>,
        regelId: RegelId = RegelId.REGEL_11_5_1
) : LovvalgRegel(regelId, ytelse, periode) {

    override fun operasjon(): Resultat {
        val familierelasjon = pdlPersonhistorikk?.familierelasjoner
        val barn = familierelasjon?.hentFnrTilBarnUnder25()
        val barnITps = personhistorikkRelatertPerson.hentBarnSomFinnesITPS(barn)

        if (dataOmEktefelle != null) {
            return when {
                dataOmEktefelle.personhistorikkEktefelle?.barn?.map { it.ident}?.harAlle(barnITps.map { it.ident })!! -> ja()
                else -> nei(" Ektefelle er ikke barn/barnas mor")
            }
        }

        return nei()
    }

    companion object {

        fun fraDatagrunnlag(datagrunnlag: Datagrunnlag): ErBrukersEktefelleOgBarnasMorSammePersonRegel {
            return ErBrukersEktefelleOgBarnasMorSammePersonRegel(
                    ytelse = datagrunnlag.ytelse,
                    periode = datagrunnlag.periode,
                    dataOmEktefelle = datagrunnlag.dataOmEktefelle,
                    pdlPersonhistorikk = datagrunnlag.pdlpersonhistorikk,
                    personhistorikkRelatertPerson = datagrunnlag.personHistorikkRelatertePersoner
            )
        }
    }
}