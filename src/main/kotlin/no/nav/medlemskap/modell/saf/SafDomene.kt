package no.nav.medlemskap.modell.saf

import java.time.LocalDateTime

enum class BrukerIdType(@Suppress("unused") val beskrivelse: String) {
    AKTOERID("NAV aktørid for en person"),
    FNR("Folkeregisterets fødselsnummer for en person"),
    ORGNR("Foretaksregisterets organisasjonsnummer for en juridisk person")
}

enum class JournalStatus {
    MOTTATT,
    JOURNALFOERT,
    FERDIGSTILT,
    EKSPEDERT,
    UNDER_ARBEID,
    FEILREGISTRERT,
    UTGAAR,
    AVBRUTT,
    UKJENT_BRUKER,
    RESERVERT,
    OPPLASTING_DOKUMENT,
    UKJENT
}


enum class JournalPostType(@Suppress("unused") val beskrivelse: String) {
    I("Inngående"),
    U("Utgående"),
    N("Notat")
}

enum class Tema(@Suppress("unused") val beskrivelse: String) {
    AAP("Arbeidsavklaringspenger"),
    AAR("Aa-registeret"),
    AGR("Ajourhold - Grunnopplysninger"),
    BAR("Barnetrygd"),
    BID("Bidrag"),
    BIL("Bil"),
    DAG("Dagpenger"),
    ENF("Enslig forsørger"),
    ERS("Erstatning"),
    FAR("Farskap"),
    FEI("Feilutbetaling"),
    FOR("Foreldre- og svangerskapspenger"),
    FOS("Forsikring"),
    FUL("Fullmakt"),
    GEN("Generell"),
    GRA("Gravferdsstønad"),
    GRU("Grunn- og hjelpestønad"),
    HEL("Helsetjenester og ortopediske hjelpemidler"),
    HJE("Hjelpemidler"),
    IAR("Inkluderende arbeidsliv"),
    IND("Tiltakspenger"),
    KON("Kontantstøtte"),
    KTR("Kontroll"),
    MED("Medlemskap"),
    MOB("Mobilitetsfremmende stønad"),
    OMS("Omsorgspenger, pleiepenger og opplæringspenger"),
    OPA("Oppfølging - Arbeidsgiver"),
    OPP("Oppfølging"),
    PEN("Pensjon"),
    PER("Permittering og masseoppsigelser"),
    REH("Rehabilitering"),
    REK("Rekruttering og stilling"),
    RPO("Retting av personopplysninger"),
    RVE("Rettferdsvederlag"),
    SAA("Sanksjon - Arbeidsgiver"),
    SAK("Saksomkostninger"),
    SAP("Sanksjon - Person"),
    SER("Serviceklager"),
    SIK("Sikkerhetstiltak"),
    STO("Regnskap/utbetaling"),
    SUP("Supplerende stønad"),
    SYK("Sykepenger"),
    SYM("Sykmeldinger"),
    TIL("Tiltak"),
    TRK("Trekkhåndtering"),
    TRY("Trygdeavgift"),
    TSO("Tilleggsstønad"),
    TSR("Tilleggsstønad arbeidssøkere"),
    UFM("Unntak fra medlemskap"),
    UFO("Uføretrygd"),
    UKJ("Ukjent"),
    VEN("Ventelønn"),
    YRA("Yrkesrettet attføring"),
    YRK("Yrkesskade")
}

data class DokumentInfo(val dokumentInfoId: String, val tittel: String?)

data class JournalPost(val journalpostId: String, val tittel: String?, val journalposttype: JournalPostType?, val journalstatus: JournalStatus?, val tema: Tema?, val datoOpprettet: LocalDateTime, val dokumenter: List<DokumentInfo>?)

data class Dokumentoversikt(val journalposter: List<JournalPost>)

data class Data(val dokumentoversiktBruker: Dokumentoversikt)

data class DokumentoversiktBrukerResponse(val data: Data)

open class GraphqlQuery(val query: String, val variables: Any? = null)

data class DokumentoversiktBrukerQuery(val fnr: String, val antallJournalPoster: Int) : GraphqlQuery(
        query = """ 
            query {
              dokumentoversiktBruker(
                    brukerId: {id: "$fnr", type: ${BrukerIdType.FNR}}, 
                    tema: [${Tema.MED}, ${Tema.UFM}, ${Tema.TRY}], 
                    journalstatuser: [${JournalStatus.MOTTATT}, ${JournalStatus.JOURNALFOERT}, ${JournalStatus.FERDIGSTILT}, ${JournalStatus.EKSPEDERT}, ${JournalStatus.UNDER_ARBEID}, ${JournalStatus.RESERVERT}, ${JournalStatus.OPPLASTING_DOKUMENT}, ${JournalStatus.UKJENT}], 
                    foerste: $antallJournalPoster) {
                journalposter {
                  journalpostId
                  tittel
                  journalposttype
                  journalstatus
                  tema
                  datoOpprettet
                  dokumenter {
                    dokumentInfoId
                    tittel
                  }
                }
              }
            }
            """.trimIndent(),
        variables = null
)
