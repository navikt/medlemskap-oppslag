# language: no
# encoding: UTF-8


Egenskap: Regel 7.1: Er bruker ansatt på et norsk skip?

  Scenariomal: Regel 7.1 Bruker som er ansatt på et norsk skip får "Ja"
    Gitt følgende arbeidsforhold fra AAReg
      | Fra og med dato | Til og med dato | Arbeidsgivertype | Arbeidsforholdstype |
      | 01.01.2018      |                 | Organisasjon     | MARITIMT            |

    Og følgende arbeidsgiver i arbeidsforholdet
      | Identifikator | Arbeidsgivertype | Antall ansatte | Konkursstatus |
      | 1             | BEDR             | 9              |               |

    Og følgende arbeidsavtaler i arbeidsforholdet
      | Fra og med dato | Til og med dato | Yrkeskode | Skipsregister   | Stillingsprosent |
      | 01.01.2018      |                 | 001       | <Skipsregister> | 100              |


    Når regel "7.1" kjøres med følgende parametre
      | Fra og med dato | Til og med dato | Har hatt arbeid utenfor Norge |
      | 30.01.2020      | 30.01.2021      | Nei                           |

    Så skal svaret være "<Svar>"
    Og skal avklaringen være som definert i RegelId
    Og skal begrunnelse utfylt være "<Utfylt begrunnelse>"

    Eksempler:
      | Skipsregister | Svar | Utfylt begrunnelse |
      | NIS           | Nei  | Ja                 |
      | NOR           | Ja   | Nei                |
      | UTL           | Nei  | Ja                 |
      |               | Nei  | Ja                 |
