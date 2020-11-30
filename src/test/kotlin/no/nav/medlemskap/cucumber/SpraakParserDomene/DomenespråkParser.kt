package no.nav.medlemskap.cucumber

import io.cucumber.datatable.DataTable
import no.nav.medlemskap.cucumber.Domenebegrep.*
import no.nav.medlemskap.domene.*
import no.nav.medlemskap.regler.common.RegelId
import no.nav.medlemskap.regler.common.Svar
import java.time.LocalDate
import java.time.YearMonth

object DomenespråkParser : BasisDomeneParser() {

    fun parseValgfriYtelse(domenebegrep: Domenebegrep, rad: Map<String, String>): Ytelse? {
        val valgfriVerdi = valgfriVerdi(domenebegrep.nøkkel, rad)

        return if (valgfriVerdi == null) {
            null
        } else {
            Ytelse.valueOf(valgfriVerdi)
        }
    }

    fun parseValgfriPeriodeStatus(domenebegrep: Domenebegrep, rad: Map<String, String>): PeriodeStatus? {
        val valgfriVerdi = valgfriVerdi(domenebegrep.nøkkel, rad)

        return if (valgfriVerdi == null) {
            null
        } else {
            PeriodeStatus.valueOf(valgfriVerdi)
        }
    }

    fun parseValgfriLovvalg(domenebegrep: Domenebegrep, rad: Map<String, String>): Lovvalg? {
        val valgfriVerdi = valgfriVerdi(domenebegrep.nøkkel, rad)

        return if (valgfriVerdi == null) {
            null
        } else {
            Lovvalg.valueOf(valgfriVerdi)
        }
    }

    fun parseRegelId(domenebegrep: Domenebegrep, rad: Map<String, String>): RegelId {
        val verdi = verdi(domenebegrep.nøkkel, rad)

        return parseRegelId(verdi)
    }

    fun parseRegelId(regelIdStr: String): RegelId {
        val regelId = RegelId.fraRegelIdString(regelIdStr)
        if (regelId == null) {
            throw RuntimeException("Fant ikke regel med id = {regelIdStr}")
        }

        return regelId
    }

    fun parseSvar(domenebegrep: Domenenøkkel, rad: Map<String, String>): Svar {
        val verdi = verdi(domenebegrep.nøkkel(), rad)
        return parseSvar(verdi)
    }

    fun parseValgfrittSvar(domenebegrep: Domenenøkkel, rad: Map<String, String>): Svar? {
        val valgfriVerdi = valgfriVerdi(domenebegrep.nøkkel(), rad)

        if (valgfriVerdi == null) {
            return null
        }

        return parseSvar(domenebegrep, rad)
    }

    fun parseSvar(verdi: String): Svar {
        return when (verdi.toUpperCase()) {
            "JA" -> Svar.JA
            "NEI" -> Svar.NEI
            else -> Svar.UAVKLART
        }
    }

    fun parseAarMaaned(domenebegrep: Domenebegrep, rad: Map<String, String>): YearMonth {
        return YearMonth.parse(rad.get(domenebegrep.nøkkel)!!)
    }

    fun parseSivilstandstype(domenebegrep: Domenebegrep, rad: Map<String, String>): Sivilstandstype {
        val verdi = verdi(domenebegrep.nøkkel, rad)
        return Sivilstandstype.valueOf(verdi)
    }

    fun parsePrioritet(domenebegrep: Domenebegrep, rad: Map<String, String>): Prioritet {
        val verdi = verdi(domenebegrep.nøkkel, rad)
        return Prioritet.valueOf(verdi)
    }

    fun parseStatus(domenebegrep: Domenebegrep, rad: Map<String, String>): Status {
        val verdi = verdi(domenebegrep.nøkkel, rad)
        return Status.valueOf(verdi)
    }

    fun parseRolle(domenebegrep: Domenebegrep, rad: Map<String, String>): Familierelasjonsrolle {
        val verdi = verdi(domenebegrep.nøkkel, rad)

        return Familierelasjonsrolle.valueOf(verdi)
    }

    fun parseValgfriRolle(domenebegrep: Domenebegrep, rad: Map<String, String>): Familierelasjonsrolle? {
        val verdi = valgfriVerdi(domenebegrep.nøkkel, rad)

        if (verdi == null) {
            return null
        }

        return Familierelasjonsrolle.valueOf(verdi)
    }

    fun mapMedlemskap(dataTable: DataTable?): List<Medlemskap> {
        return mapDataTable(dataTable, MedlemskapMapper())
    }

    fun mapOppgaverFraGosys(dataTable: DataTable?): List<Oppgave> {
        return mapDataTable(dataTable, OppgaveMapper())
    }

    fun mapJournalposter(dataTable: DataTable?): List<Journalpost> {
        return mapDataTable(dataTable, JournalpostMapper())
    }

    fun mapMedlemskapsparametre(dataTable: DataTable?): Medlemskapsparametre {
        return mapDataTable(dataTable, MedlemskapsparametreMapper()).get(0)
    }

    fun mapDekning(dataTable: DataTable?): String {
        return mapDataTable(dataTable, DekningMapper())[0]
    }

