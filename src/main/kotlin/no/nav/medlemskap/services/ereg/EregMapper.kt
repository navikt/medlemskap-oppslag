package no.nav.medlemskap.services.ereg

import no.nav.medlemskap.clients.ereg.Organisasjon
import no.nav.medlemskap.domene.*

fun mapOrganisasjonTilArbeidsgiver(organisasjon: Organisasjon, juridiskeEnheter: List<Organisasjon>?): Arbeidsgiver {
    val enhetstype = organisasjon.organisasjonDetaljer?.enhetstyper?.first()?.enhetstype
    val ansatte = organisasjon.organisasjonDetaljer?.ansatte
    val konkursStatus = organisasjon.organisasjonDetaljer?.statuser?.map { it -> it?.kode }
    return Arbeidsgiver(
        organisasjonsnummer = organisasjon.organisasjonsnummer,
        ansatte = mapAnsatte(ansatte),
        konkursStatus = konkursStatus,
        juridiskeEnheter = mapJuridiskeEnheter(juridiskeEnheter)
    )
}

fun mapJuridiskeEnheter(juridiskeEnheter: List<Organisasjon>?): List<JuridiskEnhet?>? {
    return juridiskeEnheter?.map {
        JuridiskEnhet(
            organisasjonsnummer = it.organisasjonsnummer,
            enhetstype = it.organisasjonDetaljer?.enhetstyper?.first()?.enhetstype,
            antallAnsatte = it.organisasjonDetaljer?.ansatte?.first()?.antall
        )
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
