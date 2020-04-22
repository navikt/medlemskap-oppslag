package no.nav.medlemskap.services.ereg
import no.nav.medlemskap.domene.Periode
import java.time.LocalDate

data class OrganisasjonNøkkelinfo(val enhetstype: String?)


data class OrganiasjonsDetaljer(val ansatte: List<Ansatte>?,
                                val dublettAv: Dublettorganisasjon?,
                                val dubletter: List<Dublettorganisasjon>?,
                                val enhetstyper: List<Enhetstyper>?,
                                val epostAdresser: List<Epostadresse>?,
                                val formaal: List<Formaal>?,
                                val forretningsAdresser: List<ForretningsAdresser>?,
                                val hjemlandregistre: List<Hjemlandregistre>?,
                                val internettadresser: List<Internettadresse>,
                                val maalform: String?,
                                val mobiltelefonnummer: List<Telefonnummer>?,
                                val naeringer: List<Naering>?,
                                val navSpesifikkInformasjon: NAVSpesifikkInformasjon?,
                                val navn: List<Navn>?,
                                val opphoersdato: LocalDate?,
                                val postAdresse: Postadresse?,
                                val registreringsdato: LocalDate?,
                                val registrertMVA: List<MVA>?,
                                val sistEndret: LocalDate?,
                                val statuser: List<Status>?,
                                val stiftelsesdato: LocalDate?,
                                val telefaksnummer: List<Telefonnummer>?,
                                val telefonnummer: List<Telefonnummer?>,
                                val underlagtHjemlandLovgivningForetaksform: List<UnderlagtHjemlandLovgivningForetaksform>?)

data class Dublettorganisasjon(val string: String?)


data class UnderlagtHjemlandLovgivningForetaksform(
        val beskrivelseHjemland: String?,
        val beskrivelseNorge: String?,
        val bruksperiode: Bruksperiode?,
        val foretaksform: String?,
        val gyldighetsperiode: Gyldighetsperiode?,
        val landkode: String?
)

data class Status(val bruksperiode: Bruksperiode,
                  val gyldighetsperiode: Gyldighetsperiode,
                  val kode: String)

data class MVA(val registrertIMVA: String,
               val bruksperiode: Bruksperiode,
               val gyldighetsperiode: Gyldighetsperiode

)

data class NAVSpesifikkInformasjon(
        val bruksperiode: Bruksperiode,
        val erIA: Boolean,
        val gyldighetsperiode: Gyldighetsperiode
)


data class Naering(val bruksperiode: Bruksperiode,
                   val gyldighetsperiode: Gyldighetsperiode,
                   val hjelpeenhet: Boolean,
                   val naeringskode: String)


data class Internettadresse(val adresse: String,
                            val bruksperiode: Bruksperiode,
                            val gyldighetsperiode: Gyldighetsperiode)

data class Telefonnummer(val nummer: String?,
                         val bruksperiode: Bruksperiode?,
                         val gyldighetsperiode: Gyldighetsperiode?,
                         val Telefontype: String?)


data class Hjemlandregistre(val bruksperiode: Bruksperiode?,
                            val gyldighetsperiode: Gyldighetsperiode?,
                            val navn1: String?,
                            val navn2: String?,
                            val navn3: String?,
                            val postAdresse: Postadresse?,
                            val registernummer: String?)


data class Postadresse(val adresselinje1: String?,
                       val adresselinje2: String?,
                       val adresselinje3: String?,
                       val bruksperiode: Bruksperiode?,
                       val gyldighetsperiode: Gyldighetsperiode?,
                       val kommunenr: String?,
                       val landkode: String?,
                       val postnummer: String?,
                       val poststed: String?
                       )





data class ForretningsAdresser(
        val adresselinje1: String,
        val adresselinje2: String,
        val adresselinje3: String,
        val bruksperiode: Bruksperiode,
        val gyldighetsperiode: Gyldighetsperiode,
        val kommunenr: String,
        val landkode: String,
        val postnummer: String,
        val poststed: String

)

data class Formaal(val bruksperiode: Bruksperiode,
                   val formaal: String,
                   val gyldighetsperiode: Gyldighetsperiode)




data class Epostadresse(val adresse: String,
                        val bruksperiode: Bruksperiode,
                        val gyldighetsperiode: Gyldighetsperiode)

data class Enhetstyper(val bruksperiode: Bruksperiode,
                       val enhetstype: String,
                       val gyldighetsperiode: Gyldighetsperiode)


data class Ansatte(val antall: Int, val bruksperiode: Bruksperiode, val gyldighetsperiode: Gyldighetsperiode)

data class Bruksperiode(val fom: LocalDate, val tom: LocalDate?)

data class Gyldighetsperiode(val fom: LocalDate?, val tom: LocalDate?)

data class Organisasjon(val navn: Navn?,
                        val organisasjonDetaljer: OrganiasjonsDetaljer?,
                        val organisasjonsNummer: String?,
                        val organisasjonstype: String?)

data class Navn(val bruksperiode: Bruksperiode,
                val gyldighetsperiode: Gyldighetsperiode,
                val navnelinje1: String?,
                val navnelinje2: String?,
                val navnelinje3: String?,
                val navnelinje4: String?,
                val navnelinje5: String?,
                val redigertnavn: String?
)


    enum class Organisasjonstype{
        VIRKSOMHET,
        JURDISKENHET,
        ORGANISASJONSLEDD

    }




