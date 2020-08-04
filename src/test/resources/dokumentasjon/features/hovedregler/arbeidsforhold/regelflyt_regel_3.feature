# language: no
# encoding: UTF-8

Egenskap: Regelflyt for regel 3

  Bakgrunn:
    Gitt følgende bostedsadresser i personhistorikken
      | Adresse | Landkode | Fra og med dato | Til og med dato |
      | Oslo    | NOR      | 01.01.2000      |                 |

    Og følgende personstatuser i personhistorikken
      | Personstatus | Fra og med dato | Til og med dato |
      | FØDR         | 01.01.2000      |                 |

    Og følgende statsborgerskap i personhistorikken
      | Landkode | Fra og med dato | Til og med dato |
      | NOR      | 01.01.2000      |                 |

    Og følgende arbeidsgiver i arbeidsforholdet
      | Identifikator | Arbeidsgivertype | Landkode | Antall ansatte |
      | 1             | BEDR             | NOR      | 9              |

  Scenariomal: Regel 3: Bruker får "ja" på spørsmålet om medlemskap hvis det er sammenhengende arbeidsforhold i
  hele kontrollperioden ,UAVKLART ellers

    Gitt følgende arbeidsforhold fra AAReg
      | Fra og med dato   | Til og med dato | Arbeidsgivertype | Arbeidsforholdstype | Arbeidsgiver Id |
      | <Fra og med dato> |                 | Organisasjon     | NORMALT             | 1               |

    Og følgende arbeidsavtaler i arbeidsforholdet
      | Fra og med dato | Til og med dato | Yrkeskode | Stillingsprosent |
      | 01.01.2018      |                 | 001       | 100              |

    Når medlemskap beregnes med følgende parametre
      | Fra og med dato | Til og med dato | Har hatt arbeid utenfor Norge |
      | 30.01.2020      | 30.01.2021      | Nei                           |

    Så skal svaret være "<Medlemskap>"
    Og skal regel "3" gi svaret "<Regel 3>"

    Eksempler:
      | Fra og med dato | Regel 3 | Medlemskap |
      | 01.01.2018      | Ja      | Ja         |
      | 29.01.2019      | Ja      | Ja         |
      | 30.01.2019      | Nei     | UAVKLART   |
      | 05.02.2019      | Nei     | UAVKLART   |
      |                 | Nei     | UAVKLART   |
