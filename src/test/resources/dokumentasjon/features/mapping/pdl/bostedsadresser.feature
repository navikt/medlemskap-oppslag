# language: no
# encoding: UTF-8

Egenskap: Mapping av bostedsadresser fra PDL HentPerson.Bostedsadresse

  Scenario: En person som har flyttet flere ganger i Norge
    Gitt følgende bostedsadresser fra PDL:
      | Folkeregistermetadata gyldighetstidspunkt | Folkeregistermetadata opphoerstidspunkt | Spørsmål til PDL                                                                   |
      |                                           |                                         | Bruke gyldig fra og med/til og med eller folkereg.metadata? Gjelder alle scenarier |
      | 2015-03-25 10:03:03                       |                                         |                                                                                    |
      | 2018-05-20 12:03:05                       |                                         |                                                                                    |


    //Snart tilgjengelig --> Utenlandsk bostedsadresse

    Når bostedsadresser mappes

    Så skal mappede bostedsadresser være
      | Landkode | Fra og med dato | Til og med dato |
      | NOR      |                 |                 |
      | NOR      | 2015-03-25      |                 |
      | NOR      | 2018-05-20      |                 |

  Scenario: En person som har flyttet utenlands
    Gitt følgende bostedsadresser fra PDL:
      | Folkeregistermetadata gyldighetstidspunkt | Folkeregistermetadata opphoerstidspunkt |
      | 2015-03-25 10:03:03                       |                                         |
      | 2018-05-20 12:03:05                       | 2020-05-20 12:03:05                     |

    Når bostedsadresser mappes

    Så skal mappede bostedsadresser være
      | Landkode | Fra og med dato | Til og med dato |
      | NOR      | 2015-03-25      |                 |
      | NOR      | 2018-05-20      | 2020-05-20      |


  Scenario: En person som har flyttet utenlands, og som senere har flyttet tilbake til Norge
    Gitt følgende bostedsadresser fra PDL:
      | Folkeregistermetadata gyldighetstidspunkt | Folkeregistermetadata opphoerstidspunkt |
      | 2008-05-20 12:03:05                       | 2010-05-20 12:03:05                     |
      | 2018-05-20 12:03:05                       |                                         |

    Når bostedsadresser mappes

    Så skal mappede bostedsadresser være
      | Landkode | Fra og med dato | Til og med dato |
      | NOR      | 2008-05-20      | 2010-05-20      |
      | NOR      | 2018-05-20      |                 |

  Scenario: Bostedsadresser skal sorteres på "Fra og med dato", stigende
    Gitt følgende bostedsadresser fra PDL:
      | Folkeregistermetadata gyldighetstidspunkt | Folkeregistermetadata opphoerstidspunkt |
      | 2018-05-20 12:03:05                       | 2019-05-20 12:03:05                     |
      |                                           |                                         |
      | 2008-05-20 12:03:05                       |                                         |

    Når bostedsadresser mappes

    Så skal mappede bostedsadresser være
      | Landkode | Fra og med dato | Til og med dato |
      | NOR      |                 |                 |
      | NOR      | 2008-05-20      |                 |
      | NOR      | 2018-05-20      | 2019-05-20      |