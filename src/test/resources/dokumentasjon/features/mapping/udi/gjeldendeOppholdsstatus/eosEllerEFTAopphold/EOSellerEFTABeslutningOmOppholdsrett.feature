# language: no
# encoding: UTF-8

Egenskap: Mapping av EOSEllerEFTAOpphold

  Scenariomal: Bruker har EOSellerEFTABeslutningOmOppholdsrett fra EOSEllerEFTAOpphold fra EOSellerEFTABeslutningOmOppholdsrett
    Gitt følgende periode fra EOSellerEFTABeslutningOmOppholdsrett fra EOSEllerEFTAOpphold med følgende type
      | Gyldig fra og med   | Gyldig til og med   |
      | <Gyldig fra og med> | <Gyldig til og med> |


    Og uttrekkstidspunkt fra HentPersonstatusResultat
      | Uttrekkstidspunkt       |
      | 2018-11-15T21:37:40.835 |

    Når GjeldendeOppholdsstatus med EOSEllerEFTAOpphold med EOSellerEFTABeslutningOmOppholdsrett mappes

    Så skal mappede EOSEllerEFTAOpphold være
      | Gyldig fra og med   | Gyldig til og med   |EOSEllerEFTAOpphold   |
      | <Gyldig fra og med> | <Gyldig til og med> |<EOSEllerEFTAOpphold> |

    Eksempler:
      | Gyldig fra og med | Gyldig til og med  |EOSEllerEFTAOpphold                      |
      | 2019-03-25        | 2020-02-03         |EOS_ELLER_EFTA_BESLUTNING_OM_OPPHOLDSRETT|
      | 2019-03-25        |                    |EOS_ELLER_EFTA_BESLUTNING_OM_OPPHOLDSRETT|
      |                   |                    |EOS_ELLER_EFTA_BESLUTNING_OM_OPPHOLDSRETT|
