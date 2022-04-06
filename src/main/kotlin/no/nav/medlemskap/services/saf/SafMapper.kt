package no.nav.medlemskap.services.saf

import no.nav.medlemskap.clients.saf.generated.Dokumenter
import no.nav.medlemskap.domene.Dokument
import no.nav.medlemskap.domene.Journalpost

fun mapDokumentoversiktBrukerResponse(response: Dokumenter.Result): List<Journalpost> =
    mapJournalResultat(response.dokumentoversiktBruker.journalposter)

fun mapJournalResultat(journal: List<no.nav.medlemskap.clients.saf.generated.dokumenter.Journalpost?>): List<Journalpost> {
    return journal.filterNotNull().map {
        Journalpost(
            dokumenter = mapDokumenter(it),
            journalpostId = it.journalpostId,
            journalfortAvNavn = it.journalfortAvNavn,
            journalposttype = it.journalposttype.toString(),
            journalstatus = it.journalstatus.toString(),
            tema = it.tema.toString(),
            tittel = it.tittel
        )
    }
}

fun mapDokumenter(journalPost: no.nav.medlemskap.clients.saf.generated.dokumenter.Journalpost): List<Dokument> {
    return journalPost.dokumenter?.filterNotNull()?.map {
        Dokument(
            dokumentId = it.dokumentInfoId,
            tittel = it.tittel
        )
    } ?: emptyList()
}
