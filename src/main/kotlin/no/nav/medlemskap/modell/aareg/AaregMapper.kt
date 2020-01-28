package no.nav.medlemskap.modell.aareg

import no.nav.medlemskap.domene.*
import no.nav.medlemskap.domene.Arbeidsavtale
import no.nav.medlemskap.domene.Periode
import no.nav.medlemskap.domene.Utenlandsopphold


fun mapAaregResultat(arbeidsforhold: List<Arbeidsforhold>): List<no.nav.medlemskap.domene.Arbeidsforhold> {
    return  arbeidsforhold.map {Arbeidsforhold(
                 periode = mapPeriodeTilArbeidsforhold(it),
                 utenlandsopphold = mapUtenLandsopphold(it),
                 arbeidsfolholdstype = mapArbeidsForholdType(it),
                 arbeidsgiver = mapArbeidsgiver(it),
                 arbeidsavtaler = mapArbeidsAvtaler(it)

            )
    }

}

fun mapPeriodeTilArbeidsforhold(arbeidsforhold: Arbeidsforhold): Periode {
    return Periode(
            fom = arbeidsforhold.ansettelsesperiode.periode.fom,
            tom = arbeidsforhold.ansettelsesperiode.periode.tom)

}

fun mapArbeidsAvtaler(arbeidsforhold: Arbeidsforhold): List<Arbeidsavtale> {
    return arbeidsforhold.arbeidsavtaler.map {
                Arbeidsavtale(
                    periode = mapPeriodeTilArbeidsavtale(it),
                    skipsregister = mapSkipsregister(it),
                    stillingsprosent = it.stillingsprosent,
                    yrkeskode = it.yrke
                )
    }

}

fun mapSkipsregister(arbeidsavtale: no.nav.medlemskap.modell.aareg.Arbeidsavtale): Skipsregister? {
    when (arbeidsavtale.skipsregister) {
        "nis" -> return Skipsregister.nis
        "nor" -> return Skipsregister.nor
        else -> {
            return Skipsregister.utl
        }
    }
}

fun mapArbeidsForholdType(arbeidsforhold: Arbeidsforhold): Arbeidsforholdstype {
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

fun mapArbeidsgiver(arbeidsforhold: Arbeidsforhold): Arbeidsgiver {
    return Arbeidsgiver(arbeidsforhold.type, arbeidsforhold.opplysningspliktig.organisasjonsnummer, null);

}

fun mapPeriodeTilArbeidsavtale(arbeidsavtale: no.nav.medlemskap.modell.aareg.Arbeidsavtale): Periode {
    return Periode(
            fom = arbeidsavtale.bruksperiode.fom.toLocalDate(),
            tom = arbeidsavtale.bruksperiode.tom?.toLocalDate())

}

fun mapUtenLandsopphold(arbeidsforhold: Arbeidsforhold): List<Utenlandsopphold>? {
    return arbeidsforhold.utenlandsopphold?.map {
        Utenlandsopphold(
                landkode = it.landkode,
                periode = mapPeriodeTilUtenlandsopphold(it),
                rapporteringsperiode = it.rapporteringsperiode)

    }

}


fun mapPeriodeTilUtenlandsopphold(utenlandsopphold: no.nav.medlemskap.modell.aareg.Utenlandsopphold): Periode {
    return Periode(
            fom = utenlandsopphold.periode?.fom,
            tom = utenlandsopphold.periode?.tom)
}
