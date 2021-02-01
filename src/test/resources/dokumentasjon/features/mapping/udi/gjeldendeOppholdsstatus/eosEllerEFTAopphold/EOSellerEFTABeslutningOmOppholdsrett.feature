# language: no
# encoding: UTF-8

Egenskap: Mapping av EOSEllerEFTAOpphold

  Scenariomal: Bruker har EOSellerEFTABeslutningOmOppholdsrett fra EOSEllerEFTAOpphold fra EOSellerEFTABeslutningOmOppholdsrett
    Gitt følgende periode fra EOSellerEFTABeslutningOmOppholdsrett fra EOSEllerEFTAOpphold med følgende type
      | Gyldig fra og med   | Gyldig til og med   |
      | <Gyldig fra og med> | <Gyldig til og med> |

    Og følgende EOSellerEFTAGrunnlagskategoriOppholdsrett fra EOSellerEFTABeslutningOmOppholdsrett
      | EOSellerEFTAGrunnlagskategoriOppholdsrett   |
      | <EOSellerEFTAGrunnlagskategoriOppholdsrett> |

    Og uttrekkstidspunkt fra HentPersonstatusResultat
      | Uttrekkstidspunkt       |
      | 2018-11-15T21:37:40.835 |

    Når GjeldendeOppholdsstatus med EOSEllerEFTAOpphold med EOSellerEFTABeslutningOmOppholdsrett mappes

    Så skal mappede oppholdstillatelse med EOS_ELLER_EFTA_BESLUTNING_OM_OPPHOLDSRETT
      | Gyldig fra og med   | Gyldig til og med   |EOSEllerEFTAOpphold   |EOSellerEFTAGrunnlagskategoriOppholdsrett   |
      | <Gyldig fra og med> | <Gyldig til og med> |<EOSEllerEFTAOpphold> |<EOSellerEFTAGrunnlagskategoriOppholdsrett> |

    Eksempler:
      | Gyldig fra og med | Gyldig til og med  | EOSEllerEFTAOpphold                       | EOSellerEFTAGrunnlagskategoriOppholdsrett  |
      | 2019-03-25        | 2022-02-03         | EOS_ELLER_EFTA_BESLUTNING_OM_OPPHOLDSRETT | VARIG                                      |
      | 2019-03-25        |                    | EOS_ELLER_EFTA_BESLUTNING_OM_OPPHOLDSRETT | INGEN_INFORMASJON                          |
      | 2019-03-25        |                    | EOS_ELLER_EFTA_BESLUTNING_OM_OPPHOLDSRETT | FAMILIE                                    |
      | 2019-03-25        | 2022-02-03         | EOS_ELLER_EFTA_BESLUTNING_OM_OPPHOLDSRETT | TJENESTEYTING_ELLER_ETABLERING             |
      | 2019-03-25        | 2022-02-03         | EOS_ELLER_EFTA_BESLUTNING_OM_OPPHOLDSRETT | UAVKLART                                   |