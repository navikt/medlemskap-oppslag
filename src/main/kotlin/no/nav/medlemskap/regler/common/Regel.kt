package no.nav.medlemskap.regler.common

import no.nav.medlemskap.common.regelCounter
import no.nav.medlemskap.domene.Ytelse
import no.nav.medlemskap.domene.Ytelse.Companion.metricName

data class Regel(
    val regelId: RegelId,
    val ytelse: Ytelse,
    val operasjon: () -> Resultat
) {
    fun utfør(): Resultat = operasjon.invoke().apply {
        regelCounter(this@Regel.regelId.identifikator + ". " + this@Regel.regelId.avklaring.replace("?", ""), this.svar.name, ytelse.metricName()).increment()
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
            operasjon = { Resultat.ja() }
        )

        fun regelNeiKonklusjon(ytelse: Ytelse, regelId: RegelId) = Regel(
            regelId = regelId,
            ytelse = ytelse,
            operasjon = { Resultat.nei("Regelflyt konkluderer med NEI") }
        )

        fun uavklartKonklusjon(ytelse: Ytelse, regelId: RegelId? = null) = Regel(
            regelId = RegelId.REGEL_MEDLEM_KONKLUSJON,
            ytelse = ytelse,
            operasjon = { Resultat.uavklart("Kan ikke konkludere med medlemskap", regelId) }
        )

        fun jaKonklusjon(ytelse: Ytelse) = Regel(
            regelId = RegelId.REGEL_MEDLEM_KONKLUSJON,
            ytelse = ytelse,
            operasjon = { Resultat.ja("Bruker er medlem") }
        )

        fun neiKonklusjon(ytelse: Ytelse) = Regel(
            regelId = RegelId.REGEL_MEDLEM_KONKLUSJON,
            ytelse = ytelse,
            operasjon = { Resultat.nei("Bruker er ikke medlem") }
        )
    }
}
