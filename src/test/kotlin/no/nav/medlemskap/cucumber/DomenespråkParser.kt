package no.nav.medlemskap.cucumber

import io.cucumber.datatable.DataTable
import no.nav.medlemskap.cucumber.Domenebegrep.*
import no.nav.medlemskap.cucumber.DomenespråkParser.Companion.VANLIG_NORSK_ARBEIDSGIVER
import no.nav.medlemskap.domene.*
import no.nav.medlemskap.regler.common.Datohjelper
import no.nav.medlemskap.regler.common.RegelId
import no.nav.medlemskap.regler.common.Svar
import no.nav.medlemskap.services.aareg.AaRegOpplysningspliktigArbeidsgiverType
import no.nav.medlemskap.services.ereg.Ansatte
import java.time.LocalDate
import java.time.YearMonth

class DomenespråkParser {

    fun parseDato(domenebegrep: Domenebegrep, rad: Map<String, String>): LocalDate {
        return Datohjelper.parseDato(verdi(domenebegrep.nøkkel, rad))
    }

    fun parseValgfriDato(domenebegrep: Domenebegrep, rad: Map<String, String?>): LocalDate? {
        if (rad.get(domenebegrep.nøkkel) == null || rad.get(domenebegrep.nøkkel) == "") {
            return null
        }

        return Datohjelper.parseDato(rad.get(domenebegrep.nøkkel)!!)
    }

    fun parseString(domenebegrep: Domenebegrep, rad: Map<String, String>): String {
        return verdi(domenebegrep.nøkkel, rad)
    }

    fun parseValgfriString(domenebegrep: Domenebegrep, rad: Map<String, String>): String? {
        return valgfriVerdi(domenebegrep.nøkkel, rad)
    }

    fun parseBoolean(domenebegrep: Domenebegrep, rad: Map<String, String>): Boolean {
        val verdi = verdi(domenebegrep.nøkkel, rad)

        return when (verdi) {
            "Ja" -> true
            else -> false
        }
    }

    fun parseValgfriYtelse(domenebegrep: Domenebegrep, rad: Map<String, String>): Ytelse? {
        val valgfriVerdi = valgfriVerdi(domenebegrep.nøkkel, rad)

        return if (valgfriVerdi == null) {
            null
        } else {
            Ytelse.valueOf(valgfriVerdi)
        }
    }

    fun parseRegelId(regelIdStr: String): RegelId {
        val regelId = RegelId.fraRegelIdString(regelIdStr)
        if (regelId == null) {
            throw RuntimeException("Fant ikke regel med id = {regelIdStr}")
        }

        return regelId
    }

    fun parseSvar(verdi: String): Svar {
        return when (verdi) {
            "Ja" -> Svar.JA
            "Nei" -> Svar.NEI
            else -> Svar.UAVKLART
        }
    }

    private fun verdi(nøkkel: String, rad: Map<String, String>): String {
        val verdi = rad.get(nøkkel)

        if (verdi == null || verdi == "") {
            throw java.lang.RuntimeException("Fant ingen verdi for $nøkkel")
        }

        return verdi
    }

    private fun valgfriVerdi(nøkkel: String, rad: Map<String, String>): String? {
        val verdi = rad.get(nøkkel)

        return verdi
    }

    fun <T> mapDataTable(dataTable: DataTable?, radMapper: RadMapper<T>): List<T> {
        if (dataTable == null) {
            return emptyList()
        }

        return dataTable.asMaps().map { radMapper.mapRad(this, it) }
    }

    fun mapArbeidsforhold(
            dataTable: DataTable?,
            utenlandsopphold: List<Utenlandsopphold> = emptyList(),
            arbeidsgiver: Arbeidsgiver = VANLIG_NORSK_ARBEIDSGIVER): List<Arbeidsforhold> {
        if (dataTable == null) {
            return emptyList()
        }

        return dataTable.asMaps().map { ArbeidsforholdMapper().mapRad(this, it, utenlandsopphold, arbeidsgiver) }
    }

    fun parseAarMaaned(domenebegrep: Domenebegrep, rad: Map<String, String>): YearMonth {
        return YearMonth.parse(rad.get(domenebegrep.nøkkel)!!)
    }

    fun parseInt(domenebegrep: Domenebegrep, rad: Map<String, String>): Int {
        val verdi = verdi(domenebegrep.nøkkel, rad)

        return Integer.parseInt(verdi)
    }

