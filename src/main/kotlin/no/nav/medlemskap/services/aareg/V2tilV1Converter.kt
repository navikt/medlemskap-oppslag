package no.nav.medlemskap.services.aareg

import no.nav.medlemskap.clients.aareg.Ansettelsesdetaljer
import no.nav.medlemskap.clients.aareg.Arbeidsforhold
import no.nav.medlemskap.clients.aareg.Opplysningspliktig
import no.nav.medlemskap.domene.Periode
import no.nav.medlemskap.domene.arbeidsforhold.*
import java.time.LocalDate
import java.time.YearMonth

fun List<Arbeidsforhold>.mapTilV1(): List<no.nav.medlemskap.domene.arbeidsforhold.Arbeidsforhold> {
    return this.map {
        no.nav.medlemskap.domene.arbeidsforhold.Arbeidsforhold(
            periode = mapAnsettelsesperiodeTilV1(it),
            utenlandsopphold = mapUtenlandsoppholdTilV1(it),
            arbeidsgivertype = mapOpplysningspliktigTypeTilV1(it.opplysningspliktig),
            arbeidsgiver = Arbeidsgiver(null, null, null, null),
            arbeidsforholdstype = mapArbeidsforholdstypeTilV1(it.ansettelsesdetaljer.sortedBy { it.rapporteringsmaaneder.fra }.last()),
            arbeidsavtaler = mapAnsettelsesdetaljerTilArbeidsavtaler(it.ansettelsesdetaljer),
            permisjonPermittering = mapPermisjonPermitteringTilV1(it)
        )
    }
}

private fun mapAnsettelsesperiodeTilV1(v2: Arbeidsforhold): Periode {
    return Periode(
        fom = v2.ansettelsesperiode.startdato,
        tom = v2.ansettelsesperiode.sluttdato
    )
}

private fun mapUtenlandsoppholdTilV1(v2: Arbeidsforhold): List<Utenlandsopphold>? {
    return v2.utenlandsopphold?.map {
        Utenlandsopphold(
            landkode = it.land?.kode ?: "null",
            periode = mapUtenlandsoppholdsperiodeTilV1(it),
            rapporteringsperiode = it.rapporteringsmaaneder?.fra
                ?: it.rapporteringsmaaneder?.til
                ?: YearMonth.of(LocalDate.EPOCH.year, LocalDate.EPOCH.month)
        )
    }
}

private fun mapUtenlandsoppholdsperiodeTilV1(v2: no.nav.medlemskap.clients.aareg.Utenlandsopphold): Periode {
    return Periode(
        fom = v2.startdato,
        tom = v2.sluttdato
    )
}

private fun mapOpplysningspliktigTypeTilV1(v2: Opplysningspliktig): OpplysningspliktigArbeidsgiverType {
    return when (v2.type) {
        "Hovedenhet" -> OpplysningspliktigArbeidsgiverType.Organisasjon
        "Person" -> OpplysningspliktigArbeidsgiverType.Person
        else -> OpplysningspliktigArbeidsgiverType.Person
    }
}

private fun mapArbeidsforholdstypeTilV1(v2: Ansettelsesdetaljer): Arbeidsforholdstype {
    return when (v2.type) {
        "Ordinaer" -> Arbeidsforholdstype.NORMALT
        "Maritim" -> Arbeidsforholdstype.MARITIMT
        "Forenklet" -> Arbeidsforholdstype.FORENKLET
        "Frilanser" -> Arbeidsforholdstype.FRILANSER
        else -> Arbeidsforholdstype.ANDRE
    }
}

private fun mapAnsettelsesdetaljerTilArbeidsavtaler(ansettelsesdetaljer: List<Ansettelsesdetaljer>): List<Arbeidsavtale> {
    return ansettelsesdetaljer.map {
        Arbeidsavtale(
            periode = Periode(null, null),
            gyldighetsperiode = Periode(null, null),
            yrkeskode = it.yrke.kode,
            skipsregister = null,
            fartsomraade = null,
            stillingsprosent = it.avtaltStillingsprosent,
            beregnetAntallTimerPrUke = it.antallTimerPrUke,
            skipstype = null
        )
    }
}

private fun mapPermisjonPermitteringTilV1(v2: Arbeidsforhold): List<PermisjonPermittering> {
    val permisjoner = v2.permisjoner?.flatMap { lagPermisjoner(v2.permisjoner) }
    val permittering = v2.permitteringer?.flatMap { lagPermitteringer(v2.permitteringer) }

    if (permisjoner == null || permittering == null)
        return listOf()

    return permisjoner.plus(permittering)
}

private fun lagPermisjoner(permisjoner: List<no.nav.medlemskap.clients.aareg.PermisjonPermittering>): List<PermisjonPermittering> {
    return permisjoner.map {
        PermisjonPermittering(
            periode = mapPermisjonPermitteringPeriode(it),
            permisjonPermitteringId = it.id ?: "null",
            prosent = it.prosent,
            type = mapPermisjonstypeTilV1(it),
            varslingskode = it.varsling?.kode
        )
    }
}

private fun lagPermitteringer(permitteringer: List<no.nav.medlemskap.clients.aareg.PermisjonPermittering>): List<PermisjonPermittering> {
    return permitteringer.map {
        PermisjonPermittering(
            periode = mapPermisjonPermitteringPeriode(it),
            permisjonPermitteringId = it.id ?: "null",
            prosent = it.prosent,
            type = mapPermisjonstypeTilV1(it),
            varslingskode = it.varsling?.kode
        )
    }
}

private fun mapPermisjonPermitteringPeriode(v2: no.nav.medlemskap.clients.aareg.PermisjonPermittering): Periode {
    return Periode(v2.startdato, v2.sluttdato)
}

private fun mapPermisjonstypeTilV1(v2: no.nav.medlemskap.clients.aareg.PermisjonPermittering): PermisjonPermitteringType {
    return PermisjonPermitteringType.fraPermisjonPermitteringVerdi(v2.type.kode)
}
