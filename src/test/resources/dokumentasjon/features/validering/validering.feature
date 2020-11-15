# language: no
# encoding: UTF-8

Egenskap: Validering av request

  Bakgrunn:

    Gitt følgende statsborgerskap i personhistorikken
      | Landkode   | Fra og med dato | Til og med dato |
      | <Landkode> | 01.01.2000      |                 |

  Scenariomal: Gyldig fra og med dato avhenger av hvilket statsborgerskap brukeren har

    Gitt følgende statsborgerskap i personhistorikken
      | Landkode   | Fra og med dato | Til og med dato |
      | <Landkode> | 01.01.2000      |                 |

    Når regel "0.1" kjøres med følgende parametre
      | Fra og med dato   | Til og med dato | Har hatt arbeid utenfor Norge |
      | <Fra og med dato> | 30.01.2021      | <Har jobbet utenfor Norge>    |

    Så skal svaret være "<Regel 0.1>"
    Og skal begrunnelsen være "<Begrunnelse>"

    Eksempler:
      | Landkode | Fra og med dato | Regel 0.1 | Begrunnelse                              |
      | NOR      | 31.12.2015      | Nei       | Periode fom kan ikke være før 2016-01-01 |
      | NOR      | 01.01.2016      | Ja        |                                          |
      | NOR      | 31.12.2016      | Ja        |                                          |
      | CHE      | 31.12.2016      | Nei       | Periode fom kan ikke være før 2017-01-01 |
      | CHE      | 01.01.2017      | Ja        |                                          |


  Scenariomal: Hvis første dag for ytelse er angitt, er det den som brukes istedet for "fra og med dato"

    Gitt følgende statsborgerskap i personhistorikken
      | Landkode   | Fra og med dato | Til og med dato |
      | <Landkode> | 01.01.2000      |                 |

    Når regel "0.1" kjøres med følgende parametre
      | Fra og med dato | Første dag for ytelse   | Til og med dato | Har hatt arbeid utenfor Norge |
      | 01.01.2017      | <Første dag for ytelse> | 30.01.2021      | <Har jobbet utenfor Norge>    |

    Så skal svaret være "<Regel 0.1>"
    Og skal begrunnelsen være "<Begrunnelse>"

    Eksempler:
      | Landkode | Første dag for ytelse | Regel 0.1 | Begrunnelse                                        |
      | NOR      | 31.12.2015            | Nei       | Første dag for ytelse kan ikke være før 2016-01-01 |
      | NOR      | 01.01.2016            | Ja        |                                                    |
      | NOR      | 31.12.2016            | Ja        |                                                    |
      | CHE      | 31.12.2016            | Nei       | Første dag for ytelse kan ikke være før 2017-01-01 |
      | CHE      | 01.01.2017            | Ja        |                                                    |

  Scenariomal: Bruker har annet EØS statsborgerskap i tillegg til sveitsisk

    Gitt følgende statsborgerskap i personhistorikken
      | Landkode   | Fra og med dato | Til og med dato |
      | CHE        | 01.01.2000      |                 |
      | <Landkode> | 01.01.2000      |                 |


    Når regel "0.1" kjøres med følgende parametre
      | Fra og med dato   | Til og med dato | Har hatt arbeid utenfor Norge |
      | <Fra og med dato> | 30.01.2021      | <Har jobbet utenfor Norge>    |

    Så skal svaret være "<Regel 0.1>"
    Og skal begrunnelsen være "<Begrunnelse>"

    Eksempler:
      | Landkode | Fra og med dato | Regel 0.1 | Begrunnelse                              |
      | NOR      | 31.12.2015      | Nei       | Periode fom kan ikke være før 2016-01-01 |
      | NOR      | 01.01.2016      | Ja        |                                          |
      | NOR      | 31.12.2016      | Ja        |                                          |
      | SWE      | 31.12.2015      | Nei       | Periode fom kan ikke være før 2016-01-01 |
      | SWE      | 01.01.2016      | Ja        |                                          |
      | SWE      | 01.01.2017      | Ja        |                                          |