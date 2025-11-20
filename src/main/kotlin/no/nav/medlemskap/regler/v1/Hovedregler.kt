package no.nav.medlemskap.regler.v1

import no.nav.medlemskap.domene.Datagrunnlag
import no.nav.medlemskap.domene.Statsborgerskapskategori
import no.nav.medlemskap.domene.Ytelse
import no.nav.medlemskap.regler.common.Regel.Companion.jaKonklusjon
import no.nav.medlemskap.regler.common.Regel.Companion.neiKonklusjon
import no.nav.medlemskap.regler.common.Regel.Companion.uavklartKonklusjon
import no.nav.medlemskap.regler.common.RegelId
import no.nav.medlemskap.regler.common.Regler
import no.nav.medlemskap.regler.common.Resultat
import no.nav.medlemskap.regler.common.Resultat.Companion.finnÅrsaker
import no.nav.medlemskap.regler.common.Svar
import no.nav.medlemskap.regler.common.Svar.*
import no.nav.medlemskap.regler.v1.brukergruppe.ReglerForBrukerGruppe
import no.nav.medlemskap.regler.v1.frilanser.ReglerForFrilanser
import no.nav.medlemskap.regler.v1.regelflyt.arbeid.ReglerForPermisjoner
import no.nav.medlemskap.regler.v1.regelflyt.arbeid.ReglerForUtenlandsforhold

class Hovedregler(private val datagrunnlag: Datagrunnlag, val brukerGrupeResultat: Resultat? = null) {
    private val reglerForRequestValidering = ReglerForRequestValidering.fraDatagrunnlag(datagrunnlag)
    private val reglerForStatsborgerskap = ReglerForStatsborgerskap.fraDatagrunnlag(datagrunnlag)
    private val reglerForAndreBorgereMedFamilie = ReglerForAndreBorgereOgEktefelle.fraDatagrunnlag(datagrunnlag)

    fun kjørHovedregler(): Resultat {

        // 1. Validering av forespørsel
        val requestValideringResultat = reglerForRequestValidering.kjørRegel()
        if (requestValideringResultat.svar != JA) return requestValideringResultat

        val resultater = mutableListOf<Resultat>()
        brukerGrupeResultat?.let { resultater += it }

        //TODO: Fjerne alle steder denne refereres til. Den er hardkodet til tom. Verdien settes aldri.
        val reglerSomSkalOverstyres = emptyMap<RegelId, Svar>()

        // 2. Kjør felles regler
        val resultatForFellesregler = kjørFellesRegler()
        resultater += resultatForFellesregler

        // 3. Kjør regler for statsborgerskap
        val resultatStatsborgerskap = reglerForStatsborgerskap.kjørHovedflyt()
        resultater += resultatStatsborgerskap


        // 4. Kjør regler for statsborgerskapskategori (Norsk/EØS/andre borgere/andre borgere med EØS-familie)
        val resultaterForStatsborgerskapskategori = kjørReglerForStatsborgerskapskategori(
            resultatStatsborgerskap, reglerSomSkalOverstyres
        )
        resultater += resultaterForStatsborgerskapskategori

        return utledResultat(reglerForRequestValidering.ytelse, resultater)
    }

    private fun kjørReglerForStatsborgerskapskategori(
        resultatStatsborgerskap: Resultat,
        reglerSomSkalOverstyres: Map<RegelId, Svar>
    ): List<Resultat> {
        val kanskjeAndreBorgereMedEØSFamilie = kanskjeAndreBorgereMedEØSFamilie(resultatStatsborgerskap)
        val statsborgerskapskategori = velgStatsborgerskapKategori(resultatStatsborgerskap, kanskjeAndreBorgereMedEØSFamilie)

        return when (statsborgerskapskategori) {
            Statsborgerskapskategori.NORSK_BORGER -> kjørReglerForNorskeBorgere(reglerSomSkalOverstyres)
            Statsborgerskapskategori.EØS_BORGER -> kjørReglerForEøsBorgere(reglerSomSkalOverstyres)
            Statsborgerskapskategori.ANDRE_BORGERE -> kjørReglerForAndreBorgere(kanskjeAndreBorgereMedEØSFamilie)
        }
    }

    private fun velgStatsborgerskapKategori(
        resultatStatsborgerskap: Resultat,
        kanskjeAndreBorgereMedEØSFamilie: Resultat?
    ): Statsborgerskapskategori {
        return if (kanskjeAndreBorgereMedEØSFamilie?.svar == JA) kanskjeAndreBorgereMedEØSFamilie.bestemStatsborgerskapskategori()
        else resultatStatsborgerskap.bestemStatsborgerskapskategori()
    }

    private fun kjørReglerForAndreBorgere(
        kanskjeAndreBorgereMedEØSFamilie: Resultat?
    ): List<Resultat> {
        val resultater = mutableListOf<Resultat>()
        kanskjeAndreBorgereMedEØSFamilie?.let { resultater.add(it) }

        val resultatAvUDIValidering = UDIValidering.fraDatagrunnlag(datagrunnlag).kjørHovedflyt()
        resultater.add(resultatAvUDIValidering)

        if (regelflytErIkkeUavklart(resultatAvUDIValidering)) {

            val resultatUDIIkkeLovligOpphold = UDIIkkeLovligOpphold.fraDatagrunnlag(datagrunnlag).kjørHovedflyt()
            resultater.add(resultatUDIIkkeLovligOpphold)

            if (regelflytErIkkeUavklart(resultatUDIIkkeLovligOpphold)){
                resultater.add(UDILovligOpphold.fraDatagrunnlag(datagrunnlag).kjørHovedflyt())
                resultater.add(UDIArbeidsAdgang.fraDatagrunnlag(datagrunnlag).kjørHovedflyt())
                resultater.add(UDIBritiskeBorgere.fraDatagrunnlag(datagrunnlag).kjørHovedflyt())
            }
        }

        if (kanskjeAndreBorgereMedEØSFamilie?.erFamilieEOS() == true) {
            resultater.add(ReglerForArbeidsforhold.fraDatagrunnlag(datagrunnlag).kjørHovedflyt())
            resultater.add(ReglerForEøsBorgere.fraDatagrunnlag(datagrunnlag).kjørHovedflyt())
        } else {
            resultater.add(ReglerForArbeidstakerAndreBorgere.fraDatagrunnlag(datagrunnlag).kjørHovedflyt())
        }
            return resultater
    }

