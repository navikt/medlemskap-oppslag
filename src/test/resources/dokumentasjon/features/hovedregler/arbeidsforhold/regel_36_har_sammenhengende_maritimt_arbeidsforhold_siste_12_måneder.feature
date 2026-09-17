# language: no
# encoding: UTF-8


Egenskap: Regel 36: Har bruker hatt et sammenhengende maritimt arbeidsforhold på et NOR-skip i løpet av de siste 12 månedene?

  Scenariomal: Bruker med maritimt arbeidsforhold får "Ja"
    Gitt følgende arbeidsforhold fra AAReg
      | Fra og med dato | Til og med dato | Arbeidsgivertype | Arbeidsforholdstype   |
      | 01.01.2018      |                 | Organisasjon     | <Arbeidsforholdstype> |

    Og følgende arbeidsgiver i arbeidsforholdet
      | Identifikator | Arbeidsgivertype | Antall ansatte | Konkursstatus |
      | 1             | BEDR             | 9              |               |

    Og følgende arbeidsavtaler i arbeidsforholdet
      | Fra og med dato | Til og med dato | Yrkeskode | Skipsregister   | Stillingsprosent | Fartsområde |
      | 01.01.2018      |                 | 1         | <Skipsregister> | 100              |             |

    Når regel "36" kjøres med følgende parametre
      | Fra og med dato | Til og med dato | Har hatt arbeid utenfor Norge |
      | 30.01.2020      | 30.01.2021      | Nei                           |

    Så skal svaret være "<Svar>"

    Eksempler:
      | Arbeidsforholdstype | Skipsregister | Svar |
      | NORMALT             |               | Nei  |
      | MARITIMT            | NIS           | Nei  |
      | MARITIMT            | UTL           | Nei  |
      | MARITIMT            | NOR           | Ja   |


  Scenariomal: Bruker med maritimt arbeidsforhold får "Ja"
    Gitt følgende arbeidsforhold fra AAReg
      | Fra og med dato | Til og med dato | Arbeidsgivertype | Arbeidsforholdstype |
      | 01.01.2018      |                 | Organisasjon     | MARITIMT            |

    Og følgende arbeidsgiver i arbeidsforholdet
      | Identifikator | Arbeidsgivertype | Antall ansatte | Konkursstatus |
      | 1             | BEDR             | 9              |               |

    Og følgende arbeidsavtaler i arbeidsforholdet
      | Fra og med dato | Til og med dato | Yrkeskode | Skipsregister | Stillingsprosent | Fartsområde |
      | <fom>           | <tom>           | 1         | NOR           | 50               |             |
      | <fom2>          | <tom2>          | 1         | NOR           | 50               |             |

    Når regel "36" kjøres med følgende parametre
      | Fra og med dato | Til og med dato | Har hatt arbeid utenfor Norge |
      | 30.01.2020      | 30.01.2021      | Nei                           |

    Så skal svaret være "<Svar>"

    Eksempler:
      | fom        | tom        | fom2       | tom2       | Svar | kommentar                              |
      | 01.01.2019 | 01.05.2019 | 03.05.2019 | 01.02.2020 | Nei  | Opphold på 1 dag mellom arbeidsforhold |
      | 01.01.2019 | 01.05.2019 | 02.05.2019 | 01.02.2020 | Ja   | ingen opphold mellom arbeidsforhold    |
      | 01.01.2019 | 01.05.2019 | 25.04.2019 | 01.02.2020 | Ja   | To arbeidsforhold som overlapper       |
      | 01.01.2018 |            | 01.01.2018 |            | Ja   | To parallelle arbeidsforhold           |


  Scenariomal: Bruker med både NIS og NOR i arbeidsavtale
    Gitt følgende arbeidsforhold fra AAReg
      | Fra og med dato | Til og med dato | Arbeidsgivertype | Arbeidsforholdstype   |
      | 01.01.2018      |                 | Organisasjon     | <Arbeidsforholdstype> |

    Og følgende arbeidsgiver i arbeidsforholdet
      | Identifikator | Arbeidsgivertype | Antall ansatte | Konkursstatus |
      | 1             | BEDR             | 9              |               |

    Og følgende arbeidsavtaler i arbeidsforholdet
      | Fra og med dato | Til og med dato | Yrkeskode | Skipsregister    | Stillingsprosent | Fartsområde |
      | 01.01.2018      |                 | 1         | <Skipsregister>  | 50               |             |
      | 01.01.2018      |                 | 1         | <Skipsregister2> | 50               |             |

    Når regel "36" kjøres med følgende parametre
      | Fra og med dato | Til og med dato | Har hatt arbeid utenfor Norge |
      | 30.01.2020      | 30.01.2021      | Nei                           |

    Så skal svaret være "<Svar>"

    Eksempler:
      | Arbeidsforholdstype | Skipsregister | Skipsregister2 | Svar |
      | NORMALT             |               |                | Nei  |
      | MARITIMT            | NIS           | NOR            | Nei  |
      | MARITIMT            | NIS           | NIS            | Nei  |
      | MARITIMT            | NOR           | NOR            | Ja   |


  Scenario: Regel 36: Bruker uten arbeidsforhold får "Nei"

    Når regel "36" kjøres med følgende parametre
      | Fra og med dato | Til og med dato | Har hatt arbeid utenfor Norge |
      | 30.01.2020      | 30.01.2021      | Nei                           |

    Så skal svaret være "Nei"


