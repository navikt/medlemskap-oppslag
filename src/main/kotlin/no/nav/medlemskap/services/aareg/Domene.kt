package no.nav.medlemskap.services.aareg

import java.time.LocalDate
import java.time.LocalDateTime
import java.time.YearMonth


data class Ansettelsesperiode(
        val bruksperiode: Bruksperiode,
        val periode: Periode,
        val sporingsinformasjon: Sporingsinformasjon,
        val varslingskode: String?
)

data class AntallTimerForTimeloennet(
        val antallTimer: Double,
        val periode: Periode?,
        val rapporteringsperiode: YearMonth,
        val sporingsinformasjon: Sporingsinformasjon
)

data class Arbeidsavtale(
        val antallTimerPrUke: Double?,
        val arbeidstidsordning: String?,
        val beregnetAntallTimerPrUke: Double?,
        val bruksperiode: Bruksperiode,
        val gyldighetsperiode: Gyldighetsperiode,
        val sistLoennsendring: String?,
        val sistStillingsendring: String?,
        val sporingsinformasjon: Sporingsinformasjon,
        val stillingsprosent: Double?,
        val yrke: String
)

data class Arbeidsforhold(
        val ansettelsesperiode: Ansettelsesperiode,
        val antallTimerForTimeloennet: AntallTimerForTimeloennet,
        val arbeidsavtaler: List<Arbeidsavtale>,
        val arbeidsforholdId: String,
        val arbeidsgiver: OpplysningspliktigArbeidsgiver,
        val arbeidstaker: Person,
        val innrapportertEtterAOrdningen: Boolean,
        val navArbeidsforholdId: Int,
        val opplysningspliktig: OpplysningspliktigArbeidsgiver,
        val permisjonPermitteringer: PermisjonPermittering,
        val registrert: LocalDateTime,
        val sistBekreftet: LocalDateTime?,
        val sporingsinformasjon: Sporingsinformasjon,
        val type: String,
        val utenlandsopphold: List<Utenlandsopphold>
)

data class ArbeidsgiverArbeidsforhold(
        val antall: Int,
        val arbeidsforhold: List<Arbeidsforhold>
)

data class Bruksperiode(
        val fom: LocalDateTime,
        val tom: LocalDateTime?
)

enum class OpplysningspliktigArbeidsgiverType {
    Organisasjon, Person
}

data class Gyldighetsperiode(
        val fom: LocalDate,
        val tom: LocalDate?
)

data class OpplysningspliktigArbeidsgiver(
        val type: OpplysningspliktigArbeidsgiverType
)

enum class OrganisasjonType {
    Organisasjon
}


data class Organisasjon(
        val type: OrganisasjonType,
        val organisasjonsnummer: String
)

data class Periode(
        val fom: LocalDate?,
        val tom: LocalDate?
)

data class PermisjonPermittering(
        val periode: Periode?,
        val permisjonPermitteringId: String,
        val prosent: Double?,
        val sporingsinformasjon: Sporingsinformasjon,
        val type: String
)

enum class PersonType {
    Person
}

data class Person(
        val type: PersonType,
        val aktoerId: String,
        val offentligIdent: String
)

data class Sporingsinformasjon(
        val endretAv: String,
        val endretKilde: String,
        val endretKildeReferanse: String,
        val endretTidspunkt: LocalDateTime,
        val opprettetAv: String,
        val opprettetKilde: String,
        val opprettetKildereferanse: String,
        val opprettetTidspunkt: LocalDateTime
)

data class TjenestefeilResponse(
        val melding: String
)

data class Utenlandsopphold(
        val landkode: String,
        val periode: Periode?,
        val rapporteringsperiode: YearMonth,
        val sporingsinformasjon: Sporingsinformasjon
)







