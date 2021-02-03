package no.nav.medlemskap.regler.common

import no.nav.medlemskap.common.regelCounter
import no.nav.medlemskap.common.regelInfluxCounter
import no.nav.medlemskap.domene.Ytelse
import no.nav.medlemskap.domene.Ytelse.Companion.name
import no.nav.medlemskap.regler.common.Konklusjonstype.MEDLEM
import no.nav.medlemskap.regler.common.Konklusjonstype.REGELFLYT
import no.nav.medlemskap.regler.common.RegelId.Companion.metricName
import no.nav.medlemskap.regler.common.RegelId.REGEL_FLYT_KONKLUSJON
import no.nav.medlemskap.regler.common.RegelId.REGEL_MEDLEM_KONKLUSJON

data class Regel(
    val regelId: RegelId,
    val ytelse: Ytelse,
    val operasjon: () -> Resultat
) {
    fun utfør(): Resultat = operasjon.invoke().apply {
        regelCounter(this@Regel.regelId.metricName(), this.svar.name, ytelse.name()).increment()
        regelInfluxCounter(this@Regel.regelId.identifikator, this.svar.name, ytelse.name()).increment()
    }.copy(
        regelId = regelId,
        avklaring = regelId.avklaring
    )

    companion object {
        fun regelUavklart(ytelse: Ytelse, regelId: RegelId, konklusjonstype: Konklusjonstype = REGELFLYT) = Regel(
            regelId = regelId,
            ytelse = ytelse,
            operasjon = { Resultat.uavklart(REGEL_FLYT_KONKLUSJON, konklusjonstype) }
        )

        fun regelJa(ytelse: Ytelse, regelId: RegelId, konklusjonstype: Konklusjonstype = REGELFLYT) = Regel(
            regelId = regelId,
            ytelse = ytelse,
            operasjon = { Resultat.ja(REGEL_FLYT_KONKLUSJON, konklusjonstype) }
        )

        fun regelNei(ytelse: Ytelse, regelId: RegelId, konklusjonstype: Konklusjonstype = REGELFLYT) = Regel(
            regelId = regelId,
            ytelse = ytelse,
            operasjon = { Resultat.nei(REGEL_FLYT_KONKLUSJON, konklusjonstype) }
        )

        fun uavklartKonklusjon(ytelse: Ytelse, regelId: RegelId = REGEL_MEDLEM_KONKLUSJON) = Regel(
            regelId = regelId,
            ytelse = ytelse,
            operasjon = { Resultat.uavklart(REGEL_MEDLEM_KONKLUSJON, MEDLEM) }
        )

        fun jaKonklusjon(ytelse: Ytelse) = Regel(
            regelId = REGEL_MEDLEM_KONKLUSJON,
            ytelse = ytelse,
            operasjon = { Resultat.ja(REGEL_MEDLEM_KONKLUSJON, MEDLEM) }
        )

        fun neiKonklusjon(ytelse: Ytelse) = Regel(
            regelId = REGEL_MEDLEM_KONKLUSJON,
            ytelse = ytelse,
            operasjon = { Resultat.nei(REGEL_MEDLEM_KONKLUSJON, MEDLEM) }
        )
    }
}
