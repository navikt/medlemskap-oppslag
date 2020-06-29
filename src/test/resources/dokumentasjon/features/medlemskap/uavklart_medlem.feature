# language: no
# encoding: UTF-8

Egenskap: Uavklart hvis man har medlemsunntak fra MEDL

Bakgrunn:
Gitt følgende bostedsadresser i personhistorikken
| Adresse | Landkode | Fra og med dato | Til og med dato |
| Oslo    | NOR      | 01.01.2000      |                 |

Og følgende statsborgerskap i personhistorikken
| Landkode          | Fra og med dato | Til og med dato |
| <Statsborgerskap> | 30.01.2000      |                 |

Og følgende arbeidsforhold fra AAReg
| Fra og med dato | Til og med dato | Arbeidsgivertype | Arbeidsforholdstype | Yrkeskode | Stillingsprosent | Skipsregister |
| 01.01.2018      |                 | Organisasjon     | NORMALT             | 001       | 100              | NIS           |

Og følgende arbeidsgiver i arbeidsforholdet
| Identifikator | Arbeidsgivertype | Landkode | Antall ansatte |
| 1             | BEDR             | NOR      | 9              |

Og følgende utenlandsopphold i arbeidsforholdet
| Landkode | Fra og med dato | Til og med dato | Rapporteringsperiode |
| FRA      | 01.01.2018      | 30.06.2019      | 2019-07              |

  Scenario: Uavklart hvis man har medlemsunntak

    Gitt følgende medlemsunntak fra MEDL
      | Dekning | Fra og med dato | Til og med dato | Er medlem | Lovvalg | Lovvalgsland |
      | FTL_2-6 | 01.01.2019      | 01.01.2020      | Ja        | NOR     | NOR          |

    Når medlemskap beregnes med følgende parametre
      | Fra og med dato | Til og med dato | Har hatt arbeid utenfor Norge |
      | 15.01.2020      | 30.01.2020      | Nei                           |

    Så skal svaret på hovedregelen være "UAVKLART"

  Scenario: Uavklart hvis man har medlemsunntak

    Gitt følgende medlemsunntak fra MEDL
      | Dekning | Fra og med dato | Til og med dato | Er medlem | Lovvalg | Lovvalgsland |
      | FTL_2-6 | 01.01.2019      | 01.01.2020      | Ja        | NOR     | NOR          |

    Når medlemskap beregnes med følgende parametre
      | Fra og med dato | Til og med dato | Har hatt arbeid utenfor Norge |
      | 15.01.2020      | 30.01.2020      | Nei                           |

    Så skal medlemskap være "UAVKLART"
