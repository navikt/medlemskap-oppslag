# language: no
# encoding: UTF-8

Egenskap: Regel 15: Har bruker permittering?

  Scenario: Bruker med permittering får "Ja"

    Gitt følgende arbeidsforhold fra AAReg
      | Fra og med dato | Til og med dato | Arbeidsgivertype | Arbeidsforholdstype |
      | 01.01.2018      |                 | Organisasjon     | NORMALT             |

    Og følgende permisjonspermitteringer i arbeidsforholdet
      | Fra og med dato | Til og med dato | PermisjonPermitteringId | Prosent | Type         | Varslingkode  |
      | 10.10.2019      | 01.08.2020      | permisjonPermitteringId | 100     | PERMITTERING | varslingskode |

    Når regel "15" kjøres med følgende parametre
      | Fra og med dato | Til og med dato | Har hatt arbeid utenfor Norge |
      | 30.01.2020      | 30.01.2021      | Nei                           |

    Så skal svaret være "Ja"

  Scenariomal: Bruker med permisjon får "Nei"

    Gitt følgende arbeidsforhold fra AAReg
      | Fra og med dato | Til og med dato | Arbeidsgivertype | Arbeidsforholdstype |
      | 01.01.2018      |                 | Organisasjon     | NORMALT             |

    Og følgende permisjonspermitteringer i arbeidsforholdet
      | Fra og med dato | Til og med dato | PermisjonPermitteringId | Prosent | Type             | Varslingkode  |
      | 10.10.2019      | 01.08.2020      | permisjonPermitteringId | 100     | <permisjonstype> | varslingskode |

    Når regel "15" kjøres med følgende parametre
      | Fra og med dato | Til og med dato | Har hatt arbeid utenfor Norge |
      | 30.01.2020      | 30.01.2021      | Nei                           |

    Så skal svaret være "Nei"

    Eksempler:
      | permisjonstype                     |
      | PERMISJON                          |
      | PERMISJON_MED_FORELDREPENGER       |
      | PERMISJON_VED_MILITAERTJENESTE     |
      | UTDANNINGSPERMISJON                |
      | VELFERDSPERMISJON                  |
      | ANDRE_IKKE_LOVFESTEDE_PERMISJONER  |
      | ANDRE_LOVFESTEDE_PERMISJONER       |
      | UTDANNINGSPERMISJON_IKKE_LOVFESTET |
      | UTDANNINGSPERMISJON_LOVFESTET      |
      | ANNET                              |

  Scenariomal: Tester grensedatoer

    Gitt følgende arbeidsforhold fra AAReg
      | Fra og med dato | Til og med dato | Arbeidsgivertype | Arbeidsforholdstype |
      | 01.01.2018      |                 | Organisasjon     | NORMALT             |

    Og følgende permisjonspermitteringer i arbeidsforholdet
      | Fra og med dato | Til og med dato | PermisjonPermitteringId | Prosent | Type         | Varslingkode  |
      | <Fom>           | <Tom>           | permisjonPermitteringId | 100     | PERMITTERING | varslingskode |

    Når regel "15" kjøres med følgende parametre
      | Fra og med dato | Til og med dato | Har hatt arbeid utenfor Norge |
      | 30.01.2020      | 30.01.2021      | Nei                           |

    Så skal svaret være "<Svar>"

    Eksempler:
      | Fom        | Tom        | Svar |
      | 10.01.2019 | 01.08.2019 | Nei  |
      | 10.10.2019 | 01.08.2020 | Ja   |
      | 01.02.2020 | 01.08.2020 | Ja   |
      | 10.01.2019 | 01.02.2021 | Ja   |
      | 10.02.2020 | 01.02.2021 | Ja   |
      | 10.02.2021 | 01.03.2022 | Nei  |