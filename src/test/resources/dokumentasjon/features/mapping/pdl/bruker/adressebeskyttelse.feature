# language: no
# encoding: UTF-8

Egenskap: Mapping av adressebeskyttelse fra PDL HentPerson.Adressebeskyttelse

  Scenario: En person med ugradert

    Gitt følgende adressebeskyttelse fra PDL
      | Gradering |
      | UGRADERT  |

    Når adressebeskyttelse mappes

    Så skal mappede adressebeskyttelse være
      | Gradering |
      | UGRADERT  |
