# language: no
# encoding: UTF-8

Egenskap: Regel 11.6: Har bruker vært i minst 80 % stilling de siste 12 mnd?

  Bakgrunn:
    Gitt følgende familerelasjoner i personhistorikk fra TPS/PDL
      | Relatert persons ident | Relatert persons rolle | Min rolle for person |
      | 18071076276            | BARN                   | FAR                  |

    Og følgende sivilstand i personhistorikk fra TPS/PDL
      | Sivilstandstype | Gyldig fra og med dato | Relatert ved sivilstand |
      | GIFT            | 29.06.2015             | 23027524079             |

    Og følgende personhistorikk for relaterte personer fra TPS
      | Ident       | Bosted | Fra og med dato | Til og med dato |
      | 23027524079 | NOR    | 18.07.2010      | 15.05.2019      |
      | 18071076276 | NOR    | 18.07.2010      | 15.05.2019      |

  Scenariomal: Er bruker med ektefelle sitt barn folkeregistrert?
    Gitt følgende arbeidsforhold fra AAReg
      | Fra og med dato | Til og med dato | Arbeidsgivertype | Arbeidsforholdstype |
      | 01.01.2018      |                 | Organisasjon     | NORMALT             |

    Og følgende arbeidsgiver i arbeidsforholdet
      | Identifikator | Arbeidsgivertype | Landkode | Antall ansatte |
      | 1             | BEDR             | NOR      | 9              |

    Og følgende arbeidsavtaler i arbeidsforholdet
      | Fra og med dato | Til og med dato | Yrkeskode | Stillingsprosent   |
      | 01.01.2018      |                 | 001       | <Stillingsprosent> |

    Når regel "11.6" kjøres med følgende parametre
      | Fra og med dato | Til og med dato | Har hatt arbeid utenfor Norge |
      | 30.01.2020      | 30.01.2021      | Nei                           |

    Så skal svaret være "<Svar>"

    Eksempler:
      | Stillingsprosent | Svar |
      | 70               | Nei  |
      | 80               | Ja   |
      | 90               | Ja   |