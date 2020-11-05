import no.nav.medlemskap.clients.aareg.*
import no.nav.medlemskap.clients.ereg.*
import no.nav.medlemskap.services.aareg.ArbeidsforholdOrganisasjon
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.YearMonth

class AaregBuilder() {

    var arbeidsforhold = hentAaregArbeidsforhold()
    var organisasjon = hentOrganisasjon()
    var juridiskEnhet = mutableListOf(hentOrganisasjon())

    fun build(): List<ArbeidsforholdOrganisasjon> {
        return listOf(
            ArbeidsforholdOrganisasjon(
                arbeidsforhold = arbeidsforhold,
                organisasjon = organisasjon,
                juridiskeEnheter = juridiskEnhet
            )
        )
    }

    fun hentAnsettelsesPeriode(): AaRegAnsettelsesperiode =
        AaRegAnsettelsesperiode(
            bruksperiode = hentAaregBruksperiode(),
            periode = hentAaregPeriode(),
            varslingskode = String(),
            sporingsinformasjon = AaRegSporingsinformasjon(
                endretAv = String(),
                endretKilde = String(),
                endretKildeReferanse = String(),
                endretTidspunkt = LocalDateTime.now(),
                opprettetAv = String(),
                opprettetKilde = String(),
                opprettetKildereferanse = String(),
                opprettetTidspunkt = LocalDateTime.now()
            )
        )

    fun hentArbeidsgiver(): AaRegOpplysningspliktigArbeidsgiver =
        AaRegOpplysningspliktigArbeidsgiver(
            type = AaRegOpplysningspliktigArbeidsgiverType.Organisasjon,
            organisasjonsnummer = String(),
            aktoerId = String(),
            offentligIdent = String()
        )

    fun hentArbeidstaker(): AaRegPerson =
        AaRegPerson(
            type = AaRegPersonType.Person,
            aktoerId = String(),
            offentligIdent = String()
        )

    fun hentOpplysningspliktigArbeidsgiver(): AaRegOpplysningspliktigArbeidsgiver =
        AaRegOpplysningspliktigArbeidsgiver(
            type = AaRegOpplysningspliktigArbeidsgiverType.Organisasjon,
            organisasjonsnummer = String(),
            aktoerId = String(),
            offentligIdent = String()
        )

    fun hentAaregSporingsinformasjon(): AaRegSporingsinformasjon =
        AaRegSporingsinformasjon(
            endretAv = String(),
            endretKilde = String(),
            endretKildeReferanse = String(),
            endretTidspunkt = LocalDateTime.now(),
            opprettetAv = String(),
            opprettetKilde = String(),
            opprettetKildereferanse = String(),
            opprettetTidspunkt = LocalDateTime.now()
        )

    fun hentArbeidsavtale(): AaRegArbeidsavtale =
        AaRegArbeidsavtale(
            antallTimerPrUke = 1.0,
            arbeidstidsordning = String(),
            beregnetAntallTimerPrUke = 1.0,
            bruksperiode = hentAaregBruksperiode(),
            gyldighetsperiode = hentAaregGyldighetsperiode(),
            sistStillingsendring = String(),
            skipsregister = String(),
            skipstype = String(),
            fartsomraade = String(),
            yrke = String(),
            sporingsinformasjon = hentAaregSporingsinformasjon(),
            stillingsprosent = 100.0,
            sistLoennsendring = String()
        )

    fun hentAaregGyldighetsperiode(): AaRegGyldighetsperiode =
        AaRegGyldighetsperiode(
            fom = LocalDate.MIN,
            tom = LocalDate.MAX
        )
    fun hentAaregPeriode(): AaRegPeriode =
        AaRegPeriode(
            fom = LocalDate.MIN,
            tom = LocalDate.MAX
        )

    fun hentYearMonth(): YearMonth = YearMonth.now()

    fun hentAntallTimeForLoennet(): AaRegAntallTimerForTimeloennet =
        AaRegAntallTimerForTimeloennet(
            antallTimer = 1.0,
            periode = hentAaregPeriode(),
            rapporteringsperiode = hentYearMonth(),
            sporingsinformasjon = hentAaregSporingsinformasjon()
        )

