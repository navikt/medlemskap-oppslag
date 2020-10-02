# language: no
# encoding: UTF-8

Egenskap: Mapping av sivilstand fra PDL HentPerson.sivilstand

  TODO:
  * Er dette realistiske eksempler?
  * Hva hvis folkeregistermetadata er null?

  Scenario: En person som har byttet sivilstand flere ganger
    Gitt følgende sivilstander fra PDL:
      | Type     | Relatert ved sivilstand | Gyldig fra og med | Folkeregister metadata ajourholdstidspunkt | Folkeregistermetadata gyldighetstidspunkt | Folkeregistermetadata opphoerstidspunkt |
      | UOPPGITT |                         |                   |                                            |                                           |                                         |
      | UGIFT    |                         |                   |                                            |                                           |                                         |
      | GIFT     | 10108000398             | 1995-02-04        | 1995-02-05 10:02:02                        | 1995-02-05 15:02:02                       | 1998-05-06 10:03:03                     |
      | SEPARERT | 10108000398             | 1998-05-05        |                                            |                                           |                                         |
      | SKILT    | 10108000398             | 1999-05-05        |                                            |                                           |                                         |

    Når sivilstander mappes

    Så skal mappede sivilstander være
      | Sivilstandstype | Relatert ved sivilstand | Gyldig fra og med dato | Gyldig til og med dato |
      | GIFT            | 10108000398             | 1995-02-04             | 1998-05-04             |
      | SEPARERT        | 10108000398             | 1998-05-05             | 1999-05-04             |
      | SKILT           | 10108000398             | 1999-05-05             |                        |


  Scenario: Sivilstander skal sorteres på "Gyldig fra og med", stigende
    Gitt følgende sivilstander fra PDL:
      | Type     | Relatert ved sivilstand | Gyldig fra og med |
      | SKILT    | 10108000398             | 1999-05-05        |
      | SEPARERT | 10108000398             | 1998-05-05        |

    Når sivilstander mappes

    Så skal mappede sivilstander være
      | Sivilstandstype | Relatert ved sivilstand | Gyldig fra og med dato | Gyldig til og med dato |
      | SEPARERT        | 10108000398             | 1998-05-05             | 1999-05-04             |
      | SKILT           | 10108000398             | 1999-05-05             |                        |