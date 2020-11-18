# language: no
# encoding: UTF-8
@ignored
Egenskap: Bakoverkompatibel test

  Bakgrunn:

    Gitt følgende oppgaver fra Gosys
      | Aktiv dato | Prioritet | Status | Tema |
      | 10.10.1975 | NORM      | AAPNET | Tema |

    Og følgende journalposter fra Joark
      | JournalpostId | Tittel | Journalposttype | Journalstatus | Tema |
      | Id            | Tittel | Posttype        | Status        | Tema |

    Og følgende medlemsunntak fra MEDL
      | Dekning | Fra og med dato | Til og med dato | Er medlem | Lovvalg | Lovvalgsland | Periodestatus |
      | dekning | 10.10.1975      | 01.08.2020      | Ja        | ENDL    | NOR          | GYLD          |

    Og følgende bostedsadresser i personhistorikken
      | Landkode | Fra og med dato | Til og med dato |
      | NOR      | 10.10.1975      | 01.08.2020      |

    Og følgende bostedsadresser i personhistorikken
      | Landkode | Fra og med dato | Til og med dato |
      | NOR      | 10.10.1975      | 01.08.2020      |

    Og følgende kontaktadresser i personhistorikken
      | Landkode | Fra og med dato | Til og med dato |
      | NOR      | 10.10.1975      | 01.08.2020      |

    Og følgende statsborgerskap i personhistorikken
      | Landkode | Fra og med dato | Til og med dato |
      | NOR      | 10.10.1975      | 01.08.2020      |

    Og følgende sivilstand i personhistorikk fra PDL
      | Sivilstandstype | Gyldig fra og med dato | Gyldig til og med dato | Relatert ved sivilstand |
      | GIFT            | 10.10.1975             | 01.08.2020             | 0101197512345           |

    Og følgende personhistorikk for ektefelle fra PDL
      | Ident         | Bosted | Fra og med dato | Til og med dato |
      | 0101197512345 | NOR    | 10.10.1975      | 01.08.2020      |

    Og følgende arbeidsforhold til ektefelle fra AAReg
      | Fra og med dato | Til og med dato | Arbeidsgivertype | Arbeidsforholdstype |
      | 10.10.1975      | 01.08.2020      | Organisasjon     | NORMALT             |

    Og følgende arbeidsavtaler til ektefelle i arbeidsforholdet
      | Fra og med dato | Til og med dato | Yrkeskode | Stillingsprosent | Skipsregister | Beregnet antall timer pr uke |
      | 10.10.1975      | 01.08.2020      | yrkeskode | 100              | NIS           | 37.5                         |

    Og følgende barn i personhistorikk for ektefelle fra PDL
      | Ident         |
      | 0101201012345 |

    Og følgende familierelasjoner fra PDL:
      | Relatert persons ident | Relatert persons rolle | Min rolle for person |
      | 0101201012345          | BARN                   | FAR                  |

    Og følgende arbeidsforhold fra AAReg
      | Fra og med dato | Til og med dato | Arbeidsgivertype | Arbeidsforholdstype | Arbeidsgiver Id |
      | 10.10.1975      | 01.08.2020      | Organisasjon     | NORMALT             | 1               |

    Og følgende arbeidsgiver i arbeidsforholdet
      | Identifikator       | Arbeidsgivertype | Landkode | Antall ansatte | Juridisk enhetstype |
      | organisasjonsnummer | STAT             | NOR      | 10             | STAT                |

    Og følgende arbeidsavtaler i arbeidsforholdet
      | Fra og med dato | Til og med dato | Yrkeskode | Stillingsprosent | Skipsregister | Beregnet antall timer pr uke |
      | 10.10.1975      | 01.08.2020      | yrkeskode | 100              | NIS           | 37.5                         |


  @ignored
  Scenario: Bakoverkompatibel test uten ytelse i request
    Når rest kall med følgende parametere
      | Fødselsnummer | Fra og med dato | Til og med dato | Har hatt arbeid utenfor Norge |
      | 15076500565   | 01.01.2019      | 31.12.2019      | Nei                           |

    Så skal forventet json respons være "forventetRespons"

  @ignored
  Scenario: Kontraktstest
    Når rest kall med følgende parametere
      | Fødselsnummer | Fra og med dato | Til og med dato | Har hatt arbeid utenfor Norge | Ytelse     |
      | 15076500565   | 01.01.2019      | 31.12.2019      | Nei                           | SYKEPENGER |

    Så Skal kontrakt være OK