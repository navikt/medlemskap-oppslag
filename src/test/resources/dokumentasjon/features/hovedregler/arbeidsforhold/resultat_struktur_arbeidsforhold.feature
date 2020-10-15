# language: no
# encoding: UTF-8

Egenskap: Resultat-struktur arbeidsforhold

  Bakgrunn: Belgisk statsborger som er bosatt i Norge
    Gitt følgende bostedsadresser i personhistorikken
      | Adresse | Landkode | Fra og med dato | Til og med dato |
      | Oslo    | NOR      | 01.01.2000      |                 |

    Og følgende personstatuser i personhistorikken
      | Personstatus | Fra og med dato | Til og med dato |
      | BOSATT       | 01.01.2000      |                 |

    Og følgende statsborgerskap i personhistorikken
      | Landkode | Fra og med dato | Til og med dato |
      | BEL      | 01.01.2000      |                 |

    Og følgende arbeidsforhold fra AAReg
      | Fra og med dato | Til og med dato | Arbeidsgivertype | Arbeidsforholdstype |
      | 01.01.2018      |                 | Organisasjon     | NORMALT             |

    Og følgende arbeidsgiver i arbeidsforholdet
      | Identifikator | Arbeidsgivertype | Landkode | Antall ansatte |
      | 1             | BEDR             | NOR      | 9              |

  Scenario: Resultat-struktur arbeidsforhold
    Gitt følgende arbeidsavtaler i arbeidsforholdet
      | Fra og med dato | Til og med dato | Yrkeskode | Stillingsprosent |
      | 01.01.2018      |                 | 001       | 100              |

    Når medlemskap beregnes med følgende parametre
      | Fra og med dato | Til og med dato | Har hatt arbeid utenfor Norge |
      | 30.01.2020      | 30.01.2021      | Nei                           |

    Så skal regel "ARBEIDSFORHOLD" gi svaret "Ja"

    Og skal resultat gi følgende delresultater:
      | Regel           |
      | MEDL            |
      | ARBEIDSFORHOLD  |
      | EØS-BOSATT      |
      | STATSBORGERSKAP |
      | BOSATT          |

    Og skal regel "ARBEIDSFORHOLD" inneholde følgende delresultater:
      | Regel |
      | 3     |
      | 4     |
      | 5     |
      | 6     |
      | 7     |
      | 8     |
      | 9     |




