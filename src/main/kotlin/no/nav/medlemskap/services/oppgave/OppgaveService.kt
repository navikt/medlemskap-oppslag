package no.nav.medlemskap.services.oppgave

import no.nav.medlemskap.clients.oppgave.OppgaveClient
import no.nav.medlemskap.common.testContextCallMetrics
import no.nav.medlemskap.domene.Oppgave

class OppgaveService(private val oppgaveClient: OppgaveClient) {

    suspend fun hentOppgaver(aktorIder: List<String>, callId: String): List<Oppgave> {
        testContextCallMetrics()
        return mapOppgaveResultat(oppgaveClient.hentOppgaver(aktorIder, callId).oppgaver)
    }
}
