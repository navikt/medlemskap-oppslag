# language: no
# encoding: UTF-8

Egenskap: Mapping av OppholdstillatelseEllerOppholdsPaSammeVilkar

  Scenariomal: Bruker har EOSEllerEFTAOpphold fra EOSellerEFTABeslutningOmOppholdsrett
    Gitt følgende periode fra EOSellerEFTAOppholdstillatelse fra EOSEllerEFTAOpphold med følgende type
      | Gyldig fra og med   | Gyldig til og med   |
      | <Gyldig fra og med> | <Gyldig til og med> |

    Og uttrekkstidspunkt fra HentPersonstatusResultat
      | Uttrekkstidspunkt       |
      | 2018-11-15T21:37:40.835 |

    Når GjeldendeOppholdsstatus med EOSEllerEFTAOpphold med EOSellerEFTAOppholdstillatelse mappes

    Så skal mappede EOSEllerEFTAOpphold være
      | Gyldig fra og med   | Gyldig til og med   |EOSEllerEFTAOpphold   |
      | <Gyldig fra og med> | <Gyldig til og med> |<EOSEllerEFTAOpphold> |

    Eksempler:
      | Gyldig fra og med | Gyldig til og med  |EOSEllerEFTAOpphold                       |
      | 2019-03-25        | 2020-02-03         |EOS_ELLER_EFTA_OPPHOLDSTILLATELSE         |
      | 2019-03-25        |                    |EOS_ELLER_EFTA_OPPHOLDSTILLATELSE         |
      |                   |                    |EOS_ELLER_EFTA_OPPHOLDSTILLATELSE         |