package no.nav.medlemskap.regler.v1

import no.nav.medlemskap.domene.*
import no.nav.medlemskap.regler.common.*
import no.nav.medlemskap.regler.common.Funksjoner.alleEr
import no.nav.medlemskap.regler.common.Funksjoner.erIkkeTom
import no.nav.medlemskap.regler.common.Funksjoner.erTom
import no.nav.medlemskap.regler.common.Funksjoner.inneholder
import no.nav.medlemskap.regler.common.Funksjoner.harAlle
import no.nav.medlemskap.regler.common.RegelId.*
import no.nav.medlemskap.regler.funksjoner.AdresseFunksjoner.adresserForKontrollPeriode
import no.nav.medlemskap.regler.funksjoner.AdresseFunksjoner.landkodeTilAdresserForKontrollPeriode
import no.nav.medlemskap.regler.funksjoner.ArbeidsforholdFunksjoner.harBrukerJobbetMerEnnGittStillingsprosentTilEnhverTid
import no.nav.medlemskap.regler.funksjoner.RelasjonFunksjoner.hentFnrTilBarnUnder25
import no.nav.medlemskap.regler.funksjoner.RelasjonFunksjoner.hentFnrTilEktefellerEllerPartnerForDato
import no.nav.medlemskap.regler.funksjoner.RelasjonFunksjoner.hentRelatertSomFinnesITPS
import no.nav.medlemskap.regler.funksjoner.RelasjonFunksjoner.hentBarnSomFinnesITPS
import no.nav.medlemskap.regler.funksjoner.StatsborgerskapFunksjoner.sjekkStatsborgerskap