    fun parseDouble(domenebegrep: Domenebegrep, rad: Map<String, String>): Double {
        val verdi = verdi(domenebegrep.nøkkel, rad)
        return verdi.toDouble()
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

    fun parseValgfriInt(domenebegrep: Domenebegrep, rad: Map<String, String>): Int? {
        val verdi = valgfriVerdi(domenebegrep.nøkkel, rad)
        if (verdi == null) {
            return null
        }

        return parseInt(domenebegrep, rad)
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

    companion object {
        val ANSATTE_9 = listOf(Ansatte(9, null, null))
        val STATSBORGERSKAP_NOR = listOf(Statsborgerskap("NOR",LocalDate.MIN , null))
        val VANLIG_NORSK_ARBEIDSGIVER = Arbeidsgiver(type = "BEDR", identifikator = "1", statsborgerskap = STATSBORGERSKAP_NOR, ansatte = ANSATTE_9, konkursStatus = null)

    }

}

interface RadMapper<T> {
    fun mapRad(domenespråkParser: DomenespråkParser, rad: Map<String, String>): T

}

class StatsborgerskapMapper : RadMapper<Statsborgerskap> {
    override fun mapRad(domenespråkParser: DomenespråkParser, rad: Map<String, String>): Statsborgerskap {
        return Statsborgerskap(
                domenespråkParser.parseString(LANDKODE, rad),
                domenespråkParser.parseDato(FRA_OG_MED_DATO, rad),
                domenespråkParser.parseValgfriDato(TIL_OG_MED_DATO, rad)
        )
    }
}

class InputPeriodeMapper : RadMapper<InputPeriode> {
    override fun mapRad(domenespråkParser: DomenespråkParser, rad: Map<String, String>): InputPeriode {
        return InputPeriode(
                domenespråkParser.parseDato(FRA_OG_MED_DATO, rad),
                domenespråkParser.parseDato(TIL_OG_MED_DATO, rad)
        )
    }
}

class MedlemskapsparametreMapper : RadMapper<Medlemskapsparametre> {
    override fun mapRad(domenespråkParser: DomenespråkParser, rad: Map<String, String>): Medlemskapsparametre {
        return Medlemskapsparametre(
                InputPeriode(
                        domenespråkParser.parseDato(FRA_OG_MED_DATO, rad),
                        domenespråkParser.parseDato(TIL_OG_MED_DATO, rad)
                ),
                domenespråkParser.parseBoolean(HAR_HATT_ARBEID_UTENFOR_NORGE, rad),
                domenespråkParser.parseValgfriYtelse(YTELSE, rad)
        )
    }
}

class AdresseMapper : RadMapper<Adresse> {
    override fun mapRad(domenespråkParser: DomenespråkParser, rad: Map<String, String>): Adresse {
        return Adresse(
                domenespråkParser.parseString(LANDKODE, rad),
                domenespråkParser.parseValgfriDato(FRA_OG_MED_DATO, rad),
                domenespråkParser.parseValgfriDato(TIL_OG_MED_DATO, rad)
        )
    }
}


class MedlemskapMapper : RadMapper<Medlemskap> {
    override fun mapRad(domenespråkParser: DomenespråkParser, rad: Map<String, String>): Medlemskap {
        return Medlemskap(
                domenespråkParser.parseValgfriString(DEKNING, rad),
                domenespråkParser.parseDato(FRA_OG_MED_DATO, rad),
                domenespråkParser.parseDato(TIL_OG_MED_DATO, rad),
                domenespråkParser.parseBoolean(ER_MEDLEM, rad),
                domenespråkParser.parseValgfriString(LOVVALG, rad),
                domenespråkParser.parseValgfriString(LOVVALGSLAND, rad),
                domenespråkParser.parseValgfriString(PERIODESTATUS, rad)
        )
    }
}

class OppgaveMapper : RadMapper<Oppgave> {
    override fun mapRad(domenespråkParser: DomenespråkParser, rad: Map<String, String>): Oppgave {
        return Oppgave(
                aktivDato = domenespråkParser.parseDato(AKTIV_DATO, rad),
                prioritet = domenespråkParser.parsePrioritet(PRIORITET, rad),
                status = domenespråkParser.parseStatus(STATUS, rad),
                tema = domenespråkParser.parseValgfriString(TEMA, rad)
        )
    }
}

class JournalpostMapper : RadMapper<Journalpost> {
    override fun mapRad(domenespråkParser: DomenespråkParser, rad: Map<String, String>): Journalpost {
        return Journalpost(
                domenespråkParser.parseString(JOURNALPOST_ID, rad),
                domenespråkParser.parseValgfriString(TITTEL, rad),
                domenespråkParser.parseValgfriString(JOURNALPOST_TYPE, rad),
                domenespråkParser.parseValgfriString(JOURNAL_STATUS, rad),
                domenespråkParser.parseValgfriString(TEMA, rad),
                null
        )
    }
}

class ArbeidsforholdMapper {
    fun mapRad(domenespråkParser: DomenespråkParser,
               rad: Map<String, String>,
               utenlandsopphold: List<Utenlandsopphold> = emptyList(),
               arbeidsgiver: Arbeidsgiver?
    ): Arbeidsforhold {
        val periode = Periode(
                domenespråkParser.parseValgfriDato(FRA_OG_MED_DATO, rad),
                domenespråkParser.parseValgfriDato(TIL_OG_MED_DATO, rad))
        return Arbeidsforhold(
                periode = periode,
                utenlandsopphold = utenlandsopphold,
                arbeidsgivertype = AaRegOpplysningspliktigArbeidsgiverType.valueOf(domenespråkParser.parseString(ARBEIDSGIVERTYPE, rad)),
                arbeidsgiver = arbeidsgiver ?: VANLIG_NORSK_ARBEIDSGIVER,
                arbeidsfolholdstype = domenespråkParser.parseArbeidsforholdstype(rad),
                arbeidsavtaler = emptyList()
        )
    }
}

class ArbeidsavtaleMapper : RadMapper<Arbeidsavtale> {
    override fun mapRad(domenespråkParser: DomenespråkParser, rad: Map<String, String>): Arbeidsavtale {
        return Arbeidsavtale(
                Periode(
                        domenespråkParser.parseDato(FRA_OG_MED_DATO, rad),
                        domenespråkParser.parseValgfriDato(TIL_OG_MED_DATO, rad)),
                domenespråkParser.parseString(YRKESKODE, rad),
                domenespråkParser.parseSkipsregister(rad),
                domenespråkParser.parseDouble(STILLINGSPROSENT, rad)
        )
    }
}

class ArbeidsgiverMapper : RadMapper<Arbeidsgiver> {
    override fun mapRad(domenespråkParser: DomenespråkParser, rad: Map<String, String>): Arbeidsgiver {
        val konkursStatus = domenespråkParser.parseValgfriString(KONKURSSTATUS, rad)
        val konkursStatuser = if (konkursStatus == null) {
            null
        } else {
            listOf(konkursStatus)
        }

        return Arbeidsgiver(
                identifikator = domenespråkParser.parseValgfriString(IDENTIFIKATOR, rad),
                type = domenespråkParser.parseValgfriString(ARBEIDSGIVERTYPE, rad),
                statsborgerskap = listOf(Statsborgerskap(domenespråkParser.parseString(LANDKODE, rad), LocalDate.MIN, null)),
                ansatte = listOf(Ansatte(domenespråkParser.parseValgfriInt(ANTALL_ANSATTE, rad), null, null)),
                konkursStatus = konkursStatuser
        )
    }
}

class UtenlandsoppholdMapper : RadMapper<Utenlandsopphold> {
    override fun mapRad(domenespråkParser: DomenespråkParser, rad: Map<String, String>): Utenlandsopphold {
        return Utenlandsopphold(
                landkode = domenespråkParser.parseString(LANDKODE, rad),
                periode = Periode(
                        domenespråkParser.parseDato(FRA_OG_MED_DATO, rad),
                        domenespråkParser.parseDato(TIL_OG_MED_DATO, rad)),
                rapporteringsperiode = domenespråkParser.parseAarMaaned(RAPPORTERINGSPERIODE, rad)
        )
    }
}

class PersonstatusMapper : RadMapper<FolkeregisterPersonstatus> {
    override fun mapRad(domenespråkParser: DomenespråkParser, rad: Map<String, String>): FolkeregisterPersonstatus {
        return FolkeregisterPersonstatus(
                personstatus = PersonStatus.valueOf(domenespråkParser.parseString(PERSONSTATUS, rad)),
                fom = domenespråkParser.parseValgfriDato(FRA_OG_MED_DATO, rad),
                tom = domenespråkParser.parseValgfriDato(TIL_OG_MED_DATO, rad)
        )
    }
}

class PersonhistorikkRelatertePersonerMapper : RadMapper<PersonhistorikkRelatertPerson> {
    override fun mapRad(domenespråkParser: DomenespråkParser, rad: Map<String, String>): PersonhistorikkRelatertPerson {
        val fraOgMedDato = domenespråkParser.parseValgfriDato(FRA_OG_MED_DATO, rad)
        val tilOgMedDato = domenespråkParser.parseValgfriDato(TIL_OG_MED_DATO, rad)

        val bostedsadresser = mutableListOf<Adresse>()
        val bostedsadresse = domenespråkParser.parseValgfriString(BOSTED, rad)
        if (bostedsadresse != null) {
            bostedsadresser.add(Adresse(bostedsadresse, fraOgMedDato, tilOgMedDato))
        }

        val postadresser = mutableListOf<Adresse>()
        val postadresse = domenespråkParser.parseValgfriString(POSTADRESSE, rad)
        if (postadresse != null) {
            postadresser.add(Adresse(postadresse, fraOgMedDato, tilOgMedDato))
        }

        val midlertidigAdresser = mutableListOf<Adresse>()
        val midlertidigAdresse = domenespråkParser.parseValgfriString(MIDLERTIDIG_ADRESSE, rad)
        if (midlertidigAdresse != null) {
            midlertidigAdresser.add(Adresse(midlertidigAdresse, fraOgMedDato, tilOgMedDato))
        }

        return PersonhistorikkRelatertPerson(
                ident = domenespråkParser.parseString(IDENT, rad),
                personstatuser = emptyList(),
                bostedsadresser = bostedsadresser,
                postadresser = postadresser,
                midlertidigAdresser = midlertidigAdresser
        )
    }
}

class SivilstandMapper: RadMapper<Sivilstand> {
    override fun mapRad(domenespråkParser: DomenespråkParser, rad: Map<String, String>): Sivilstand {
        return Sivilstand(
                type = domenespråkParser.parseSivilstandstype(SIVILSTANDSTYPE, rad),
                gyldigFraOgMed = domenespråkParser.parseValgfriDato(GYLDIG_FRA_OG_MED, rad),
                gyldigTilOgMed = domenespråkParser.parseValgfriDato(GYLDIG_TIL_OG_MED, rad),
                relatertVedSivilstand = domenespråkParser.parseValgfriString(RELATERT_VED_SIVILSTAND, rad),
                folkeregistermetadata = null
        )
    }
}

class FamilieRelasjonMapper: RadMapper<Familierelasjon> {
    override fun mapRad(domenespråkParser: DomenespråkParser, rad: Map<String, String>): Familierelasjon {
        return Familierelasjon(
                relatertPersonsIdent = domenespråkParser.parseString(RELATERT_PERSONS_IDENT, rad),
                relatertPersonsRolle = domenespråkParser.parseRolle(RELATERT_PERSONS_ROLLE, rad),
                minRolleForPerson = domenespråkParser.parseValgfriRolle(MIN_ROLLE_FOR_PERSON, rad),
                folkeregistermetadata = null
        )
    }
}

enum class Domenebegrep(val nøkkel: String) {
    ADRESSE("Adresse"),
    BOSTED("Bosted"),
    AKTIV_DATO("Aktiv dato"),
    ANTALL_ANSATTE("Antall ansatte"),
    ARBEIDSFORHOLDSTYPE("Arbeidsforholdstype"),
    ARBEIDSGIVER_ID("Arbeidsgiver Id"),
    ARBEIDSGIVERTYPE("Arbeidsgivertype"),
    DEKNING("Dekning"),
    ER_MEDLEM("Er medlem"),
    FRA_OG_MED_DATO("Fra og med dato"),
    GYLDIG_FRA_OG_MED("Gyldig fra og med dato"),
    GYLDIG_TIL_OG_MED("Gyldig til og med dato"),
    HAR_HATT_ARBEID_UTENFOR_NORGE("Har hatt arbeid utenfor Norge"),
    IDENT("Ident"),
    IDENTIFIKATOR("Identifikator"),
    JOURNAL_STATUS("Journalstatus"),
    JOURNALPOST_ID("JournalpostId"),
    JOURNALPOST_TYPE("JournalpostType"),
    KONKURSSTATUS("Konkursstatus"),
    LANDKODE("Landkode"),
    LOVVALG("Lovvalg"),
    LOVVALGSLAND("Lovvalgsland"),
    MIDLERTIDIG_ADRESSE("Midlertidig adresse"),
    MIN_ROLLE_FOR_PERSON("Min rolle for person"),
    PERIODESTATUS("Periodestatus"),
    PERSONSTATUS("Personstatus"),
    POSTADRESSE("Postadresse"),
    PRIORITET("Prioritet"),
    RELATERT_PERSONS_IDENT("Relatert persons ident"),
    RELATERT_PERSONS_ROLLE("Relatert persons rolle"),
    RELATERT_VED_SIVILSTAND("Relatert ved sivilstand"),
    RAPPORTERINGSPERIODE("Rapporteringsperiode"),
    SIVILSTANDSTYPE("Sivilstandstype"),
    SKIPSREGISTER("Skipsregister"),
    STATUS("Status"),
    STILLINGSPROSENT("Stillingsprosent"),
    TEMA("Tema"),
    TIL_OG_MED_DATO("Til og med dato"),
    TITTEL("Tittel"),
    YRKESKODE("Yrkeskode"),
    YTELSE("Ytelse")
}


data class Medlemskapsparametre(
        val inputPeriode: InputPeriode,
        val harHattArbeidUtenforNorge: Boolean,
        val ytelse: Ytelse?)


