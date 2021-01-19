# language: no
# encoding: UTF-8
# Mest sannsynlig vil kun ett felt fra
# IkkeOppholdstillatelseIkkeOppholdsPaSammeVilkarIkkeVisum forekommer

Egenskap: Mapping av OppholdstillatelseEllerOppholdsPaSammeVilkar

  Scenariomal: Bruker har følgende fra IkkeOppholdstillatelseIkkeOppholdsPaSammeVilkarIkkeVisum
    Gitt følgende JaNeiUavklart fra UtvistMedInnreiseForbud
      | JaNeiUavklart    |
      | <JaNeiUavklart>  |

    Og  avgjorelsesDato fra AvslagEllerBortfallAvPOBOSellerTilbakekallEllerFormeltVedtak
      | Avgjørelsesdato   |
      | <Avgjørelsesdato> |

    Og følgende OvrigIkkeOppholdsKategori fra OvrigIkkeOpphold
      | OvrigIkkeOppholdsKategori   |
      | <OvrigIkkeOppholdsKategori> |

    Og foresporselsfodselsnummer fra HentPersonstatusResultat
      | Foresporselsfodselsnummer  |
      | 20041276216                |

    Og uttrekkstidspunkt fra HentPersonstatusResultat
      | Uttrekkstidspunkt       |
      | 2018-11-15T21:37:40.835 |

    Når GjeldendeOppholdsstatus med IkkeOppholdstillatelseIkkeOppholdsPaSammeVilkarIkkeVisum mappes

    Så skal mappede UtvistMedInnreiseForbud i IkkeOppholdstillatelseIkkeOppholdsPaSammeVilkarIkkeVisum i Oppholdstillatelse være
      | JaNeiUavklart   |
      | <JaNeiUavklart> |

    Og mappede AvslagEllerBortfallAvPOBOSellerTilbakekallEllerFormeltVedtak i IkkeOppholdstillatelseIkkeOppholdsPaSammeVilkarIkkeVisum i Oppholdstillatelse være
      | Avgjorelsesdato   |
      | <Avgjørelsesdato> |

    Og mappede OvrigIkkeOppholdsKategori i IkkeOppholdstillatelseIkkeOppholdsPaSammeVilkarIkkeVisum i Oppholdstillatelse være
      | OvrigIkkeOppholdsKategori   |
      | <OvrigIkkeOppholdsKategori> |

    Eksempler:
      | JaNeiUavklart     | Avgjørelsesdato | OvrigIkkeOppholdsKategori                                                   |
      | JA                | 2016-02-03      | OPPHEVET_INNREISEFORBUD                                                     |
      | NEI               | 2016-02-03      | ANNULERING_AV_VISUM                                                         |
      | UAVKLART          | 2016-02-03      | UTLOPT_OPPHOLDSTILLATELSE                                                   |
      | JA                | 2016-02-03      | UTLOPT_EO_SELLER_EFTA_OPPHOLDSRETT_ELLER_EO_SELLER_EFTA_OPPHOLDSTILLATELSE  |