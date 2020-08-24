package no.nav.medlemskap.regler.v1.lovvalg

import no.nav.medlemskap.domene.Adresse
import no.nav.medlemskap.domene.Eøsland
import no.nav.medlemskap.domene.InputPeriode
import no.nav.medlemskap.domene.Ytelse
import no.nav.medlemskap.regler.common.BasisRegel
import no.nav.medlemskap.regler.common.Datohjelper
import no.nav.medlemskap.regler.common.Funksjoner.erIkkeTom
import no.nav.medlemskap.regler.common.Funksjoner.erTom
import no.nav.medlemskap.regler.common.RegelId

abstract class LovvalgRegel(
        regelId: RegelId,
        ytelse: Ytelse,
        periode: InputPeriode
) : BasisRegel(regelId, ytelse) {
    val datohjelper = Datohjelper(periode, ytelse)
    val kontrollPeriodeForArbeidsforhold = datohjelper.kontrollPeriodeForArbeidsforhold()
    val kontrollPeriodeForPersonhistorikk = datohjelper.kontrollPeriodeForPersonhistorikk()

    protected fun erPersonBosattINorge(boadadresse: List<Adresse>, postadresseLandkoder: List<String>, midlertidigAdresseLandkoder: List<String>): Boolean {
        return boadadresse.brukerHarNorskBostedsadresse()
                && personHarIngenEllerNorskPostadresse(postadresseLandkoder)
                && personHarIngenEllerNorskMidlertidigadresse(midlertidigAdresseLandkoder)
    }

    protected fun List<Adresse>.brukerHarNorskBostedsadresse(): Boolean {
        return this.erIkkeTom()
    }

    protected fun personHarIngenEllerNorskPostadresse(postadresseLandkoder: List<String>): Boolean {
        return postadresseLandkoder.all { Eøsland.erNorsk(it) } || postadresseLandkoder.erTom()
    }

    protected fun personHarIngenEllerNorskMidlertidigadresse(midlertidigadresserLandkoder: List<String>): Boolean {
        return midlertidigadresserLandkoder.all { Eøsland.erNorsk(it)}  || midlertidigadresserLandkoder.erTom()
    }
}