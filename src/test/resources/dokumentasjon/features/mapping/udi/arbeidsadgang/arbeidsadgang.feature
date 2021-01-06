# language: no
# encoding: UTF-8

Egenskap: Mapping av Arbeidsadgang

  Scenario: Bruker har Arbeidsadgang
    Gitt følgende arbeidsadgangType fra Arbeidsadgang
      | ArbeidsadgangType   |
      | GENERELL            |

    Og følgende arbeidsadgangType fra Arbeidsadgang
      | ArbeidsadgangType   |
      | GENERELL            |

    Og følgende arbeidomfangKategori fra Arbeidsadgang
      | ArbeidomfangKategori |
      | KUN_ARBEID_HELTID    |

    Og foresporselsfodselsnummer fra HentPersonstatusResultat
      | Foresporselsfodselsnummer  |
      | 20041276216                |

    Og uttrekkstidspunkt fra HentPersonstatusResultat
      | Uttrekkstidspunkt       |
      | 2018-11-15T21:37:40.835 |

    Når arbeidsadgang mappes

    Så skal mappede arbeidsadgangtype i medlemskap være
      | ArbeidsadgangType |
      | GENERELL          |

    Og foresporselsfodselsnummer i mappet Oppholdsstatus
      | Foresporselsfodselsnummer  |
      | 20041276216                |

    Og uttrekkstidspunkt i mappet Oppholdsstatus
      | Uttrekkstidspunkt        |
      | 2018-11-15T21:37:40.835  |





























