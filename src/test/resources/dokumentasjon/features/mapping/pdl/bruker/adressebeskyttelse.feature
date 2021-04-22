# language: no
# encoding: UTF-8

Egenskap: Mapping av adressebeskyttelse fra PDL HentPerson.Adressebeskyttelse

  Scenariomal: en person med gradering

    Gitt følgende adressebeskyttelse fra PDL
      | Gradering   |
      | <Gradering> |

    Når adressebeskyttelse mappes

    Så skal mappede adressebeskyttelse være
      | Gradering   |
      | <Gradering> |

    Eksempler:
      | Gradering                |
      | UGRADERT                 |
      | FORTROLIG                |
      | STRENGT_FORTROLIG        |
      | STRENGT_FORTROLIG_UTLAND |