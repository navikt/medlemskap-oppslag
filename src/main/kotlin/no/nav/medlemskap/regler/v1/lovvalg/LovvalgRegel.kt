package no.nav.medlemskap.regler.v1.lovvalg

import no.nav.medlemskap.domene.*
import no.nav.medlemskap.regler.common.BasisRegel
import no.nav.medlemskap.regler.common.Datohjelper
import no.nav.medlemskap.regler.common.Funksjoner.alleEr
import no.nav.medlemskap.regler.common.Funksjoner.erIkkeTom
import no.nav.medlemskap.regler.common.Funksjoner.erTom
import no.nav.medlemskap.regler.common.RegelId
import java.time.LocalDate

abstract class LovvalgRegel(
    regelId: RegelId,
    ytelse: Ytelse,
    periode: InputPeriode,
    førsteDagForYtelse: LocalDate?
) : BasisRegel(regelId, ytelse) {
    val datohjelper = Datohjelper(periode, førsteDagForYtelse, ytelse)
    val kontrollPeriodeForArbeidsforhold = datohjelper.kontrollPeriodeForArbeidsforhold()
    val kontrollPeriodeForPersonhistorikk = datohjelper.kontrollPeriodeForPersonhistorikk()

    protected fun erPersonBosattINorge(boadadresse: List<Adresse>, postadresseLandkoder: List<String>, midlertidigAdresseLandkoder: List<String>): Boolean {
        return boadadresse.brukerHarNorskBostedsadresse() && boadadresse.alleErNorske() &&
            personHarIngenEllerNorskPostadresse(postadresseLandkoder) &&
            personHarIngenEllerNorskMidlertidigadresse(midlertidigAdresseLandkoder)
    }

    protected fun List<Adresse>.alleErNorske(): Boolean {
        return this.map { it.landkode } alleEr "NOR"
    }

    protected fun List<Adresse>.brukerHarNorskBostedsadresse(): Boolean {
        return this.erIkkeTom()
    }

    protected fun personHarIngenEllerNorskPostadresse(postadresseLandkoder: List<String>): Boolean {
        return postadresseLandkoder.all { Eøsland.erNorsk(it) } || postadresseLandkoder.erTom()
    }

    protected fun personHarIngenEllerNorskMidlertidigadresse(midlertidigadresserLandkoder: List<String>): Boolean {
        return midlertidigadresserLandkoder.all { Eøsland.erNorsk(it) } || midlertidigadresserLandkoder.erTom()
    }

    protected fun erBrukerSveitsiskStatsborger(statsborgerskap: List<Statsborgerskap>): Boolean {
        return Statsborgerskap.erSveitsiskBorger(statsborgerskap, kontrollPeriodeForPersonhistorikk)
    }

    protected fun erBrukerBritiskStatsborger(statsborgerskap: List<Statsborgerskap>): Boolean {
        return Statsborgerskap.erBritiskBorger(statsborgerskap, kontrollPeriodeForPersonhistorikk)
    }

    protected fun erBrukerNorskStatsborger(statsborgerskap: List<Statsborgerskap>): Boolean {
        return Statsborgerskap.erNorskBorger(statsborgerskap, kontrollPeriodeForPersonhistorikk)
    }

    protected fun erBrukerNordiskStatsborger(statsborgerskap: List<Statsborgerskap>): Boolean {
        return Statsborgerskap.erNordiskBorger(statsborgerskap, kontrollPeriodeForPersonhistorikk)
    }

    protected fun erBrukerEøsBorger(statsborgerskap: List<Statsborgerskap>): Boolean {
        return Statsborgerskap.erEøsBorger(statsborgerskap, kontrollPeriodeForPersonhistorikk)
    }

    protected fun gyldigeStatsborgerskap(statsborgerskap: List<Statsborgerskap>): List<String> {
        return Statsborgerskap.gyldigeStatsborgerskap(statsborgerskap, kontrollPeriodeForPersonhistorikk)
    }

    protected fun erBrukerSveitsiskBorgerUtenAnnetEøsStatsborgerskap(statsborgerskap: List<Statsborgerskap>): Boolean {
        if (!erBrukerSveitsiskStatsborger(statsborgerskap)) {
            return false
        }

        val statsborgerskapUtenomSveits = gyldigeStatsborgerskap(statsborgerskap).filterNot { Eøsland.erSveitsisk(it) }

        return !statsborgerskapUtenomSveits.any { Eøsland.erEØSland(it) }
    }

    protected fun erBrukerBritiskBorgerUtenAnnetEøsStatsborgerskap(statsborgerskap: List<Statsborgerskap>): Boolean {
        if (!erBrukerBritiskStatsborger(statsborgerskap)) {
            return false
        }

        val statsborgerskapUtenomBritisk = gyldigeStatsborgerskap(statsborgerskap).filterNot { Eøsland.erBritisk(it) }

        return !statsborgerskapUtenomBritisk.any { Eøsland.erEØSland(it) }
    }
}
