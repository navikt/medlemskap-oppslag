# language: no
# encoding: UTF-8

Egenskap: Regel 1.3.3: Er unntaket etter USA- eller Canada-avtalen?

  Scenariomal: Regel 1.3.3:  Er unntaket etter USA- eller Canada-avtalen?

    Gitt følgende medlemsunntak fra MEDL
      | Fra og med dato | Til og med dato | Er medlem | Lovvalg | Lovvalgsland   | Periodestatus |
      | 01.01.2019      | 28.01.2020      | Nei       | ENDL    | <Lovvalgsland> | GYLD          |

    Når regel "1.3.3" kjøres med følgende parametre
      | Fra og med dato | Til og med dato | Har hatt arbeid utenfor Norge |
      | 30.01.2020      | 10.02.2020      | Nei                           |

    Så skal svaret være "<Svar>"
    Og skal avklaringen være som definert i RegelId
    Og skal begrunnelsen være som definert i RegelId

    Eksempler:
      | Lovvalgsland | Svar |
      | USA          | Ja   |
      | CAN          | Ja   |
      | BEL          | Nei  |