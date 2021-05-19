# language: no
# encoding: UTF-8

Egenskap: Regel 23: Er utenlandsoppholdet uten periode?

  Scenario: Bruker med periode i utenlandsoppholdet får "Nei"

    Gitt følgende arbeidsforhold fra AAReg
      | Fra og med dato | Til og med dato | Arbeidsgivertype | Arbeidsforholdstype |
      | 01.01.2018      | 30.06.2019      | Organisasjon     | NORMALT             |

    Og følgende utenlandsopphold i arbeidsforholdet
      | Landkode | Fra og med dato | Til og med dato | Rapporteringsperiode |
      | JPN      | 01.05.2019      | 01.10.2019      | 2019-07              |

    Når regel "23" kjøres med følgende parametre
      | Fra og med dato | Til og med dato | Har hatt arbeid utenfor Norge |
      | 30.01.2019      | 30.01.2020      | Nei                           |

    Så skal svaret være "Nei"

  Scenario: Bruker uten periode i utenlandsoppholdet får "Ja"

    Gitt følgende arbeidsforhold fra AAReg
      | Fra og med dato | Til og med dato | Arbeidsgivertype | Arbeidsforholdstype |
      | 01.01.2018      | 30.06.2019      | Organisasjon     | NORMALT             |

    Og følgende utenlandsopphold i arbeidsforholdet
      | Landkode | Rapporteringsperiode |
      | JPN      | 2019-07              |

    Når regel "23" kjøres med følgende parametre
      | Fra og med dato | Til og med dato | Har hatt arbeid utenfor Norge |
      | 30.01.2019      | 30.01.2020      | Nei                           |

    Så skal svaret være "Ja"
