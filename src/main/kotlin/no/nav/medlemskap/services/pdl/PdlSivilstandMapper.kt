package no.nav.medlemskap.services.pdl

import no.nav.medlemskap.client.generated.pdl.HentPerson
import no.nav.medlemskap.clients.pdl.Sivilstandstype
import no.nav.medlemskap.client.generated.pdl.HentPerson.Folkeregistermetadata
import no.nav.medlemskap.common.exceptions.DetteSkalAldriSkje
import no.nav.medlemskap.services.pdl.PdlMapper.mapFolkeregisterMetadata
import java.time.LocalDate
import java.time.format.DateTimeFormatter


object PdlSivilstandMapper {

    fun mapSivilstander(pdlSivilstander: List<HentPerson.Sivilstand>): List<no.nav.medlemskap.domene.Sivilstand> {
        val sivilstander =
                pdlSivilstander.filter {
                    it.type != HentPerson.Sivilstandstype.UGIFT && it.type != HentPerson.Sivilstandstype.UOPPGITT
                }.sortedBy {
                    convertToLocalDate(it.gyldigFraOgMed) ?: LocalDate.MIN
                }



            if (sivilstander.size < 2) {
                return sivilstander.map {
                    mapSivilstander(it)
                }
            }



            return sivilstander
                    .zipWithNext { sivilstand, neste ->
                        mapSivilstander(sivilstand, convertToLocalDate(neste.gyldigFraOgMed)?.minusDays(1))
                    }.plus(mapSivilstander(sivilstander.last()))


    }

    fun convertToLocalDate(dateToConvert: String?): LocalDate? {
        return LocalDate.parse(dateToConvert, DateTimeFormatter.ofPattern("yyyy-MM-dd"));
    }

    private fun mapSivilstander(sivilstand: HentPerson.Sivilstand, gyldigTilOgMed: LocalDate? = null): no.nav.medlemskap.domene.Sivilstand{
        return no.nav.medlemskap.domene.Sivilstand(
                type = mapSivilstandType(sivilstand.type),
                gyldigFraOgMed = convertToLocalDate(sivilstand.gyldigFraOgMed),
                gyldigTilOgMed = gyldigTilOgMed,
                relatertVedSivilstand = sivilstand.relatertVedSivilstand,
                folkeregistermetadata = mapFolkeregisterMetadata(sivilstand.folkeregistermetadata)
        )
    }

    private fun mapSivilstandType(type: HentPerson.Sivilstandstype): no.nav.medlemskap.domene.Sivilstandstype {
        return when (type) {
            Sivilstandstype.GIFT -> no.nav.medlemskap.domene.Sivilstandstype.GIFT
            Sivilstandstype.ENKE_ELLER_ENKEMANN -> no.nav.medlemskap.domene.Sivilstandstype.ENKE_ELLER_ENKEMANN
            Sivilstandstype.SKILT -> no.nav.medlemskap.domene.Sivilstandstype.SKILT
            Sivilstandstype.SEPARERT -> no.nav.medlemskap.domene.Sivilstandstype.SEPARERT
            Sivilstandstype.REGISTRERT_PARTNER -> no.nav.medlemskap.domene.Sivilstandstype.REGISTRERT_PARTNER
            Sivilstandstype.SEPARERT_PARTNER -> no.nav.medlemskap.domene.Sivilstandstype.SEPARERT_PARTNER
            Sivilstandstype.SKILT_PARTNER -> no.nav.medlemskap.domene.Sivilstandstype.SKILT_PARTNER
            Sivilstandstype.GJENLEVENDE_PARTNER -> no.nav.medlemskap.domene.Sivilstandstype.GJENLEVENDE_PARTNER
            else -> throw DetteSkalAldriSkje("Denne sivilstandstypen skal være filtrert bort")
        }
    }
}