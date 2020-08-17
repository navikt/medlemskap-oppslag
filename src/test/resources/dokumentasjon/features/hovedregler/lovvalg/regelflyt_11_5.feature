# language: no
# encoding: UTF-8

Egenskap: Regelflyt for reglene 11.5 for bruker som har ektefelle og barn. Ektefellen er bosatt i Norge.

  Bakgrunn: Belgisk statsborger som er bosatt i Norge og som jobber i Norge
    Gitt følgende bostedsadresser i personhistorikken
      | Adresse | Landkode | Fra og med dato | Til og med dato |
      | Oslo    | NOR      | 01.01.2000      |                 |

    Og følgende personstatuser i personhistorikken
      | Personstatus | Fra og med dato | Til og med dato |
      | BOSA         | 01.01.2000      |                 |

    Og følgende statsborgerskap i personhistorikken
      | Landkode | Fra og med dato | Til og med dato |
      | BEL      | 01.01.2000      |                 |

    Og følgende arbeidsforhold fra AAReg
      | Fra og med dato | Til og med dato | Arbeidsgivertype | Arbeidsforholdstype |
      | 01.01.2018      |                 | Organisasjon     | NORMALT             |

    Og følgende arbeidsgiver i arbeidsforholdet
      | Identifikator | Arbeidsgivertype | Landkode | Antall ansatte |
      | 1             | BEDR             | NOR      | 9              |

  Scenario: Barnet er ikke bosatt i Norge
    Gitt følgende arbeidsavtaler i arbeidsforholdet
      | Fra og med dato | Til og med dato | Yrkeskode | Stillingsprosent |
      | 01.01.2018      |                 | 001       | 100              |

    Og følgende familerelasjoner i personhistorikk fra TPS/PDL
      | Relatert persons ident | Relatert persons rolle | Min rolle for person |
      | 09069534888            | BARN                   | FAR                  |

    Og følgende sivilstand i personhistorikk fra TPS/PDL
      | Sivilstandstype | Gyldig fra og med dato | Relatert ved sivilstand |
      | GIFT            | 29.06.2015             | 10108000398             |

    Og følgende personhistorikk for relaterte personer fra TPS
      | Ident       | Bosted | Fra og med dato |
      | 10108000398 | NOR    | 29.06.2015      |
      | 09069534888 |        | 18.07.2010      |

    Når medlemskap beregnes med følgende parametre
      | Fra og med dato | Til og med dato | Har hatt arbeid utenfor Norge |
      | 30.01.2020      | 30.01.2021      | Nei                           |

    Så skal svaret være "UAVKLART"
    Og skal regel "11" gi svaret "Nei"
    Og skal regel "11.2" gi svaret "Ja"
    Og skal regel "11.3" gi svaret "Ja"
    Og skal regel "11.4" gi svaret "Ja"
    Og skal regel "11.5" gi svaret "Nei"

  Scenario: Ett barn som er bosatt i Norge og ett som ikke er bosatt i Norge
    Gitt følgende arbeidsavtaler i arbeidsforholdet
      | Fra og med dato | Til og med dato | Yrkeskode | Stillingsprosent |
      | 01.01.2018      |                 | 001       | 100              |

    Og følgende familerelasjoner i personhistorikk fra TPS/PDL
      | Relatert persons ident | Relatert persons rolle | Min rolle for person |
      | 09069534888            | BARN                   | FAR                  |
      | 10079541651            | BARN                   | FAR                  |

    Og følgende sivilstand i personhistorikk fra TPS/PDL
      | Sivilstandstype | Gyldig fra og med dato | Relatert ved sivilstand |
      | GIFT            | 29.06.2015             | 10108000398             |

    Og følgende personhistorikk for relaterte personer fra TPS
      | Ident       | Bosted | Fra og med dato |
      | 10108000398 | NOR    | 29.06.2015      |
      | 09069534888 | NOR    | 18.07.2010      |
      | 10079541651 |        | 18.07.2010      |

    Når medlemskap beregnes med følgende parametre
      | Fra og med dato | Til og med dato | Har hatt arbeid utenfor Norge |
      | 30.01.2020      | 30.01.2021      | Nei                           |

    Så skal svaret være "UAVKLART"
    Og skal regel "11" gi svaret "Nei"
    Og skal regel "11.2" gi svaret "Ja"
    Og skal regel "11.3" gi svaret "Ja"
    Og skal regel "11.4" gi svaret "Ja"
    Og skal regel "11.5" gi svaret "UAVKLART"
