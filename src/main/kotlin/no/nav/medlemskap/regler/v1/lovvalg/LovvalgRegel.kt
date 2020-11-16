package no.nav.medlemskap.regler.v1.lovvalg

import no.nav.medlemskap.domene.*
import no.nav.medlemskap.regler.common.BasisRegel
import no.nav.medlemskap.regler.common.Datohjelper
import no.nav.medlemskap.regler.common.Funksjoner.alleEr
import no.nav.medlemskap.regler.common.Funksjoner.erIkkeTom
import no.nav.medlemskap.regler.common.Funksjoner.erTom
import no.nav.medlemskap.regler.common.RegelId
import no.nav.medlemskap.regler.funksjoner.StatsborgerskapFunksjoner.sjekkStatsborgerskap
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
        return sjekkStatsborgerskap(statsborgerskap, kontrollPeriodeForPersonhistorikk, { s -> Eøsland.erSveitsisk(s) })
    }

    protected fun erBrukerNorskStatsborger(statsborgerskap: List<Statsborgerskap>): Boolean {
        return sjekkStatsborgerskap(statsborgerskap, kontrollPeriodeForPersonhistorikk, { s -> Eøsland.erNorsk(s) })
    }

    protected fun erBrukerNordiskStatsborger(statsborgerskap: List<Statsborgerskap>): Boolean {
        return sjekkStatsborgerskap(statsborgerskap, kontrollPeriodeForPersonhistorikk, { s -> Eøsland.erNordisk(s) })
    }

    protected fun erBrukerEøsBorger(statsborgerskap: List<Statsborgerskap>): Boolean {
        return sjekkStatsborgerskap(statsborgerskap, kontrollPeriodeForPersonhistorikk, { s -> Eøsland.erEØSland(s) })
    }

    protected fun erBrukerSveitsiskBorgerUtenAnnetEøsStatsborgerskap(statsborgerskap: List<Statsborgerskap>): Boolean {
        if (!erBrukerSveitsiskStatsborger(statsborgerskap)) {
            return false
        }

        val statsborgerskapUtenomSveits = statsborgerskap.filterNot { Eøsland.erSveitsisk(it.landkode) }

        return !sjekkStatsborgerskap(statsborgerskapUtenomSveits, kontrollPeriodeForPersonhistorikk, { s -> Eøsland.erEØSland(s) })
    }
}
