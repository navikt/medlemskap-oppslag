# language: no
# encoding: UTF-8

Egenskap: Regelflyt for regel 16

  Bakgrunn:
    Gitt følgende bostedsadresser i personhistorikken
      | Adresse | Landkode | Fra og med dato | Til og med dato |
      | Oslo    | NOR      | 01.01.2000      |                 |

    Og følgende statsborgerskap i personhistorikken
      | Landkode | Fra og med dato | Til og med dato |
      | NOR      | 01.01.2000      |                 |


  Scenariomal: Regelflyt regel 16: Bruker får "ja" på spørsmålet om medlemskap hvis arbeidsforholdet er frilanser

    Gitt følgende arbeidsforhold fra AAReg
      | Fra og med dato | Til og med dato | Arbeidsgivertype | Arbeidsforholdstype   | Arbeidsgiver Id |
      | 01.01.2018      |                 | Organisasjon     | <Arbeidsforholdstype> | 1               |

    Og følgende arbeidsgiver i arbeidsforholdet
      | Identifikator | Arbeidsgivertype | Landkode | Antall ansatte |
      | 1             | BEDR             | NOR      | 9              |

    Og følgende arbeidsavtaler i arbeidsforholdet
      | Fra og med dato | Til og med dato | Yrkeskode | Skipsregister | Stillingsprosent |
      | 01.01.2018      |                 | 001       |               | 100              |

    Når medlemskap beregnes med følgende parametre
      | Fra og med dato | Til og med dato | Har hatt arbeid utenfor Norge |
      | 30.01.2020      | 30.01.2021      | Nei                           |

    Så skal svaret være "<Medlemskap>"
    Og skal regel "16" gi svaret "<Regel 16>"

    Eksempler:
      | Arbeidsforholdstype | Regel 16 | Medlemskap |
      | NORMALT             | Nei        | Ja         |
      | FRILANSER           | Ja         | UAVKLART   |