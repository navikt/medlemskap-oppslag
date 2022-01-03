# Funksjonell dokumentasjon

Dette er en tjeneste som finner ut lovvalg og medlemskap.

## Inn-parametre
* Fødselsnummer: Identifiserer en person, brukes til oppslag mot andre tjenester
* Periode (fra og med dato, til og med dato)
* Brukerinput
    * arbeid utenfor Norge (Ja/Nei)
* Ytelse utledes fra request.

## Datagrunnlag
* Personhistorikk hentes fra PDL
* Medlemskap hentes fra MEDL
* Arbeidsforhold hentes fra AAReg
* InntektListe hentes fra InntektService
* Journalposter hentes fra JOARK
* Oppgaver hentes fra GOSYS

## Mapping til datagrunnlag
* [Mapping fra PDL til datagrunnlag](features/mapping/pdl/bruker/readme.md)

## De forskjellige reglene 

Reglene er forklart detaljert på Confluence:
* [Regelbeskrivelser på Confluence](https://confluence.adeo.no/x/z7rzFg)

| RegelId        | Identifikator | Avklaring                                                                                           |
|----------------|---------------|-----------------------------------------------------------------------------------------------------|
| REGEL_0_1      | 0.1           | Er request gyldig?                                                                                  |
| REGEL_1_1      | 1.1           | Er alle perioder siste 12 mnd avklart (endelig/gyldig)?                                             |
| REGEL_1_2      | 1.2           | Er det periode både med og uten medlemskap innenfor 12 mnd?                                         |
| REGEL_1_3      | 1.3           | Er det en periode med medlemskap?                                                                   |
| REGEL_1_3_1    | 1.3.1         | Er hele perioden uten medlemskap innenfor 12-måneders perioden?                                     |
| REGEL_1_3_2    | 1.3.2         | Er bruker uten medlemskap sin situasjon uendret?                                                    |
| REGEL_1_4      | 1.4           | Er hele perioden med medlemskap innenfor 12-måneders perioden?                                      |
| REGEL_1_5      | 1.5           | Er brukers arbeidsforhold uendret?                                                                  |
| REGEL_1_6      | 1.6           | Er brukers dekning uavklart?                                                                        |
| REGEL_1_7      | 1.7           | Har bruker et medlemskap som omfatter ytelse? (Dekning i MEDL)                                      |
| REGEL_2        | 2             | Er bruker omfattet av grunnforordningen (EØS)? Dvs er bruker statsborger i et EØS-land inkl. Norge? |
| REGEL_3        | 3             | Har bruker hatt et sammenhengende arbeidsforhold i Aa-registeret de siste 12 månedene?              |
| REGEL_4        | 4             | Er foretaket registrert i foretaksregisteret?                                                       |
| REGEL_5        | 5             | Har arbeidsgiver sin hovedaktivitet i Norge?                                                        |
| REGEL_6        | 6             | Er foretaket aktivt?                                                                                |
| REGEL_7        | 7             | Er arbeidsforholdet maritimt?                                                                       |
| REGEL_7_1      | 7.1           | Er bruker ansatt på et NOR-skip?                                                                    |
| REGEL_8        | 8             | Er bruker pilot eller kabinansatt?                                                                  |
| REGEL_9        | 9             | Har bruker utført arbeid utenfor Norge?                                                             |
| REGEL_10       | 10            | Er bruker folkeregistrert som bosatt i Norge og har vært det i 12 mnd?"                             |
| REGEL_11       | 11            | Er bruker norsk statsborger?                                                                        |
| REGEL_11_2     | 11.2          | Har bruker ektefelle i PDL?                                                                         |
| REGEL_11_2_1   | 11.2.1        | Har bruker barn i PDL?                                                                              |
| REGEL_11_2_2   | 11.2.2        | Er brukers barn folkeregistrert som bosatt i Norge?                                                 |
| REGEL_11_2_2_1 | 11.2.2.1      | Har bruker uten ektefelle og folkeregistrerte barn jobbet mer enn 100 prosent?                      |
| REGEL_11_2_2_2 | 11.2.2.2      | Har bruker vært i 80 % stilling eller mer de siste 12 mnd?                                          |
| REGEL_11_2_3   | 11.2.3        | Har bruker vært i minst 80 % stilling de siste 12 mnd?                                              |
| REGEL_11_3     | 11.3          | Har bruker barn i PDL?                                                                              |
| REGEL_11_3_1   | 11.3.1        | Er brukers ektefelle folkeregistrert som bosatt i Norge?                                            |
| REGEL_11_3_1_1 | 11.3.1.1      | Har bruker vært i 100 % stilling eller mer de siste 12 mnd?                                         |
| REGEL_11_4     | 11.4          | Er brukers ektefelle folkeregistrert som bosatt i Norge?                                            |
| REGEL_11_4_1   | 11.4.1        | Er brukers barn folkeregistrert som bosatt i Norge?                                                 |
| REGEL_11_4_2   | 11.4.2        | Har bruker vært i 100 % stilling eller mer de siste 12 mnd?                                         |
| REGEL_11_5     | 11.5          | Er brukers barn folkeregistrert som bosatt i Norge?                                                 |
| REGEL_11_5_1   | 11.5.1        | Er brukers ektefelle og barnas mor/far samme person?                                                |
| REGEL_11_5_2   | 11.5.2        | Har bruker vært i 80 % stilling eller mer de siste 12 mnd?                                          |
| REGEL_11_5_3   | 11.5.3        | Har bruker vært i 100 % stilling eller mer de siste 12 mnd?                                         |
| REGEL_11_6     | 11.6          | Har bruker vært i minst 80 % stilling de siste 12 mnd?                                              |
| REGEL_11_6_1   | 11.6.1        | Har brukers ektefelle  vært i minst 100 % stilling de siste 12 mnd?                                 |
| REGEL_12       | 12            | Har bruker vært i minst 25% stilling de siste 12 mnd?                                               |
| REGEL_13       | 13            | Er bruker død?                                                                                      |
| REGEL_14       | 14            | Er bruker ansatt i staten eller i en kommune?                                                       |


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
* [Arbeidsforhold](features/hovedregler/arbeidsforhold/README.md)

## Ytelsen sykepenger
* [Ytelsen sykepenger](features/ytelse/sykepenger/README.md)

## Kontrollperiode
* [Kontrollperiode](features/kontrollperiode/README.md)



