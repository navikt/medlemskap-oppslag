package no.nav.medlemskap.services.saf

import no.nav.medlemskap.domene.Dokument
import no.nav.medlemskap.domene.Journalpost

fun mapDokumentoversiktBrukerResponse(response: DokumentoversiktBrukerResponse): List<Journalpost> =
        response.data?.let { mapJournalResultat(it.dokumentoversiktBruker.journalposter) } ?: listOf()

fun mapJournalResultat(journal: List<JournalPost>): List<Journalpost> {
    return  journal.map {
       Journalpost(
               dokumenter = mapDokumenter(it),
               journalpostId = it.journalpostId,
               journalposttype = it.journalposttype.toString(),
               journalstatus = it.journalstatus.toString(),
               tema = it.tema.toString(),
               tittel = it.tittel)
    }
}

fun mapDokumenter(journalPost: JournalPost): List<Dokument>? {
    return journalPost.dokumenter?.map {
        Dokument(dokumentId = it.dokumentInfoId, tittel = it.tittel) }
}






