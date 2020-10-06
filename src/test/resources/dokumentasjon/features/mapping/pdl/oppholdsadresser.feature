# language: no
# encoding: UTF-8

Egenskap: Mapping av oppholdsadresser fra PDL HentPerson.Oppholdsadresse

  Scenario: En person som har hatt flere forskjellige oppholdsadresser i Norge
    Gitt følgende oppholdsadresser fra PDL:
      | Gyldig fra og med   | Utenlandsk adresse landkode | Folkeregistermetadata opphoerstidspunkt |
      |                     |                             |                                         |
      | 2015-03-25 10:03:03 |                             |                                         |
      | 2018-05-20 12:03:05 |                             |                                         |


    Når oppholdsadresser mappes

    Så skal mappede oppholdsadresser være
      | Landkode | Fra og med dato | Til og med dato |
      | NOR      |                 |                 |
      | NOR      | 2015-03-25      |                 |
      | NOR      | 2018-05-20      |                 |

  Scenario: En person som har hatt flere forskjellige oppholdsadresser i Norge og i utlandet
    Gitt følgende oppholdsadresser fra PDL:
      | Gyldig fra og med   | Utenlandsk adresse landkode | Folkeregistermetadata opphoerstidspunkt | Spørsmål til PDL                                         |
      |                     |                             | 2016-03-26 11:03:03                     | Bør vi bruke Gyldig til og med eller folkereg. metadata? |
      | 2015-03-25 10:03:03 | BEL                         | 2017-04-26 11:03:03                     |                                                          |
      | 2018-05-20 12:03:05 |                             |                                         |                                                          |


    Når oppholdsadresser mappes

    Så skal mappede oppholdsadresser være
      | Landkode | Fra og med dato | Til og med dato |
      | NOR      |                 | 2016-03-26      |
      | BEL      | 2015-03-25      | 2017-04-26      |
      | NOR      | 2018-05-20      |                 |


  Scenario: Oppholdsadresser skal sorteres på "Fra og med dato", stigende
    Gitt følgende oppholdsadresser fra PDL:
      | Gyldig fra og med   | Utenlandsk adresse landkode | Folkeregistermetadata opphoerstidspunkt |
      | 2018-05-20 12:03:05 |                             |                                         |
      |                     |                             | 2016-03-26 11:03:03                     |
      | 2015-03-25 10:03:03 | BEL                         | 2017-04-26 11:03:03                     |

    Når oppholdsadresser mappes

    Så skal mappede oppholdsadresser være
      | Landkode | Fra og med dato | Til og med dato |
      | NOR      |                 | 2016-03-26      |
      | BEL      | 2015-03-25      | 2017-04-26      |
      | NOR      | 2018-05-20      |                 |