package no.nav.medlemskap.cucumber

import io.cucumber.datatable.DataTable
import no.nav.medlemskap.cucumber.Domenebegrep.*
import no.nav.medlemskap.cucumber.mapping.pdl.aareg.AaregDomenespraakParser
import no.nav.medlemskap.domene.*
import no.nav.medlemskap.domene.barn.DataOmBarn
import no.nav.medlemskap.domene.barn.PersonhistorikkBarn
import no.nav.medlemskap.domene.ektefelle.PersonhistorikkEktefelle
import no.nav.medlemskap.regler.common.RegelId
import no.nav.medlemskap.regler.common.Svar
import java.time.LocalDate
import java.time.YearMonth

object DomenespråkParser : BasisDomeneParser() {
    val ANSATTE_9 = listOf(Ansatte(9, null, null))
    val VANLIG_NORSK_ARBEIDSGIVER = Arbeidsgiver(type = "BEDR", organisasjonsnummer = "1", ansatte = ANSATTE_9, konkursStatus = null, juridiskeEnheter = null)

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

    fun mapArbeidsforhold(
        dataTable: DataTable?,
        utenlandsopphold: List<Utenlandsopphold> = emptyList(),
        arbeidsgiver: Arbeidsgiver = VANLIG_NORSK_ARBEIDSGIVER
    ): List<Arbeidsforhold> {
        if (dataTable == null) {
            return emptyList()
        }

        return dataTable.asMaps().map { ArbeidsforholdMapper().mapRad(it, utenlandsopphold, arbeidsgiver) }
    }

    fun parseAarMaaned(domenebegrep: Domenebegrep, rad: Map<String, String>): YearMonth {
        return YearMonth.parse(rad.get(domenebegrep.nøkkel)!!)
    }

    fun parseSkipsregister(rad: Map<String, String>): Skipsregister? {
        val verdi = valgfriVerdi(SKIPSREGISTER.nøkkel, rad)

        return if (verdi == null) null else Skipsregister.valueOf(verdi)
    }

