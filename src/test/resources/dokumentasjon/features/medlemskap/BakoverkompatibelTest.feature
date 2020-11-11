# language: no
# encoding: UTF-8

Egenskap: Bakoverkompatibel test

  Scenario:
    Når rest kall med følgende parametere
      | Fødselsnummer | Fra og med dato | Til og med dato | Har hatt arbeid utenfor Norge |
      | 15076500565   | 01.01.2019      | 31.12.2019      | Nei                           |

    Så skal forventet json respons være "forventetRespons"

  Scenario:
    Når rest kall med følgende parametere
      | Fødselsnummer | Fra og med dato | Til og med dato | Har hatt arbeid utenfor Norge | Ytelse     |
      | 15076500565   | 01.01.2019      | 31.12.2019      | Nei                           | SYKEPENGER |

    Så Skal kontrakt være OK