# language: no
# encoding: UTF-8

Egenskap: Mapping av folkeregistrertpersonstatus fra PDL HentPerson.folkeregisterpersonstatus


  Scenario: En person som er registrert som bosatt
    Gitt følgende personstatuser fra PDL:
      | Personstatus         | Folkeregistermetadata gyldighetstidspunkt   | Folkeregistermetadata opphoerstidspunkt |
      | foedselsregistrert   | 1952-05-05 10:03:03                         |                                         |
      | bosatt               | 1952-02-04 10:03:03                         | 1960-02-04 10:03:03                            |
      | utflyttet            | 1960-02-04 10:03:03                         |                                         |
      | ikkeBosatt           | 1960-02-05 10:03:03                         |                                         |
      | midlertidig          | 1999-05-05 10:03:03                         |                                         |
      | doed                 | 2020-03-03 10:03:03                         |                                         |
      | inaktiv              | 2020-05-05 10:03:03                         |                                         |
      | opphoert             | 2020-05-05 10:03:03                         |                                         |

    Når personstatuser mappes

    Så skal mappede personstatuser være
      | Personstatus         | Fra og med dato     | Til og med dato        |
      | FOEDSELSREGISTRERT   | 1952-05-05          |                        |
      | BOSATT               | 1952-02-04          | 1960-02-04             |
      | UTFLYTTET            | 1960-02-04          |                        |
      | IKKEBOSATT           | 1960-02-05          |                        |
      | MIDLERTIDIG          | 1999-05-05          |                        |
      | DOED                 | 2020-03-03          |                        |
      | INAKTIV              | 2020-05-05          |                        |
      | OPPHOERT             | 2020-05-05          |                        |




