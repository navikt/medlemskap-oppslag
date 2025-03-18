# language: no
# encoding: UTF-8

Egenskap: Regelflyt for regel 35

  Bakgrunn:
    Gitt følgende bostedsadresser i personhistorikken
      | Adresse | Landkode | Fra og med dato | Til og med dato |
      | Oslo    | NOR      | 01.01.2000      |                 |

    Og følgende statsborgerskap i personhistorikken
      | Landkode | Fra og med dato | Til og med dato |
      | NOR      | 01.01.2000      |                 |

  Scenariomal: Regelflyt regel 35

    Gitt følgende arbeidsforhold fra AAReg
      | Fra og med dato | Til og med dato | Arbeidsgivertype | Arbeidsforholdstype   | Arbeidsgiver Id |
      | 20.01.2020      |                 | Organisasjon     | <Arbeidsforholdstype> | 1               |

    Og følgende arbeidsgiver i arbeidsforholdet
      | Identifikator | Arbeidsgivertype | Landkode | Antall ansatte |
      | 1             | BEDR             | NOR      | 9              |

    Og følgende arbeidsavtaler i arbeidsforholdet
      | Fra og med dato | Til og med dato | Yrkeskode | Skipsregister | Stillingsprosent |
      | 01.01.2018      |                 | 001       | NOR           | 100              |

    Når medlemskap beregnes med følgende parametre
      | Fra og med dato | Til og med dato | Har hatt arbeid utenfor Norge |
      | 30.01.2020      | 30.01.2021      | Nei                           |

    Og skal regel "35" gi svaret "<Regel 35>"

    Eksempler:
      | Arbeidsforholdstype | Regel 35 |
      | NORMALT             | Nei      |
      | MARITIMT            | Ja       |