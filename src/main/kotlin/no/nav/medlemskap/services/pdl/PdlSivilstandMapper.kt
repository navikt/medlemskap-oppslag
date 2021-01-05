package no.nav.medlemskap.services.pdl

import no.nav.medlemskap.clients.pdl.generated.HentPerson
import no.nav.medlemskap.common.exceptions.DetteSkalAldriSkje
import no.nav.medlemskap.domene.personhistorikk.Sivilstand
import no.nav.medlemskap.domene.personhistorikk.Sivilstandstype
import no.nav.medlemskap.regler.common.Datohjelper.parseIsoDato
import java.time.LocalDate

object PdlSivilstandMapper {

    fun mapSivilstander(pdlSivilstander: List<HentPerson.Sivilstand>): List<Sivilstand> {
        val sivilstander =
            pdlSivilstander.filter {
                it.type != HentPerson.Sivilstandstype.UGIFT && it.type != HentPerson.Sivilstandstype.UOPPGITT
            }.sortedBy {
                parseIsoDato(it.gyldigFraOgMed) ?: LocalDate.MIN
            }

        if (sivilstander.size < 2) {
            return sivilstander.map {
                mapSivilstand(it)
            }
        }

        return sivilstander
            .zipWithNext { sivilstand, neste ->
                mapSivilstand(sivilstand, parseIsoDato(neste.gyldigFraOgMed)?.minusDays(1))
            }.plus(mapSivilstand(sivilstander.last()))
    }

    private fun mapSivilstand(sivilstand: HentPerson.Sivilstand, gyldigTilOgMed: LocalDate? = null): Sivilstand {
        val gyldigFraOgMed = if (sivilstand.gyldigFraOgMed == null) {
            parseIsoDato(sivilstand.bekreftelsesdato)
        } else {
            parseIsoDato(sivilstand.gyldigFraOgMed)
        }

        return Sivilstand(
            type = mapSivilstandType(sivilstand.type),
            gyldigFraOgMed = gyldigFraOgMed,
            gyldigTilOgMed = gyldigTilOgMed,
            relatertVedSivilstand = sivilstand.relatertVedSivilstand
        )
    }

    private fun mapSivilstandType(type: HentPerson.Sivilstandstype): Sivilstandstype {
        return when (type) {
            HentPerson.Sivilstandstype.GIFT -> Sivilstandstype.GIFT
            HentPerson.Sivilstandstype.ENKE_ELLER_ENKEMANN -> Sivilstandstype.ENKE_ELLER_ENKEMANN
            HentPerson.Sivilstandstype.SKILT -> Sivilstandstype.SKILT
            HentPerson.Sivilstandstype.SEPARERT -> Sivilstandstype.SEPARERT
            HentPerson.Sivilstandstype.REGISTRERT_PARTNER -> Sivilstandstype.REGISTRERT_PARTNER
            HentPerson.Sivilstandstype.SEPARERT_PARTNER -> Sivilstandstype.SEPARERT_PARTNER
            HentPerson.Sivilstandstype.SKILT_PARTNER -> Sivilstandstype.SKILT_PARTNER
            HentPerson.Sivilstandstype.GJENLEVENDE_PARTNER -> Sivilstandstype.GJENLEVENDE_PARTNER
            else -> throw DetteSkalAldriSkje("Denne sivilstandstypen skal være filtrert bort")
        }
    }
}
