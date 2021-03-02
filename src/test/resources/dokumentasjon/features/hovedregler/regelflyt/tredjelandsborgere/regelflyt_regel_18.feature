# language: no
# encoding: UTF-8

Egenskap: Regelflyt for regel 18, for tredjelandsborger

  Bakgrunn:

    Gitt følgende bostedsadresser i personhistorikken
      | Landkode | Fra og med dato | Til og med dato |
      | NOR      | 10.10.1975      |                 |

    Og følgende arbeidsforhold fra AAReg
      | Fra og med dato | Til og med dato | Arbeidsgivertype | Arbeidsforholdstype | Arbeidsgiver Id |
      | 10.10.1975      |                 | Organisasjon     | NORMALT             | 1               |

    Og følgende arbeidsgiver i arbeidsforholdet
      | Identifikator       | Arbeidsgivertype | Landkode | Antall ansatte |
      | organisasjonsnummer | STAT             | NOR      | 10             |

    Og følgende detaljer om ansatte for arbeidsgiver
      | Antall ansatte | Gyldighetsperiode gyldig fra | Gyldighetsperiode gyldig til |
      | 10             | 10.10.1975                   | 01.08.2021                   |

    Og følgende statsborgerskap i personhistorikken
      | Landkode | Fra og med dato | Til og med dato |
      | USA      | 10.10.1975      |                 |

    Og følgende oppholdstillatelse
      | Gyldig fra og med | Gyldig til og med | Klasse                          | Har tillatelse | Type      |
      | 01.01.2020        |                   | OppholdstillatelsePaSammeVilkar | Ja             | PERMANENT |

    Og følgende arbeidsadgang
      | Gyldig fra og med | Gyldig til og med | Arbeidsadgang | ArbeidsadgangType | ArbeidomfangKategori |
      | 01.01.2020        |                   | Ja            | GENERELL          | KUN_ARBEID_HELTID    |

  Scenariomal: Regelflyt regel 18, for tredjelandsborger

    Gitt følgende arbeidsavtaler i arbeidsforholdet
      | Fra og med dato | Til og med dato | Yrkeskode | Stillingsprosent   |
      | 10.10.1975      | 01.08.2021      | yrkeskode | <Stillingsprosent> |

    Når medlemskap beregnes med følgende parametre
      | Fra og med dato | Til og med dato | Har hatt arbeid utenfor Norge |
      | 27.01.2021      | 12.02.2021      | Nei                           |

    Så skal svaret være "<Medlemskap>"
    Og skal regel "18" gi svaret "<Regel 18>"

    Eksempler:
      | Stillingsprosent | Regel 18 | Medlemskap |
      | 20               | Nei      | Uavklart   |
      | 80               | Ja       | Ja         |