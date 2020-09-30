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
            arbeidsgiver = mappedOrganisasjonAsArbeidsgiver.first { p -> p.identifikator == it.arbeidsgiver.organisasjonsnummer },
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
            arbeidsgiver = mapOrganisasjonTilArbeidsgiver(it.organisasjon, it.juridiskeEnhetstyper),
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
            skipsregister = mapSkipsregister(it),
            stillingsprosent = it.stillingsprosent,
            yrkeskode = it.yrke
        )
    }
}

fun mapSkipsregister(arbeidsavtale: AaRegArbeidsavtale): Skipsregister {
    logger.info("Mapper skipsregister {}", arbeidsavtale.skipsregister)
    when (arbeidsavtale.skipsregister) {
        "nis" -> return Skipsregister.NIS
        "nor" -> return Skipsregister.NOR
        "utl" -> return Skipsregister.UTL
        "NIS" -> return Skipsregister.NIS
        "NOR" -> return Skipsregister.NOR
        "UTL" -> return Skipsregister.UTL
        else -> {
            return Skipsregister.UKJENT
        }
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

fun mapAnsatte(ansatte: List<no.nav.medlemskap.clients.ereg.Ansatte>?): List<Ansatte>? =
    ansatte?.map {
        Ansatte(
            antall = it.antall,
            bruksperiode = mapBruksperiode(it.bruksperiode),
            gyldighetsperiode = mapGyldighetsperiode(it.gyldighetsperiode)
        )
    }

fun mapGyldighetsperiode(gyldighetsperiode: no.nav.medlemskap.clients.ereg.Gyldighetsperiode?): Gyldighetsperiode? =
    gyldighetsperiode?.let { Gyldighetsperiode(fom = it.fom, tom = it.tom) }

fun mapBruksperiode(bruksperiode: no.nav.medlemskap.clients.ereg.Bruksperiode?): Bruksperiode? =
    bruksperiode?.let { Bruksperiode(fom = it.fom, tom = it.tom) }

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
