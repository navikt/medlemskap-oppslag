# language: no
# encoding: UTF-8

Egenskap: Regel 4: Er foretaket registrert i Foretaksregisteret?

  Scenariomal: Regel 4: Er foretaket registrert i Foretaksregisteret?
    Gitt følgende arbeidsforhold fra AAReg
      | Fra og med dato | Til og med dato | Arbeidsgivertype   | Arbeidsforholdstype |
      | 01.01.2018      |                 | <Arbeidsgivertype> | NORMALT             |

    Når regel "4" kjøres med følgende parametre
      | Fra og med dato | Til og med dato | Har hatt arbeid utenfor Norge |
      | 30.01.2020      | 30.01.2021      | Nei                           |

    Så skal svaret være "<Svar>"

    Eksempler:
      | Arbeidsgivertype | Svar |
      | Organisasjon     | Ja   |
      | Person           | Nei  |