    fun mapFraOgMed(dataTable: DataTable?): LocalDate {
        return mapDataTable(dataTable, FraOgMedMapper())[0]
    }

    fun mapTilOgMed(dataTable: DataTable?): LocalDate {
        return mapDataTable(dataTable, TilOgMedMapper())[0]
    }

    fun mapErMedlem(dataTable: DataTable?): Boolean {
        return mapDataTable(dataTable, ErBrukerMedlemMapper())[0]
    }

    fun mapLovvalg(dataTable: DataTable?): Lovvalg {
        return mapDataTable(dataTable, LovvalgMapper())[0]
    }

    fun mapLovvalgsland(dataTable: DataTable?): String {
        return mapDataTable(dataTable, LovvalgslandMapper())[0]
    }

    fun mapPeriodeStatus(dataTable: DataTable?): PeriodeStatus {
        return mapDataTable(dataTable, PeriodeStatusMapper())[0]
    }

    fun mapOverstyrteRegler(dataTable: DataTable?): Map<RegelId, Svar> {
        val overstyrteRegler = mapDataTable(dataTable, OverstyrteReglerMapper())
        return overstyrteRegler
            .filter { it -> it.second != null }
            .map { Pair(it.first, it.second!!) }
            .toMap()
    }

    fun mapRegelId(dataTable: DataTable?): List<RegelId> {
        return mapDataTable(dataTable, RegelIdMapper())
    }

    fun mapInputPeriode(dataTable: DataTable?): InputPeriode? {
        return mapDataTable(dataTable, InputPeriodeMapper()).get(0)
    }

    class PeriodeStatusMapper : RadMapper<PeriodeStatus> {
        override fun mapRad(rad: Map<String, String>): PeriodeStatus {
            return PeriodeStatus.valueOf(parseString(PERIODESTATUS, rad))
        }
    }

    class ErBrukerMedlemMapper : RadMapper<Boolean> {
        override fun mapRad(rad: Map<String, String>): Boolean {
            return parseBoolean(ER_MEDLEM, rad)
        }
    }

    class LovvalgslandMapper : RadMapper<String> {
        override fun mapRad(rad: Map<String, String>): String {
            return parseString(LOVVALGSLAND, rad)
        }
    }

    class LovvalgMapper : RadMapper<Lovvalg> {
        override fun mapRad(rad: Map<String, String>): Lovvalg {
            return Lovvalg.valueOf(parseString(LOVVALG, rad))
        }
    }

    class RegelIdMapper : RadMapper<RegelId> {
        override fun mapRad(rad: Map<String, String>): RegelId {

            return parseRegelId(REGEL, rad)
        }
    }

    class DekningMapper : RadMapper<String> {
        override fun mapRad(rad: Map<String, String>): String {
            return parseString(MEDLEMSKAP_DEKNING, rad)
        }
    }

    class FraOgMedMapper : RadMapper<LocalDate> {
        override fun mapRad(rad: Map<String, String>): LocalDate {
            return parseDato(GYLDIG_FRA_OG_MED, rad)
        }
    }

    class TilOgMedMapper : RadMapper<LocalDate> {
        override fun mapRad(rad: Map<String, String>): LocalDate {
            return parseDato(GYLDIG_TIL_OG_MED, rad)
        }
    }

    class InputPeriodeMapper : RadMapper<InputPeriode> {
        override fun mapRad(rad: Map<String, String>): InputPeriode {
            return InputPeriode(
                parseDato(FRA_OG_MED_DATO, rad),
                parseDato(TIL_OG_MED_DATO, rad)
            )
        }
    }

    class MedlemskapsparametreMapper : RadMapper<Medlemskapsparametre> {
        override fun mapRad(rad: Map<String, String>): Medlemskapsparametre {
            return Medlemskapsparametre(
                fnr = parseValgfriString(FØDSELSNUMMER, rad),
                inputPeriode = InputPeriode(
                    parseDato(FRA_OG_MED_DATO, rad),
                    parseDato(TIL_OG_MED_DATO, rad)
                ),
                førsteDagForYtelse = parseValgfriDato(FØRSTE_DAG_FOR_YTELSE, rad),
                harHattArbeidUtenforNorge = parseBoolean(HAR_HATT_ARBEID_UTENFOR_NORGE, rad),
                ytelse = parseValgfriYtelse(YTELSE, rad)
            )
        }
    }

    class OverstyrteReglerMapper : RadMapper<Pair<RegelId, Svar?>> {
        override fun mapRad(rad: Map<String, String>): Pair<RegelId, Svar?> {
            return Pair(parseRegelId(REGEL, rad), parseValgfrittSvar(SVAR, rad))
        }
    }

    class MedlemskapMapper : RadMapper<Medlemskap> {
        override fun mapRad(rad: Map<String, String>): Medlemskap {
            return Medlemskap(
                parseValgfriString(DEKNING, rad),
                parseDato(FRA_OG_MED_DATO, rad),
                parseDato(TIL_OG_MED_DATO, rad),
                parseBoolean(ER_MEDLEM, rad),
                parseValgfriLovvalg(LOVVALG, rad),
                parseValgfriString(LOVVALGSLAND, rad),
                parseValgfriPeriodeStatus(PERIODESTATUS, rad)
            )
        }
    }

