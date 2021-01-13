# language: no
# encoding: UTF-8

Egenskap: Regel 15: Har bruker arbeids- og oppholdstillatelse i kontrollperiode?

  Scenariomal: Regel 15: Har bruker arbeids- og oppholdstillatelse i kontrollperiode?

    Gitt følgende oppholdstillatelse
      | Gyldig fra og med | Gyldig til og med   | Har tillatelse   |
      | 01.01.2018        | <Gyldig til og med> | <Har tillatelse> |

    Og følgende arbeidsadgang
      | Gyldig fra og med | Gyldig til og med   | Arbeidsadgang   | ArbeidsadgangType   | ArbeidomfangKategori   |
      | 01.01.2018        | <Gyldig til og med> | <Arbeidsadgang> | <ArbeidsadgangType> | <ArbeidomfangKategori> |

    Når regel "15" kjøres med følgende parametre
      | Fra og med dato | Til og med dato | Har hatt arbeid utenfor Norge |
      | 23.01.2020      | 30.01.2020      | Nei                           |

    Så skal svaret være "<Svar>"

    Eksempler:
      | Gyldig til og med | Har tillatelse | Arbeidsadgang | ArbeidsadgangType                        | ArbeidomfangKategori            | Svar |
      |                   | Ja             | Ja            | GENERELL                                 | KUN_ARBEID_HELTID               | Ja   |
      | 21.02.2020        | Ja             | Ja            | GENERELL                                 | KUN_ARBEID_HELTID               | Nei  |
      | 22.02.2020        | Ja             | Ja            | GENERELL                                 | KUN_ARBEID_HELTID               | Ja   |
      |                   | Ja             | Nei           | GENERELL                                 | KUN_ARBEID_HELTID               | Nei  |
      |                   | Ja             | Ja            | BESTEMT_ARBEIDSGIVER_ELLER_OPPDRAGSGIVER | KUN_ARBEID_HELTID               | Nei  |
      |                   | Ja             | Ja            | BESTEMT_ARBEID_ELLER_OPPDRAG             | KUN_ARBEID_HELTID               | Nei  |
      |                   | Ja             | Ja            | UAVKLART                                 | KUN_ARBEID_HELTID               | Nei  |
      |                   | Ja             | Ja            | GENERELL                                 | INGEN_KRAV_TIL_STILLINGSPROSENT | Nei  |
      |                   | Ja             | Ja            | GENERELL                                 | KUN_ARBEID_DELTID               | Nei  |
      |                   | Ja             | Ja            | GENERELL                                 | UAVKLART                        | Nei  |
      |                   | Ja             | Ja            | BESTEMT_ARBEIDSGIVER_ELLER_OPPDRAGSGIVER | INGEN_KRAV_TIL_STILLINGSPROSENT | Nei  |