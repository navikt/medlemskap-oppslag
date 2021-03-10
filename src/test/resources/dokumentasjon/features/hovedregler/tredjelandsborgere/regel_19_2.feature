# language: no
# encoding: UTF-8

Egenskap: Regel 19.2: Har bruker arbeids- og oppholdstillatelse i kontrollperiode?

  Scenariomal: Regel 19.2: Har bruker arbeids- og oppholdstillatelse i kontrollperiode?

    Gitt følgende oppholdstillatelse
      | Gyldig fra og med   | Gyldig til og med   | Har tillatelse   | Type                                 |
      | <Gyldig fra og med> | <Gyldig til og med> | <Har tillatelse> | <OppholdstillaelsePaSammeVilkarType> |

    Når regel "19.2" kjøres med følgende parametre
      | Fra og med dato | Til og med dato | Første dag for ytelse | Har hatt arbeid utenfor Norge |
      | 23.01.2020      | 30.01.2020      | 22.01.2020            | Nei                           |


    Så skal svaret være "<Svar>"
    Og skal startdato for ytelse være "<Startdato for ytelse>"
    Og skal kontrollperioden være:
      | Fra og med dato                   | Til og med dato                   |
      | <Kontrollperiode fra og med dato> | <Kontrollperiode til og med dato> |

    Eksempler:
      | Gyldig fra og med | Gyldig til og med | Har tillatelse | Svar | OppholdstillaelsePaSammeVilkarType | Kontrollperiode fra og med dato | Kontrollperiode til og med dato | Startdato for ytelse | Forklaring |
      | 01.01.2018        |                   | Ja             | Ja   | PERMANENT                          | 23.01.2019                      | 30.03.2020                      |                      |            |
      | 23.01.2019        |                   | Ja             | Nei  | PERMANENT                          | 23.01.2019                      | 30.03.2020                      |                      |            |
      | 22.01.2019        |                   | Ja             | Ja   | PERMANENT                          | 23.01.2019                      | 30.03.2020                      |                      |            |
      | 22.01.2019        | 22.03.2020        | Ja             | Ja   | PERMANENT                          | 23.01.2019                      | 30.03.2020                      |                      |            |
      |                   | 22.03.2020        | Ja             | Ja   | MIDLERTIDIG                        | 23.01.2019                      | 30.03.2020                      |                      |            |
      |                   | 21.03.2020        | Ja             | Nei  | MIDLERTIDIG                        | 23.01.2019                      | 30.03.2020                      |                      |            |