    fun hentPermisjonsPermitteringer(): AaRegPermisjonPermittering =
        AaRegPermisjonPermittering(
            periode = hentAaregPeriode(),
            permisjonPermitteringId = String(),
            sporingsinformasjon = hentAaregSporingsinformasjon(),
            type = String(),
            varslingskode = String(),
            prosent = 1.0
        )

    fun hentAaregArbeidsforhold(): AaRegArbeidsforhold =
        AaRegArbeidsforhold(
            ansettelsesperiode = hentAnsettelsesPeriode(),
            antallTimerForTimeloennet = mutableListOf(hentAntallTimeForLoennet()),
            arbeidsavtaler = mutableListOf(hentArbeidsavtale()),
            arbeidsforholdId = String(),
            arbeidsgiver = hentArbeidsgiver(),
            arbeidstaker = hentArbeidstaker(),
            innrapportertEtterAOrdningen = true,
            navArbeidsforholdId = 1,
            opplysningspliktig = hentOpplysningspliktigArbeidsgiver(),
            permisjonPermitteringer = mutableListOf(hentPermisjonsPermitteringer()),
            registrert = LocalDateTime.MAX,
            sistBekreftet = LocalDateTime.MAX,
            sporingsinformasjon = hentAaregSporingsinformasjon(),
            type = String(),
            utenlandsopphold = mutableListOf(hentUtenlandsopphold())
        )

    fun hentUtenlandsopphold(): AaRegUtenlandsopphold =
        AaRegUtenlandsopphold(
            landkode = String(),
            rapporteringsperiode = YearMonth.now(),
            sporingsinformasjon = hentAaregSporingsinformasjon(),
            periode = AaRegPeriode(LocalDate.MIN, LocalDate.MAX)
        )
    fun hentAaregBruksperiode(): AaRegBruksperiode =
        AaRegBruksperiode(
            fom = LocalDateTime.MIN,
            tom = LocalDateTime.MAX
        )

    fun hentBruksperiode(): Bruksperiode =
        Bruksperiode(
            fom = LocalDate.MIN,
            tom = LocalDate.MAX
        )

    fun hentGyldighetsperiode(): Gyldighetsperiode =
        Gyldighetsperiode(
            fom = LocalDate.MIN,
            tom = LocalDate.MAX
        )

    fun hentOrganisasjonsNavn(): Navn =
        Navn(
            bruksperiode = hentBruksperiode(),
            gyldighetsperiode = hentGyldighetsperiode(),
            navnelinje1 = String(),
            navnelinje2 = String(),
            navnelinje3 = String(),
            navnelinje4 = String(),
            navnelinje5 = String(),
            redigertnavn = String()
        )

    fun hentOrganisasjon(): Organisasjon =
        Organisasjon(
            navn = hentOrganisasjonsNavn(),
            organisasjonsnummer = String(),
            type = String(),
            bestaarAvOrganisasjonsledd = mutableListOf<BestaarAvOrganisasjonsledd?>(),
            inngaarIJuridiskEnheter = mutableListOf<JuridiskEnhet>(),
            organisasjonDetaljer = hentOrganisasjonsDetaljer()
        )

    fun hentMVA(): MVA =
        MVA(
            registrertIMVA = String(),
            bruksperiode = hentBruksperiode(),
            gyldighetsperiode = hentGyldighetsperiode()
        )

    fun hentAnsatt(): Ansatte =
        Ansatte(
            antall = 1,
            gyldighetsperiode = hentGyldighetsperiode(),
            bruksperiode = hentBruksperiode()
        )

    fun hentStatus(): Status =
        Status(
            kode = String(),
            bruksperiode = hentBruksperiode(),
            gyldighetsperiode = hentGyldighetsperiode()
        )

    fun hentEnhetstype(): Enhetstyper =
        Enhetstyper(
            enhetstype = String(),
            bruksperiode = hentBruksperiode(),
            gyldighetsperiode = hentGyldighetsperiode()
        )

    fun hentEpostAdresse(): Epostadresse =
        Epostadresse(
            adresse = String(),
            bruksperiode = hentBruksperiode(),
            gyldighetsperiode = hentGyldighetsperiode()
        )

    fun hentTelefonnummer(): Telefonnummer =
        Telefonnummer(
            nummer = String(),
            bruksperiode = hentBruksperiode(),
            gyldighetsperiode = hentGyldighetsperiode(),
            Telefontype = String()
        )

