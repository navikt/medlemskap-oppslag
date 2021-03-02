# language: no
# encoding: UTF-8

Egenskap: Regelflyt for britiske borgere

  Bakgrunn:

    Gitt følgende bostedsadresser i personhistorikken
      | Landkode | Fra og med dato | Til og med dato |
      | NOR      | 10.10.1975      |                 |

    Og følgende oppholdstillatelse
      | Gyldig fra og med | Gyldig til og med | Har tillatelse | Type      |
      | 01.01.2021        |                   | Ja             | PERMANENT |

    Og følgende arbeidsadgang
      | Gyldig fra og med | Gyldig til og med | Arbeidsadgang | ArbeidsadgangType | ArbeidomfangKategori |
      | 01.01.2021        |                   | Ja            | GENERELL          | KUN_ARBEID_HELTID    |

    Og følgende arbeidsforhold fra AAReg
      | Fra og med dato | Til og med dato | Arbeidsgivertype | Arbeidsforholdstype | Arbeidsgiver Id |
      | 10.10.1975      |                 | Organisasjon     | NORMALT             | 1               |

    Og følgende arbeidsgiver i arbeidsforholdet
      | Identifikator       | Arbeidsgivertype | Landkode | Antall ansatte |
      | organisasjonsnummer | STAT             | NOR      | 10             |

    Og følgende detaljer om ansatte for arbeidsgiver
      | Antall ansatte | Gyldighetsperiode gyldig fra | Gyldighetsperiode gyldig til |
      | 10             | 10.10.1975                   | 01.08.2021                   |

    Og følgende arbeidsavtaler i arbeidsforholdet
      | Fra og med dato | Til og med dato | Yrkeskode | Stillingsprosent |
      | 10.10.1975      | 01.08.2021      | yrkeskode | 100              |


  Scenariomal: Britiske borgere er EØS-borgere bare fram til og med 31.12.2020
    Gitt følgende statsborgerskap i personhistorikken
      | Landkode | Fra og med dato | Til og med dato |
      | GBR      | 10.10.1975      |                 |

    Når medlemskap beregnes med følgende parametre
      | Første dag for ytelse   | Fra og med dato   | Til og med dato | Har hatt arbeid utenfor Norge |
      | <Første dag for ytelse> | <Fra og med dato> | 12.02.2021      | Nei                           |

    Så skal svaret være "<Medlemskap>"
    Og skal regel "2" gi svaret "<EØS-borger>"

    Eksempler:
      | Første dag for ytelse | Fra og med dato | EØS-borger | Medlemskap |
      |                       | 20.05.2020      | Ja         | Ja         |
      |                       | 30.12.2020      | Ja         | Ja         |
      |                       | 31.12.2020      | Ja         | Ja         |
      | 31.12.2020            | 31.12.2020      | Ja         | Ja         |
      | 01.01.2021            | 05.01.2021      | Nei        | Uavklart   |
      |                       | 05.04.2021      | Nei        | Uavklart   |