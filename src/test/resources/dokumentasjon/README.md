# Funksjonell dokumentasjon

Dette er en tjeneste som finner ut lovvalg og medlemskap.

## Input til tjenesten
* Fødselsnummer: Identifiserer en person, brukes til oppslag mot andre tjenester
* Sykemeldingsperiode (fra og med dato, til og med dato)
* arbeid utenfor Norge (Ja/Nei)
* Eksempel: Utlede første sykemeldingsdag fra sykemeldingsperiode

## Datagrunnlag
* Personhistorikk hentes fra TPS/PDL
* Medlemskap hentes fra MEDL
* Arbeidsforhold hentes fra AAReg
* InntektListe hentes fra InntektService
* Journalposter hentes fra JOARK
* Oppgaver hentes fra GOSYS

## Lovvalg og medlemskap for ytelsen sykepenger
* [Lovvalg og medlemskap for ytelsen sykepenger](features/ytelse/sykepenger/README.md)


