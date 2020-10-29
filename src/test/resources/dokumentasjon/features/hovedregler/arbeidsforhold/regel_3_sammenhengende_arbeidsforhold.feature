# language: no
# encoding: UTF-8

Egenskap: Regel 3: Har bruker sammenhengende arbeidsforhold siste 12 måneder?

  Scenario: Person med ett sammenhengende arbeidsforhold i hele perioden får "Ja"
    Gitt følgende arbeidsforhold fra AAReg
      | Fra og med dato | Til og med dato | Arbeidsgivertype | Arbeidsforholdstype |
      | 01.01.2018      |                 | Organisasjon     | NORMALT             |

    Når regel "3" kjøres med følgende parametre
      | Fra og med dato | Til og med dato | Har hatt arbeid utenfor Norge |
      | 30.01.2020      | 30.01.2021      | Nei                           |

    Så skal svaret være "Ja"


  Scenario: Bruker med  mer enn 10 arbeidsforhold i perioden
    Gitt følgende arbeidsforhold fra AAReg
      | Fra og med dato | Til og med dato | Arbeidsgivertype | Arbeidsforholdstype |
      | 01.01.2018      | 15.02.2019      | Organisasjon     | NORMALT             |
      | 16.02.2019      | 15.03.2019      | Organisasjon     | NORMALT             |
      | 16.03.2019      | 15.04.2019      | Organisasjon     | NORMALT             |
      | 16.04.2019      | 15.05.2019      | Organisasjon     | NORMALT             |
      | 16.05.2019      | 15.06.2019      | Organisasjon     | NORMALT             |
      | 16.06.2019      | 15.07.2019      | Organisasjon     | NORMALT             |
      | 16.07.2019      | 15.08.2019      | Organisasjon     | NORMALT             |
      | 16.08.2019      | 15.09.2019      | Organisasjon     | NORMALT             |
      | 16.09.2019      | 15.10.2019      | Organisasjon     | NORMALT             |
      | 16.10.2019      | 15.11.2019      | Organisasjon     | NORMALT             |
      | 16.11.2019      |                 | Organisasjon     | NORMALT             |

    Når regel "3" kjøres med følgende parametre
      | Fra og med dato | Til og med dato | Har hatt arbeid utenfor Norge |
      | 30.01.2020      | 30.01.2021      | Nei                           |

    Så skal svaret være "Nei"


  Scenariomal: Person med to arbeidsforhold i perioden
    Gitt følgende arbeidsforhold fra AAReg
      | Fra og med dato | Til og med dato | Arbeidsgivertype | Arbeidsforholdstype |
      | <Fom>           | <Tom>           | Organisasjon     | NORMALT             |
      | <Fom 2>         | <Tom 2>         | Organisasjon     | NORMALT             |

    Når regel "3" kjøres med følgende parametre
      | Fra og med dato | Til og med dato | Har hatt arbeid utenfor Norge |
      | 30.01.2020      | 30.01.2021      | Nei                           |

    Så skal svaret være "<Svar>"

    Eksempler:
      | Fom        | Tom        | Fom 2      | Tom 2      | Svar | Kommentar                                   |
      | 01.01.2019 | 15.03.2019 | 19.03.2019 |            | Nei  | Arbeidsforhold med tre dager opphold        |
      | 01.01.2019 | 15.03.2019 | 17.04.2019 |            | Nei  | Arbeidsforhold som ikke er sammenhengende   |
      | 01.01.2016 | 15.03.2019 | 16.03.2019 | 29.05.2019 | Nei  | Mangler arbeidsforhold i bakkant            |
      | 01.03.2019 | 15.03.2019 | 16.03.2019 | 29.05.2019 | Nei  | Mangler arbeidsforhold i forkant og bakkant |
      | 01.03.2019 | 15.03.2019 | 16.03.2019 |            | Nei  | Mangler arbeidsforhold i forkant            |
      | 01.01.2019 | 15.03.2019 | 18.03.2019 |            | Ja   | Arbeidsforhold med to dagers opphold        |


  Scenario: Person med mange arbeidsforhold i perioden
    Gitt følgende arbeidsforhold fra AAReg
      | Fra og med dato | Til og med dato | Arbeidsgivertype | Arbeidsforholdstype |
      | 08.04.2019      | 30.09.2019      | Organisasjon     | NORMALT             |
      | 01.10.2019      | 31.12.2020      | Organisasjon     | NORMALT             |
      | 01.01.2020      | 31.12.2020      | Organisasjon     | NORMALT             |
      | 01.01.2020      |                 | Organisasjon     | NORMALT             |
      | 24.08.2020      | 25.08.2020      | Organisasjon     | NORMALT             |

    Når regel "3" kjøres med følgende parametre
      | Fra og med dato | Til og med dato | Har hatt arbeid utenfor Norge |
      | 24.09.2020      | 15.10.2020      | Nei                           |

    Så skal svaret være "Ja"

  Scenariomal: Hvis "første dag for ytelse" er angitt, så skal kontrollperioden være 12 måneder før denne datoen.
    Gitt følgende arbeidsforhold fra AAReg
      | Fra og med dato | Til og med dato | Arbeidsgivertype | Arbeidsforholdstype |
      | 01.01.2019      |                 | Organisasjon     | NORMALT             |

    Når regel "3" kjøres med følgende parametre
      | Fra og med dato | Til og med dato | Første dag for ytelse   | Har hatt arbeid utenfor Norge |
      | 30.01.2020      | 30.01.2020      | <Første dag for ytelse> | Nei                           |

    Så skal svaret være "<Regel 3>"

    Eksempler:
      | Første dag for ytelse | Regel 3 |
      |                       | Ja      |
      | 30.01.2020            | Ja      |
      | 01.01.2020            | Ja      |
      | 31.12.2019            | Nei     |
      | 01.11.2018            | Nei     |