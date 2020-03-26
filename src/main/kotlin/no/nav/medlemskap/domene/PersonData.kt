package no.nav.medlemskap.domene

data class PersonData(val bostedsadresser: List<Adresse>,
                      val postadresser: List<Adresse>,
                      val midlertidigAdresser: List<Adresse>)