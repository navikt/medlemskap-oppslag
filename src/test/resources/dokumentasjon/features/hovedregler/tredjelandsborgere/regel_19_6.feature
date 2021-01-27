# language: no
# encoding: UTF-8

Egenskap: Regel 19.6: Er oppholdstillatelse uavklart?

  Scenario: Regel 19.6 - En uavklart oppholdstillatelse

    Gitt følgende oppholdstillatelse
      | Gyldig fra og med | Gyldig til og med | Klasse   |
      | 27.01.2021        |                   | Uavklart |

    Når regel "19.6" kjøres med følgende parametre
      | Fra og med dato | Til og med dato | Har hatt arbeid utenfor Norge |
      | 30.01.2020      | 30.01.2021      | Nei                           |

    Så skal svaret være "Ja"
    Og skal avklaringen være som definert i RegelId
    Og skal begrunnelsen være som definert i RegelId

  Scenario: Regel 19.6 - En avklart oppholdstillatelse av type OppholdstillaelsePaSammeVilkarType

    Gitt følgende oppholdstillatelse
      | Gyldig fra og med | Gyldig til og med | Klasse                          | Type      |
      | 27.01.2021        |                   | OppholdstillatelsePaSammeVilkar | PERMANENT |

    Når regel "19.6" kjøres med følgende parametre
      | Fra og med dato | Til og med dato | Har hatt arbeid utenfor Norge |
      | 30.01.2021      | 12.02.2021      | Nei                           |

    Så skal svaret være "Nei"
    Og skal avklaringen være som definert i RegelId
    Og skal begrunnelsen være som definert i RegelId