    class OppgaveMapper : RadMapper<Oppgave> {
        override fun mapRad(rad: Map<String, String>): Oppgave {
            return Oppgave(
                aktivDato = parseDato(AKTIV_DATO, rad),
                prioritet = parsePrioritet(PRIORITET, rad),
                status = parseStatus(STATUS, rad),
                tema = parseValgfriString(TEMA, rad)
            )
        }
    }

    class JournalpostMapper : RadMapper<Journalpost> {
        override fun mapRad(rad: Map<String, String>): Journalpost {
            return Journalpost(
                parseString(JOURNALPOST_ID, rad),
                parseValgfriString(TITTEL, rad),
                parseValgfriString(JOURNALPOST_TYPE, rad),
                parseValgfriString(JOURNAL_STATUS, rad),
                parseValgfriString(TEMA, rad),
                null
            )
        }
    }

    class PeriodeMapper : RadMapper<Periode> {
        override fun mapRad(rad: Map<String, String>): Periode {
            return Periode(
                parseValgfriDato(FRA_OG_MED_DATO, rad),
                parseValgfriDato(TIL_OG_MED_DATO, rad)
            )
        }
    }
}

enum class Domenebegrep(val nøkkel: String) : Domenenøkkel {
    ADRESSE("Adresse"),
    BOSTED("Bosted"),
    AKTIV_DATO("Aktiv dato"),
    ANTALL_ANSATTE("Antall ansatte"),
    ARBEIDSFORHOLDSTYPE("Arbeidsforholdstype"),
    ARBEIDSGIVER_ID("Arbeidsgiver Id"),
    ARBEIDSGIVERTYPE("Arbeidsgivertype"),
    BEREGNET_ANTALL_TIMER_PR_UKE("Beregnet antall timer pr uke"),
    DEKNING("Dekning"),
    DOEDSDATO("Dødsdato"),
    DOKUMENT_INFO_ID("DokumentInfoId"),
    ER_MEDLEM("Er medlem"),
    FRA_OG_MED_DATO("Fra og med dato"),
    FARTSOMRÅDE("Fartsområde"),
    FØRSTE_DAG_FOR_YTELSE("Første dag for ytelse"),
    FØDSELSNUMMER("Fødselsnummer"),
    GYLDIG_FRA_OG_MED("Gyldig fra og med dato"),
    GYLDIG_TIL_OG_MED("Gyldig til og med dato"),
    GYLDIGHETSPERIODE_FRA_OG_MED("Gyldighetsperiode gyldig fra"),
    GYLDIGHETSPERIODE_TIL_OG_MED("Gyldighetsperiode gyldig til"),
    HAR_HATT_ARBEID_UTENFOR_NORGE("Har hatt arbeid utenfor Norge"),
    IDENT("Ident"),
    IDENTIFIKATOR("Identifikator"),
    JOURNAL_STATUS("Journalstatus"),
    JOURNALPOST_ID("JournalpostId"),
    JOURNALPOST_TYPE("JournalpostType"),
    JURIDISK_ANTALL_ANSATTE("Antall ansatte i juridisk enhet"),
    JURIDISK_ENHETSTYPE("Juridisk enhetstype"),
    KONKURSSTATUS("Konkursstatus"),
    KONTAKTADRESSE("Kontaktadresse"),
    LANDKODE("Landkode"),
    LOVVALG("Lovvalg"),
    LOVVALGSLAND("Lovvalgsland"),
    MEDLEMSKAP_DEKNING("MedlemskapDekning"),
    MIN_ROLLE_FOR_PERSON("Min rolle for person"),
    JOURNAL_POST_ID("JournalpostId"),
    OPPHOLDSADRESSE("Oppholdsadresse"),
    ORGANISASJONSNUMMER("Organisasjonsnummer"),
    PERIODESTATUS("Periodestatus"),
    PRIORITET("Prioritet"),
    PROSENT("Prosent"),
    REGEL("Regel"),
    RELATERT_PERSONS_IDENT("Relatert persons ident"),
    RELATERT_PERSONS_ROLLE("Relatert persons rolle"),
    RELATERT_VED_SIVILSTAND("Relatert ved sivilstand"),
    RAPPORTERINGSPERIODE("Rapporteringsperiode"),
    SIVILSTANDSTYPE("Sivilstandstype"),
    STATUS("Status"),
    STILLINGSPROSENT("Stillingsprosent"),
    SVAR("Svar"),
    TEMA("Tema"),
    TIL_OG_MED_DATO("Til og med dato"),
    TITTEL("Tittel"),
    YRKESKODE("Yrkeskode"),
    YTELSE("Ytelse");

    override fun nøkkel(): String {
        return nøkkel
    }
}

data class Medlemskapsparametre(
    val fnr: String?,
    val inputPeriode: InputPeriode,
    val førsteDagForYtelse: LocalDate?,
    val harHattArbeidUtenforNorge: Boolean,
    val ytelse: Ytelse?
)
