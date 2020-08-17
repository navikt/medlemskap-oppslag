package no.nav.medlemskap.services.pdl

import no.nav.medlemskap.common.exceptions.DetteSkalAldriSkje
import no.nav.medlemskap.domene.Sivilstand
import no.nav.medlemskap.services.pdl.PdlMapper.mapFolkeregisterMetadata
import java.time.LocalDate

import no.nav.medlemskap.services.pdl.Sivilstand as PdlSivilstand

object PdlSivilstandMapper {

    fun mapSivilstander(pdlSivilstander : List<PdlSivilstand>): List<Sivilstand> {
        val sivilstander = pdlSivilstander
                .filter { it.type != Sivilstandstype.UGIFT && it.type != Sivilstandstype.UOPPGITT }
                .sortedBy { it.gyldigFraOgMed ?: LocalDate.MIN }

        if (sivilstander.size < 2) {
            return sivilstander.map {
                mapSivilstander(it)
            }
        }

        return sivilstander
                .zipWithNext { sivilstand, neste ->
                    mapSivilstander(sivilstand, neste.gyldigFraOgMed?.minusDays(1))
                }.plus(mapSivilstander(sivilstander.last()))
    }

    private fun mapSivilstander(sivilstand: no.nav.medlemskap.services.pdl.Sivilstand, gyldigTilOgMed: LocalDate? = null): Sivilstand {
        return Sivilstand(
                type = mapSivilstandType(sivilstand.type),
                gyldigFraOgMed = sivilstand.gyldigFraOgMed,
                gyldigTilOgMed = gyldigTilOgMed,
                relatertVedSivilstand = sivilstand.relatertVedSivilstand,
                folkeregistermetadata = mapFolkeregisterMetadata(sivilstand.folkeregistermetadata)
        )
    }

    private fun mapSivilstandType(type: Sivilstandstype): no.nav.medlemskap.domene.Sivilstandstype {
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