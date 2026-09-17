# language: no
# encoding: UTF-8

Egenskap: Regel 50: Har bruker ingen eller kun en permisjon siste 12 mnd ?

  Regel: Regel 50: Har bruker ingen eller kun en permisjon siste 12 mnd ?

    Scenariomal: Person som har ett arbeidsforhold uten permisjoner får "Ja"
      Gitt følgende arbeidsforhold fra AAReg
        | Fra og med dato | Til og med dato | Arbeidsgivertype | Arbeidsforholdstype |
        | <Fra og med>    | <Til og med>    | Organisasjon     | NORMALT             |


      Når regel "50" kjøres med følgende parametre
        | Fra og med dato | Til og med dato | Har hatt arbeid utenfor Norge |
        | 30.01.2020      | 30.01.2021      | Ja                           |

      Så skal svaret være "<Svar>"

      Eksempler:
        | Fra og med | Til og med | Svar |
        | 01.01.2018 |            | Ja  |



    Scenariomal: Person med flere arbeidsforhold og permisjoner får "Nei"
      Gitt følgende arbeidsforhold fra AAReg
        | Fra og med dato | Til og med dato | Arbeidsgivertype | Arbeidsforholdstype |
        | <Fra og med>    | <Til og med>    | Organisasjon     | NORMALT             |
        | <Fra og med>    | <Til og med>    | Organisasjon     | NORMALT             |


      Og følgende permisjonspermitteringer i arbeidsforholdet
        | Fra og med dato | Til og med dato | PermisjonPermitteringId | Prosent   | Type      | Varslingkode  |
        | 10.10.2019      | 01.08.2020      | permisjonPermitteringId | <PROSENT> | PERMISJON | varslingskode |

      Når regel "32" kjøres med følgende parametre
        | Fra og med dato | Til og med dato | Har hatt arbeid utenfor Norge |
        | 30.01.2020      | 30.01.2021      | Nei                           |

      Så skal svaret være "<Svar>"

      Eksempler:
        | Fra og med | Til og med | Svar | PROSENT |
        | 01.01.2018 |            | Ja   | 60      |
        | 01.01.2018 |            | Ja   | 100     |