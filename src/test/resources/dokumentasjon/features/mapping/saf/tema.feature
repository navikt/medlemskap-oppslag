# language: no
# encoding: UTF-8

Egenskap: Mapping av journalpoststatus

  Scenariomal: Bruker har journalstatus i journalpost fra journalposter fra dokumentoversiktBruker
    Gitt følgende journalstatus fra journalpost
      | Tema   |
      | <Journalposttype> |

    Når journalposter mappes

    Så skal mappede journalstatus være
      | Journalposttype             |
      | <MedlemskapTema> |

    Eksempler:
      | <Tema>   | <MedlemskapTema>  |
      | MOTTATT           | MOTTATT                    |
      | JOURNALFOERT      | JOURNALFOERT               |
      | FERDIGSTILT       | FERDIGSTILT                |
      | EKSPEDERT         | EKSPEDERT                  |
      | UNDER_ARBEID      | UNDER_ARBEID               |
      | FEILREGISTRERT    | FEILREGISTRERT             |
      | UTGAAR            | UTGAAR                     |
      | UKJENT_BRUKER     | UKJENT_BRUKER              |
      | RESERVERT         | RESERVERT                  |
      | UKJENT            | UKJENT                     |

    # language: no
# encoding: UTF-8

Egenskap: Mapping av tema

  Scenariomal: Bruker har tema i journalpost fra journalposter fra dokumentoversiktBruker
    Gitt følgende tema fra journalpost
      | Tema   |
      | <Tema> |

    Når journalposter mappes

    Så skal mappede tema være
      | Tema            |
      | <MedlemskapTema> |

    Eksempler:
      | <Journaltema>     | <MedlemskapTema>  |
      | AAP               | AAP               |
      | AAR               | AAR               |


























