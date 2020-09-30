# language: no
# encoding: UTF-8

Egenskap: Mapping av statsborgerskap fra PDL hentPerson

  TODO: Er dette realistiske eksempler?

  Scenario: En person som har flyttet flere ganger i Norge
    Gitt følgende statsborgerskap fra PDL:
      | Land | Gyldig fra og med dato | Gyldig til og med dato |
      | NOR  | 2015-03-25             |                        |
      | NOR  | 2018-05-20             |                        |
      | NOR  |                        |                        |

    Når statsborgerskap mappes

    Så skal mappet statsborgerskap være
      | Landkode | Fra og med dato | Til og med dato |
      | NOR      | 2015-03-25      |                 |
      | NOR      | 2018-05-20      |                 |
      | NOR      |                 |                 |


  Scenario: En person som har flyttet flere ganger i Norge, og så flyttet utenlands
    Gitt følgende statsborgerskap fra PDL:
      | Land | Gyldig fra og med dato | Gyldig til og med dato | Spørsmål                                                                |
      | NOR  | 2005-01-30             |                        | Vil gyldig til og med dato være satt på denne raden?                    |
      | NOR  | 2015-03-25             | 2018-03-26             | Vil en person som har flyttet utenlands ha gyldig til og med dato satt? |
      | NOR  |                        | 2018-05-05             |                                                                         |
      | BEL  | 2018-06-07             |                        |                                                                         |

    Når statsborgerskap mappes

    Så skal mappet statsborgerskap være
      | Landkode | Fra og med dato | Til og med dato |
      | NOR      | 2005-01-30      |                 |
      | NOR      | 2015-03-25      | 2018-03-26      |
      | NOR      |                 | 2018-05-05      |
      | BEL      | 2018-06-07      |                 |


  Scenario: En person som har flyttet fra Belgia til Norge, og så tilbake til Belgia
    Gitt følgende statsborgerskap fra PDL:
      | Land | Gyldig fra og med dato | Gyldig til og med dato |
      | BEL  | 2005-01-30             |                        |
      | NOR  | 2015-03-25             | 2018-03-26             |
      | NOR  |                        | 2018-05-05             |
      | BEL  | 2018-06-07             |                        |

    Når statsborgerskap mappes

    Så skal mappet statsborgerskap være
      | Landkode | Fra og med dato | Til og med dato |
      | BEL      | 2005-01-30      |                 |
      | NOR      | 2015-03-25      | 2018-03-26      |
      | NOR      |                 | 2018-05-05      |
      | BEL      | 2018-06-07      |                 |


  Scenario: En person som er svensk statsborger

    Gitt følgende statsborgerskap fra PDL:
      | Land | Gyldig fra og med dato | Gyldig til og med dato |
      | SWE  |                        |                        |


    Når statsborgerskap mappes

    Så skal mappet statsborgerskap være
      | Landkode | Fra og med dato | Til og med dato |
      | SWE      |                 |                 |


  Scenario: En person med flere samtidige statsborgerskap
    Gitt følgende statsborgerskap fra PDL:
      | Land | Gyldig fra og med dato | Gyldig til og med dato |
      | PAK  |                        |                        |
      | NOR  | 1994-09-20             |                        |

    Når statsborgerskap mappes

    Så skal mappet statsborgerskap være
      | Landkode | Fra og med dato | Til og med dato |
      | PAK      |                 |                 |
      | NOR      | 1994-09-20      |                 |