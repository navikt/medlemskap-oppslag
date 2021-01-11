package no.nav.medlemskap.regler.v1.lovvalg

import no.nav.medlemskap.domene.Eøsland
import no.nav.medlemskap.domene.InputPeriode
import no.nav.medlemskap.domene.Kontrollperiode.Companion.kontrollPeriodeForArbeidsforhold
import no.nav.medlemskap.domene.Kontrollperiode.Companion.kontrollPeriodeForPersonhistorikk
import no.nav.medlemskap.domene.Kontrollperiode.Companion.startDatoForYtelse
import no.nav.medlemskap.domene.Ytelse
import no.nav.medlemskap.domene.personhistorikk.Adresse
import no.nav.medlemskap.domene.personhistorikk.Statsborgerskap
import no.nav.medlemskap.domene.personhistorikk.Statsborgerskap.Companion.erBritiskBorger
import no.nav.medlemskap.domene.personhistorikk.Statsborgerskap.Companion.erSveitsiskBorger
import no.nav.medlemskap.domene.personhistorikk.Statsborgerskap.Companion.gyldigeStatsborgerskap
import no.nav.medlemskap.regler.common.BasisRegel
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
    val startDatoForYtelse = startDatoForYtelse(periode, førsteDagForYtelse)
    val kontrollPeriodeForArbeidsforhold = kontrollPeriodeForArbeidsforhold(startDatoForYtelse)
    val kontrollPeriodeForPersonhistorikk = kontrollPeriodeForPersonhistorikk(startDatoForYtelse)

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

    protected fun erBrukerSveitsiskBorgerUtenAnnetEøsStatsborgerskap(statsborgerskap: List<Statsborgerskap>): Boolean {
        if (!statsborgerskap.erSveitsiskBorger(kontrollPeriodeForPersonhistorikk)) {
            return false
        }

        val statsborgerskapUtenomSveits = statsborgerskap
            .gyldigeStatsborgerskap(kontrollPeriodeForPersonhistorikk)
            .filterNot { Eøsland.erSveitsisk(it) }

        return !statsborgerskapUtenomSveits.any { Eøsland.erEØSland(it) }
    }

    protected fun erBrukerBritiskBorgerUtenAnnetEøsStatsborgerskap(statsborgerskap: List<Statsborgerskap>): Boolean {
        if (!statsborgerskap.erBritiskBorger(kontrollPeriodeForPersonhistorikk)) {
            return false
        }

        val statsborgerskapUtenomBritisk = statsborgerskap
            .gyldigeStatsborgerskap(kontrollPeriodeForPersonhistorikk)
            .filterNot { Eøsland.erBritisk(it) }

        return !statsborgerskapUtenomBritisk.any { Eøsland.erEØSland(it) }
    }
}
