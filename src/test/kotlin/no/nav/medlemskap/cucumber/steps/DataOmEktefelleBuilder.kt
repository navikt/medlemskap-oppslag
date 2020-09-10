package no.nav.medlemskap.cucumber.steps

import no.nav.medlemskap.domene.Adresse
import no.nav.medlemskap.domene.Arbeidsforhold
import no.nav.medlemskap.domene.barn.PersonhistorikkBarn
import no.nav.medlemskap.domene.ektefelle.DataOmEktefelle
import no.nav.medlemskap.domene.ektefelle.PersonhistorikkEktefelle

class DataOmEktefelleBuilder {
    var personhistorikkEktefelle = PersonhistorikkEktefelle(
            ident = String(),
            barn = mutableListOf<String>(),
            bostedsadresser = mutableListOf<Adresse>(),
            kontaktadresser = mutableListOf<Adresse>(),
            oppholdsadresser = mutableListOf<Adresse>()

    )
    var arbeidsforholdEktefelle = listOf<Arbeidsforhold>()


    fun build(): DataOmEktefelle {
        return DataOmEktefelle(
                personhistorikkEktefelle,
                arbeidsforholdEktefelle
        )
    }
}