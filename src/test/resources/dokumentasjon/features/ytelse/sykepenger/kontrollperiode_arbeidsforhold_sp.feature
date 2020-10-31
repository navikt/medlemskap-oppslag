# language: no
# encoding: UTF-8

Egenskap: Beregne kontrollperiode for arbeidsforhold fra sykepengeperiode

  Scenariomal:  Beregne kontrollperiode for arbeidsforhold for ytelsen sykepenger

    Når kontrollperiode for arbeidsforhold beregnes med følgende parametre:
      | Fra og med dato | Til og med dato | Første dag for ytelse   |
      | 05.05.2020      | 15.05.2020      | <Første dag for ytelse> |

    Så skal kontrollperioden være:
      | Fra og med dato                   | Til og med dato                   |
      | <Kontrollperiode fra og med dato> | <Kontrollperiode til og med dato> |

    Eksempler:
      | Første dag for ytelse | Kontrollperiode fra og med dato | Kontrollperiode til og med dato |
      |                       | 04.05.2019                      | 04.05.2020                      |
      | 01.04.2020            | 01.04.2019                      | 01.04.2020                      |
