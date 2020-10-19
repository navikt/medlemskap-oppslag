# language: no
# encoding: UTF-8

Egenskap: Mapping av bostedsadresser fra PDL HentPerson.Bostedsadresse

  Scenario: En person som har flyttet flere ganger i Norge
    Gitt følgende bostedsadresser fra PDL:
      | Gyldig fra og med | Gyldig yil og med |
      |                   |                   |
      | 2015-03-25        |                   |
      | 2018-05-20        |                   |

    Når bostedsadresser mappes

    Så skal mappede bostedsadresser være
      | Landkode | Fra og med dato | Til og med dato |
      | NOR      |                 |                 |
      | NOR      | 2015-03-25      |                 |
      | NOR      | 2018-05-20      |                 |

  Scenario: En person som har flyttet utenlands
    Gitt følgende bostedsadresser fra PDL:
      | Gyldig fra og med | Gyldig til og med |
      | 2015-03-25        |                   |
      | 2018-05-20        | 2020-05-20        |

    Når bostedsadresser mappes

    Så skal mappede bostedsadresser være
      | Landkode | Fra og med dato | Til og med dato |
      | NOR      | 2015-03-25      |                 |
      | NOR      | 2018-05-20      | 2020-05-20      |


  Scenario: En person som har flyttet utenlands, og som senere har flyttet tilbake til Norge
    Gitt følgende bostedsadresser fra PDL:
      | Gyldig fra og med | Gyldig til og med |
      | 2008-05-20        | 2010-05-20        |
      | 2018-05-20        |                   |

    Når bostedsadresser mappes

    Så skal mappede bostedsadresser være
      | Landkode | Fra og med dato | Til og med dato |
      | NOR      | 2008-05-20      | 2010-05-20      |
      | NOR      | 2018-05-20      |                 |

  Scenario: Bostedsadresser skal sorteres på "Fra og med dato", stigende
    Gitt følgende bostedsadresser fra PDL:
      | Gyldig fra og med | Gyldig til og med |
      | 2018-05-20        | 2019-05-20        |
      |                   |                   |
      | 2008-05-20        |                   |

    Når bostedsadresser mappes

    Så skal mappede bostedsadresser være
      | Landkode | Fra og med dato | Til og med dato |
      | NOR      |                 |                 |
      | NOR      | 2008-05-20      |                 |
      | NOR      | 2018-05-20      | 2019-05-20      |