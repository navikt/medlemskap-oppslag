# language: no
# encoding: UTF-8

Egenskap: Mapping av familierelasjoner fra PDL HentPerson.Familierelasjon

  TODO:
  * Er dette realistiske eksempler?
  * Bør vi ha flere scenarier?

  Scenario: En person som har flere familierelasjoner
    Gitt følgende familierelasjoner fra PDL:
      | Relatert persons ident | Relatert persons rolle | Min rolle for person | Folkeregister metadata ajourholdstidspunkt | Folkeregistermetadata gyldighetstidspunkt | Folkeregistermetadata opphoerstidspunkt |
      | 20041276216            | BARN                   | FAR                  | 2012-04-22 11:03:02                        | 2012-04-20 12:00:00                       |                                         |

    Når familierelasjoner mappes

    Så skal mappede familierelasjoner være
      | Relatert persons ident | Relatert persons rolle | Min rolle for person |
      | 20041276216            | BARN                   | FAR                  |
