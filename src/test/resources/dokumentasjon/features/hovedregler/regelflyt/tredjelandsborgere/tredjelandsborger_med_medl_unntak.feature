# language: no
# encoding: UTF-8

Egenskap: Tredjelandsborger med medl unntak.

  Bakgrunn:

    Gitt følgende statsborgerskap i personhistorikken
      | Landkode | Fra og med dato | Til og med dato |
      | USA      | 01.01.2000      |                 |

    Og følgende bostedsadresser i personhistorikken
      | Landkode | Fra og med dato | Til og med dato |
      | NOR      | 10.10.1975      |                 |

    Og følgende arbeidsforhold fra AAReg
      | Fra og med dato | Til og med dato | Arbeidsgivertype | Arbeidsforholdstype | Arbeidsgiver Id |
      | 10.10.1975      |                 | Organisasjon     | NORMALT             | 1               |

    Og følgende arbeidsgiver i arbeidsforholdet
      | Identifikator       | Arbeidsgivertype | Landkode | Antall ansatte | Antall ansatte i juridisk enhet | Juridisk enhetstype | Juridisk orgnr    |
      | organisasjonsnummer | STAT             | NOR      | 10             | 20                              | STAT                | juridiskOrgnummer |

    Og følgende detaljer om ansatte for arbeidsgiver
      | Antall ansatte | Gyldighetsperiode gyldig fra | Gyldighetsperiode gyldig til |
      | 10             | 10.10.1975                   | 01.08.2021                   |


  Scenariomal: Tredjelandsborger med medl unntak "Ja" skal få uavklart medlemskap hvis oppholdstillatelse ikke er i orden.

    Gitt følgende oppholdstillatelse
      | Gyldig fra og med | Gyldig til og med    | Har tillatelse | Type      |
      | 01.01.2018        | <Opphold til og med> | Ja             | PERMANENT |

    Og følgende arbeidsadgang
      | Gyldig fra og med | Gyldig til og med    | Arbeidsadgang | ArbeidsadgangType | ArbeidomfangKategori |
      | 01.01.2018        | <Opphold til og med> | Ja            | GENERELL          | KUN_ARBEID_HELTID    |

    Og følgende medlemsunntak fra MEDL
      | Dekning             | Fra og med dato | Til og med dato | Er medlem   | Lovvalg | Lovvalgsland | Periodestatus |
      | FTL_2-9_2_ld_jfr_1c | 01.01.2018      | 01.06.2021      | <Er medlem> | ENDL    | NOR          | GYLD          |

    Og følgende arbeidsavtaler i arbeidsforholdet
      | Fra og med dato | Til og med dato | Yrkeskode | Stillingsprosent |
      | 10.10.1975      | 01.08.2020      | yrkeskode | 100              |

    Når medlemskap beregnes med følgende parametre
      | Fra og med dato | Til og med dato | Har hatt arbeid utenfor Norge |
      | 05.01.2021      | 12.02.2021      | Nei                           |

    Så skal svaret være "<Medlemskap>"
    Og skal regel "19.2.1" gi svaret "<Regel 19.2.1>"
    Og skal regel-årsaker være "<Årsak>"

    Og skal resultat gi følgende delresultater:
      | Regel                          |
      | REGEL_OVERSTYRING              |
      | REGEL_DOED                     |
      | REGEL_FELLES_ARBEIDSFORHOLD    |
      | REGEL_STATSBORGERSKAP          |
      | REGEL_HOVEDSAKLIG_ARBEIDSTAKER |
      | REGEL_OPPHOLDSTILLATELSE       |
      | REGEL_MEDL                     |

    Eksempler:
      | Opphold til og med | Er medlem | Regel 19.2.1 | Medlemskap | Årsak  |
      | 20.01.2019         | Ja        | Nei          | Uavklart   | 19.2.1 |


  Scenariomal: Tredjelandsborger med medl unntak "Ja" skal få "Ja" på medlemskap hvis oppholdstillatelse er i orden.

    Gitt følgende oppholdstillatelse
      | Gyldig fra og med | Gyldig til og med    | Har tillatelse | Type      |
      | 01.01.2018        | <Opphold til og med> | Ja             | PERMANENT |

    Og følgende arbeidsadgang
      | Gyldig fra og med | Gyldig til og med    | Arbeidsadgang | ArbeidsadgangType | ArbeidomfangKategori |
      | 01.01.2018        | <Opphold til og med> | Ja            | GENERELL          | KUN_ARBEID_HELTID    |

    Og følgende medlemsunntak fra MEDL
      | Dekning             | Fra og med dato | Til og med dato | Er medlem | Lovvalg | Lovvalgsland | Periodestatus |
      | FTL_2-9_2_ld_jfr_1c | 01.01.2018      | 01.06.2021      | Ja        | ENDL    | NOR          | GYLD          |

    Og følgende arbeidsavtaler i arbeidsforholdet
      | Fra og med dato | Til og med dato | Yrkeskode | Stillingsprosent |
      | 10.10.1975      | 01.08.2020      | yrkeskode | 100              |

    Når medlemskap beregnes med følgende parametre
      | Fra og med dato | Til og med dato | Har hatt arbeid utenfor Norge |
      | 05.01.2021      | 12.02.2021      | Nei                           |

    Så skal svaret være "<Medlemskap>"

    Og skal resultat gi følgende delresultater:
      | Regel                          |
      | REGEL_OVERSTYRING              |
      | REGEL_DOED                     |
      | REGEL_FELLES_ARBEIDSFORHOLD    |
      | REGEL_STATSBORGERSKAP          |
      | REGEL_HOVEDSAKLIG_ARBEIDSTAKER |
      | REGEL_OPPHOLDSTILLATELSE       |
      | REGEL_MEDL                     |

    Eksempler:
      | Opphold til og med | Medlemskap |
      | 20.03.2021         | Ja         |
      | 20.01.2019         | Uavklart   |


  Scenariomal: Tredjelandsborger med gyldig oppholdstillatelse og medl unntak "Ja" skal få "Ja" på medlemskap.

    Gitt følgende oppholdstillatelse
      | Gyldig fra og med | Gyldig til og med | Har tillatelse | Type      |
      | 01.01.2018        | 20.03.2021        | Ja             | PERMANENT |

    Og følgende arbeidsadgang
      | Gyldig fra og med | Gyldig til og med | Arbeidsadgang | ArbeidsadgangType | ArbeidomfangKategori |
      | 01.01.2018        | 20.03.2021        | Ja            | GENERELL          | KUN_ARBEID_HELTID    |

    Og følgende medlemsunntak fra MEDL
      | Dekning             | Fra og med dato | Til og med dato | Er medlem | Lovvalg | Lovvalgsland | Periodestatus |
      | FTL_2-9_2_ld_jfr_1c | 01.01.2018      | 01.06.2021      | Ja        | ENDL    | NOR          | GYLD          |

    Og følgende arbeidsavtaler i arbeidsforholdet
      | Fra og med dato | Til og med dato | Yrkeskode | Stillingsprosent   |
      | 10.10.1975      | 01.08.2020      | yrkeskode | <Stillingsprosent> |

    Når medlemskap beregnes med følgende parametre
      | Fra og med dato | Til og med dato | Har hatt arbeid utenfor Norge |
      | 05.01.2021      | 12.02.2021      | Nei                           |

    Så skal svaret være "<Medlemskap>"

    Og skal regel-årsaker være "<Årsaker>"

    Eksempler:
      | Stillingsprosent | Medlemskap | Årsaker |
      | 50               | Uavklart   | 18      |
      | 100              | Ja         |         |

  Scenario: Tredjelandsborger med medl unntak "Nei" skal få "Uavklart" på medlemskap selv om oppholdstillatelse er i orden.

    Gitt følgende oppholdstillatelse
      | Gyldig fra og med | Gyldig til og med | Har tillatelse | Type      |
      | 01.01.2018        | 20.03.2021        | Ja             | PERMANENT |

    Og følgende arbeidsadgang
      | Gyldig fra og med | Gyldig til og med | Arbeidsadgang | ArbeidsadgangType | ArbeidomfangKategori |
      | 01.01.2018        | 20.03.2021        | Ja            | GENERELL          | KUN_ARBEID_HELTID    |

    Og følgende medlemsunntak fra MEDL
      | Dekning             | Fra og med dato | Til og med dato | Er medlem | Lovvalg | Lovvalgsland | Periodestatus |
      | FTL_2-9_2_ld_jfr_1c | 01.01.2018      | 01.06.2021      | Nei       | ENDL    | NOR          | GYLD          |

    Og følgende arbeidsavtaler i arbeidsforholdet
      | Fra og med dato | Til og med dato | Yrkeskode | Stillingsprosent |
      | 10.10.1975      | 01.08.2020      | yrkeskode | 100              |

    Når medlemskap beregnes med følgende parametre
      | Fra og med dato | Til og med dato | Har hatt arbeid utenfor Norge |
      | 05.01.2021      | 12.02.2021      | Nei                           |

    Så skal svaret være "Uavklart"

    Og skal resultat gi følgende delresultater:
      | Regel                          |
      | REGEL_OVERSTYRING              |
      | REGEL_DOED                     |
      | REGEL_FELLES_ARBEIDSFORHOLD    |
      | REGEL_STATSBORGERSKAP          |
      | REGEL_HOVEDSAKLIG_ARBEIDSTAKER |
      | REGEL_OPPHOLDSTILLATELSE       |
      | REGEL_MEDL                     |
      | REGEL_ANDRE_BORGERE            |
