package no.nav.medlemskap.services.saf

import java.time.LocalDateTime


enum class JournalStatus {
    MOTTATT, JOURNALFOERT, FERDIGSTILT, EKSPEDERT, UNDER_ARBEID, FEILREGISTRERT, UTGAAR, AVBRUTT, UKJENT_BRUKER, RESERVERT, OPPLASTING_DOKUMENT, UKJENT
}

enum class JournalPostType {
    I, U, N
}

data class DokumentInfo(val dokumentInfoId: String, val tittel: String?)

data class JournalPost(val journalpostId: String, val tittel: String?, val journalposttype: JournalPostType?, val journalstatus: JournalStatus?, val tema: String?, val datoOpprettet: LocalDateTime, val dokumenter: List<DokumentInfo>?)

data class Dokumentoversikt(val journalposter: List<JournalPost>)

data class Data(val dokumentoversiktBruker: Dokumentoversikt)

data class SafResponse(val data: Data)


// Kan utvides:
//
//journalposter {
//    journalpostId
//    tittel
//    journalposttype
//    journalstatus
//    tema
//    temanavn
//    avsenderMottakerNavn
//    journalfortAvNavn
//    kanal
//    kanalnavn
//    sak {
//        arkivsaksnummer
//        arkivsaksystem
//        fagsakId
//        fagsaksystem
//        datoOpprettet
//    }
//    bruker {
//        id
//        type
//    }
//    relevanteDatoer {
//        dato
//        datotype
//    }
//    dokumenter {
//        dokumentInfoId
//        tittel
//        brevkode
//        dokumentvarianter {
//            saksbehandlerHarTilgang
//            variantformat
//        }
//        logiskeVedlegg {
//            tittel
//        }
//    }
//}
