package no.nav.medlemskap.services.aareg

import no.nav.medlemskap.domene.*


fun mapAaregResultat(arbeidsforhold: List<AaRegArbeidsforhold>): List<Arbeidsforhold> {
    return  arbeidsforhold.map {
        Arbeidsforhold(
                periode = mapPeriodeTilArbeidsforhold(it),
                utenlandsopphold = mapUtenLandsopphold(it),
                arbeidsfolholdstype = mapArbeidsForholdType(it),
                arbeidsgiver = mapArbeidsgiver(it),
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

fun mapSkipsregister(arbeidsavtale: AaRegArbeidsavtale): Skipsregister? {
    when (arbeidsavtale.skipsregister) {
        "nis" -> return Skipsregister.nis
        "nor" -> return Skipsregister.nor
        "utl" -> return Skipsregister.utl
        else -> {
            return null
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
//TODO --> LANDEKODE (BRUKES FOR Å TEST)
fun mapArbeidsgiver(arbeidsforhold: AaRegArbeidsforhold): Arbeidsgiver {
    return Arbeidsgiver(arbeidsforhold.type, arbeidsforhold.opplysningspliktig.organisasjonsnummer, "NOR");

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
