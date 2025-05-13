# language: no
# encoding: UTF-8

Egenskap: Regel 58: Er periode avsluttet for mer en 30 dager siden og permisjonen er kortere enn 15 dager ?

  Regel: Regel 58: Er periode avsluttet for mer en 30 dager siden og permisjonen er kortere enn 15 dager ?

    Scenariomal: Person som har ett arbeidsforhold uten permisjoner får Nei
      Gitt følgende arbeidsforhold fra AAReg
        | Fra og med dato | Til og med dato | Arbeidsgivertype | Arbeidsforholdstype |
        | <Fra og med>    | <Til og med>    | Organisasjon     | NORMALT             |


      Når regel "58" kjøres med følgende parametre
        | Fra og med dato | Til og med dato | Har hatt arbeid utenfor Norge |
        | 30.01.2020      | 30.01.2021      | Nei                           |

      Så skal svaret være "<Svar>"

      Eksempler:
        | Fra og med | Til og med | Svar |
        | 01.01.2018 |            | Nei  |

    Scenariomal: Permisjoner som er avsluttet for mer enn 30 dager siden første dag syk og er kortere enn 15 dager får Ja
      Gitt følgende arbeidsforhold fra AAReg
        | Fra og med dato | Til og med dato | Arbeidsgivertype | Arbeidsforholdstype |
        | 30.01.2015      |                 | Organisasjon     | NORMALT             |

      Og følgende permisjonspermitteringer i arbeidsforholdet
        | Fra og med dato | Til og med dato | PermisjonPermitteringId | Prosent | Type              | Varslingkode  |
        | <Fra og med>    | <Til og med>    | permisjonPermitteringId | 100     | VELFERDSPERMISJON | varslingskode |

      Når regel "58" kjøres med følgende parametre
        | Fra og med dato | Til og med dato | Har hatt arbeid utenfor Norge |
        | 15.03.2024      | 31.03.2024      | Nei                           |

      Så skal svaret være "<Svar>"

      Eksempler:
        | Fra og med | Til og med | Svar | Kommentar                                                 |
        | 28.01.2024 | 29.01.2024 | Ja   | 1 dag permisjon og mer enn 30 dager mellom første dag syk |
        | 01.01.2024 | 15.01.2024 | Nei  | 15 dager permisjon                                        |
        | 20.02.2024 | 28.02.2024 | Nei  | 8 dager permisjon og 15 dager mellom  første dag syk      |
