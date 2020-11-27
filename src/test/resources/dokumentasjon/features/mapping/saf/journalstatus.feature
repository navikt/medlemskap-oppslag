# language: no
# encoding: UTF-8

Egenskap: Mapping av journalpoststatus

  Scenariomal: Bruker har journalstatus i journalpost fra journalposter fra dokumentoversiktBruker
    Gitt følgende journalstatus fra journalpost
      | Journalposttype   |
      | <Journalposttype> |

    Når journalposter mappes

    Så skal mappede journalstatus være
      | Journalposttype             |
      | <MedlemskapJournalposttype> |

    Eksempler:
      | <Journalstatus>   | <MedlemskapJournalstatus>  |
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












