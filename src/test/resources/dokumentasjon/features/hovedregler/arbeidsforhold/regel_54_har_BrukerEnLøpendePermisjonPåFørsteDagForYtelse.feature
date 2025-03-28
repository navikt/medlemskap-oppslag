
# language: no
# encoding: UTF-8

Egenskap: Regel 54: Har bruker overlappende permisjoner ?

  Regel: Regel 54: Har bruker overlappende permisjoner ?

    Scenariomal: Person som har ett arbeidsforhold uten permisjoner får Nei
      Gitt følgende arbeidsforhold fra AAReg
        | Fra og med dato | Til og med dato | Arbeidsgivertype | Arbeidsforholdstype |
        | <Fra og med>    | <Til og med>    | Organisasjon     | NORMALT             |


      Når regel "54" kjøres med følgende parametre
        | Fra og med dato | Til og med dato | Har hatt arbeid utenfor Norge |
        | 30.01.2020      | 30.01.2021      | Nei                           |

      Så skal svaret være "<Svar>"

      Eksempler:
        | Fra og med | Til og med | Svar |
        | 01.01.2018 |            | Nei  |

    Scenariomal: Person som har en permisjon som overlapper
      Gitt følgende arbeidsforhold fra AAReg
        | Fra og med dato | Til og med dato | Arbeidsgivertype | Arbeidsforholdstype |
        | 30.01.1970      |                 | Organisasjon     | NORMALT             |

      Og følgende permisjonspermitteringer i arbeidsforholdet
        | Fra og med dato | Til og med dato | PermisjonPermitteringId | Prosent   | Type      | Varslingkode  |
        | <Fra og med>      | <Til og med>     | permisjonPermitteringId | 10        | PERMISJON | varslingskode |

      Når regel "54" kjøres med følgende parametre
        | Fra og med dato | Til og med dato | Har hatt arbeid utenfor Norge |
        | 30.01.2020      | 30.02.2021      | Nei                           |

      Så skal svaret være "<Svar>"

      Eksempler:
        | Fra og med | Til og med | Svar |
        | 29.01.2020 | 30.02.2020 | Ja  |
        | 29.01.2018 | 30.02.2019 | Nei  |

