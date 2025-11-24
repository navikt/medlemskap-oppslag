# language: no
# encoding: UTF-8

Egenskap: Regel 70: Har bruker EOS eller EFTA opphold?

  Scenariomal: Kontrollperioden overlapper med oppholdstillatelsen


    Gitt følgende oppholdstillatelse
      | Klasse              | Gyldig fra og med | Gyldig til og med | EOSEllerEFTAOpphold                       | EOSellerEFTAGrunnlagskategoriOppholdstillatelse |
      | EOSellerEFTAOpphold | 01.12.2018        | 01.12.2023        | EOS_ELLER_EFTA_BESLUTNING_OM_OPPHOLDSRETT | ARBEID                                          |


    Når regel "70" kjøres med følgende parametre
      | Fra og med dato | Til og med dato | Har hatt arbeid utenfor Norge |
      | 30.01.2020      | 30.01.2021      | Nei                           |

    Så skal svaret være "<Svar>"
    Og skal avklaringen være som definert i RegelId
    Og skal begrunnelsen være som definert i RegelId

    Eksempler:
      | Svar |
      | Ja   |

  Scenariomal: Kontrollperioden overlapper ikke med oppholdstillatelsen
    Gitt følgende oppholdstillatelse
      | Klasse              | Gyldig fra og med | Gyldig til og med | EOSEllerEFTAOpphold                       | EOSellerEFTAGrunnlagskategoriOppholdstillatelse |
      | EOSellerEFTAOpphold | 01.12.2016        | 01.12.2017        | EOS_ELLER_EFTA_BESLUTNING_OM_OPPHOLDSRETT | ARBEID                                          |


    Når regel "70" kjøres med følgende parametre
      | Fra og med dato | Til og med dato | Har hatt arbeid utenfor Norge |
      | 30.01.2020      | 30.01.2021      | Nei                           |

    Så skal svaret være "<Svar>"
    Og skal avklaringen være som definert i RegelId
    Og skal begrunnelsen være som definert i RegelId

    Eksempler:
      | Svar |
      | Nei  |

  Scenariomal: Finnes ingen oppholdstillatelse
    Gitt følgende oppholdstillatelse
      | Klasse              | Gyldig fra og med | Gyldig til og med | EOSEllerEFTAOpphold                       | EOSellerEFTAGrunnlagskategoriOppholdstillatelse |
      | EOSellerEFTAOpphold |                   |                   | EOS_ELLER_EFTA_BESLUTNING_OM_OPPHOLDSRETT | ARBEID                                          |


    Når regel "70" kjøres med følgende parametre
      | Fra og med dato | Til og med dato | Har hatt arbeid utenfor Norge |
      | 30.01.2021      | 30.01.2022      | Nei                           |

    Så skal svaret være "<Svar>"
    Og skal avklaringen være som definert i RegelId
    Og skal begrunnelsen være som definert i RegelId

    Eksempler:
      | Svar |
      | Nei  |

  Scenariomal: Kontrollperioden overlapper ikke med varig oppholdstillatelse
    Gitt følgende oppholdstillatelse
      | Klasse              | Gyldig fra og med | Gyldig til og med | EOSEllerEFTAOpphold                       | EOSellerEFTAGrunnlagskategoriOppholdstillatelse |
      | EOSellerEFTAOpphold | 30.01.2022        |                   | EOS_ELLER_EFTA_BESLUTNING_OM_OPPHOLDSRETT | ARBEID                                          |


    Når regel "70" kjøres med følgende parametre
      | Fra og med dato | Til og med dato | Har hatt arbeid utenfor Norge |
      | 30.01.2021      | 30.01.2022      | Nei                           |

    Så skal svaret være "<Svar>"
    Og skal avklaringen være som definert i RegelId
    Og skal begrunnelsen være som definert i RegelId

    Eksempler:
      | Svar |
      | Nei  |

  Scenariomal: Kontrollperioden overlapper med varig oppholdstillatelse
    Gitt følgende oppholdstillatelse
      | Klasse              | Gyldig fra og med | Gyldig til og med | EOSEllerEFTAOpphold                       | EOSellerEFTAGrunnlagskategoriOppholdstillatelse |
      | EOSellerEFTAOpphold | 30.12.2019        |                   | EOS_ELLER_EFTA_BESLUTNING_OM_OPPHOLDSRETT | ARBEID                                          |


    Når regel "70" kjøres med følgende parametre
      | Fra og med dato | Til og med dato | Har hatt arbeid utenfor Norge |
      | 30.01.2021      | 30.01.2022      | Nei                           |

    Så skal svaret være "<Svar>"
    Og skal avklaringen være som definert i RegelId
    Og skal begrunnelsen være som definert i RegelId

    Eksempler:
      | Svar |
      | Ja   |