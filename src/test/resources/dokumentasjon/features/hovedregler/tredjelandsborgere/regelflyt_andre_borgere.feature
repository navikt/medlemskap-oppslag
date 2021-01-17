# language: no
# encoding: UTF-8

Egenskap: Regelflyt for tredjelandsborgere

  Bakgrunn:

    Gitt følgende bostedsadresser i personhistorikken
      | Landkode | Fra og med dato | Til og med dato |
      | NOR      | 10.10.1975      | 01.08.2020      |

    Og følgende statsborgerskap i personhistorikken
      | Landkode | Fra og med dato | Til og med dato |
      | RUS      | 10.10.1975      |                 |

    Og følgende oppholdstillatelse
      | Gyldig fra og med | Gyldig til og med | Har tillatelse | Type      |
      | 01.01.2018        |                   | Ja             | PERMANENT |

    Og følgende arbeidsadgang
      | Gyldig fra og med | Gyldig til og med | Arbeidsadgang | ArbeidsadgangType | ArbeidomfangKategori |
      | 01.01.2018        |                   | Ja            | GENERELL          | KUN_ARBEID_HELTID    |

    Og følgende arbeidsforhold fra AAReg
      | Fra og med dato | Til og med dato | Arbeidsgivertype | Arbeidsforholdstype | Arbeidsgiver Id |
      | 10.10.1975      | 01.08.2020      | Organisasjon     | NORMALT             | 1               |

    Og følgende arbeidsgiver i arbeidsforholdet
      | Identifikator       | Arbeidsgivertype | Landkode | Antall ansatte | Antall ansatte i juridisk enhet | Juridisk enhetstype | Juridisk orgnr    | Konkursstatus |
      | organisasjonsnummer | STAT             | NOR      | 10             | 20                              | STAT                | juridiskOrgnummer | Konkursstatus |

    Og følgende detaljer om ansatte for arbeidsgiver
      | Antall ansatte | Gyldighetsperiode gyldig fra | Gyldighetsperiode gyldig til | Bruksperiode gyldig fra | Bruksperiode gyldig til |
      | 10             | 10.10.1975                   | 01.08.2020                   | 10.10.1975              | 01.08.2020              |

    Og følgende arbeidsavtaler i arbeidsforholdet
      | Fra og med dato | Til og med dato | Yrkeskode | Stillingsprosent | Skipsregister | Beregnet antall timer pr uke |
      | 10.10.1975      | 01.08.2020      | yrkeskode | 100              | NIS           | 37.5                         |

  Scenario: Opplysninger i UDI
    Når medlemskap beregnes med følgende parametre
      | Fra og med dato | Til og med dato | Har hatt arbeid utenfor Norge |
      | 01.02.2020      | 12.02.2020      | Nei                           |

    Så skal svaret være "UAVKLART"
    Og skal regel-årsaker være "ANDRE BORGERE"