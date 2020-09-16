# language: no
# encoding: UTF-8

Egenskap: Resultat-struktur for norsk statsborger

  Bakgrunn:
    Gitt følgende bostedsadresser i personhistorikken
      | Adresse | Landkode | Fra og med dato | Til og med dato |
      | Oslo    | NOR      | 01.01.2000      |                 |

    Og følgende arbeidsforhold fra AAReg
      | Fra og med dato | Til og med dato | Arbeidsgivertype | Arbeidsforholdstype |
      | 01.01.2018      |                 | Organisasjon     | NORMALT             |
      | 01.01.2018      |                 | Organisasjon     | NORMALT             |

    Og følgende arbeidsavtaler i arbeidsforholdet
      | Fra og med dato | Til og med dato | Yrkeskode | Stillingsprosent | Skipsregister |
      | 01.01.2018      |                 | 001       | 100              |               |


  Scenario: Resultat-struktur for norsk statsborger med avklart arbeidsforhold
    Gitt følgende arbeidsgiver i arbeidsforholdet
      | Identifikator | Arbeidsgivertype | Landkode | Antall ansatte |
      | 1             | BEDR             | NOR      | 9              |

    Og følgende personstatuser i personhistorikken
      | Personstatus | Fra og med dato | Til og med dato |
      | FØDR         | 01.01.2000      |                 |

    Og følgende statsborgerskap i personhistorikken
      | Landkode | Fra og med dato | Til og med dato |
      | NOR      | 01.01.2000      |                 |

    Når medlemskap beregnes med følgende parametre
      | Fra og med dato | Til og med dato | Har hatt arbeid utenfor Norge |
      | 30.01.2020      | 30.01.2021      | Nei                           |

    Så skal regel "NORSK" gi svaret "Ja"

    Og skal resultat gi følgende delresultater:
      | Regel           |
      | MEDL            |
      | ARBEIDSFORHOLD  |
      | NORSK           |
      | STATSBORGERSKAP |
      | BOSATT          |

    Og skal regel "NORSK" inneholde følgende delresultater:
      | Regel |
      | 12    |

  Scenario: Resultat-struktur for norsk statsborger med uavklart arbeidsforhold
    Gitt følgende arbeidsgiver i arbeidsforholdet
      | Identifikator | Arbeidsgivertype | Landkode | Antall ansatte | Konkursstatus |
      | 1             | BEDR             | NOR      | 9              | OSKP          |

    Og følgende personstatuser i personhistorikken
      | Personstatus | Fra og med dato | Til og med dato |
      | FØDR         | 01.01.2000      |                 |

    Og følgende statsborgerskap i personhistorikken
      | Landkode | Fra og med dato | Til og med dato |
      | NOR      | 01.01.2000      |                 |

    Når medlemskap beregnes med følgende parametre
      | Fra og med dato | Til og med dato | Har hatt arbeid utenfor Norge |
      | 30.01.2020      | 30.01.2021      | Nei                           |

    Så skal regel "NORSK" gi svaret "Ja"

    Og skal resultat gi følgende delresultater:
      | Regel           |
      | MEDL            |
      | ARBEIDSFORHOLD  |
      | NORSK           |
      | STATSBORGERSKAP |
      | BOSATT          |

    Og skal regel "NORSK" inneholde følgende delresultater:
      | Regel |
      | 12    |