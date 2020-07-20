# language: no
# encoding: UTF-8

Egenskap: Hovedregel "Finnes det registrerte opplysninger på bruker?"

  Scenario: Det finnes opplysninger i MEDL
    Gitt følgende medlemsunntak fra MEDL
      | Dekning | Fra og med dato | Til og med dato | Er medlem | Lovvalg | Lovvalgsland |
      |         | 01.01.2019      | 28.02.2020      | Nei       | ENDL    | USA          |

    Når hovedregel med avklaring "Finnes det registrerte opplysninger på bruker?" kjøres med følgende parametre
      | Fra og med dato | Til og med dato | Har hatt arbeid utenfor Norge |
      | 15.01.2020      | 30.01.2020      | Nei                           |

    Så skal svaret være "Ja"

  Scenariomal: Det finnes opplysninger i Joark
    Gitt følgende journalposter fra Joark
      | JournalpostId | Tittel | Journalposttype | Journalstatus | Tema   |
      | 123           | Test   | T               | AAPEN         | <Tema> |

    Når hovedregel med avklaring "Finnes det registrerte opplysninger på bruker?" kjøres med følgende parametre
      | Fra og med dato | Til og med dato | Har hatt arbeid utenfor Norge |
      | 15.01.2020      | 30.01.2020      | Nei                           |

    Så skal svaret være "<Svar>"

    Eksempler:
      | Tema              | Svar |
      | MED               | Ja   |
      | Ikke tillatt tema | Nei  |

  Scenariomal: Det finnes opplysninger i Gosys
    Gitt følgende oppgaver fra Gosys
      | Aktiv dato | Prioritet | Status   | Tema   |
      | 15.01.2020 | NORM      | <Status> | <Tema> |

    Når hovedregel med avklaring "Finnes det registrerte opplysninger på bruker?" kjøres med følgende parametre
      | Fra og med dato | Til og med dato | Har hatt arbeid utenfor Norge |
      | 01.01.2020      | 30.01.2020      | Nei                           |

    Så skal svaret være "<Svar>"

    Eksempler:
      | Status      | Tema          | Svar |
      | AAPNET      | MED           | Ja   |
      | FERDIGSTILT | MED           | Nei  |
      | AAPNET      | Ikke godkjent | Nei  |

  Scenario: Det finnes ikke opplysninger på bruker i MEDL, Joark eller Gosys
    Når hovedregel med avklaring "Finnes det registrerte opplysninger på bruker?" kjøres med følgende parametre
      | Fra og med dato | Til og med dato | Har hatt arbeid utenfor Norge |
      | 15.01.2020      | 30.01.2020      | Nei                           |

    Så skal svaret være "Nei"