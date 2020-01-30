package no.nav.medlemskap.modell.oppgave

import no.nav.medlemskap.domene.Oppgave

fun mapOppgaveResultat(oppgaver: List<no.nav.medlemskap.modell.oppgave.Oppgave>): List<Oppgave> {
    return  oppgaver.map { no.nav.medlemskap.domene.Oppgave(
            aktivDato = it.aktivDato,
            prioritet = mapPrioritert(it),
            status = mapStatus(it),
            tema = it.tema)}

}

fun mapStatus(oppgave: no.nav.medlemskap.modell.oppgave.Oppgave): no.nav.medlemskap.domene.Status {
    when (oppgave.status.name) {
        "OPPRETTET" -> return no.nav.medlemskap.domene.Status.OPPRETTET
        "AAPNET" -> return no.nav.medlemskap.domene.Status.AAPNET
        "UNDER_BEHANDLING" -> return no.nav.medlemskap.domene.Status.UNDER_BEHANDLING
        "FERDIGSTILT" -> return no.nav.medlemskap.domene.Status.FERDIGSTILT
        else -> {
            return no.nav.medlemskap.domene.Status.FEILREGISTRERT
        }
    }

}

fun mapPrioritert(oppgave: no.nav.medlemskap.modell.oppgave.Oppgave): no.nav.medlemskap.domene.Prioritet{
    when (oppgave.prioritet.name) {
        "HØY" -> return no.nav.medlemskap.domene.Prioritet.HOY
        "LAV" -> return no.nav.medlemskap.domene.Prioritet.LAV
        else -> {
            return no.nav.medlemskap.domene.Prioritet.NORM
        }
    }

}
