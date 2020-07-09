# language: no
# encoding: UTF-8

Egenskap: Regel 1.2.1: Er det periode uten medlemskap innenfor 12 mnd?

  Scenariomal: Sjekk en periode fra MEDL

    Gitt følgende medlemsunntak fra MEDL
      | Dekning | Fra og med dato | Til og med dato   | Er medlem   | Lovvalg | Lovvalgsland |
      |         | 01.01.2019      | <Til og med dato> | <Er medlem> | ENDL    | NOR          |

    Når regel "1.2.1" kjøres med følgende parametre
      | Fra og med dato | Til og med dato | Har hatt arbeid utenfor Norge |
      | 30.01.2020      | 10.02.2020      | Nei                           |

    Så skal svaret være "<Svar>"

    Eksempler:
      | Er medlem | Til og med dato | Svar |
      | Nei       | 01.06.2020      | Ja   |
      | Nei       | 02.02.2020      | Nei  |
      | Ja        | 01.06.2020      | Nei  |

  Scenariomal: Flere perioder fra MEDL

    Gitt følgende medlemsunntak fra MEDL
      | Dekning | Fra og med dato   | Til og med dato    | Er medlem | Lovvalg | Lovvalgsland |
      |         | 01.01.2019        | <Til og med dato>  | Nei       | ENDL    | NOR          |
      |         | <Fra og med dato> | <Til og med dato2> | Nei       | ENDL    | NOR          |

    Når regel "1.2.1" kjøres med følgende parametre
      | Fra og med dato | Til og med dato | Har hatt arbeid utenfor Norge |
      | 30.01.2020      | 10.02.2020      | Nei                           |

    Så skal svaret være "<Svar>"

    Eksempler:
      | Til og med dato | Fra og med dato | Til og med dato2 | Svar |
      | 01.06.2019      | 02.06.2019      | 10.02.2020       | Ja   |
      | 01.06.2019      | 02.06.2019      | 09.02.2020       | Nei  |
      | 01.06.2019      | 04.06.2019      | 01.06.2021       | Nei  |
