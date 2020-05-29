package no.nav.medlemskap.services.aareg

import no.nav.medlemskap.domene.*


fun mapAaregResultat(arbeidsforhold: List<AaRegArbeidsforhold>, dataOmArbeidsgiver: MutableMap<String, AaRegService.ArbeidsgiverInfo>, dataOmPerson: MutableMap<String, String?>): List<Arbeidsforhold> {
    return arbeidsforhold.map {
        Arbeidsforhold(
                periode = mapPeriodeTilArbeidsforhold(it),
                utenlandsopphold = mapUtenLandsopphold(it),
                arbeidsfolholdstype = mapArbeidsForholdType(it),
                arbeidsgivertype = it.arbeidsgiver.type,
                arbeidsgiver = mapArbeidsgiver(it, dataOmArbeidsgiver, dataOmPerson),
                arbeidsavtaler = mapArbeidsAvtaler(it)

        )
    }

}

fun mapPeriodeTilArbeidsforhold(arbeidsforhold: AaRegArbeidsforhold): Periode {
    return Periode(
            fom = arbeidsforhold.ansettelsesperiode.periode.fom,
            tom = arbeidsforhold.ansettelsesperiode.periode.tom)

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

fun mapArbeidsForholdType(arbeidsforhold: AaRegArbeidsforhold): Arbeidsforholdstype {
    when (arbeidsforhold.type) {
        "maritimtArbeidsforhold" -> return Arbeidsforholdstype.MARITIM
        "forenkletOppgjoersordning" -> return Arbeidsforholdstype.FORENKLET
        "frilanserOppdragstakerHonorarPersonerMm" -> return Arbeidsforholdstype.FRILANSER
        "ordinaertArbeidsforhold" -> return Arbeidsforholdstype.NORMALT
        else -> {
            return Arbeidsforholdstype.ANDRE
        }
    }
}

fun mapArbeidsgiver(arbeidsforhold: AaRegArbeidsforhold, dataOmArbeidsgiver: MutableMap<String, AaRegService.ArbeidsgiverInfo>, dataOmPerson: MutableMap<String, String?>): Arbeidsgiver {
    val enhetstype = dataOmArbeidsgiver[arbeidsforhold.arbeidsgiver.organisasjonsnummer]?.arbeidsgiverEnhetstype
    val ansatte = dataOmArbeidsgiver[arbeidsforhold.arbeidsgiver.organisasjonsnummer]?.ansatte
    val arbeidsgiversLand = dataOmPerson[arbeidsforhold.arbeidsgiver.offentligIdent
            ?: arbeidsforhold.arbeidsgiver.aktoerId]
    val konkursStatus = dataOmArbeidsgiver[arbeidsforhold.arbeidsgiver.organisasjonsnummer]?.konkursStatus
    return Arbeidsgiver(
            type = enhetstype,
            landkode = arbeidsgiversLand,
            ansatte = ansatte,
            konkursStatus = konkursStatus)
}

fun mapPeriodeTilArbeidsavtale(arbeidsavtale: AaRegArbeidsavtale): Periode {
    return Periode(
            fom = arbeidsavtale.bruksperiode.fom.toLocalDate(),
            tom = arbeidsavtale.bruksperiode.tom?.toLocalDate())

}

fun mapUtenLandsopphold(arbeidsforhold: AaRegArbeidsforhold): List<Utenlandsopphold>? {
    return arbeidsforhold.utenlandsopphold?.map {
        Utenlandsopphold(
                landkode = it.landkode,
                periode = mapPeriodeTilUtenlandsopphold(it),
                rapporteringsperiode = it.rapporteringsperiode)

    }

}


fun mapPeriodeTilUtenlandsopphold(utenlandsopphold: AaRegUtenlandsopphold): Periode {
    return Periode(
            fom = utenlandsopphold.periode?.fom,
            tom = utenlandsopphold.periode?.tom)
}
