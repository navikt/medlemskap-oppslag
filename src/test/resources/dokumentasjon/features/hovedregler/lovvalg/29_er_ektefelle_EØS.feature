# language: no
# encoding: UTF-8


Egenskap: Regel 29: Er ektefelle EØS borger?

  Scenariomal: Regel 29 - Ektefelle er EØS borger
    Gitt følgende sivilstand i personhistorikk fra PDL
      | Sivilstandstype | Gyldig fra og med dato | Gyldig til og med dato | Relatert ved sivilstand |
      | GIFT            | 29.06.2015             |                        | 10108000398             |

    Og følgende personhistorikk for ektefelle fra PDL
      | Ident       | Bosted | Statsborgerskap   | Fra og med dato | Til og med dato |
      | 10108000398 | NOR    | <Statsborgerskap> | 18.07.2010      |                 |


    Når regel "29" kjøres med følgende parametre
      | Fra og med dato | Til og med dato | Har hatt arbeid utenfor Norge |
      | 30.01.2020      | 30.01.2021      | Nei                           |

    Så skal svaret være "<Svar>"

    Eksempler:
      | Statsborgerskap | Svar |
      | DEU             | Ja   |
      | JPN             | Nei  |

  Scenariomal: Regel 29 - Registrert partner er EØS borger
    Gitt følgende sivilstand i personhistorikk fra PDL
      | Sivilstandstype    | Gyldig fra og med dato | Gyldig til og med dato | Relatert ved sivilstand |
      | REGISTRERT_PARTNER | 29.06.2015             |                        | 10108000398             |

    Og følgende personhistorikk for ektefelle fra PDL
      | Ident       | Bosted | Statsborgerskap   | Fra og med dato | Til og med dato |
      | 10108000398 | NOR    | <Statsborgerskap> | 18.07.2010      |                 |

    Når regel "29" kjøres med følgende parametre
      | Fra og med dato | Til og med dato | Har hatt arbeid utenfor Norge |
      | 30.01.2020      | 30.01.2021      | Nei                           |

    Så skal svaret være "<Svar>"

    Eksempler:
      | Statsborgerskap | Svar |
      | DEU             | Ja   |
      | JPN             | Nei  |


  Scenario: Regel 28 - Sivilstatus bestemmes av sivilstatus dagen før inputperiodens fra og med dato
    Gitt følgende sivilstand i personhistorikk fra PDL
      | Sivilstandstype | Gyldig fra og med dato | Gyldig til og med dato | Relatert ved sivilstand |
      | GIFT            | 29.06.2015             | 15.01.2020             | 10108000398             |
      | SEPARERT        | 16.01.2020             |                        | 10108000398             |


    Når regel "28" kjøres med følgende parametre
      | Fra og med dato | Til og med dato | Har hatt arbeid utenfor Norge |
      | 30.01.2020      | 30.01.2021      | Nei                           |

    Så skal svaret være "Nei"


  Scenario: Regel 28 - Fødselsnummer med fem nuller
    Gitt følgende sivilstand i personhistorikk fra PDL
      | Sivilstandstype | Gyldig fra og med dato | Gyldig til og med dato | Relatert ved sivilstand |
      | GIFT            | 29.06.2015             |                        | 10108000000             |


    Når regel "28" kjøres med følgende parametre
      | Fra og med dato | Til og med dato | Har hatt arbeid utenfor Norge |
      | 30.01.2020      | 30.01.2021      | Nei                           |

    Så skal svaret være "Nei"

  Scenario: Regel 28 - Bruker er gift, men "Relatert ved sivilstand" mangler
    Gitt følgende sivilstand i personhistorikk fra PDL
      | Sivilstandstype | Gyldig fra og med dato | Gyldig til og med dato | Relatert ved sivilstand |
      | GIFT            | 29.06.2015             |                        |                         |

    Når regel "28" kjøres med følgende parametre
      | Fra og med dato | Til og med dato | Har hatt arbeid utenfor Norge |
      | 30.01.2020      | 30.01.2021      | Nei                           |

    Så skal svaret være "Nei"