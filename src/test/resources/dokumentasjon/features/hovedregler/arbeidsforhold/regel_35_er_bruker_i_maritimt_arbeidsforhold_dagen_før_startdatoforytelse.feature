# language: no
# encoding: UTF-8


Egenskap: Regel 35: Er bruker i et maritimt arbeidsforhold på tidspunktet inputdato minus 1 dag?

  Scenariomal: Bruker med maritimt arbeidsforhold får "Ja"
    Gitt følgende arbeidsforhold fra AAReg
      | Fra og med dato | Til og med dato | Arbeidsgivertype | Arbeidsforholdstype   |
      | 29.01.2020      |                 | Organisasjon     | <Arbeidsforholdstype> |

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


  Scenariomal: Bruker med maritimt arbeidsforhold får "Ja"
    Gitt følgende arbeidsforhold fra AAReg
      | Fra og med dato | Til og med dato | Arbeidsgivertype | Arbeidsforholdstype |
      | <fom>           | <tom>           | Organisasjon     | MARITIMT            |

    Og følgende arbeidsgiver i arbeidsforholdet
      | Identifikator | Arbeidsgivertype | Antall ansatte | Konkursstatus |
      | 1             | BEDR             | 9              |               |

    Når regel "35" kjøres med følgende parametre
      | Fra og med dato | Til og med dato | Har hatt arbeid utenfor Norge |
      | 30.01.2020      | 30.01.2021      | Nei                           |

    Så skal svaret være "<Svar>"

    Eksempler:
      | fom        | tom        | Svar |
      | 29.01.2020 |            | Ja   |
      | 30.01.2020 |            | Nei  |
      | 20.02.2020 |            | Nei  |
      | 01.01.2019 | 20.01.2020 | Nei  |