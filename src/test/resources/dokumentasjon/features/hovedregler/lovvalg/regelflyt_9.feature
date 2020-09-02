# language: no
# encoding: UTF-8

Egenskap: Regelflyt for regel 9

  Bakgrunn: Belgisk statsborger som er bosatt i Norge
    Gitt følgende bostedsadresser i personhistorikken
      | Adresse | Landkode | Fra og med dato | Til og med dato |
      | Oslo    | NOR      | 01.01.2000      |                 |

    Og følgende personstatuser i personhistorikken
      | Personstatus | Fra og med dato | Til og med dato |
      | BOSA         | 01.01.2000      |                 |

    Og følgende statsborgerskap i personhistorikken
      | Landkode | Fra og med dato | Til og med dato |
      | BEL      | 01.01.2000      |                 |

    Og følgende arbeidsforhold fra AAReg
      | Fra og med dato | Til og med dato | Arbeidsgivertype | Arbeidsforholdstype |
      | 01.01.2018      |                 | Organisasjon     | NORMALT             |

    Og følgende arbeidsgiver i arbeidsforholdet
      | Identifikator | Arbeidsgivertype | Landkode | Antall ansatte |
      | 1             | BEDR             | NOR      | 9              |

  Scenariomal:
    Gitt følgende arbeidsavtaler i arbeidsforholdet
      | Fra og med dato | Til og med dato | Yrkeskode | Stillingsprosent |
      | 01.01.2018      |                 | 001       | 100              |

    Når medlemskap beregnes med følgende parametre
      | Fra og med dato | Til og med dato | Har hatt arbeid utenfor Norge   |
      | 30.01.2020      | 30.01.2021      | <Har hatt arbeid utenfor Norge> |

    Så skal svaret være "<Medlemskap>"
    Og skal regel "9" gi svaret "<Regel 9>"
    Og skal regel "10" gi svaret "Ja"
    Og skal regel "11" gi svaret "Nei"
    Og skal regel "11.2" gi svaret "Nei"
    Og skal regel "11.2.1" gi svaret "Nei"

    Eksempler:
      | Har hatt arbeid utenfor Norge  | Regel 9 | Medlemskap |
      | Ja                             | Ja      | UAVKLART   |
      | Nei                            | Nei     | Ja         |

