# language: no
# encoding: UTF-8

Egenskap: Regel 10: Er bruker folkeregistert i Norge?

  Bakgrunn:
    Gitt følgende personstatuser i personhistorikken
      | Personstatus | Fra og med dato | Til og med dato |
      | FØDR         | 01.01.2000      |                 |

    Og følgende statsborgerskap i personhistorikken
      | Landkode | Fra og med dato | Til og med dato |
      | NOR      | 01.01.2000      |                 |

    Og følgende arbeidsforhold fra AAReg
      | Fra og med dato | Til og med dato | Arbeidsgivertype | Arbeidsforholdstype | Yrkeskode | Stillingsprosent | Skipsregister |
      | 01.01.2018      |                 | Organisasjon     | NORMALT             | 001       | 100              |               |

    Og følgende arbeidsgiver i arbeidsforholdet
      | Identifikator | Arbeidsgivertype | Landkode | Antall ansatte |
      | 1             | BEDR             | NOR      | 9              |

  Scenariomal: Bruker har folkeregistrert postadresse og bostedsadresse

    Gitt følgende bostedsadresser i personhistorikken
      | Adresse | Landkode     | Fra og med dato | Til og med dato |
      | Oslo    | <Bor i land> | 01.01.2000      |                 |

    Gitt følgende postadresser i personhistorikken
      | Adresse | Landkode    | Fra og med dato | Til og med dato |
      | Oslo    | <Post land> | 01.01.2000      |                 |

    Når hovedregel med avklaring "Er lovvalget norsk lov?" kjøres med følgende parametre
      | Fra og med dato | Til og med dato | Har hatt arbeid utenfor Norge |
      | 30.01.2020      | 30.01.2021      | <Arbeid utenfor Norge>        |

    Så skal regel "10" gi svaret "<Svar>"

    Eksempler:
    | Bor i land | Post land | Svar |
    | NOR        | NOR       | Ja   |
    | NOR        | FRA       | Nei  |
    | FRA        | FRA       | Nei  |

  Scenariomal: Ingen postadresse, men bostedsadresse

    Gitt følgende bostedsadresser i personhistorikken
      | Adresse | Landkode | Fra og med dato | Til og med dato |
      | XXX     | <Land>   | 01.01.2000      |                 |

    Når hovedregel med avklaring "Er lovvalget norsk lov?" kjøres med følgende parametre
      | Fra og med dato | Til og med dato | Har hatt arbeid utenfor Norge |
      | 30.01.2020      | 30.01.2021      | <Arbeid utenfor Norge>        |

    Så skal regel "10" gi svaret "<Svar>"

    Eksempler:
      | Land | Svar |
      | NOR  | Ja   |
      | FRA  | Nei  |