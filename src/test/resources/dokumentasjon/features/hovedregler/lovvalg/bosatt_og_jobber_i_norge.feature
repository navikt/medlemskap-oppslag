# language: no
# encoding: UTF-8

Egenskap: Reglene 9 til 12
          Bruker som er folkeregistrert (regel 10), er norsk borger (regel 11) og har mer enn 25 % stilling (regel 12)

  Scenariomal: Bruker som er folkeregistrert i Norge og som jobber i Norge

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
      | Landkode          | Fra og med dato | Til og med dato |
      | <Statsborgerskap> | 01.01.2000      |                 |

    Og følgende arbeidsforhold fra AAReg
      | Fra og med dato | Til og med dato | Arbeidsgivertype | Arbeidsforholdstype | Yrkeskode | Stillingsprosent   | Skipsregister |
      | 01.01.2018      |                 | Organisasjon     | NORMALT             | 001       | <Stillingsprosent> |               |

    Og følgende arbeidsgiver i arbeidsforholdet
      | Identifikator | Arbeidsgivertype | Landkode | Antall ansatte |
      | 1             | BEDR             | NOR      | 9              |

    Når hovedregel med avklaring "Er lovvalget norsk lov?" kjøres med følgende parametre
      | Fra og med dato | Til og med dato | Har hatt arbeid utenfor Norge |
      | 30.01.2020      | 30.01.2021      | Nei                           |

    Så skal svaret på hovedregelen være "<Svar>"

    Eksempler:
      | Statsborgerskap | Stillingsprosent | Svar     |
      | NOR             | 100              | Ja       |
      | NOR             | 20               | UAVKLART |
      | FRA             | 100              | UAVKLART |
      | USA             | 100              | UAVKLART |

