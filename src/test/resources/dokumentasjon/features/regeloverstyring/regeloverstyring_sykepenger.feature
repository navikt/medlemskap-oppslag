# language: no
# encoding: UTF-8

Egenskap: Regel 0.5: Skal regler overstyres?

  Scenariomal: Regel 0.5 - Overstyring av regler 3, 5 og 12 hvis følgende kriterier er oppfylt:
  - Ytelse sykepenger
  - Norsk statsborger
  - Bruker har svart nei på brukerspørsmålet "Har du jobbet utenfor Norge siste 12 måneder?"
  - TODO: Regelen skal bare gjelde brukere som ikke er frilansere eller selvstendig næringsdrivende

    Gitt følgende statsborgerskap i personhistorikken
      | Landkode          | Fra og med dato | Til og med dato |
      | <Statsborgerskap> | 01.01.2000      |                 |

    Når regel "0.5" kjøres med følgende parametre
      | Fra og med dato | Til og med dato | Har hatt arbeid utenfor Norge | Ytelse   |
      | 30.01.2020      | 30.01.2021      | <Arbeid utenfor Norge?>       | <Ytelse> |

    Så skal svaret være "<Regel 0.5>"
    Og skal begrunnelsen være som definert i RegelId
    Og skal avklaringen være som definert i RegelId

    Eksempler:
      | Statsborgerskap | Ytelse     | Arbeid utenfor Norge? | Regel 0.5 | Begrunnelse                                                                   |
      | NOR             | DAGPENGER  | Nei                   | Nei       |                                                                               |
      | NOR             | DAGPENGER  | Ja                    | Nei       |                                                                               |
      | NOR             | SYKEPENGER | Nei                   | Ja        | Svar på brukerspørsmålet er Nei, og regler 3, 5 og 12 skal derfor overstyres. |
      | NOR             | SYKEPENGER | Ja                    | Nei       |                                                                               |
      | BEL             | DAGPENGER  | Nei                   | Nei       |                                                                               |
      | BEL             | DAGPENGER  | Ja                    | Nei       |                                                                               |
      | BEL             | SYKEPENGER | Nei                   | Nei       |                                                                               |
      | BEL             | SYKEPENGER | Ja                    | Nei       |                                                                               |