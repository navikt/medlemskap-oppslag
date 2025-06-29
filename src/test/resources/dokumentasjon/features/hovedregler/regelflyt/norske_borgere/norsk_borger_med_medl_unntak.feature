# language: no
# encoding: UTF-8

Egenskap: Norsk borger med medl unntak.

  Bakgrunn:

    Gitt følgende statsborgerskap i personhistorikken
      | Landkode | Fra og med dato | Til og med dato |
      | NOR      | 01.01.2000      |                 |

    Og følgende bostedsadresser i personhistorikken
      | Landkode | Fra og med dato | Til og med dato |
      | NOR      | 10.10.1975      |                 |

    Og følgende arbeidsforhold fra AAReg
      | Fra og med dato | Til og med dato | Arbeidsgivertype | Arbeidsforholdstype | Arbeidsgiver Id |
      | 10.10.1975      |                 | Organisasjon     | NORMALT             | 1               |

    Og følgende arbeidsgiver i arbeidsforholdet
      | Identifikator       | Arbeidsgivertype | Landkode | Antall ansatte | Antall ansatte i juridisk enhet | Juridisk enhetstype | Juridisk orgnr    | Konkursstatus |
      | organisasjonsnummer | STAT             | NOR      | 10             | 20                              | STAT                | juridiskOrgnummer | Konkursstatus |

    Og følgende detaljer om ansatte for arbeidsgiver
      | Antall ansatte | Gyldighetsperiode gyldig fra | Gyldighetsperiode gyldig til | Bruksperiode gyldig fra | Bruksperiode gyldig til |
      | 10             | 10.10.1975                   | 01.08.2021                   | 10.10.1975              | 01.08.2021              |

    Og følgende arbeidsavtaler i arbeidsforholdet
      | Fra og med dato | Til og med dato | Yrkeskode | Stillingsprosent | Skipsregister | Beregnet antall timer pr uke |
      | 10.10.1975      | 01.08.2020      | yrkeskode | 100              | NIS           | 37.5                         |

  Scenariomal: Norsk borger med medl unntak

    Gitt følgende medlemsunntak fra MEDL
      | Dekning             | Fra og med dato | Til og med dato | Er medlem   | Lovvalg | Lovvalgsland | Periodestatus |
      | FTL_2-9_2_ld_jfr_1c | 01.01.2018      | 01.06.2021      | <Er medlem> | ENDL    | NOR          | GYLD          |

    Når medlemskap beregnes med følgende parametre
      | Fra og med dato | Til og med dato | Har hatt arbeid utenfor Norge |
      | 05.01.2021      | 12.02.2021      | Nei                           |

    Så skal svaret være "<Medlemskap>"

    Og skal resultat gi følgende delresultater:
      | Regel                   |
      #| OVERSTYRING           |
      | DOED                    |
      | BOSATT                  |
      | MEDL                    |
      | REGEL_FRILANSER         |
      | REGEL_BRUKERGRUPPE      |
      | FELLES ARBEIDSFORHOLD   |
      | REGEL_BRUKERSVAR_ARBEID |
      | REGEL_PERMITTERING      |
      | REGEL_PERMISJONER       |
      | REGEL_UTENLANDSFORHOLD  |
      | REGEL_MARITIM           |
      | REGEL_YTELSER           |
      | STATSBORGERSKAP         |
      | ARBEIDSFORHOLD          |
      | NORSK                   |

    Og skal regel "MEDL" gi svaret "<Regel MEDL>"

    Eksempler:
      | Er medlem | Regel MEDL | Medlemskap |
      | Ja        | Ja         | Ja         |
      | Nei       | Uavklart   | Uavklart   |
