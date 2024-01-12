package no.nav.medlemskap.clients.aareg

import java.time.LocalDate
import java.time.LocalDateTime
import java.time.YearMonth

enum class Identtype {
    AKTORID, FOLKEREGISTERIDENT, ORGANISASJONSNUMMER
}

enum class Entitet {
    Arbeidsforhold, Ansettelsesperiode, Permisjon, Permittering
}

data class Ident(
    val type: Identtype,
    val ident: String,
    val gjeldende: Boolean?,
)

data class Identer(
    val identer: List<Ident>
)

data class Kodeverksentitet(
    val kode: String,
    val beskrivelse: String,
)

data class Arbeidssted(
    val type: String, // "Underenhet,Person"
    val identer: List<Ident>
)

data class Opplysningspliktig(
    val type: String, // "Hovedenhet,Person"
    val identer: List<Ident>
)

data class Ansettelsesperiode(
    val startdato: LocalDate,
    val sluttdato: LocalDate?,
    val sluttaarsak: Kodeverksentitet?,
    val varsling: Kodeverksentitet?,
    val sporingsinformasjon: Sporingsinformasjon?
)

data class Rapporteringsmaaneder(
    val fra: YearMonth,
    val til: YearMonth?,
)

data class Bruksperiode(
    val fom: String, // LocalDateTime
    val tom: String?, // LocalDateTime
)

data class Ansettelsesdetaljer(
    val type: String, // "Ordinaer,Maritim,Forenklet,Frilanser"
    val arbeidstidsordning: Kodeverksentitet?,
    val ansettelsesform: Kodeverksentitet?,
    val yrke: Kodeverksentitet,
    val antallTimerPrUke: Double?,
    val avtaltStillingsprosent: Double?,
    val sisteStillingsprosentendring: LocalDate?,
    val sisteLoennsendring: LocalDate?,
    val rapporteringsmaaneder: Rapporteringsmaaneder, // v1.gyldighetsperiode
    val sporingsinformasjon: Sporingsinformasjon?
)

data class Arbeidsforhold(
    val id: String?,
    val type: Kodeverksentitet,
    val arbeidstaker: Identer,
    val arbeidssted: Arbeidssted,
    val opplysningspliktig: Opplysningspliktig,
    val ansettelsesperiode: Ansettelsesperiode,
    val ansettelsesdetaljer: List<Ansettelsesdetaljer>,
    val permisjoner: List<PermisjonPermittering>?,
    val permitteringer: List<PermisjonPermittering>?,
    val timerMedTimeloenn: List<TimerMedTimeloenn>?,
    val utenlandsopphold: List<Utenlandsopphold>?,
    val idHistorikk: List<IdHistorikk>?,
    val varsler: List<Varsel>?,
    val rapporteringsordning: Kodeverksentitet,
    val navArbeidsforholdId: Int,
    val navVersjon: Int,
    val navUuid: String,
    val opprettet: LocalDateTime,
    val sistBekreftet: LocalDateTime,
    val sistEndret: LocalDateTime?,
    val bruksperiode: Bruksperiode,
    val sporingsinformasjon: Sporingsinformasjon?
)

data class Varsel(
    val entitet: Entitet,
    val varsling: Kodeverksentitet?
)

data class Utenlandsopphold(
    val land: Kodeverksentitet?,
    val startdato: LocalDate,
    val sluttdato: LocalDate?,
    val rapporteringsmaaneder: Rapporteringsmaaneder?,
    val sporingsinformasjon: Sporingsinformasjon?
)

data class TimerMedTimeloenn(
    val antall: Double,
    val startdato: String?,
    val sluttdato: String?,
    val rapporteringsmaaneder: Rapporteringsmaaneder?,
    val sporingsinformasjon: Sporingsinformasjon?
)

data class PermisjonPermittering(
    val id: String?,
    val type: Kodeverksentitet,
    val startdato: LocalDate,
    val sluttdato: LocalDate?,
    val prosent: Double,
    val varsling: Kodeverksentitet?,
    val idHistorikk: List<IdHistorikk>?,
    val sporingsinformasjon: Sporingsinformasjon?
)

data class IdHistorikk(
    val id: String,
    val bruksperiode: Bruksperiode
)

data class Sporingsinformasjon(
    val opprettetTidspunkt: LocalDateTime,
    val opprettetAv: String,
    val opprettetKilde: String,
    val opprettetKildereferanse: String,
    val endretTidspunkt: LocalDateTime,
    val endretAv: String,
    val endretKilde: String,
    val endretKildereferanse: String
)
