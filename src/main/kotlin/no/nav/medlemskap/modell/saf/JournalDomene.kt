package no.nav.medlemskap.modell.saf

import no.nav.medlemskap.domene.Dokument
import no.nav.medlemskap.domene.Journalpost

fun mapDokumentoversiktBrukerResponse(response: DokumentoversiktBrukerResponse): List<Journalpost> {
    return response?.data?.dokumentoversiktBruker?.journalposter?.let {
        mapJournalResultat(it)
    } ?: emptyList()
}

fun mapJournalResultat(journal: List<no.nav.medlemskap.modell.saf.JournalPost>): List<Journalpost> {
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






