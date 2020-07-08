# language: no
# encoding: UTF-8

Egenskap: Regel 1.5: Er dekning uavklart?

  Scenariomal: Sjekk en periode fra MEDL

    Gitt følgende medlemsunntak fra MEDL
      | Dekning   | Fra og med dato | Til og med dato | Er medlem | Lovvalg | Lovvalgsland |
      | <Dekning> | 01.01.2019      | 01.06.2020      | Ja        | ENDL    | NOR          |

    Når regel "1.5" kjøres med følgende parametre
      | Fra og med dato | Til og med dato | Har hatt arbeid utenfor Norge | Ytelse   |
      | 30.01.2020      | 10.02.2020      | Nei                           | <Ytelse> |

    Så skal svaret være "<Svar>"

    Eksempler:
      | Dekning           | Ytelse           | Svar |
      |                   | SYKEPENGER       | Ja   |
      | Full              | SYKEPENGER       | Ja   |
      | FTL_2-6           | SYKEPENGER       | Nei  |
      | FTL_2-7a_2_ledd_a | SYKEPENGER       | Ja   |
      | FTL_2-7a_2_ledd_a | DAGPENGER        | Nei  |
      | Full              | DAGPENGER        | Ja   |
      | Full              | ENSLIG_FORSORGER | Ja   |

  Scenariomal: Flere perioder fra MEDL

    Gitt følgende medlemsunntak fra MEDL
      | Dekning   | Fra og med dato | Til og med dato | Er medlem | Lovvalg | Lovvalgsland |
      | Full      | 01.01.2019      | 01.06.2019      | Ja        | ENDL    | NOR          |
      | <Dekning> | 02.06.2019      | 31.12.2022      | Ja        | ENDL    | NOR          |

    Når regel "1.5" kjøres med følgende parametre
      | Fra og med dato | Til og med dato | Har hatt arbeid utenfor Norge | Ytelse   |
      | 30.01.2020      | 10.02.2020      | Nei                           | <Ytelse> |

    Så skal svaret være "<Svar>"

    Eksempler:
      | Dekning | Ytelse     | Svar |
      |         | SYKEPENGER | Ja   |
      | FTL_2-6 | SYKEPENGER | Nei  |