    private fun regelflytErIkkeUavklart(resultat: Resultat): Boolean = resultat.svar != UAVKLART

    private fun kanskjeAndreBorgereMedEØSFamilie(resultatStatsborgerskap: Resultat): Resultat? {
        if (!resultatStatsborgerskap.erNorskBorger() && !resultatStatsborgerskap.erEøsBorger()) {
            return reglerForAndreBorgereMedFamilie.kjørHovedflyt()
        }
        return null
    }


    private fun kjørReglerForNorskeBorgere(overstyrteRegler: Map<RegelId, Svar>): List<Resultat> {
        return listOf(
            ReglerForArbeidsforhold.fraDatagrunnlag(datagrunnlag, overstyrteRegler),
            ReglerForNorskeStatsborgere.fraDatagrunnlag(datagrunnlag, overstyrteRegler)
        ).map { it.kjørHovedflyt() }
    }

    private fun kjørReglerForEøsBorgere(
        overstyrteRegler: Map<RegelId, Svar>
    ): List<Resultat> {
        val resultater = mutableListOf<Resultat>()
        val reglerForEØSBorgerResultater = listOf(
            ReglerForArbeidsforhold.fraDatagrunnlag(datagrunnlag, overstyrteRegler).kjørHovedflyt(),
            ReglerForEøsBorgere.fraDatagrunnlag(datagrunnlag).kjørHovedflyt()
        )

        resultater.addAll(reglerForEØSBorgerResultater)

        return resultater
    }

    private fun kjørFellesRegler(): List<Resultat> {
        val fellesRegler = listOf(
            ReglerForDoedsfall.fraDatagrunnlag(datagrunnlag),
            ReglerForBosatt.fraDatagrunnlag(datagrunnlag),
            ReglerForMedl.fraDatagrunnlag(datagrunnlag),
            ReglerForFrilanser.fraDatagrunnlag(datagrunnlag),
            ReglerForBrukerGruppe.fraDatagrunnlag(datagrunnlag),
            ReglerForFellesArbeidsforhold.fraDatagrunnlag(datagrunnlag),
            ReglerForBrukersvarArbeid.fraDatagrunnlag(datagrunnlag),
            ReglerForPermittering.fraDatagrunnlag(datagrunnlag),
            ReglerForPermisjoner.fraDatagrunnlag(datagrunnlag),
            ReglerForUtenlandsforhold.fraDatagrunnlag(datagrunnlag),
            ReglerForMaritim.fraDatagrunnlag(datagrunnlag),
            ReglerForStonader.fraDatagrunnlag(datagrunnlag)
        )

        return fellesRegler.map(Regler::kjørHovedflyt)
    }

    companion object {
        private fun utledResultat(ytelse: Ytelse, resultater: List<Resultat>): Resultat {

            val førsteNei = resultater.find { it.svar == NEI }
            if (førsteNei != null) {
                return lagKonklusjon(neiResultat(ytelse), resultater)
            }

            val medlemskonklusjon = resultater.find { it.erMedlemskonklusjon() }
            if (medlemskonklusjon != null) {
                return lagKonklusjon(konklusjon(ytelse, medlemskonklusjon), resultater)
            }

            if (resultater.all { it.svar == JA }) {
                return lagKonklusjon(jaResultat(ytelse), resultater)
            }

            return lagKonklusjon(uavklartResultat(ytelse), resultater)
        }

        private fun lagKonklusjon(konklusjon: Resultat, resultater: List<Resultat>): Resultat {
            return konklusjon.copy(delresultat = lagDelresultat(resultater), årsaker = resultater.finnÅrsaker())
        }

        private fun lagDelresultat(resultater: List<Resultat>): List<Resultat> {
            return resultater.map { if (it.regelId == RegelId.REGEL_FLYT_KONKLUSJON || it.regelId == RegelId.REGEL_MEDLEM_KONKLUSJON && it.delresultat.isNotEmpty()) it.delresultat.first() else it }
        }

        private fun konklusjon(ytelse: Ytelse, resultat: Resultat): Resultat {
            return when (resultat.svar) {
                JA -> jaKonklusjon(ytelse).utfør().copy(harDekning = resultat.harDekning, dekning = resultat.dekning)
                NEI -> neiKonklusjon(ytelse).utfør().copy(harDekning = resultat.harDekning, dekning = resultat.dekning)
                UAVKLART -> uavklartKonklusjon(ytelse).utfør()
                    .copy(harDekning = resultat.harDekning, dekning = resultat.dekning)
            }
        }

        private fun uavklartResultat(ytelse: Ytelse): Resultat {
            return uavklartKonklusjon(ytelse).utfør()
        }

        private fun neiResultat(ytelse: Ytelse): Resultat {
            return neiKonklusjon(ytelse).utfør()
        }

        private fun jaResultat(ytelse: Ytelse): Resultat {
            return jaKonklusjon(ytelse).utfør()
        }
    }
}
