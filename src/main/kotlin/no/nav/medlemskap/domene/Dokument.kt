package no.nav.medlemskap.domene

data class Journalpost(
    val journalpostId: String,
    val tittel: String?,
    val journalposttype: String?,
    val journalstatus: String?,
    val tema: String?,
    val dokumenter: List<Dokument>?
)

data class Dokument(
    val dokumentId: String,
    val tittel: String?
)
