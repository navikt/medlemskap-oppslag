package no.nav.medlemskap.regler.common

import no.nav.medlemskap.common.regelCounter
import no.nav.medlemskap.common.regelInfluxCounter
import no.nav.medlemskap.domene.Ytelse
import no.nav.medlemskap.domene.Ytelse.Companion.name

data class Regel(
    val regelId: RegelId,
    val ytelse: Ytelse,
    val operasjon: () -> Resultat
) {
    fun utfør(): Resultat = operasjon.invoke().apply {
        regelCounter(this@Regel.regelId.identifikator + ". " + this@Regel.regelId.avklaring.replace("?", ""), this.svar.name, ytelse.name()).increment()
        regelInfluxCounter(this@Regel.regelId.identifikator, this.svar.name, ytelse.name()).increment()
    }.copy(
        regelId = regelId,
        avklaring = regelId.avklaring
    )

    companion object {
        fun regelUavklartKonklusjon(ytelse: Ytelse, regelId: RegelId) = Regel(
            regelId = regelId,
            ytelse = ytelse,
            operasjon = { Resultat.uavklart("Regelflyt konkluderer med UAVKLART") }
        )

        fun regelJaKonklusjon(ytelse: Ytelse, regelId: RegelId) = Regel(
            regelId = regelId,
            ytelse = ytelse,
            operasjon = { Resultat.ja(RegelId.REGEL_FLYT_KONKLUSJON) }
        )

        fun regelNeiKonklusjon(ytelse: Ytelse, regelId: RegelId) = Regel(
            regelId = regelId,
            ytelse = ytelse,
            operasjon = { Resultat.nei(RegelId.REGEL_FLYT_KONKLUSJON) }
        )

        fun uavklartKonklusjon(ytelse: Ytelse, regelId: RegelId? = null) = Regel(
            regelId = RegelId.REGEL_MEDLEM_KONKLUSJON,
            ytelse = ytelse,
            operasjon = { Resultat.uavklart(RegelId.REGEL_MEDLEM_KONKLUSJON.neiBegrunnelse, regelId) }
        )

        fun jaKonklusjon(ytelse: Ytelse) = Regel(
            regelId = RegelId.REGEL_MEDLEM_KONKLUSJON,
            ytelse = ytelse,
            operasjon = { Resultat.ja(RegelId.REGEL_MEDLEM_KONKLUSJON) }
        )

        fun neiKonklusjon(ytelse: Ytelse) = Regel(
            regelId = RegelId.REGEL_MEDLEM_KONKLUSJON,
            ytelse = ytelse,
            operasjon = { Resultat.nei(RegelId.REGEL_MEDLEM_KONKLUSJON) }
        )
    }
}
