package no.nav.medlemskap.services.aareg

import mu.KotlinLogging
import no.nav.medlemskap.clients.aareg.AaRegArbeidsavtale
import no.nav.medlemskap.clients.aareg.AaRegArbeidsforhold
import no.nav.medlemskap.clients.aareg.AaRegOpplysningspliktigArbeidsgiverType
import no.nav.medlemskap.clients.aareg.AaRegUtenlandsopphold
import no.nav.medlemskap.domene.*
import no.nav.medlemskap.services.ereg.mapOrganisasjonTilArbeidsgiver

private val logger = KotlinLogging.logger { }

fun mapAaregResultat(arbeidsforhold: List<AaRegArbeidsforhold>, mappedOrganisasjonAsArbeidsgiver: List<Arbeidsgiver>): List<Arbeidsforhold> {
    return arbeidsforhold.map {
        Arbeidsforhold(
            periode = mapPeriodeFraArbeidsforhold(it),
            utenlandsopphold = mapUtenlandsopphold(it),
            arbeidsforholdstype = mapArbeidsforholdType(it),
            arbeidsgivertype = mapArbeidsgiverType(it.arbeidsgiver.type),
            arbeidsgiver = mappedOrganisasjonAsArbeidsgiver.first { p -> p.organisasjonsnummer == it.arbeidsgiver.organisasjonsnummer },
            arbeidsavtaler = mapArbeidsAvtaler(it)
        )
    }
}

fun mapArbeidsforhold(arbeidsforholdMedOrganisasjon: List<ArbeidsforholdOrganisasjon>): List<Arbeidsforhold> {
    return arbeidsforholdMedOrganisasjon.map {
        Arbeidsforhold(
            periode = mapPeriodeFraArbeidsforhold(it.arbeidsforhold),
            utenlandsopphold = mapUtenlandsopphold(it.arbeidsforhold),
            arbeidsforholdstype = mapArbeidsforholdType(it.arbeidsforhold),
            arbeidsgivertype = mapArbeidsgiverType(it.arbeidsforhold.arbeidsgiver.type),
            arbeidsgiver = mapOrganisasjonTilArbeidsgiver(it.organisasjon, it.juridiskeEnheter),
            arbeidsavtaler = mapArbeidsAvtaler(it.arbeidsforhold)
        )
    }
}

fun mapArbeidsgiverType(type: AaRegOpplysningspliktigArbeidsgiverType): OpplysningspliktigArbeidsgiverType =
    when (type) {
        AaRegOpplysningspliktigArbeidsgiverType.Person -> OpplysningspliktigArbeidsgiverType.Person
        AaRegOpplysningspliktigArbeidsgiverType.Organisasjon -> OpplysningspliktigArbeidsgiverType.Organisasjon
    }

fun mapPeriodeFraArbeidsforhold(arbeidsforhold: AaRegArbeidsforhold): Periode {
    return Periode(
        fom = arbeidsforhold.ansettelsesperiode.periode.fom,
        tom = arbeidsforhold.ansettelsesperiode.periode.tom
    )
}

fun mapArbeidsAvtaler(arbeidsforhold: AaRegArbeidsforhold): List<Arbeidsavtale> {
    return arbeidsforhold.arbeidsavtaler.map {
        Arbeidsavtale(
            periode = mapPeriodeTilArbeidsavtale(it),
            gyldighetsperiode = Periode(it.gyldighetsperiode.fom, it.gyldighetsperiode.tom),
            skipsregister = Skipsregister.fraSkipsregisterVerdi(it.skipsregister),
            beregnetAntallTimerPrUke = it.beregnetAntallTimerPrUke,
            stillingsprosent = it.stillingsprosent,
            yrkeskode = it.yrke
        )
    }
}

fun mapArbeidsforholdType(arbeidsforhold: AaRegArbeidsforhold): Arbeidsforholdstype {
    return when (arbeidsforhold.type) {
        "maritimtArbeidsforhold" -> Arbeidsforholdstype.MARITIM
        "forenkletOppgjoersordning" -> Arbeidsforholdstype.FORENKLET
        "frilanserOppdragstakerHonorarPersonerMm" -> Arbeidsforholdstype.FRILANSER
        "ordinaertArbeidsforhold" -> Arbeidsforholdstype.NORMALT
        else -> {
            Arbeidsforholdstype.ANDRE
        }
    }
}

fun mapPeriodeTilArbeidsavtale(arbeidsavtale: AaRegArbeidsavtale): Periode {
    return Periode(
        fom = arbeidsavtale.bruksperiode.fom.toLocalDate(),
        tom = arbeidsavtale.bruksperiode.tom?.toLocalDate()
    )
}

fun mapUtenlandsopphold(arbeidsforhold: AaRegArbeidsforhold): List<Utenlandsopphold>? {
    return arbeidsforhold.utenlandsopphold?.map {
        Utenlandsopphold(
            landkode = it.landkode,
            periode = mapPeriodeTilUtenlandsopphold(it),
            rapporteringsperiode = it.rapporteringsperiode
        )
    }
}

fun mapPeriodeTilUtenlandsopphold(utenlandsopphold: AaRegUtenlandsopphold): Periode {
    return Periode(
        fom = utenlandsopphold.periode?.fom,
        tom = utenlandsopphold.periode?.tom
    )
}
