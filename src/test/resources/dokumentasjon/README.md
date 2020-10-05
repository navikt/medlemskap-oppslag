# Funksjonell dokumentasjon

Dette er en tjeneste som finner ut lovvalg og medlemskap.

## Inn-parametre
* Fødselsnummer: Identifiserer en person, brukes til oppslag mot andre tjenester
* Periode (fra og med dato, til og med dato)
* Brukerinput
    * arbeid utenfor Norge (Ja/Nei)
* Ytelse utledes fra request

## Datagrunnlag
* Personhistorikk hentes fra PDL
* Medlemskap hentes fra MEDL
* Arbeidsforhold hentes fra AAReg
* InntektListe hentes fra InntektService
* Journalposter hentes fra JOARK
* Oppgaver hentes fra GOSYS

# Mapping til datagrunnlag
* Mapping fra PDL til datagrunnlag


## Regelsett

Reglene er gruppert i forskjellige regelsett.

### Regelsett Grunnforordningen
* [Grunnforordningen](features/hovedregler/grunnforordningen/README.md)

### Regelsett Lovvalg
* [Lovvalg](features/hovedregler/lovvalg/README.md)

### Regelsett Medlemsopplysninger
* [Medlemsopplysninger](features/hovedregler/medlemsopplysninger/README.md)

### Regelsett Medlemskap 
* [Medlemskap](features/medlemskap/README.md)

### Regelsett for arbeidsforhold
* [Arbeidsforhold](features/arbeidsforhold/README.md)

## Lovvalg og medlemskap for ytelsen sykepenger
* [Lovvalg og medlemskap for ytelsen sykepenger](features/ytelse/sykepenger/README.md)



