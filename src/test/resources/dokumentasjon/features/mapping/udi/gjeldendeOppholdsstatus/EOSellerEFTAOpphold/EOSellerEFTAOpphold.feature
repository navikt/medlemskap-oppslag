# language: no
# encoding: UTF-8

Egenskap: Mapping av GjeldendeOppholdsstatus

  Scenariomal: Bruker har EOSellerEFTAOpphold fra GjeldendeOppholdsstatus
    Gitt følgende eoSellerEFTABeslutningOmOppholdsrett fra EOSellerEFTAOpphold fra GjeldendeOppholdstatus
      | EOSellerEFTAGrunnlagskategoriOppholdsrett    |
      | <EOSellerEFTAGrunnlagskategoriOppholdsrett>  |

    Og følgende periode fra EOSellerEFTAOpphold
      | Gyldig fra og med   | Gyldig til og med |
      | 2015-03-25          | 2016-02-03        |

    Og foresporselsfodselsnummer fra HentPersonstatusResultat
      | Foresporselsfodselsnummer  |
      | 20041276216                |

    Og uttrekkstidspunkt fra HentPersonstatusResultat
      | Uttrekkstidspunkt       |
      | 2018-11-15T21:37:40.835 |

    Når GjeldendeOppholdsstatus mappes

    Så skal mappede EOSellerEFTAGrunnlagskategoriOppholdsrett i GjeldendeOppholdsstatus i medlemskap være
      | EOSellerEFTAGrunnlagskategoriOppholdsrett     |
      | <EOSellerEFTAGrunnlagskategoriOppholdsrett >  |

    Og mappede følgende periode fra EOSellerEFTAOpphold
      | Gyldig fra og med   | Gyldig til og med |
      | 2015-03-25          | 2016-02-03        |

    Og foresporselsfodselsnummer i mappet Oppholdsstatus
      | Foresporselsfodselsnummer  |
      | 20041276216                |

    Og uttrekkstidspunkt i mappet Oppholdsstatus
      | Uttrekkstidspunkt        |
      | 2018-11-15T21:37:40.835  |

    Eksempler:
      | EOSellerEFTAGrunnlagskategoriOppholdsrett | EOSellerEFTAGrunnlagskategoriOppholdsrett|
      | VARIG                                     | VARIG                                    |
      | INGEN_INFORMASJON                         | INGEN_INFORMASJON                        |
      | FAMILIE                                   | FAMILIE                                  |
      | TJENESTEYTING_ELLER_ETABLERING            | TJENESTEYTING_ELLER_ETABLERING           |
      | UAVKLART                                  | UAVKLART                                 |



























