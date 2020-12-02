package no.nav.medlemskap.domene

import no.nav.medlemskap.common.statsborgerskapUavklartForRegel
import no.nav.medlemskap.regler.common.RegelId
import java.time.LocalDate
import java.time.LocalDateTime

data class Personhistorikk(
    val statsborgerskap: List<Statsborgerskap>,
    val bostedsadresser: List<Adresse>,
    val kontaktadresser: List<Adresse>,
    val oppholdsadresser: List<Adresse>,
    val sivilstand: List<Sivilstand>,
    val familierelasjoner: List<Familierelasjon>,
    val doedsfall: List<LocalDate>
)

data class Statsborgerskap(
    val landkode: String,
    val fom: LocalDate?,
    val tom: LocalDate?
) {
    private val periode = Periode(fom, tom)

    fun overlapper(annenPeriode: Periode): Boolean {
        return periode.overlapper(annenPeriode)
    }

    fun overlapper(dato: LocalDate): Boolean {
        return periode.overlapper(dato)
    }

    fun encloses(annenPeriode: Periode): Boolean {
        return periode.encloses(annenPeriode)
    }

    fun encloses(dato: LocalDate): Boolean {
        return periode.encloses(dato)
    }

    fun erSveitsisk(): Boolean {
        return Eøsland.erSveitsisk(landkode)
    }

    companion object {
        fun gyldigeStatsborgerskap(
            statsborgerskap: List<Statsborgerskap>,
            kontrollPeriodeForPersonhistorikk: Kontrollperiode
        ): List<String> {
            val statsborgerskapFørst = statsborgerskap
                .filter { it.overlapper(kontrollPeriodeForPersonhistorikk.fom) }
                .map { it.landkode }
                .toSet()
            val statsborgerskapSist = statsborgerskap
                .filter { it.overlapper(kontrollPeriodeForPersonhistorikk.tom) }
                .map { it.landkode }
                .toSet()

            return statsborgerskapFørst.filter { statsborgerskapSist.contains(it) }
        }

        fun erSveitsiskBorger(
            statsborgerskap: List<Statsborgerskap>,
            kontrollPeriodeForPersonhistorikk: Kontrollperiode
        ): Boolean {
            return gyldigeStatsborgerskap(statsborgerskap, kontrollPeriodeForPersonhistorikk).any { Eøsland.erSveitsisk(it) }
        }

        fun erBritiskBorger(
            statsborgerskap: List<Statsborgerskap>,
            kontrollPeriodeForPersonhistorikk: Kontrollperiode
        ): Boolean {
            return gyldigeStatsborgerskap(statsborgerskap, kontrollPeriodeForPersonhistorikk).any { Eøsland.erBritisk(it) }
        }

        fun erNorskBorger(
            statsborgerskap: List<Statsborgerskap>,
            kontrollPeriodeForPersonhistorikk: Kontrollperiode
        ): Boolean {
            return gyldigeStatsborgerskap(statsborgerskap, kontrollPeriodeForPersonhistorikk).any { Eøsland.erNorsk(it) }
        }

        fun erNordiskBorger(
            statsborgerskap: List<Statsborgerskap>,
            kontrollPeriodeForPersonhistorikk: Kontrollperiode
        ): Boolean {
            return gyldigeStatsborgerskap(statsborgerskap, kontrollPeriodeForPersonhistorikk).any { Eøsland.erNordisk(it) }
        }

        fun erEøsBorger(
            statsborgerskap: List<Statsborgerskap>,
            kontrollPeriodeForPersonhistorikk: Kontrollperiode
        ): Boolean {
            return gyldigeStatsborgerskap(statsborgerskap, kontrollPeriodeForPersonhistorikk).any { Eøsland.erEØSland(it) }
        }

        infix fun List<Statsborgerskap>.hentStatsborgerskapFor(dato: LocalDate): List<String> =
            this.filter {
                it.overlapper(dato)
            }.map { it.landkode }

        infix fun List<Statsborgerskap>.harEndretSisteÅret(kontrollPeriode: Kontrollperiode): Boolean =
            this.any { erStatsborgerskapetInnenforPerioden(it, kontrollPeriode) }

        private fun erStatsborgerskapetInnenforPerioden(it: Statsborgerskap, kontrollPeriode: Kontrollperiode): Boolean =
            kontrollPeriode.periode.encloses(Periode(fom = it.fom, tom = it.fom)) || kontrollPeriode.periode.encloses(Periode(fom = it.tom, tom = it.tom))

        fun List<Statsborgerskap>.registrerStatsborgerskapGrafana(kontrollPeriode: Kontrollperiode, ytelse: Ytelse, regelId: RegelId) =
            this.hentStatsborgerskapFor(kontrollPeriode.tom).forEach { statsborgerskapUavklartForRegel(it, ytelse, regelId).increment() }
    }
}

data class Adresse(
    val landkode: String,
    val fom: LocalDate?,
    val tom: LocalDate?
) {
    private val periode = Periode(fom, tom)

    fun overlapper(annenPeriode: Periode): Boolean {
        return periode.overlapper(annenPeriode)
    }
}

data class Sivilstand(
    val type: Sivilstandstype,
    val gyldigFraOgMed: LocalDate?,
    val gyldigTilOgMed: LocalDate?,
    val relatertVedSivilstand: String?
)

enum class Sivilstandstype {
    GIFT,
    ENKE_ELLER_ENKEMANN,
    SKILT,
    SEPARERT,
    REGISTRERT_PARTNER,
    SEPARERT_PARTNER,
    SKILT_PARTNER,
    GJENLEVENDE_PARTNER
}

data class Familierelasjon(
    val relatertPersonsIdent: String,
    val relatertPersonsRolle: Familierelasjonsrolle,
    val minRolleForPerson: Familierelasjonsrolle?,
    val folkeregistermetadata: Folkeregistermetadata?
)

enum class Familierelasjonsrolle {
    BARN, MOR, FAR, MEDMOR
}

data class Folkeregistermetadata(
    val ajourholdstidspunkt: LocalDateTime?,
    val gyldighetstidspunkt: LocalDateTime?,
    val opphoerstidspunkt: LocalDateTime?
)
