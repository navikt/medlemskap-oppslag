# language: no
# encoding: UTF-8

Egenskap: Regelflyt for kontroll av utenlandsforhold

  Bakgrunn:
    Gitt følgende bostedsadresser i personhistorikken
      | Adresse | Landkode | Fra og med dato | Til og med dato |
      | Oslo    | NOR      | 01.01.2000      |                 |

    Og følgende statsborgerskap i personhistorikken
      | Landkode | Fra og med dato | Til og med dato |
      | NOR      | 01.01.2000      |                 |

  Scenariomal: Regelflyt utenlandsforhold: Bruker får "uavklart" på spørsmålet om medlemskap hvis det ikke finnes arbeidsforhold i
  hele kontrollperioden.

    Gitt følgende arbeidsforhold fra AAReg
      | Fra og med dato | Til og med dato | Arbeidsgivertype | Arbeidsforholdstype | Arbeidsgiver Id |

    Når medlemskap beregnes med følgende parametre
      | Fra og med dato | Til og med dato | Har hatt arbeid utenfor Norge | Ytelse   |
      | 30.01.2020      | 30.01.2021      | Nei                           | <Ytelse> |

    Så skal svaret være "<Medlemskap>"
    Og skal regel "Utenlandsforhold" gi svaret "<SVAR>"
    Og skal regel "FELLES ARBEIDSFORHOLD" gi svaret "<SVAR2>"

    Eksempler:
      | Ytelse    | Fra og med dato | SVAR | SVAR2    | Medlemskap |
      | DAGPENGER | 02.02.2019      | Ja   | UAVKLART | UAVKLART   |


  Scenariomal: Regelflyt utenlandsforhold: Bruker får "ja" i både utenlandsopphold og felles arbeidsforhold på spørsmålet om medlemskap hvis det finnes arbeidsforhold i
  hele kontrollperioden, uten utenlandsopphold.

    Gitt følgende arbeidsforhold fra AAReg
      | Fra og med dato   | Til og med dato | Arbeidsgivertype | Arbeidsforholdstype | Arbeidsgiver Id |
      | <Fra og med dato> |                 | Organisasjon     | NORMALT             | 1               |

    Og følgende arbeidsavtaler i arbeidsforholdet
      | Fra og med dato | Til og med dato | Yrkeskode | Stillingsprosent |
      | 01.01.2018      |                 | 001       | 100              |

    Når medlemskap beregnes med følgende parametre
      | Fra og med dato | Til og med dato | Har hatt arbeid utenfor Norge | Ytelse   |
      | 30.01.2021      | 30.01.2022      | Nei                           | <Ytelse> |

    Så skal svaret være "<Medlemskap>"
    Og skal regel "Utenlandsforhold" gi svaret "<SVAR>"
    Og skal regel "FELLES ARBEIDSFORHOLD" gi svaret "<SVAR2>"

    Eksempler:
      | Ytelse    | Fra og med dato | SVAR | SVAR2 | Medlemskap |
      | DAGPENGER | 02.02.2019      | Ja   | Ja    | Ja         |
