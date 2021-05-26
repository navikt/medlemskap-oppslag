# language: no
# encoding: UTF-8

Egenskap: Regelflyt for regel 23

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

    Og følgende arbeidsavtaler i arbeidsforholdet
      | Fra og med dato | Til og med dato | Yrkeskode | Stillingsprosent |
      | 10.10.1975      | 01.08.2021      | yrkeskode | 100              |

    Og følgende statsborgerskap i personhistorikken
      | Landkode | Fra og med dato | Til og med dato |
      | NOR      | 10.10.1975      |                 |


  Scenariomal: Regelflyt regel 23

    Gitt følgende utenlandsopphold i arbeidsforholdet
      | Landkode | Fra og med dato | Til og med dato | Rapporteringsperiode |
      | JPN      | <Fra og med>    | <Til og med>    | 2019-07              |

    Når medlemskap beregnes med følgende parametre
      | Fra og med dato | Til og med dato | Har hatt arbeid utenfor Norge |
      | 27.01.2021      | 12.02.2021      | Nei                           |

    Så skal svaret være "<Medlemskap>"
    Og skal regel "23" gi svaret "<Regel 23>"

    Eksempler:
      | Fra og med | Til og med | Regel 23 | Medlemskap |
      | 01.05.2019 | 01.10.2019 | Nei      | Uavklart   |
      |            |            | Ja       | Uavklart   |