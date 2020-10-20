# language: no
# encoding: UTF-8

Egenskap: Regelflyt for regel 5

  Bakgrunn:
    Gitt følgende bostedsadresser i personhistorikken
      | Adresse | Landkode | Fra og med dato | Til og med dato |
      | Oslo    | NOR      | 01.01.2000      |                 |

    Og følgende statsborgerskap i personhistorikken
      | Landkode | Fra og med dato | Til og med dato |
      | NOR      | 01.01.2000      |                 |


  Scenariomal: Regelflyt regel 5: Bruker får "ja" på spørsmålet om medlemskap hvis arbeidsgiver har flere enn 5 ansatte

    Gitt følgende arbeidsforhold fra AAReg
      | Fra og med dato | Til og med dato | Arbeidsgivertype | Arbeidsforholdstype | Arbeidsgiver Id |
      | 01.01.2018      |                 | Organisasjon     | NORMALT             | 1               |

    Og følgende arbeidsgiver i arbeidsforholdet
      | Identifikator | Arbeidsgivertype | Landkode | Antall ansatte   |
      | 1             | BEDR             | NOR      | <Antall ansatte> |

    Og følgende arbeidsavtaler i arbeidsforholdet
      | Fra og med dato | Til og med dato | Yrkeskode | Stillingsprosent |
      | 01.01.2018      |                 | 001       | 100              |

    Når medlemskap beregnes med følgende parametre
      | Fra og med dato | Til og med dato | Har hatt arbeid utenfor Norge |
      | 30.01.2020      | 30.01.2021      | Nei                           |

    Så skal svaret være "<Medlemskap>"
    Og skal regel "5" gi svaret "<Regel 5>"

    Eksempler:
      | Antall ansatte | Regel 5 | Medlemskap |
      | 4              | Nei     | UAVKLART   |
      | 5              | Nei     | UAVKLART   |
      | 6              | Ja      | Ja         |
      | 9              | Ja      | Ja         |