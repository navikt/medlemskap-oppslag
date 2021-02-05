# language: no
# encoding: UTF-8

Egenskap: Regelflyt for regel 20, brukere som ikke er registrert som bosatt i Norge

  Bakgrunn:

    Gitt følgende statsborgerskap i personhistorikken
      | Landkode | Fra og med dato | Til og med dato |
      | JPN      | 01.01.2000      |                 |

    Og følgende arbeidsgiver i arbeidsforholdet
      | Identifikator | Arbeidsgivertype | Landkode | Antall ansatte |
      | 1             | BEDR             | NOR      | 9              |

    Og følgende oppholdstillatelse
      | Gyldig fra og med | Gyldig til og med | Har tillatelse | Type      |
      | 01.01.2018        |                   | Ja             | PERMANENT |

    Og følgende arbeidsadgang
      | Gyldig fra og med | Gyldig til og med | Arbeidsadgang | ArbeidsadgangType | ArbeidomfangKategori |
      | 01.01.2018        |                   | Ja            | GENERELL          | KUN_ARBEID_HELTID    |

  Scenariomal: For tredjelandsborgere skal man ta hensyn til regel 10, 20 og 9 hvis det ikke er avklaring i MEDL
    Gitt følgende arbeidsforhold fra AAReg
      | Fra og med dato | Til og med dato | Arbeidsgivertype | Arbeidsforholdstype |
      | 01.01.2018      |                 | Organisasjon     | NORMALT             |

    Og følgende arbeidsavtaler i arbeidsforholdet
      | Fra og med dato | Til og med dato | Yrkeskode | Stillingsprosent   | Skipsregister |
      | 01.01.2018      |                 | 001       | <Stillingsprosent> |               |

    Når medlemskap beregnes med følgende parametre
      | Fra og med dato | Til og med dato | Har hatt arbeid utenfor Norge |
      | 30.01.2020      | 30.01.2021      | Nei                           |

    Så skal svaret være "<Svar>"

    Eksempler:
      | Stillingsprosent | Svar     |
      | 80               | Ja       |
      | 70               | Uavklart |