    fun parseArbeidsforholdstype(rad: Map<String, String>): Arbeidsforholdstype {
        val verdi = verdi(ARBEIDSFORHOLDSTYPE.nøkkel, rad)

        return Arbeidsforholdstype.valueOf(verdi)
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

    fun mapStatsborgerskap(dataTable: DataTable?): List<Statsborgerskap> {
        return mapDataTable(dataTable, StatsborgerskapMapper())
    }

    fun mapPeriodeIArbeidsforhold(dataTable: DataTable?): Periode {
        return mapDataTable(dataTable, PeriodeMapper())[0]
    }

    fun mapTypeIArbeidsforhold(dataTable: DataTable?): String {
        return mapDataTable(dataTable, TypeMapper())[0]
    }

    fun mapOrganisasjonsnummer(dataTable: DataTable?): String {
        return mapDataTable(dataTable, OrganisasjonsnummerMapper())[0]
    }

    fun mapStillingsprosent(dataTable: DataTable?): Double {
        return mapDataTable(dataTable, StillingsprosentMapper())[0]
    }

    fun mapAnsatte(dataTable: DataTable?): List<Ansatte> {
        return mapDataTable(dataTable, AnsattMapper())
    }

    fun mapArbeidsgivertype(dataTable: DataTable?): OpplysningspliktigArbeidsgiverType {
        return mapDataTable(dataTable, ArbeidsgivertypeMapper())[0]
    }

    fun mapArbeidsforholdstype(dataTable: DataTable?): Arbeidsforholdstype {
        return mapDataTable(dataTable, ArbeidsforholdtypeMapper())[0]
    }

    fun mapBeregnetAntallTimerUke(dataTable: DataTable?): Double {
        return mapDataTable(dataTable, BeregnetAntallTimerPerUkeMapper())[0]
    }

    fun mapYrkeskode(dataTable: DataTable?): String {
        return mapDataTable(dataTable, YrkeskodeMapper())[0]
    }

    fun mapAdresser(dataTable: DataTable?): List<Adresse> {
        return mapDataTable(dataTable, AdresseMapper())
    }

    fun mapSivilstander(dataTable: DataTable?): List<Sivilstand> {
        return mapDataTable(dataTable, SivilstandMapper())
    }

    fun mapFamilierelasjoner(dataTable: DataTable?): List<Familierelasjon> {
        return mapDataTable(dataTable, FamilieRelasjonMapper())
    }

    fun mapStatuser(dataTable: DataTable?): List<String> {
        return mapDataTable(dataTable, StatusMapper())
    }

    fun mapSkipsregister(dataTable: DataTable?): Skipsregister {
        return mapDataTable(dataTable, SkipsregisterMapper())[0]
    }

    fun mapPersonhistorikkEktefelle(dataTable: DataTable?): List<PersonhistorikkEktefelle> {
        return mapDataTable(dataTable, PersonhistorikkEktefelleMapper())
    }

    fun mapBarnTilEktefelle(dataTable: DataTable?): List<String> {
        return mapDataTable(dataTable, BarnTilEktefelleMapper())
    }

    fun mapPersonhistorikkBarn(dataTable: DataTable?): List<DataOmBarn> {
        return mapDataTable(dataTable, PersonhistorikkBarnMapper())
    }

    fun mapMedlemskap(dataTable: DataTable?): List<Medlemskap> {
        return mapDataTable(dataTable, MedlemskapMapper())
    }

    fun mapArbeidsgivere(dataTable: DataTable?): List<Arbeidsgiver> {
        return mapDataTable(dataTable, ArbeidsgiverMapper())
    }

    fun mapArbeidsavtaler(dataTable: DataTable?): List<Arbeidsavtale> {
        return mapDataTable(dataTable, ArbeidsavtaleMapper())
    }

    fun mapUtenlandsopphold(dataTable: DataTable?): List<Utenlandsopphold> {
        return mapDataTable(dataTable, UtenlandsoppholdMapper())
    }

    fun mapOppgaverFraGosys(dataTable: DataTable?): List<Oppgave> {
        return mapDataTable(dataTable, OppgaveMapper())
    }

    fun mapDoedsfall(dataTable: DataTable?): List<LocalDate> {
        return mapDataTable(dataTable, DoedsfallMapper())
    }

    fun mapJournalposter(dataTable: DataTable?): List<Journalpost> {
        return mapDataTable(dataTable, JournalpostMapper())
    }

    fun mapMedlemskapsparametre(dataTable: DataTable?): Medlemskapsparametre {
        return mapDataTable(dataTable, MedlemskapsparametreMapper()).get(0)
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

    fun mapPeriodeIArbeidsavtale(dataTable: DataTable?): Periode {
        return mapDataTable(dataTable, PeriodeMapper())[0]
    }

    fun mapLandkode(dataTable: DataTable?): String {
        return mapDataTable(dataTable, LandkodeMapper())[0]
    }

    fun mapPeriodeIUtenlandsopphold(dataTable: DataTable?): Periode {
        return mapDataTable(dataTable, PeriodeMapper())[0]
    }

    fun mapRapporteringsperiode(dataTable: DataTable?): YearMonth {
        return mapDataTable(dataTable, RapporteringsperiodeMapper())[0]
    }

    class RapporteringsperiodeMapper : RadMapper<YearMonth> {
        override fun mapRad(rad: Map<String, String>): YearMonth {
            return YearMonth.parse(parseString(AaregDomenespraakParser.Domenebegrep.RAPPORTERINGSPERIODE, rad))
        }
    }

    class LandkodeMapper : RadMapper<String> {
        override fun mapRad(rad: Map<String, String>): String {
            return parseString(LANDKODE, rad)
        }
    }

    class YrkeskodeMapper : RadMapper<String> {
        override fun mapRad(rad: Map<String, String>): String {
            return parseString(YRKESKODE, rad)
        }
    }

    class BeregnetAntallTimerPerUkeMapper : RadMapper<Double> {
        override fun mapRad(rad: Map<String, String>): Double {
            return parseDouble(BEREGNET_ANTALL_TIMER_PR_UKE, rad)
        }
    }
    class TypeMapper : RadMapper<String> {
        override fun mapRad(rad: Map<String, String>): String {
            return parseString(ARBEIDSGIVERTYPE, rad)
        }
    }

    class OrganisasjonsnummerMapper : RadMapper<String> {
        override fun mapRad(rad: Map<String, String>): String {
            return parseString(ORGANISASJONSNUMMER, rad)
        }
    }

    class RegelIdMapper : RadMapper<RegelId> {
        override fun mapRad(rad: Map<String, String>): RegelId {

            return parseRegelId(REGEL, rad)
        }
    }

    class StatusMapper : RadMapper<String> {
        override fun mapRad(rad: Map<String, String>): String {
            return parseString(STATUS, rad)
        }
    }

    class SkipsregisterMapper : RadMapper<Skipsregister> {
        override fun mapRad(rad: Map<String, String>): Skipsregister {
            return Skipsregister.valueOf(parseString(SKIPSREGISTER, rad))
        }
    }

    class AnsattMapper : RadMapper<Ansatte> {
        override fun mapRad(rad: Map<String, String>): Ansatte {
            return Ansatte(
                parseInt(ANTALL_ANSATTE, rad),
                Bruksperiode(
                    parseDato(BRUKSPERIODE_GYLDIG_FRA, rad),
                    parseDato(BRUKSPERIODE_GYLDIG_TIL, rad)
                ),
                Gyldighetsperiode(
                    parseDato(GYLDIGHETSPERIODE_FRA_OG_MED, rad),
                    parseDato(GYLDIGHETSPERIODE_TIL_OG_MED, rad)
                )
            )
        }
    }

    class StatsborgerskapMapper : RadMapper<Statsborgerskap> {
        override fun mapRad(rad: Map<String, String>): Statsborgerskap {
            return Statsborgerskap(
                parseString(LANDKODE, rad),
                parseValgfriDato(FRA_OG_MED_DATO, rad),
                parseValgfriDato(TIL_OG_MED_DATO, rad)
            )
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

    class PeriodeMapper : RadMapper<Periode> {
        override fun mapRad(rad: Map<String, String>): Periode {
            return Periode(
                parseValgfriDato(FRA_OG_MED_DATO, rad),
                parseValgfriDato(TIL_OG_MED_DATO, rad)
            )
        }
    }

    class DoedsfallMapper : RadMapper<LocalDate> {
        override fun mapRad(rad: Map<String, String>): LocalDate {
            return parseDato(DOEDSDATO, rad)
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

    class AdresseMapper : RadMapper<Adresse> {
        override fun mapRad(rad: Map<String, String>): Adresse {
            return Adresse(
                parseString(LANDKODE, rad),
                parseValgfriDato(FRA_OG_MED_DATO, rad),
                parseValgfriDato(TIL_OG_MED_DATO, rad)
            )
        }
    }

    class StillingsprosentMapper : RadMapper<Double> {
        override fun mapRad(rad: Map<String, String>): Double {
            return parseDouble(STILLINGSPROSENT, rad)
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

    class ArbeidsgivertypeMapper : RadMapper<OpplysningspliktigArbeidsgiverType> {
        override fun mapRad(rad: Map<String, String>): OpplysningspliktigArbeidsgiverType {
            return OpplysningspliktigArbeidsgiverType.valueOf(parseString(ARBEIDSGIVERTYPE, rad))
        }
    }

    class ArbeidsforholdtypeMapper : RadMapper<Arbeidsforholdstype> {
        override fun mapRad(rad: Map<String, String>): Arbeidsforholdstype {
            return Arbeidsforholdstype.valueOf(parseString(ARBEIDSFORHOLDSTYPE, rad))
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

    class ArbeidsforholdMapper {
        fun mapRad(
            rad: Map<String, String>,
            utenlandsopphold: List<Utenlandsopphold> = emptyList(),
            arbeidsgiver: Arbeidsgiver?
        ): Arbeidsforhold {
            val periode = Periode(
                parseValgfriDato(FRA_OG_MED_DATO, rad),
                parseValgfriDato(TIL_OG_MED_DATO, rad)
            )

            return Arbeidsforhold(
                periode = periode,
                utenlandsopphold = utenlandsopphold,
                arbeidsgivertype = OpplysningspliktigArbeidsgiverType.valueOf(parseString(ARBEIDSGIVERTYPE, rad)),
                arbeidsgiver = arbeidsgiver ?: VANLIG_NORSK_ARBEIDSGIVER,
                arbeidsforholdstype = parseArbeidsforholdstype(rad),
                arbeidsavtaler = emptyList()
            )
        }
    }

    class ArbeidsavtaleMapper : RadMapper<Arbeidsavtale> {
        override fun mapRad(rad: Map<String, String>): Arbeidsavtale {
            return Arbeidsavtale(
                Periode(
                    parseDato(FRA_OG_MED_DATO, rad),
                    parseValgfriDato(TIL_OG_MED_DATO, rad)
                ),
                Periode(
                    parseDato(FRA_OG_MED_DATO, rad),
                    parseValgfriDato(TIL_OG_MED_DATO, rad)
                ),
                parseString(YRKESKODE, rad),
                parseSkipsregister(rad),
                parseDouble(STILLINGSPROSENT, rad),
                parseValgfriDouble(BEREGNET_ANTALL_TIMER_PR_UKE, rad)
            )
        }
    }

    class ArbeidsgiverMapper : RadMapper<Arbeidsgiver> {
        override fun mapRad(rad: Map<String, String>): Arbeidsgiver {
            val konkursStatus = parseValgfriString(KONKURSSTATUS, rad)
            val konkursStatuser = if (konkursStatus == null) {
                null
            } else {
                listOf(konkursStatus)
            }

            return Arbeidsgiver(
                organisasjonsnummer = parseValgfriString(IDENTIFIKATOR, rad),
                type = parseValgfriString(ARBEIDSGIVERTYPE, rad),
                ansatte = listOf(Ansatte(parseValgfriInt(ANTALL_ANSATTE, rad), null, null)),
                konkursStatus = konkursStatuser,
                juridiskeEnheter = listOf(JuridiskEnhet(parseValgfriString(IDENTIFIKATOR, rad), parseValgfriString(JURIDISKENHETSTYPE, rad), parseValgfriInt(ANTALL_ANSATTE_I_JURIDISK_ENHET, rad)))
            )
        }
    }

    class UtenlandsoppholdMapper : RadMapper<Utenlandsopphold> {
        override fun mapRad(rad: Map<String, String>): Utenlandsopphold {
            return Utenlandsopphold(
                landkode = parseString(LANDKODE, rad),
                periode = Periode(
                    parseDato(FRA_OG_MED_DATO, rad),
                    parseDato(TIL_OG_MED_DATO, rad)
                ),
                rapporteringsperiode = parseAarMaaned(RAPPORTERINGSPERIODE, rad)
            )
        }
    }

    class PersonhistorikkEktefelleMapper : RadMapper<PersonhistorikkEktefelle> {
        override fun mapRad(rad: Map<String, String>): PersonhistorikkEktefelle {
            val fraOgMedDato = parseValgfriDato(FRA_OG_MED_DATO, rad)
            val tilOgMedDato = parseValgfriDato(TIL_OG_MED_DATO, rad)

            val bostedsadresser = mutableListOf<Adresse>()
            val bostedsadresse = parseValgfriString(BOSTED, rad)
            if (bostedsadresse != null) {
                bostedsadresser.add(Adresse(bostedsadresse, fraOgMedDato, tilOgMedDato))
            }

            val kontaktadresser = mutableListOf<Adresse>()
            val kontaktadresse = parseValgfriString(KONTAKTADRESSE, rad)
            if (kontaktadresse != null) {
                kontaktadresser.add(Adresse(kontaktadresse, fraOgMedDato, tilOgMedDato))
            }

            val oppholdsadresser = mutableListOf<Adresse>()
            val oppholdsadresse = parseValgfriString(OPPHOLDSADRESSE, rad)
            if (oppholdsadresse != null) {
                oppholdsadresser.add(Adresse(oppholdsadresse, fraOgMedDato, tilOgMedDato))
            }

            return PersonhistorikkEktefelle(
                ident = parseString(IDENT, rad),
                barn = mutableListOf<String>(),
                bostedsadresser = bostedsadresser,
                kontaktadresser = kontaktadresser,
                oppholdsadresser = oppholdsadresser

            )
        }
    }

    class PersonhistorikkBarnMapper : RadMapper<DataOmBarn> {
        override fun mapRad(rad: Map<String, String>): DataOmBarn {
            val fraOgMedDato = parseValgfriDato(FRA_OG_MED_DATO, rad)
            val tilOgMedDato = parseValgfriDato(TIL_OG_MED_DATO, rad)

            val bostedsadresser = mutableListOf<Adresse>()
            val bostedsadresse = parseValgfriString(BOSTED, rad)
            if (bostedsadresse != null) {
                bostedsadresser.add(Adresse(bostedsadresse, fraOgMedDato, tilOgMedDato))
            }

            val kontaktadresser = mutableListOf<Adresse>()
            val kontaktadresse = parseValgfriString(KONTAKTADRESSE, rad)
            if (kontaktadresse != null) {
                kontaktadresser.add(Adresse(kontaktadresse, fraOgMedDato, tilOgMedDato))
            }

            val oppholdsadresser = mutableListOf<Adresse>()
            val oppholdsadresse = parseValgfriString(OPPHOLDSADRESSE, rad)
            if (oppholdsadresse != null) {
                oppholdsadresser.add(Adresse(oppholdsadresse, fraOgMedDato, tilOgMedDato))
            }

            return DataOmBarn(
                PersonhistorikkBarn(
                    ident = parseString(IDENT, rad),
                    familierelasjoner = mutableListOf<Familierelasjon>(),
                    bostedsadresser = bostedsadresser,
                    kontaktadresser = kontaktadresser,
                    oppholdsadresser = oppholdsadresser

                )
            )
        }
    }

    class SivilstandMapper : RadMapper<Sivilstand> {
        override fun mapRad(rad: Map<String, String>): Sivilstand {
            return Sivilstand(
                type = parseSivilstandstype(SIVILSTANDSTYPE, rad),
                gyldigFraOgMed = parseValgfriDato(GYLDIG_FRA_OG_MED, rad),
                gyldigTilOgMed = parseValgfriDato(GYLDIG_TIL_OG_MED, rad),
                relatertVedSivilstand = parseValgfriString(RELATERT_VED_SIVILSTAND, rad)
            )
        }
    }

    class FamilieRelasjonMapper : RadMapper<Familierelasjon> {
        override fun mapRad(rad: Map<String, String>): Familierelasjon {
            return Familierelasjon(
                relatertPersonsIdent = parseString(RELATERT_PERSONS_IDENT, rad),
                relatertPersonsRolle = parseRolle(RELATERT_PERSONS_ROLLE, rad),
                minRolleForPerson = parseValgfriRolle(MIN_ROLLE_FOR_PERSON, rad),
                folkeregistermetadata = null
            )
        }
    }

    class BarnTilEktefelleMapper : RadMapper<String> {
        override fun mapRad(rad: Map<String, String>): String {
            return String()
        }
    }
}

enum class Domenebegrep(val nøkkel: String) : Domenenøkkel {
    ADRESSE("Adresse"),
    BOSTED("Bosted"),
    AKTIV_DATO("Aktiv dato"),
    ANTALL_ANSATTE("Antall ansatte"),
    ANTALL_ANSATTE_I_JURIDISK_ENHET("Antall ansatte i juridisk enhet"),
    ARBEIDSFORHOLDSTYPE("Arbeidsforholdstype"),
    ARBEIDSGIVER_ID("Arbeidsgiver Id"),
    ARBEIDSGIVERTYPE("Arbeidsgivertype"),
    BEREGNET_ANTALL_TIMER_PR_UKE("Beregnet antall timer pr uke"),
    BRUKSPERIODE_GYLDIG_FRA("Bruksperiode gyldig fra"),
    BRUKSPERIODE_GYLDIG_TIL("Bruksperiode gyldig til"),
    DEKNING("Dekning"),
    DOEDSDATO("Dødsdato"),
    ER_MEDLEM("Er medlem"),
    FRA_OG_MED_DATO("Fra og med dato"),
    FØRSTE_DAG_FOR_YTELSE("Første dag for ytelse"),
    FØDSELSNUMMER("Fødselsnummer"),
    GYLDIG_FRA_OG_MED("Gyldig fra og med dato"),
    GYLDIG_TIL_OG_MED("Gyldig til og med dato"),
    GYLDIGHETSPERIODE_FRA_OG_MED("Gyldighetsperiode gyldig fra"),
    GYLDIGHETSPERIODE_TIL_OG_MED("Bruksperiode gyldig til"),
    HAR_HATT_ARBEID_UTENFOR_NORGE("Har hatt arbeid utenfor Norge"),
    IDENT("Ident"),
    IDENTIFIKATOR("Identifikator"),
    JOURNAL_STATUS("Journalstatus"),
    JOURNALPOST_ID("JournalpostId"),
    JOURNALPOST_TYPE("JournalpostType"),
    KONKURSSTATUS("Konkursstatus"),
    KONTAKTADRESSE("Kontaktadresse"),
    LANDKODE("Landkode"),
    LOVVALG("Lovvalg"),
    LOVVALGSLAND("Lovvalgsland"),
    MIN_ROLLE_FOR_PERSON("Min rolle for person"),
    OPPHOLDSADRESSE("Oppholdsadresse"),
    ORGANISASJONSNUMMER("Organisasjonsnummer"),
    PERIODESTATUS("Periodestatus"),
    PRIORITET("Prioritet"),
    REGEL("Regel"),
    RELATERT_PERSONS_IDENT("Relatert persons ident"),
    RELATERT_PERSONS_ROLLE("Relatert persons rolle"),
    RELATERT_VED_SIVILSTAND("Relatert ved sivilstand"),
    RAPPORTERINGSPERIODE("Rapporteringsperiode"),
    SIVILSTANDSTYPE("Sivilstandstype"),
    SKIPSREGISTER("Skipsregister"),
    STATUS("Status"),
    STILLINGSPROSENT("Stillingsprosent"),
    SVAR("Svar"),
    TEMA("Tema"),
    TIL_OG_MED_DATO("Til og med dato"),
    TITTEL("Tittel"),
    YRKESKODE("Yrkeskode"),
    JURIDISKENHETSTYPE("Juridisk enhetstype"),
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
