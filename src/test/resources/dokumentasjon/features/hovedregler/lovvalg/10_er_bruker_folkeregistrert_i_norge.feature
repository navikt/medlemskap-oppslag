# language: no
# encoding: UTF-8

Egenskap: Regel 10: Er bruker folkeregistert i Norge?

  Scenariomal: Bruker har folkeregistrert postadresse og bostedsadresse

    Gitt følgende bostedsadresser i personhistorikken
      | Adresse | Landkode     | Fra og med dato | Til og med dato |
      | Oslo    | <Bor i land> | 01.01.2000      |                 |

    Gitt følgende postadresser i personhistorikken
      | Adresse | Landkode    | Fra og med dato | Til og med dato |
      | Oslo    | <Post land> | 01.01.2000      |                 |

    Når regel "10" kjøres med følgende parametre
      | Fra og med dato | Til og med dato | Har hatt arbeid utenfor Norge |
      | 30.01.2020      | 30.01.2021      | <Arbeid utenfor Norge>        |

    Så skal regelen gi svaret "<Svar>"

    Eksempler:
      | Bor i land | Post land | Svar |
      | NOR        | NOR       | Ja   |
      | NOR        | FRA       | Nei  |


  Scenariomal: Ingen postadresse, men bostedsadresse

    Gitt følgende bostedsadresser i personhistorikken
      | Adresse | Landkode | Fra og med dato | Til og med dato |
      | XXX     | <Land>   | 01.01.2000      |                 |

    Når regel "10" kjøres med følgende parametre
      | Fra og med dato | Til og med dato | Har hatt arbeid utenfor Norge |
      | 30.01.2020      | 30.01.2021      | <Arbeid utenfor Norge>        |

    Så skal regelen gi svaret "<Svar>"

    Eksempler:
      | Land | Svar |
      | NOR  | Ja   |
      | FRA  | Ja   |

  Scenariomal: Postadresse, men ikke bostedsadresse

    Gitt følgende postadresser i personhistorikken
      | Adresse | Landkode | Fra og med dato | Til og med dato |
      | XXX     | <Land>   | 01.01.2000      |                 |

    Når regel "10" kjøres med følgende parametre
      | Fra og med dato | Til og med dato | Har hatt arbeid utenfor Norge |
      | 30.01.2020      | 30.01.2021      | <Arbeid utenfor Norge>        |

    Så skal regelen gi svaret "<Svar>"

    Eksempler:
      | Land | Svar |
      | NOR  | Nei  |
      | FRA  | Nei  |