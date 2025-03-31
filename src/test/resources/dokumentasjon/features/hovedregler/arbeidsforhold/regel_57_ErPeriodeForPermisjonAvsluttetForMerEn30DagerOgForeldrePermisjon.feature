
# language: no
# encoding: UTF-8

Egenskap: Regel 57: Er Periode avsluttet for mer en 30 dager siden og foreldrePermisjon ?

  Regel: Regel 57: Er Periode avsluttet for mer en 30 dager siden og foreldrePermisjon ?

    Scenariomal: Person som har ett arbeidsforhold uten permisjoner får Ja
      Gitt følgende arbeidsforhold fra AAReg
        | Fra og med dato | Til og med dato | Arbeidsgivertype | Arbeidsforholdstype |
        | <Fra og med>    | <Til og med>    | Organisasjon     | NORMALT             |


      Når regel "57" kjøres med følgende parametre
        | Fra og med dato | Til og med dato | Har hatt arbeid utenfor Norge |
        | 30.01.2020      | 30.01.2021      | Nei                           |

      Så skal svaret være "<Svar>"

      Eksempler:
        | Fra og med | Til og med | Svar |
        | 01.01.2018 |            | Ja  |

    Scenariomal: Er alle permisjoner avsluttet for med en 30 dager siden OG de er av typen PERMISJON_MED_FORELDREPENGER
      Gitt følgende arbeidsforhold fra AAReg
        | Fra og med dato | Til og med dato | Arbeidsgivertype | Arbeidsforholdstype |
        | 30.01.1970      |                 | Organisasjon     | NORMALT             |

      Og følgende permisjonspermitteringer i arbeidsforholdet
        | Fra og med dato | Til og med dato | PermisjonPermitteringId | Prosent   | Type                         | Varslingkode  |
        | <Fra og med>      | <Til og med>  | permisjonPermitteringId | 10        | PERMISJON_MED_FORELDREPENGER | varslingskode |

      Når regel "57" kjøres med følgende parametre
        | Fra og med dato | Til og med dato | Har hatt arbeid utenfor Norge |
        | 29.02.2024      | 30.03.2024      | Nei                           |

      Så skal svaret være "<Svar>"

      Eksempler:
        | Fra og med | Til og med | Svar |
        | 28.01.2024 | 28.01.2024 | Ja   |
        | 29.01.2024 | 29.01.2024 | Nei  |
        | 01.03.2024 | 01.03.2024 | Nei  |


    Scenariomal: Person som har en permisjon type barn som er avsluttet for mer en 30 dager siden som ikke har BarnePensjon
      Gitt følgende arbeidsforhold fra AAReg
        | Fra og med dato | Til og med dato | Arbeidsgivertype | Arbeidsforholdstype |
        | 30.01.1970      |                 | Organisasjon     | NORMALT             |

      Og følgende permisjonspermitteringer i arbeidsforholdet
        | Fra og med dato | Til og med dato | PermisjonPermitteringId | Prosent   | Type      | Varslingkode  |
        | <Fra og med>      | <Til og med>  | permisjonPermitteringId | 10        | PERMISJON | varslingskode |

      Når regel "57" kjøres med følgende parametre
        | Fra og med dato | Til og med dato | Har hatt arbeid utenfor Norge |
        | 29.02.2024      | 30.03.2024      | Nei                           |

      Så skal svaret være "<Svar>"

      Eksempler:
        | Fra og med | Til og med | Svar |
        | 28.01.2024 | 28.01.2024 | Nei  |
        | 01.03.2024 | 01.03.2024 | Nei  |
