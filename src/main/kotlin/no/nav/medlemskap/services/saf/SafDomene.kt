package no.nav.medlemskap.services.saf

data class Dokument(val dokumentId: String, val tittel: String)

data class JournalPost(val journalpostId: String, val tittel: String, val journalposttype: String, val journalstatus: String, val tema: String, val dokumenter: List<Dokument> )

data class DokumentoversiktBruker(val journalposter: List<JournalPost>)

data class Data(val dokumentoversiktBruker: DokumentoversiktBruker)

data class SafResponse(val data: Data)
