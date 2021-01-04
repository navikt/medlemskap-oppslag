package no.nav.medlemskap.regler.funksjoner

import no.bekk.bekkopen.person.FodselsnummerValidator
import no.nav.medlemskap.domene.Familierelasjon
import no.nav.medlemskap.domene.Familierelasjonsrolle
import no.nav.medlemskap.domene.Fødselsnummer.Companion.hentBursdagsAar
import no.nav.medlemskap.domene.Personhistorikk
import no.nav.medlemskap.domene.Sivilstandstype
import java.time.LocalDate

object RelasjonFunksjoner {

    fun String.erBarnUnder25Aar(førsteDatoForYtelse: LocalDate) =
        (førsteDatoForYtelse.year - this.hentBursdagsAar().toInt()) <= 25

    fun hentFnrTilEktefelle(personHistorikkFraPdl: Personhistorikk?): String? {
        val fnrTilEktefelle =
            personHistorikkFraPdl?.sivilstand
                ?.filter { it.relatertVedSivilstand != null }
                ?.filter { FodselsnummerValidator.isValid(it.relatertVedSivilstand) }
                ?.filter { it.type == Sivilstandstype.GIFT || it.type == Sivilstandstype.REGISTRERT_PARTNER }
                ?.map { it.relatertVedSivilstand }
                ?.lastOrNull()
        return fnrTilEktefelle
    }

    fun hentFnrTilBarn(familierelasjoner: List<Familierelasjon>, førsteDatoForYtelse: LocalDate): List<String> {
        return familierelasjoner
            .filter { FodselsnummerValidator.isValid(it.relatertPersonsIdent) }
            .filter { it.erBarn() }
            .filter { it.relatertPersonsIdent.erBarnUnder25Aar(førsteDatoForYtelse) }
            .map { it.relatertPersonsIdent }
    }

    fun Familierelasjon.erBarn() = this.relatertPersonsRolle == Familierelasjonsrolle.BARN
}
