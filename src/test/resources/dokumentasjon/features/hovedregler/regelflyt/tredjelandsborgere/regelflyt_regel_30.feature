# language: no
# encoding: UTF-8

Egenskap: Regelflyt for regel 30

  Bakgrunn:

    Gitt følgende bostedsadresser i personhistorikken
      | Landkode | Fra og med dato | Til og med dato |
      | NOR      | 10.10.1975      |                 |

    Og følgende arbeidsgiver i arbeidsforholdet
      | Identifikator       | Arbeidsgivertype | Landkode | Antall ansatte | Juridisk enhetstype |
      | organisasjonsnummer | STAT             | NOR      | 10             | STAT                |

    Og følgende detaljer om ansatte for arbeidsgiver
      | Antall ansatte | Gyldighetsperiode gyldig fra | Gyldighetsperiode gyldig til |
      | 10             | 10.10.1975                   | 01.08.2021                   |

    Og følgende arbeidsavtaler i arbeidsforholdet
      | Fra og med dato | Til og med dato | Yrkeskode | Stillingsprosent |
      | 10.10.1975      | 01.08.2020      | yrkeskode | 100              |

  Scenariomal: Regelflyt for regel 30 med EØS eller EFTA oppholdstillatelse
    Gitt følgende statsborgerskap i personhistorikken
      | Landkode          | Fra og med dato | Til og med dato |
      | <Statsborgerskap> | 10.10.1975      |                 |

    Og følgende arbeidsforhold fra AAReg
      | Fra og med dato | Til og med dato | Arbeidsgivertype | Arbeidsforholdstype | Arbeidsgiver Id |
      | 01.01.2017      |                 | Organisasjon     | NORMALT             | 1               |

    Og følgende oppholdstillatelse
      | Klasse              | Gyldig fra og med | Gyldig til og med | EOSEllerEFTAOpphold                       | EOSellerEFTAGrunnlagskategoriOppholdstillatelse |
      | EOSellerEFTAOpphold | 01.01.2017        | 01.12.2021        | EOS_ELLER_EFTA_BESLUTNING_OM_OPPHOLDSRETT | ARBEID                                          |

    Og følgende arbeidsadgang
      | Gyldig fra og med | Gyldig til og med | Arbeidsadgang | ArbeidsadgangType | ArbeidomfangKategori |
      | 01.01.2017        |                   | Ja            | GENERELL          | KUN_ARBEID_HELTID    |

    Når medlemskap beregnes med følgende parametre
      | Fra og med dato | Til og med dato | Har hatt arbeid utenfor Norge |
      | 01.01.2018      | 12.02.2018      | Nei                           |

    Så skal svaret være "<Medlemskap>"
    Og skal regel "30" gi svaret "<Regel 30>"

    Eksempler:
      | Statsborgerskap | Regel 30 | Medlemskap |
      | GBR             | Ja       | Uavklart   |
      | GER             | Nei      | Uavklart   |


  Scenario: Regelflyt for regel 30 med oppholdstillatelse på samme vilkår
    Gitt følgende statsborgerskap i personhistorikken
      | Landkode | Fra og med dato | Til og med dato |
      | GBR      | 10.10.1975      |                 |

    Og følgende arbeidsforhold fra AAReg
      | Fra og med dato | Til og med dato | Arbeidsgivertype | Arbeidsforholdstype | Arbeidsgiver Id |
      | 01.01.2016      |                 | Organisasjon     | NORMALT             | 1               |

    Og følgende oppholdstillatelse
      | Gyldig fra og med | Gyldig til og med | Har tillatelse | Type      | Oppholdstillatelse på samme vilkår flagg |
      | 01.01.2016        | 31.12.2030        | Ja             | PERMANENT | Nei                                      |

    Og følgende arbeidsadgang
      | Gyldig fra og med | Gyldig til og med | Arbeidsadgang | ArbeidsadgangType | ArbeidomfangKategori |
      | 01.01.2016        |                   | Ja            | GENERELL          | KUN_ARBEID_HELTID    |

    Når medlemskap beregnes med følgende parametre
      | Fra og med dato | Til og med dato | Har hatt arbeid utenfor Norge |
      | 01.01.2018      | 12.02.2018      | Nei                           |

    Så skal svaret være "Ja"