# language: no
# encoding: UTF-8


Egenskap: Regel 17: Er bruker frilanser?

  Scenariomal: Bruker med frilanser arbeidsforholdtype får "Ja"
    Gitt følgende arbeidsforhold fra AAReg
      | Fra og med dato | Til og med dato | Arbeidsgivertype | Arbeidsforholdstype   |
      | 01.01.2018      |                 | Organisasjon     | <Arbeidsforholdstype> |

    Og følgende arbeidsgiver i arbeidsforholdet
      | Identifikator | Arbeidsgivertype | Antall ansatte | Konkursstatus |
      | 1             | BEDR             | 9              |               |

    Når regel "17" kjøres med følgende parametre
      | Fra og med dato | Til og med dato | Har hatt arbeid utenfor Norge |
      | 30.01.2020      | 30.01.2021      | Nei                           |

    Så skal svaret være "<Svar>"

    Eksempler:
      | Arbeidsforholdstype | Svar |
      | NORMALT             | Nei  |
      | FRILANSER           | Ja   |


  Scenario: Regel 17: Bruker uten arbeidsforhold får "Nei"

    Når regel "17" kjøres med følgende parametre
      | Fra og med dato | Til og med dato | Har hatt arbeid utenfor Norge |
      | 30.01.2020      | 30.01.2021      | Nei                           |

    Så skal svaret være "Nei"

