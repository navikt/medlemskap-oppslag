# language: no
# encoding: UTF-8

Egenskap: Regel 19.3: Har bruker arbeids- og oppholdstillatelse i kontrollperiode?

  Scenariomal: Regel 19.3: Har bruker arbeids- og oppholdstillatelse i kontrollperiode?

    Gitt følgende arbeidsadgang
      | Gyldig fra og med   | Gyldig til og med   | Arbeidsadgang   | ArbeidsadgangType   | ArbeidomfangKategori   |
      | <Gyldig fra og med> | <Gyldig til og med> | <Arbeidsadgang> | <ArbeidsadgangType> | <ArbeidomfangKategori> |

    Når regel "19.3" kjøres med følgende parametre
      | Fra og med dato | Til og med dato | Har hatt arbeid utenfor Norge |
      | 23.01.2020      | 30.01.2020      | Nei                           |

    Så skal svaret være "<Svar>"

    Eksempler:
      | Gyldig fra og med | Gyldig til og med | Arbeidsadgang | ArbeidsadgangType                        | ArbeidomfangKategori            | Svar |
      |                   |                   | Ja            | GENERELL                                 | KUN_ARBEID_HELTID               | Ja   |
      | 01.01.2018        |                   | Ja            | GENERELL                                 | KUN_ARBEID_HELTID               | Ja   |
      | 23.01.2019        |                   | Ja            | GENERELL                                 | KUN_ARBEID_HELTID               | Nei  |
      | 22.01.2019        |                   | Ja            | GENERELL                                 | KUN_ARBEID_HELTID               | Ja   |
      |                   | 22.03.2020        | Ja            | GENERELL                                 | KUN_ARBEID_HELTID               | Ja   |
      |                   | 21.03.2020        | Ja            | GENERELL                                 | KUN_ARBEID_HELTID               | Nei  |
      |                   |                   | Nei           | GENERELL                                 | KUN_ARBEID_HELTID               | Nei  |
      |                   |                   | Ja            | BESTEMT_ARBEIDSGIVER_ELLER_OPPDRAGSGIVER | KUN_ARBEID_HELTID               | Nei  |
      |                   |                   | Ja            | BESTEMT_ARBEID_ELLER_OPPDRAG             | KUN_ARBEID_HELTID               | Nei  |
      |                   |                   | Ja            | UAVKLART                                 | KUN_ARBEID_HELTID               | Nei  |
      |                   |                   | Ja            | GENERELL                                 | INGEN_KRAV_TIL_STILLINGSPROSENT | Nei  |
      |                   |                   | Ja            | GENERELL                                 | KUN_ARBEID_DELTID               | Nei  |
      |                   |                   | Ja            | GENERELL                                 | UAVKLART                        | Nei  |
      |                   |                   | Ja            | BESTEMT_ARBEIDSGIVER_ELLER_OPPDRAGSGIVER | INGEN_KRAV_TIL_STILLINGSPROSENT | Nei  |

  Scenariomal: Regel 19.3 - Arbeidsomfang er tom for permanent oppholdstillatelse

    Gitt følgende oppholdstillatelse
      | Gyldig fra og med | Gyldig til og med | Har tillatelse | Type           |
      |                   |                   | Ja             | <Oppholdstype> |

    Og følgende arbeidsadgang
      | Gyldig fra og med | Gyldig til og med | Arbeidsadgang   | ArbeidsadgangType   | ArbeidomfangKategori   |
      | 01.01.2018        |                   | <Arbeidsadgang> | <ArbeidsadgangType> | <ArbeidomfangKategori> |

    Når regel "19.3" kjøres med følgende parametre
      | Fra og med dato | Til og med dato | Har hatt arbeid utenfor Norge |
      | 23.01.2020      | 30.01.2020      | Nei                           |

    Så skal svaret være "<Svar>"

    Eksempler:
      | Arbeidsadgang | Oppholdstype | ArbeidsadgangType | ArbeidomfangKategori | Svar |
      | Ja            | PERMANENT    | GENERELL          |                      | Ja   |
      | Ja            | MIDLERTIDIG  | GENERELL          | KUN_ARBEID_HELTID    | Ja   |
      | Ja            | MIDLERTIDIG  | GENERELL          | KUN_ARBEID_DELTID    | Nei  |