    fun hentInternettAdresse(): Internettadresse =
        Internettadresse(
            adresse = String(),
            bruksperiode = hentBruksperiode(),
            gyldighetsperiode = hentGyldighetsperiode()
        )

    fun hentFormaal(): Formaal =
        Formaal(
            bruksperiode = hentBruksperiode(),
            formaal = String(),
            gyldighetsperiode = hentGyldighetsperiode()
        )

    fun hentForetningsAdresser(): ForretningsAdresser =
        ForretningsAdresser(
            adresselinje1 = String(),
            adresselinje2 = String(),
            adresselinje3 = String(),
            gyldighetsperiode = hentGyldighetsperiode(),
            bruksperiode = hentBruksperiode(),
            kommunenr = String(),
            landkode = String(),
            postnummer = String(),
            poststed = String()
        )

    fun hentNaering(): Naering =
        Naering(
            bruksperiode = hentBruksperiode(),
            gyldighetsperiode = hentGyldighetsperiode(),
            hjelpeenhet = true,
            naeringskode = String()
        )

    fun hentNavSpesifikkInformasjon(): NAVSpesifikkInformasjon =
        NAVSpesifikkInformasjon(
            bruksperiode = hentBruksperiode(),
            erIA = true,
            gyldighetsperiode = hentGyldighetsperiode()
        )

    fun hentPostadresse(): Postadresse =
        Postadresse(
            adresselinje1 = String(),
            adresselinje2 = String(),
            adresselinje3 = String(),
            gyldighetsperiode = hentGyldighetsperiode(),
            bruksperiode = hentBruksperiode(),
            kommunenr = String(),
            landkode = String(),
            poststed = String(),
            postnummer = String()
        )

    fun hentJuridiskeEnheter(): JuridiskEnhet =
        JuridiskEnhet(
            organisasjonsnummer = String()
        )

    fun hentHjemlandregistre(): Hjemlandregistre =
        Hjemlandregistre(
            bruksperiode = hentBruksperiode(),
            gyldighetsperiode = hentGyldighetsperiode(),
            navn1 = String(),
            navn2 = String(),
            navn3 = String(),
            postAdresse = hentPostadresse(),
            registernummer = String()
        )

    fun hentUnderlagtHjemlandLovgivningForetaksform(): UnderlagtHjemlandLovgivningForetaksform =
        UnderlagtHjemlandLovgivningForetaksform(
            beskrivelseHjemland = String(),
            beskrivelseNorge = String(),
            bruksperiode = hentBruksperiode(),
            foretaksform = String(),
            gyldighetsperiode = hentGyldighetsperiode(),
            landkode = String()
        )

    fun hentOrganisasjonsDetaljer(): Organisasjonsdetaljer =
        Organisasjonsdetaljer(
            ansatte = mutableListOf<Ansatte>(hentAnsatt()),
            dublettAv = null,
            navn = mutableListOf<Navn>(hentOrganisasjonsNavn()),
            registreringsdato = LocalDate.MIN,
            registrertMVA = mutableListOf<MVA>(hentMVA()),
            sistEndret = LocalDate.MAX,
            statuser = mutableListOf<Status>(hentStatus()),
            enhetstyper = mutableListOf<Enhetstyper>(hentEnhetstype()),
            stiftelsesdato = LocalDate.MIN,
            epostAdresser = mutableListOf(hentEpostAdresse()),
            telefonnummer = mutableListOf(hentTelefonnummer()),
            internettadresser = mutableListOf(hentInternettAdresse()),
            formaal = mutableListOf(hentFormaal()),
            forretningsAdresser = mutableListOf(hentForetningsAdresser()),
            maalform = String(),
            mobiltelefonnummer = mutableListOf(hentTelefonnummer()),
            dubletter = mutableListOf<Organisasjon>(),
            naeringer = mutableListOf(hentNaering()),
            navSpesifikkInformasjon = hentNavSpesifikkInformasjon(),
            opphoersdato = LocalDate.MIN,
            hjemlandregistre = mutableListOf(hentHjemlandregistre()),
            postAdresse = hentPostadresse(),
            telefaksnummer = mutableListOf(hentTelefonnummer()),
            inngaarIJuridiskEnheter = mutableListOf(hentJuridiskeEnheter()),
            underlagtHjemlandLovgivningForetaksform = mutableListOf(hentUnderlagtHjemlandLovgivningForetaksform())
        )

}
