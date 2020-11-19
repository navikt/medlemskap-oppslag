package no.nav.medlemskap.regler.common

import no.nav.medlemskap.common.regelCounter
import no.nav.medlemskap.domene.Ytelse
import no.nav.medlemskap.domene.Ytelse.Companion.name

data class Regel(
    val regelId: RegelId,
    val ytelse: Ytelse,
    val operasjon: () -> Resultat
) {
    fun utfør(): Resultat = operasjon.invoke().apply {
        regelCounter(this@Regel.regelId.identifikator + ". " + this@Regel.regelId.avklaring.replace("?", ""), this.svar.name, ytelse.name()).increment()
    }.copy(
        regelId = regelId,
        avklaring = regelId.avklaring
    )
}
