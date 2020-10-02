package no.nav.medlemskap.services.ereg

import no.nav.medlemskap.clients.ereg.Organisasjon
import no.nav.medlemskap.domene.Arbeidsgiver
import no.nav.medlemskap.services.aareg.mapAnsatte

fun mapOrganisasjonTilArbeidsgiver(organisasjon: Organisasjon, juridiskEnhetOrgnummerEnhetstypeMap: Map<String, String?>): Arbeidsgiver {
    val enhetstype = organisasjon.organisasjonDetaljer?.enhetstyper?.first()?.enhetstype
    val ansatte = organisasjon.organisasjonDetaljer?.ansatte
    val konkursStatus = organisasjon.organisasjonDetaljer?.statuser?.map { it -> it?.kode }
    return Arbeidsgiver(
        type = enhetstype,
        organisasjonsnummer = organisasjon.organisasjonsnummer,
        ansatte = mapAnsatte(ansatte),
        konkursStatus = konkursStatus,
        juridiskEnhetEnhetstypeMap = juridiskEnhetOrgnummerEnhetstypeMap
    )
}
