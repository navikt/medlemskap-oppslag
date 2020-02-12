package no.nav.medlemskap.services.saf

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import java.time.LocalDateTime

data class BrukerIdInput(
        val id: String,
        val type: String = "FNR"
)

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

data class Location(val line: Int, val column: Int)

@JsonIgnoreProperties(ignoreUnknown=true)
data class Errors(val message: String, val locations: List<Location>, val path: List<String>?)

data class DokumentoversiktBrukerResponse(val data: Data, val errors: List<Errors>?)

data class GraphqlQuery(val query: String, val variables: Variables)

data class Variables(
        val brukerId: BrukerIdInput,
        val foerste: Int,
        val tema: List<Tema> = listOf(Tema.MED, Tema.UFM, Tema.TRY),
        val journalstatuser: List<JournalStatus> = listOf(JournalStatus.MOTTATT, JournalStatus.JOURNALFOERT, JournalStatus.FERDIGSTILT, JournalStatus.EKSPEDERT, JournalStatus.UNDER_ARBEID, JournalStatus.RESERVERT, JournalStatus.OPPLASTING_DOKUMENT, JournalStatus.UKJENT)
)

fun hentSafQuery(fnr: String, antallJournalPoster: Int): GraphqlQuery {
    val query = GraphqlQuery::class.java.getResource("/saf/dokumenter.graphql").readText().replace("[\n\r]", "")
    return GraphqlQuery(query, Variables(BrukerIdInput(fnr), antallJournalPoster))
}
