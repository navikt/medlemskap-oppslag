package no.nav.medlemskap.cucumber.steps.aareg.aaregBuilder

import no.nav.medlemskap.clients.aareg.AaRegAntallTimerForTimeloennet
import no.nav.medlemskap.clients.aareg.AaRegArbeidsavtale
import no.nav.medlemskap.clients.aareg.AaRegArbeidsforhold
import no.nav.medlemskap.cucumber.steps.aareg.aaregBuilder.AnsettelsesPeriodeBuilder
import no.nav.medlemskap.cucumber.steps.aareg.aaregBuilder.AntallTimerForLoennetBuilder
import no.nav.medlemskap.cucumber.steps.aareg.aaregBuilder.ArbeidsavtaleBuilder
import no.nav.medlemskap.cucumber.steps.aareg.aaregBuilder.ArbeidsgiverBuilder
import java.time.LocalDateTime

class ArbeidsforholdBuilder {
    var ansettelesperiodeBuilder = AnsettelsesPeriodeBuilder()
    var antallTimerForTimeloennetBuilder = AntallTimerForLoennetBuilder()
    var arbeidsavtaleBuilder = ArbeidsavtaleBuilder()
    var arbeidsgiverBuilder = ArbeidsgiverBuilder()
    var ansettelsesPeriodeBuilder = AnsettelsesPeriodeBuilder()
    var arbeidstakerBuilder = AaRegPersonBuilder()
    var opplysningspliktigBuilder = AaregOpplysningspliktigArbeidsgiverBuilder()
    var permisjonsPermitteringerBuilder = PermisjonsPermitteringerBuilder()
    var sporingsinformasjonBuilder = SporingsinformasjonBuilder()
    var utenlandsoppholdBuilder = UtenlandsoppholdBuilder()


    var antallTimerForLoennet = mutableListOf<AaRegAntallTimerForTimeloennet>(antallTimerForTimeloennetBuilder.build())
    var arbeidsavtaler = mutableListOf<AaRegArbeidsavtale>(arbeidsavtaleBuilder.build())
    var arbeidsforholdId = String()
    var arbeidsgiver = arbeidsgiverBuilder.build()
    var arbeidsavtale = arbeidsavtaleBuilder.build()
    var arbeidstaker = arbeidstakerBuilder.build()
    var ansettelsesperiode = ansettelesperiodeBuilder.build()
    var innrapportertEtterAOrdningen = true
    var navArbeidsforholdId = 1
    var opplysningspliktig = opplysningspliktigBuilder.build()
    var permisjonPermitteringer = mutableListOf(permisjonsPermitteringerBuilder.build())
    var registrert = LocalDateTime.now()
    var sistBekreftet = LocalDateTime.now()
    var sporingsinformasjon = sporingsinformasjonBuilder.build()
    var type = String()
    var utenlandsopphold = mutableListOf(utenlandsoppholdBuilder.build())

    fun build(): AaRegArbeidsforhold {

        return AaRegArbeidsforhold(
                ansettelsesperiode = ansettelsesperiode,
                antallTimerForTimeloennet = antallTimerForLoennet,
                arbeidsavtaler = arbeidsavtaler,
                arbeidsforholdId = arbeidsforholdId,
                arbeidsgiver = arbeidsgiver,
                arbeidstaker = arbeidstaker,
                innrapportertEtterAOrdningen = innrapportertEtterAOrdningen,
                navArbeidsforholdId = navArbeidsforholdId,
                opplysningspliktig = opplysningspliktig,
                permisjonPermitteringer = permisjonPermitteringer,
                registrert = registrert,
                sistBekreftet = sistBekreftet,
                sporingsinformasjon = sporingsinformasjon,
                type = type,
                utenlandsopphold = utenlandsopphold
        )
    }
}