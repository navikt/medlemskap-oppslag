# language: no
# encoding: UTF-8
#@ignored
#TODO: Eksempel på hvordan man kan hente grunnlag fra tabeller istedet for fra JSON
#TODO: Gjøre ferdig eksempelet
Egenskap: Man er medlem hvis man er norsk statsborger som bor i Norge, og som har jobbet i Norge siste 12 måneder

  Scenariomal: Statsborgerskap avgjør om en som bor i Norge og jobber i Norge er medlem

    Gitt følgende bostedsadresser i personhistorikken
      | Adresse | Landkode | Fra og med dato | Til og med dato |
      | Oslo    | NOR      | 01.01.2000      |                 |

    Gitt følgende arbeidsgivere
      | Identifikator | Arbeidsgivertype | Landkode | Antall ansatte |
      | 1             | BEDR             | NOR      | 9              |

    Gitt følgende utenlandsopphold
      | Landkode | Fra og med dato | Til og med dato | Rapporteringsperiode |
      | FRA      | 01.01.2018      | 30.06.2019      | 2019-07              |

    Gitt følgende arbeidsforhold
      | Fra og med dato | Til og med dato | Arbeidsgivertype | Arbeidsforholdstype | Yrkeskode | Stillingsprosent | Skipsregister |
      | 01.01.2018      |                 | Organisasjon     | NORMALT             | 001       | 100              | NIS           |

    Gitt følgende statsborgerskap i personhistorikken
      | Landkode          | Fra og med dato | Til og med dato |
      | <Statsborgerskap> | 30.01.2000      |                 |

    Når medlemskap beregnes med følgende parametre
      | Fra og med dato | Til og med dato | Har hatt arbeid utenfor Norge |
      | 15.01.2020      | 30.01.2020      | Nei                           |

    Så skal medlemskap være <medlemskap>

    Eksempler:
      | Statsborgerskap | medlemskap |
      | NOR             | "Ja"       |
      | FRA             | "UAVKLART" |




