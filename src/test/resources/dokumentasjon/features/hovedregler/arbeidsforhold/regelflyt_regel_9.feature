# language: no
# encoding: UTF-8

Egenskap: Regelflyt for regel 9

  Bakgrunn: Belgisk statsborger som er bosatt i Norge
    Gitt følgende bostedsadresser i personhistorikken
      | Adresse | Landkode | Fra og med dato | Til og med dato |
      | Oslo    | NOR      | 01.01.2000      |                 |

    Og følgende statsborgerskap i personhistorikken
      | Landkode | Fra og med dato | Til og med dato |
      | BEL      | 01.01.2000      |                 |

    Og følgende arbeidsforhold fra AAReg
      | Fra og med dato | Til og med dato | Arbeidsgivertype | Arbeidsforholdstype |
      | 01.01.2018      |                 | Organisasjon     | NORMALT             |

    Og følgende arbeidsgiver i arbeidsforholdet
      | Identifikator | Arbeidsgivertype | Landkode | Antall ansatte |
      | 1             | BEDR             | NOR      | 9              |

  Scenariomal:
    Gitt følgende arbeidsavtaler i arbeidsforholdet
      | Fra og med dato | Til og med dato | Yrkeskode | Stillingsprosent |
      | 01.01.2018      |                 | 001       | 100              |

    Og følgende familerelasjoner i personhistorikk fra PDL
      | Relatert persons ident | Relatert persons rolle | Min rolle for person |
      | 09069534888            | BARN                   | FAR                  |

    Og følgende sivilstand i personhistorikk fra PDL
      | Sivilstandstype | Gyldig fra og med dato | Relatert ved sivilstand |
      | GIFT            | 29.06.2015             | 10108000398             |

    Og følgende personhistorikk for ektefelle fra PDL
      | Ident       | Bosted | Fra og med dato |
      | 10108000398 | NOR    | 29.06.2015      |

    Og følgende barn i personhistorikk for ektefelle fra PDL
      | Ident       |
      | 09069534888 |

    Og følgende personhistorikk for barn fra PDL
      | Ident       | Bosted | Fra og med dato |
      | 09069534888 | NOR    | 18.07.2010      |

    Når medlemskap beregnes med følgende parametre
      | Fra og med dato | Til og med dato | Har hatt arbeid utenfor Norge   |
      | 30.01.2020      | 30.01.2021      | <Har hatt arbeid utenfor Norge> |

    Så skal svaret være "<Medlemskap>"
    Og skal regel "10" gi svaret "Ja"
    Og skal regel "11" gi svaret "Nei"
    Og skal regel "11.2" gi svaret "Ja"
    Og skal regel "11.3" gi svaret "Ja"
    Og skal regel "11.4" gi svaret "Ja"
    Og skal regel "11.5" gi svaret "Ja"
    Og skal regel "11.6" gi svaret "Ja"
    Og skal regel "9" gi svaret "<Regel 9>"

    Eksempler:
      | Har hatt arbeid utenfor Norge | Regel 9 | Medlemskap |
      | Ja                            | Ja      | UAVKLART   |
      | Nei                           | Nei     | Ja         |

