package no.nav.medlemskap.regler.common

import no.nav.medlemskap.common.regelCounter
import no.nav.medlemskap.domene.Ytelse
import no.nav.medlemskap.domene.Ytelse.Companion.metricName

data class Regel(
        val regelId: RegelId,
        val ytelse: Ytelse,
        val operasjon: () -> Resultat,
        val hvisJa: Regel? = null,
        val hvisNei: Regel? = null,
        val hvisUavklart: Regel? = null
) {
    fun utfør(resultatliste: MutableList<Resultat>, harDekning: Svar? = null, dekning: String = ""): Resultat {
        val resultat = operasjon.invoke().apply {
            regelCounter(this@Regel.regelId.identifikator + ". " + this@Regel.regelId.avklaring.replace("?", ""), this.svar.name, ytelse.metricName()).increment()
        }.copy(
                regelId = regelId,
                avklaring = regelId.avklaring
        )

        resultatliste.add(resultat)
        if (resultat.svar == Svar.JA && hvisJa != null) {
            return hvisJa.utfør(resultatliste, resultat.harDekning, resultat.dekning)
        }

        if (resultat.svar == Svar.NEI && hvisNei != null) {
            return hvisNei.utfør(resultatliste, resultat.harDekning, resultat.dekning)
        }

        if (resultat.svar == Svar.UAVKLART && hvisUavklart != null) {
            return hvisUavklart.utfør(resultatliste, resultat.harDekning, resultat.dekning)
        }

        resultat.harDekning = harDekning
        resultat.dekning = dekning

        return resultat
    }

    fun utfør(): Resultat = operasjon.invoke().apply {
        regelCounter(this@Regel.regelId.identifikator + ". " + this@Regel.regelId.avklaring.replace("?", ""), this.svar.name, ytelse.metricName()).increment()
    }.copy(
            regelId = regelId,
            avklaring = regelId.avklaring
    )

    infix fun hvisJa(regel: () -> Regel) = this.copy(hvisJa = regel.invoke())

    infix fun hvisNei(regel: () -> Regel) = this.copy(hvisNei = regel.invoke())

    infix fun hvisUavklart(regel: () -> Regel) = this.copy(hvisUavklart = regel.invoke())
}
