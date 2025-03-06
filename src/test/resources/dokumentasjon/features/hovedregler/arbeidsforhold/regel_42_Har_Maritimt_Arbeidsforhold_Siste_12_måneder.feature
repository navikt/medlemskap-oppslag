# language: no
# encoding: UTF-8


Egenskap: Regel 35: Har bruker maritimt arbeidsforhold siste 12 måneder?

  Scenariomal: Bruker med maritimt arbeidsforhold får "Ja"
    Gitt følgende arbeidsforhold fra AAReg
      | Fra og med dato | Til og med dato | Arbeidsgivertype | Arbeidsforholdstype   |
      | 01.01.2018      |                 | Organisasjon     | <Arbeidsforholdstype> |

    Og følgende arbeidsgiver i arbeidsforholdet
      | Identifikator | Arbeidsgivertype | Antall ansatte | Konkursstatus |
      | 1             | BEDR             | 9              |               |

    Når regel "35" kjøres med følgende parametre
      | Fra og med dato | Til og med dato | Har hatt arbeid utenfor Norge |
      | 30.01.2020      | 30.01.2021      | Nei                           |

    Så skal svaret være "<Svar>"

    Eksempler:
      | Arbeidsforholdstype | Svar |
      | NORMALT             | Nei  |
      | MARITIMT            | Ja   |


  Scenario: Regel 35: Bruker uten arbeidsforhold får "Nei"

    Når regel "35" kjøres med følgende parametre
      | Fra og med dato | Til og med dato | Har hatt arbeid utenfor Norge |
      | 30.01.2020      | 30.01.2021      | Nei                           |

    Så skal svaret være "Nei"

  Scenariomal: Bruker med maritimt arbeidsforhold får "Ja"
    Gitt følgende arbeidsforhold fra AAReg
      | Fra og med dato | Til og med dato | Arbeidsgivertype | Arbeidsforholdstype    |
      | 01.01.2018      |                 | Organisasjon     | <Arbeidsforholdstype>  |
      | 01.01.2018      |                 | Organisasjon     | <Arbeidsforholdstype2> |

    Og følgende arbeidsgiver i arbeidsforholdet
      | Identifikator | Arbeidsgivertype | Antall ansatte | Konkursstatus |
      | 1             | BEDR             | 9              |               |

    Når regel "35" kjøres med følgende parametre
      | Fra og med dato | Til og med dato | Har hatt arbeid utenfor Norge |
      | 30.01.2020      | 30.01.2021      | Nei                           |

    Så skal svaret være "<Svar>"

    Eksempler:
      | Arbeidsforholdstype | Arbeidsforholdstype2 | Svar |
      | NORMALT             | NORMALT              | Nei  |
      | MARITIMT            | NORMALT              | Ja   |

  Scenariomal: Bruker med maritimt arbeidsforhold får "Ja"
    Gitt følgende arbeidsforhold fra AAReg
      | Fra og med dato | Til og med dato | Arbeidsgivertype | Arbeidsforholdstype |
      | 01.01.2018      | <tom>           | Organisasjon     | MARITIMT            |

    Og følgende arbeidsgiver i arbeidsforholdet
      | Identifikator | Arbeidsgivertype | Antall ansatte | Konkursstatus |
      | 1             | BEDR             | 9              |               |

    Når regel "35" kjøres med følgende parametre
      | Fra og med dato | Til og med dato | Har hatt arbeid utenfor Norge |
      | 30.01.2020      | 30.01.2021      | Nei                           |

    Så skal svaret være "<Svar>"

    Eksempler:
      | tom        | Svar |
      | 31.12.2018 | Nei  |
      |            | Ja   |