# language: no
# encoding: UTF-8

Egenskap: Mapping av OppholdstillatelseEllerOppholdsPaSammeVilkar

  Scenariomal: Bruker har OppholdstillatelseEllerOppholdsPaSammeVilkar fra GjeldendeOppholdsstatus
    Og følgende periode fra OppholdstillatelseEllerOppholdsPaSammeVilkar
      | Gyldig fra og med   | Gyldig til og med |
      | 2015-03-25          | 2016-02-03        |

    Og effektueringsdato fra OppholdstillatelseEllerOppholdsPaSammeVilkar
      | Effektueringsdato       |
      | 2018-11-15T21:37:40.835 |

    Og følgende Oppholdstillatelse fra OppholdstillatelseEllerOppholdsPaSammeVilkar
      | OppholdstillatelseKategori   | VedtakDato   |
      | <OppholdstillatelseKategori> | <VedtakDato> |

    Og foresporselsfodselsnummer fra HentPersonstatusResultat
      | Foresporselsfodselsnummer  |
      | 20041276216                |

    Og uttrekkstidspunkt fra HentPersonstatusResultat
      | Uttrekkstidspunkt       |
      | 2018-11-15T21:37:40.835 |

    Når GjeldendeOppholdsstatus mappes

    Så skal mappede OppholdstillatelsePaSammeVilkar være
      | Har opphold  |
      | true         |


    Eksempler:
      | OppholdstillatelseKategori     | VedtakDato |
      | PERMANENT                      | 2016-02-03  |
      | MIDLERTIDIG                    | 2016-02-03  |































