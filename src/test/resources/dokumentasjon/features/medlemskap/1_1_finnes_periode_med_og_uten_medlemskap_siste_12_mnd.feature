# language: no
# encoding: UTF-8

Egenskap: Regel 1.1: Er det periode både med og uten medlemskap innenfor 12 mnd?

  Scenario: Regel 1.1: Er det periode både med og uten medlemskap innenfor 12 mnd?

    Gitt følgende medlemsunntak fra MEDL
      | Dekning | Fra og med dato | Til og med dato | Er medlem | Lovvalg | Lovvalgsland |
      |         | 01.01.2019      | 01.06.2019      | Ja        | ENDL    | NOR          |
      |         | 02.06.2019      | 31.12.2019      | Nei       | ENDL    | NOR          |

    Når regel "1.1" kjøres med følgende parametre
      | Fra og med dato | Til og med dato | Har hatt arbeid utenfor Norge |
      | 30.01.2020      | 10.02.2020      | Nei                           |

    Så skal svaret være "Ja"

  Scenario: Regel 1.1: Er det periode både med og uten medlemskap innenfor 12 mnd?

    Gitt følgende medlemsunntak fra MEDL
      | Dekning | Fra og med dato | Til og med dato | Er medlem | Lovvalg | Lovvalgsland |
      |         | 01.01.2019      | 01.06.2019      | Ja        | ENDL    | NOR          |
      |         | 02.06.2019      | 31.12.2019      | Ja        | ENDL    | NOR          |

    Når regel "1.1" kjøres med følgende parametre
      | Fra og med dato | Til og med dato | Har hatt arbeid utenfor Norge |
      | 30.01.2020      | 10.02.2020      | Nei                           |

    Så skal svaret være "Nei"