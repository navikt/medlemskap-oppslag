# language: no
# encoding: UTF-8

Egenskap: Regel 9: Har bruker utført arbeid utenfor Norge?

  Scenariomal: Regel 9: Har bruker utført arbeid utenfor Norge

    Gitt følgende bostedsadresser i personhistorikken
      | Adresse | Landkode | Fra og med dato | Til og med dato |
      | Oslo    | NOR      | 01.01.2000      |                 |

    Og følgende postadresser i personhistorikken
      | Adresse | Landkode | Fra og med dato | Til og med dato |
      | Oslo    | NOR      | 01.01.2000      |                 |

    Og følgende midlertidige adresser i personhistorikken
      | Adresse | Landkode | Fra og med dato | Til og med dato |
      | Oslo    | NOR      | 01.01.2000      |                 |

    Og følgende personstatuser i personhistorikken
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

    Når hovedregel med avklaring "Er lovvalget norsk lov?" kjøres med følgende parametre
      | Fra og med dato | Til og med dato | Har hatt arbeid utenfor Norge |
      | 30.01.2020      | 30.01.2021      | <Arbeid utenfor Norge>        |

    Så skal regel "9" gi svaret "<Delresultat>"

    Og skal svaret på hovedregelen være "<Svar>"

    Eksempler:
      | Arbeid utenfor Norge | Delresultat | Svar |
      | Ja                   | Ja          | Nei  |
      | Nei                  | Nei         | Ja   |