class ReglerForLovvalg(
        val personhistorikk: Personhistorikk,
        val arbeidsforhold: List<Arbeidsforhold>,
        val periode: InputPeriode,
        ytelse: Ytelse,
        val arbeidUtenforNorge: Boolean,
        val personhistorikkRelatertPerson: List<PersonhistorikkRelatertPerson>,
        val pdlPersonhistorikk: Personhistorikk?,
        val personhistorikkEktefelle: PersonhistorikkEktefelle?
) : Regler(ytelse) {
    val statsborgerskap = personhistorikk.statsborgerskap
    val postadresser = personhistorikk.postadresser
    val bostedsadresser = personhistorikk.bostedsadresser
    val midlertidigAdresser = personhistorikk.midlertidigAdresser
    val sivilstand = pdlPersonhistorikk?.sivilstand
    val familierelasjon = pdlPersonhistorikk?.familierelasjoner

    private val datohjelper = Datohjelper(periode, ytelse)
    private val kontrollPeriodeForPersonhistorikk = datohjelper.kontrollPeriodeForPersonhistorikk()
    private val kontrollPeriodeForArbeidsforhold = datohjelper.kontrollPeriodeForArbeidsforhold()
    private val ektefelle = personhistorikkEktefelle?.ident
    private val ektefelleITps = personhistorikkRelatertPerson.hentRelatertSomFinnesITPS(ektefelle)
    private val barn = familierelasjon?.hentFnrTilBarnUnder25()
    private val barnITps = personhistorikkRelatertPerson.hentBarnSomFinnesITPS(barn)


    override fun hentRegelflyt(): Regelflyt {
        val harBrukerMedFolkeregistrerteBarnJobbetMerEnn80ProsentFlyt = lagRegelflyt(
                regel = harBrukerMedFolkeregistrerteBarnJobbetMerEnn80Prosent,
                hvisJa = regelFlytJa(ytelse),
                hvisNei = regelFlytUavklart(ytelse)
        )

        val harBrukerUtenFolkeregistrerteBarnJobbetMerEnn100ProsentFlyt = lagRegelflyt(
                regel = harBrukerUtenFolkeregistrerteBarnJobbetMerEnn100Prosent,
                hvisJa = regelFlytJa(ytelse),
                hvisNei = regelFlytUavklart(ytelse)
        )

        val harBrukerUtenEktefelleBarnSomErFolkeregistrertFlyt = lagRegelflyt(
                regel = harBrukerUtenEktefelleBarnSomErFolkeregistrert,
                hvisJa = harBrukerMedFolkeregistrerteBarnJobbetMerEnn80ProsentFlyt,
                hvisNei = harBrukerUtenFolkeregistrerteBarnJobbetMerEnn100ProsentFlyt
        )

        val harBrukerBarnUtenEktefelleFlyt = lagRegelflyt(
                regel = harBrukerBarnUtenEktefelle,
                hvisJa = harBrukerUtenEktefelleBarnSomErFolkeregistrertFlyt,
                hvisNei = harBrukerUtenFolkeregistrerteBarnJobbetMerEnn100ProsentFlyt
        )

        val harBarnloesBrukerMedFolkeregistrertEktefelleJobbetMerEnn100ProsentFlyt = lagRegelflyt(
                regel = harBarnloesBrukerMedFolkeregistrertEktefelleJobbetMerEnn100Prosent,
                hvisJa = regelFlytJa(ytelse),
                hvisNei = regelFlytUavklart(ytelse)
        )

        val harBarnloesBrukerMedFolkeregistrertEktefelleJobbetMerEnn80ProsentFlyt = lagRegelflyt(
                regel = harBarnloesBrukerMedFolkeregistrertEktefelleJobbetMerEnn80Prosent,
                hvisJa = regelFlytJa(ytelse),
                hvisNei = regelFlytUavklart(ytelse)
        )

        val erBarnloesBrukersEktefelleBosattINorgeFlyt = lagRegelflyt(
                regel = erBarnloesBrukersEktefelleBosattINorge,
                hvisJa = harBarnloesBrukerMedFolkeregistrertEktefelleJobbetMerEnn80ProsentFlyt,
                hvisNei = harBarnloesBrukerMedFolkeregistrertEktefelleJobbetMerEnn100ProsentFlyt
        )

        val harBrukerMedRelasjonerUtenFolkeregistreringJobbetMerEnn100ProsentFlyt = lagRegelflyt(
                regel = harBrukerMedRelasjonerUtenFolkeregistreringJobbetMerEnn100Prosent,
                hvisJa = regelFlytJa(ytelse),
                hvisNei = regelFlytUavklart(ytelse)
        )

        val harBrukerMedBarnOgEktefelleUtenTilknytningJobbetMerEnn100ProsentFlyt = lagRegelflyt(
                regel = harBrukerMedBarnOgEktefelleUtenTilknytningJobbetMerEnn100Prosent,
                hvisJa = regelFlytJa(ytelse),
                hvisNei = regelFlytUavklart(ytelse)
        )
        val erBrukersEktefelleOgBarnasMorSammePersonFlyt = lagRegelflyt(
                regel = erBrukersEktefelleOgBarnasMorSammePerson,
                hvisJa = regelFlytUavklart(ytelse),
                hvisNei = harBrukerMedBarnOgEktefelleUtenTilknytningJobbetMerEnn100ProsentFlyt
        )

        val erBrukerUtenFolkeregistrertEktefelleSittBarnFolkeregistrertFlyt = lagRegelflyt(
                regel = erBrukerUtenFolkeregistrertEktefelleSittBarnFolkeregistrert,
                hvisJa = erBrukersEktefelleOgBarnasMorSammePersonFlyt,
                hvisNei = harBrukerMedRelasjonerUtenFolkeregistreringJobbetMerEnn100ProsentFlyt
        )

        val harBrukerMedFolkeregistrerteRelasjonerJobbetMerEnn80ProsentFlyt = lagRegelflyt(
                regel = harBrukerMedFolkeregistrerteRelasjonerJobbetMerEnn80Prosent,
                hvisJa = regelFlytJa(ytelse),
                hvisNei = regelFlytUavklart(ytelse)
        )

        val erBrukerMedFolkeregistrertEktefelleSittBarnFolkeregistrertFlyt = lagRegelflyt(
                regel = erBrukerMedFolkeregistrertEktefelleSittBarnFolkeregistrert,
                hvisJa = harBrukerMedFolkeregistrerteRelasjonerJobbetMerEnn80ProsentFlyt,
                hvisNei = regelFlytUavklart(ytelse)
        )

        val erBrukerMedBarnSittEktefelleBosattINorgeFlyt = lagRegelflyt(
                regel = erBrukerMedBarnSittEktefelleBosattINorge,
                hvisJa = erBrukerMedFolkeregistrertEktefelleSittBarnFolkeregistrertFlyt,
                hvisNei = erBrukerUtenFolkeregistrertEktefelleSittBarnFolkeregistrertFlyt
        )

        val harBrukerEktefelleOgBarnFlyt = lagRegelflyt(
                regel = harBrukerEktefelleOgBarn,
                hvisJa = erBrukerMedBarnSittEktefelleBosattINorgeFlyt,
                hvisNei = erBarnloesBrukersEktefelleBosattINorgeFlyt
        )

        val harBrukerEktefelleFlyt = lagRegelflyt(
                regel = harBrukerEktefelle,
                hvisJa = harBrukerEktefelleOgBarnFlyt,
                hvisNei = harBrukerBarnUtenEktefelleFlyt
        )

        val harBrukerJobbet25ProsentEllerMerFlyt = lagRegelflyt(
                regel = harBrukerJobbet25ProsentEllerMer,
                hvisJa = regelFlytJa(ytelse),
                hvisNei = regelFlytUavklart(ytelse)
        )

        val harBrukerNorskStatsborgerskapFlyt = lagRegelflyt(
                regel = harBrukerNorskStatsborgerskap,
                hvisJa = harBrukerJobbet25ProsentEllerMerFlyt,
                hvisNei = harBrukerEktefelleFlyt
        )

        val erBrukerBosattINorgeFlyt = lagRegelflyt(
                regel = erBrukerBosattINorge,
                hvisJa = harBrukerNorskStatsborgerskapFlyt,
                hvisNei = regelFlytUavklart(ytelse)
        )

        val harBrukerJobbetUtenforNorgeFlyt = lagRegelflyt(
                regel = harBrukerJobbetUtenforNorge,
                hvisJa = regelFlytNei(ytelse),
                hvisNei = erBrukerBosattINorgeFlyt
        )

        return harBrukerJobbetUtenforNorgeFlyt
    }

    val harBrukerJobbetUtenforNorge = Regel(
            regelId = REGEL_9,
            ytelse = ytelse,
            operasjon = { sjekkOmBrukerHarJobbetUtenforNorge() }
    )

    val erBrukerBosattINorge = ErBrukerBosattINorgeRegel(
            postadresser = postadresser,
            bostedsadresser = bostedsadresser,
            midlertidigAdresser = midlertidigAdresser,
            ytelse = ytelse,
            periode = periode
    ).regel

    val harBrukerNorskStatsborgerskap = Regel(
            regelId = REGEL_11,
            ytelse = ytelse,
            operasjon = { sjekkOmBrukerErNorskStatsborger() }
    )

    val harBrukerJobbet25ProsentEllerMer = Regel(
            regelId = REGEL_12,
            ytelse = ytelse,
            operasjon = { sjekkOmBrukersStillingsprosentErMerEnn(25.0) }
    )

    val harBrukerEktefelle = Regel(
            regelId = REGEL_11_2,
            ytelse = ytelse,
            operasjon = { sjekkOmBrukerHarEktefelle() }
    )

    val harBrukerMedFolkeregistrerteBarnJobbetMerEnn80Prosent = Regel(
            regelId = REGEL_11_2_3,
            ytelse = ytelse,
            operasjon = { sjekkOmBrukersStillingsprosentErMerEnn(80.0) }
    )

    val harBrukerMedBarnOgEktefelleUtenTilknytningJobbetMerEnn100Prosent = Regel(
            regelId = REGEL_11_2_2_1,
            ytelse = ytelse,
            operasjon = {sjekkOmBrukersStillingsprosentErMerEnn(100.0)}
    )

    val harBrukerMedRelasjonerUtenFolkeregistreringJobbetMerEnn100Prosent = Regel(
            regelId = REGEL_11_2_2_1,
            ytelse = ytelse,
            operasjon = { sjekkOmBrukersStillingsprosentErMerEnn(100.0) }
    )

    val harBrukerUtenFolkeregistrerteBarnJobbetMerEnn100Prosent = Regel(
            regelId = REGEL_11_2_2_1,
            ytelse = ytelse,
            operasjon = { sjekkOmBrukersStillingsprosentErMerEnn(100.0) }
    )

    val harBrukerUtenEktefelleOgBarnJobbetMerEnn100Prosent = Regel(
            regelId = REGEL_11_2_2_1,
            ytelse = ytelse,
            operasjon = { sjekkOmBrukersStillingsprosentErMerEnn(100.0) }
    )

    val harBrukerUtenEktefelleBarnSomErFolkeregistrert = Regel(
            regelId = REGEL_11_2_2,
            ytelse = ytelse,
            operasjon = { sjekkOmBrukersBarnErBosattINorge() }
    )

    val harBrukerBarnUtenEktefelle = Regel(
            regelId = REGEL_11_2_1,
            ytelse = ytelse,
            operasjon = { sjekkOmBrukerHarBarn() }
    )

    val harBrukerEktefelleOgBarn = Regel(
            regelId = REGEL_11_3,
            ytelse = ytelse,
            operasjon = { sjekkOmBrukerHarBarn() }
    )

    val erBarnloesBrukersEktefelleBosattINorge = Regel(
            regelId = REGEL_11_3_1,
            ytelse = ytelse,
            operasjon = { sjekkOmBrukersEktefelleErBosattINorge() }
    )

    val harBarnloesBrukerMedFolkeregistrertEktefelleJobbetMerEnn80Prosent = Regel(
            regelId = REGEL_11_6,
            ytelse = ytelse,
            operasjon = { sjekkOmBrukersStillingsprosentErMerEnn(80.0) }
    )

    val harBarnloesBrukerMedFolkeregistrertEktefelleJobbetMerEnn100Prosent = Regel(
            regelId = REGEL_11_3_1_1,
            ytelse = ytelse,
            operasjon = { sjekkOmBrukersStillingsprosentErMerEnn(100.0) }
    )

    val erBrukerMedBarnSittEktefelleBosattINorge = Regel(
            regelId = REGEL_11_4,
            ytelse = ytelse,
            operasjon = { sjekkOmBrukersEktefelleErBosattINorge() }
    )

    val erBrukerUtenFolkeregistrertEktefelleSittBarnFolkeregistrert = Regel(
            regelId = REGEL_11_4_1,
            ytelse = ytelse,
            operasjon = { sjekkOmBrukersBarnErBosattINorge() }
    )

    val erBrukerMedFolkeregistrertEktefelleSittBarnFolkeregistrert = Regel(
            regelId = REGEL_11_5,
            ytelse = ytelse,
            operasjon = { sjekkOmBrukersBarnErBosattINorge() }
    )

    val erBrukersEktefelleOgBarnasMorSammePerson = Regel(
            regelId = REGEL_11_5_1,
            ytelse =  ytelse,
            operasjon = { sjekkOmBrukersEktefelleOgBarnasMorErSammePerson()}
    )



    val harBrukerMedFolkeregistrerteRelasjonerJobbetMerEnn80Prosent = Regel(
            regelId = REGEL_11_6,
            ytelse = ytelse,
            operasjon = { sjekkOmBrukersStillingsprosentErMerEnn(80.0) }
    )

    /* private val harBrukerNorsdiskStatsborgerskap = Regel (
        identifikator = "",
        avklaring = "Har bruker nordisk statsborgerskap?",
        beskrivelse = "",
        ytelse = ytelse,
        operasjon = {sjekkOmBrukerHarNordiskStatsborgerskap() }
        )
    */

    private fun sjekkOmBrukerHarBarn(): Resultat {
        return when {
            barnITps.erIkkeTom() -> ja()
            else -> nei("Bruker har ikke barn i tps")
        }
    }

    private fun sjekkOmBrukerHarEktefelle(): Resultat {
        return when {
            ektefelleITps.erIkkeTom() -> ja()
            else -> nei("Bruker har ikke ektefelle i tps")
        }
    }

    private fun sjekkOmBrukersEktefelleErBosattINorge(): Resultat {
        val bostedsadresserTilEktefelle = ektefelleITps.flatMap { it.bostedsadresser.adresserForKontrollPeriode(kontrollPeriodeForPersonhistorikk) }
        val postAdresseTilEktefelle = ektefelleITps.flatMap { it.postadresser.landkodeTilAdresserForKontrollPeriode(kontrollPeriodeForPersonhistorikk) }
        val midlertidigPostadresseTilEktefelle = ektefelleITps.flatMap { it.midlertidigAdresser.landkodeTilAdresserForKontrollPeriode(kontrollPeriodeForPersonhistorikk) }
        return when {
            erPersonBosattINorge(bostedsadresserTilEktefelle, postAdresseTilEktefelle, midlertidigPostadresseTilEktefelle) -> ja()
            else -> nei("Ikke alle adressene til ektefelle er norske, eller ektefelle mangler bostedsadresse")
        }
    }

    private fun sjekkOmBrukersEktefelleOgBarnasMorErSammePerson(): Resultat {
        if (personhistorikkEktefelle != null) {
            return when {
                personhistorikkEktefelle.barn?.map { it.ident}?.harAlle(barnITps.map { it.ident })!! -> ja()
                else -> nei(" Ektefelle er ikke barn/barnas mor")
            }
        }

        return nei()
    }

    private fun sjekkOmBrukersBarnErBosattINorge(): Resultat {
        val harBarnBosattINorge = barnITps.any {
            erPersonBosattINorge(
                    it.bostedsadresser.adresserForKontrollPeriode(kontrollPeriodeForPersonhistorikk),
                    it.postadresser.landkodeTilAdresserForKontrollPeriode(kontrollPeriodeForPersonhistorikk),
                    it.midlertidigAdresser.landkodeTilAdresserForKontrollPeriode(kontrollPeriodeForPersonhistorikk))
        }

        val harBarnSomIkkeErBosattINorge = barnITps.filterNot {
            erPersonBosattINorge(
                    it.bostedsadresser.adresserForKontrollPeriode(kontrollPeriodeForPersonhistorikk),
                    it.postadresser.landkodeTilAdresserForKontrollPeriode(kontrollPeriodeForPersonhistorikk),
                    it.midlertidigAdresser.landkodeTilAdresserForKontrollPeriode(kontrollPeriodeForPersonhistorikk))
        }.any()

        if (harBarnBosattINorge && harBarnSomIkkeErBosattINorge) {
            return uavklart("Noen barn med norsk adresse og noen barn med utenlandsk adresse")
        }

        if (harBarnBosattINorge) {
            return ja()
        } else {
            return nei("Ikke alle adressene til barna er norske, eller barn som mangler bostedsadresse")
        }
    }

    private fun sjekkOmBrukerHarJobbetUtenforNorge(): Resultat =
            when {
                arbeidUtenforNorge -> ja()
                else -> nei()
            }

    private fun sjekkOmBrukerErNorskStatsborger(): Resultat {
        val sjekkStatsborgerskap = sjekkStatsborgerskap(statsborgerskap, kontrollPeriodeForPersonhistorikk, norskLandkode)

        return when {
            sjekkStatsborgerskap -> ja()
            else -> nei("Brukeren er ikke norsk statsborger")
        }
    }

    /*
    private fun sjekkOmBrukerHarNordiskStatsborgerskap(): Resultat {
        val sjekkStatsborgerskap = StatsborgerskapFunksjoner.sjekkStatsborgerskap(statsborgerskap, kontrollPeriodeForPersonhistorikk, norskLandkode)

        return when {
            sjekkStatsborgerskap -> ja()
            else -> nei("Brukeren er ikke statsborger i et nordisk land.")
        }
    }
    */

    private fun sjekkOmBrukersStillingsprosentErMerEnn(stillingsprosent: Double): Resultat =
            when {
                arbeidsforhold.harBrukerJobbetMerEnnGittStillingsprosentTilEnhverTid(stillingsprosent, kontrollPeriodeForArbeidsforhold, ytelse) -> ja()
                else -> nei("Bruker har ikke jobbet 25% eller mer i løpet av periode.")
            }

    private fun sjekkLandkode(): Resultat {
        val bostedsadresser = bostedsadresser.adresserForKontrollPeriode(kontrollPeriodeForPersonhistorikk)
        val postadresserLandkoder = postadresser.landkodeTilAdresserForKontrollPeriode(kontrollPeriodeForPersonhistorikk)
        val midlertidigadresserLandkoder = midlertidigAdresser.landkodeTilAdresserForKontrollPeriode(kontrollPeriodeForPersonhistorikk)

        return when {
            bostedsadresser.erIkkeTom()
                    && (postadresserLandkoder alleEr NorskLandkode.NOR.name || postadresserLandkoder.erTom())
                    && (midlertidigadresserLandkoder alleEr NorskLandkode.NOR.name || midlertidigadresserLandkoder.erTom()) -> ja()
            erPersonBosattINorge(bostedsadresser, postadresserLandkoder, midlertidigadresserLandkoder) -> ja()
            else -> nei("Ikke alle adressene til bruker er norske, eller bruker mangler bostedsadresse")
        }
    }

    private fun List<Adresse>.brukerHarNorskBostedsadresse(): Boolean {
        return this.erIkkeTom()
    }

    private fun personHarIngenEllerNorskPostadresse(postadresseLandkode: List<String>): Boolean {
        return postadresseLandkode alleEr NorskLandkode.NOR.name || postadresseLandkode.erTom()
    }

    private fun personHarIngenEllerNorskMidlertidigadresse(midlertidigadresserLandkoder: List<String>): Boolean {
        return midlertidigadresserLandkoder alleEr NorskLandkode.NOR.name || midlertidigadresserLandkoder.erTom()
    }

    private fun erPersonBosattINorge(boadadresse: List<Adresse>, postadresseLandkoder: List<String>, midlertidigAdresseLandkoder: List<String>): Boolean {
        return boadadresse.brukerHarNorskBostedsadresse()
                && personHarIngenEllerNorskPostadresse(postadresseLandkoder)
                && personHarIngenEllerNorskMidlertidigadresse(midlertidigAdresseLandkoder)
    }

    enum class NorskLandkode {
        NOR
    }

    private val norskLandkode = mapOf(
            "NOR" to "NORGE"
    )

    private val nordiskeLand = mapOf(
            "DNK" to "DANMARK",
            "FIN" to "FINLAND",
            "ISL" to "ISLAND",
            "SWE" to "SVERIGE",
            "NOR" to "NORGE",
            "FRO" to "FÆRØYENE",
            "GRL" to "GRØNNLAND",
            "ALA" to "ÅLAND"
    )

    companion object {
        fun fraDatagrunnlag(datagrunnlag: Datagrunnlag): ReglerForLovvalg {
            with(datagrunnlag) {
                return ReglerForLovvalg(personhistorikk, arbeidsforhold, periode, ytelse, brukerinput.arbeidUtenforNorge, personHistorikkRelatertePersoner, pdlpersonhistorikk, personhistorikkEktefelle)
            }
        }
    }
}
