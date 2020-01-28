package no.nav.medlemskap.services.tpsws

import no.nav.tjeneste.virksomhet.person.v3.informasjon.*
import no.nav.tjeneste.virksomhet.person.v3.meldinger.HentPersonhistorikkResponse
import javax.xml.datatype.DatatypeFactory
import javax.xml.datatype.XMLGregorianCalendar

fun mockPersonMedBostedNorge(): HentPersonhistorikkResponse =
        HentPersonhistorikkResponse().apply {
            withAktoer(PersonIdent().apply {
                withIdent(NorskIdent().apply {
                    withIdent("12345678")
                    withType(Personidenter().apply {
                        withValue("FNR")
                    })
                })
            })
            withPersonstatusListe(listOf())
            withBostedsadressePeriodeListe(listOf(BostedsadressePeriode().apply {
                withPeriode(Periode().apply {
                    withFom(createXmlGregorianCalendar())
                    withTom(createXmlGregorianCalendar())
                })
                withBostedsadresse(Bostedsadresse().apply {
                    withStrukturertAdresse(Gateadresse().apply {
                        withGatenavn("Storgata")
                        withGatenummer(1)
                        withLandkode(Landkoder().apply {
                            withValue("NOR")
                        })
                    })
                })
            }))
            withPostadressePeriodeListe(listOf())
            withMidlertidigAdressePeriodeListe(listOf())
        }


private fun createXmlGregorianCalendar(): XMLGregorianCalendar = DatatypeFactory.newInstance().newXMLGregorianCalendar()
