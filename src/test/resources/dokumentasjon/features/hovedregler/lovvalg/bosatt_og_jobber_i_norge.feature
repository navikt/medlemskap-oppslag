# language: no
# encoding: UTF-8

Egenskap: Reglene 9 til 12
  Bruker som er folkeregistrert (regel 10), er norsk borger (regel 11) og har jobb i Norge (regel 12)

  Bakgrunn:
    Gitt følgende bostedsadresser i personhistorikken
     | Landkode | Fra og med dato | Til og med dato |
     | NOR      | 01.01.2000      |                 |

    Og følgende kontaktadresser i personhistorikken
     | Landkode | Fra og med dato | Til og med dato |
     | NOR      | 01.01.2000      |                 |

    Og følgende personstatuser i personhistorikken
      | Personstatus | Fra og med dato | Til og med dato |
      | BOSATT       | 01.01.2000      |                 |

    Og følgende statsborgerskap i personhistorikken
      | Landkode | Fra og med dato | Til og med dato |
      | NOR      | 01.01.2000      |                 |

    Og følgende arbeidsgiver i arbeidsforholdet
      | Identifikator | Arbeidsgivertype | Landkode | Antall ansatte |
      | 1             | BEDR             | NOR      | 9              |

  Scenariomal: Regel 12: Bruker får ja på spørsmålet om medlemskap, hvis stillingsprosent er over 25 %, uavklart ellers
    Gitt følgende arbeidsforhold fra AAReg
      | Fra og med dato | Til og med dato | Arbeidsgivertype | Arbeidsforholdstype | Arbeidsgiver Id |
      | 01.01.2018      |                 | Organisasjon     | NORMALT             | 1               |

    Og følgende arbeidsavtaler i arbeidsforholdet
      | Fra og med dato | Til og med dato | Yrkeskode | Stillingsprosent   | Skipsregister |
      | 01.01.2018      |                 | 001       | <Stillingsprosent> |               |

    Når medlemskap beregnes med følgende parametre
      | Fra og med dato | Til og med dato | Har hatt arbeid utenfor Norge |
      | 30.01.2020      | 30.01.2021      | Nei                           |

    Så skal svaret være "<Svar>"
    Og skal regel "10" gi svaret "Ja"
    Og skal regel "12" gi svaret "<Regel 12>"

    Eksempler:
      | Stillingsprosent | Svar     | Regel 12 |
      | 100              | Ja       | Ja       |
      | 20               | UAVKLART | Nei      |

  Scenariomal: Regel 9: Bruker som svarer ja på spørsmålet "Har hatt arbeid utenfor Norge" skal få "Nei"
    Gitt følgende arbeidsforhold fra AAReg
      | Fra og med dato | Til og med dato | Arbeidsgivertype | Arbeidsforholdstype |
      | 01.01.2018      |                 | Organisasjon     | NORMALT             |
      | 01.01.2018      |                 | Organisasjon     | NORMALT             |

    Og følgende arbeidsavtaler i arbeidsforholdet
      | Fra og med dato | Til og med dato | Yrkeskode | Stillingsprosent | Skipsregister |
      | 01.01.2018      |                 | 001       | 100              |               |

    Når medlemskap beregnes med følgende parametre
      | Fra og med dato | Til og med dato | Har hatt arbeid utenfor Norge   |
      | 30.01.2020      | 30.01.2021      | <Har hatt arbeid utenfor Norge> |

    Så skal svaret være "<Medlemskap>"
    Og skal regel "9" gi svaret "<Regel 9>"

    Eksempler:
      | Har hatt arbeid utenfor Norge | Medlemskap | Regel 9 |
      | Nei                           | Ja         | Nei     |
      | Ja                            | UAVKLART   | Ja      |
