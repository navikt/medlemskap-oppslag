package no.nav.medlemskap.regler.common

import no.nav.medlemskap.common.regelCounter

data class Regel(
        val identifikator: String,
        val avklaring: String,
        val beskrivelse: String,
        val operasjon: () -> Resultat,
        val hvisJa: Regel? = null,
        val hvisNei: Regel? = null
) {
    fun utfør(resultatliste: MutableList<Resultat>, harDekning: Svar? = null, dekning: List<String> = listOf()): Resultat {
        val resultat = operasjon.invoke().apply {
            regelCounter(this@Regel.identifikator + ". " + this@Regel.avklaring.replace("?", ""), this.svar.name).increment()
        }.copy(
                identifikator = identifikator,
                avklaring = avklaring
        )

        resultatliste.add(resultat)
        if (resultat.svar == Svar.JA && hvisJa != null) {
            return hvisJa.utfør(resultatliste, resultat.harDekning, resultat.dekning)
        }

        if (resultat.svar == Svar.NEI && hvisNei != null) {
            return hvisNei.utfør(resultatliste, resultat.harDekning, resultat.dekning)
        }

        resultat.harDekning = harDekning
        resultat.dekning = dekning

        return resultat
    }

    fun utfør(): Resultat = operasjon.invoke().apply {
        regelCounter(this@Regel.identifikator + ". " + this@Regel.avklaring.replace("?", ""), this.svar.name).increment()
    }.copy(
            identifikator = identifikator,
            avklaring = avklaring
    )

    infix fun hvisJa(regel: () -> Regel) = this.copy(hvisJa = regel.invoke())

    infix fun hvisNei(regel: () -> Regel) = this.copy(hvisNei = regel.invoke())
}
