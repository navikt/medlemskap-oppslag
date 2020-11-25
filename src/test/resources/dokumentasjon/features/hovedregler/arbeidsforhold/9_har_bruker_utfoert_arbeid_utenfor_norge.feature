# language: no
# encoding: UTF-8

Egenskap: Regel 9: Har bruker utført arbeid utenfor Norge?

  Scenariomal: Regel 9: Har bruker utført arbeid utenfor Norge?

    Når regel "9" kjøres med følgende parametre
      | Fra og med dato | Til og med dato | Har hatt arbeid utenfor Norge |
      | 30.01.2020      | 30.01.2021      | <Arbeid utenfor Norge>        |

    Så skal svaret være "<Svar>"
    Og skal avklaringen være som definert i RegelId
    Og skal begrunnelse utfylt være "<Utfylt begrunnelse>"

    Eksempler:
      | Arbeid utenfor Norge | Svar | Utfylt begrunnelse |
      | Ja                   | Ja   | Ja                 |
      | Nei                  | Nei  | Nei                |
