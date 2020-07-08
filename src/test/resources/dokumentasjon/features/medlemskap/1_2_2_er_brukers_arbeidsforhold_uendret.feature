# language: no
# encoding: UTF-8

Egenskap: Regel 1.2.2: Er brukers arbeidsforhold uendret

  Scenariomal: Ett arbeidsforhold

    Gitt følgende medlemsunntak fra MEDL
      | Dekning | Fra og med dato | Til og med dato | Er medlem | Lovvalg | Lovvalgsland |
      |         | 01.01.2019      | 01.01.2021      | Nei       | ENDL    | NOR          |

    Og følgende arbeidsforhold fra AAReg
      | Fra og med dato | Til og med dato   | Arbeidsgivertype | Arbeidsforholdstype | Arbeidsgiver Id |
      | 01.01.2018      | <Til og med dato> | Organisasjon     | NORMALT             | 1               |

    Når regel "1.2.2" kjøres med følgende parametre
      | Fra og med dato | Til og med dato | Har hatt arbeid utenfor Norge |
      | 30.01.2020      | 10.02.2020      | Nei                           |

    Så skal svaret være "<Svar>"

    Eksempler:
      | Til og med dato | Svar |
      |                 | Ja   |
      | 08.02.2020      | Ja   |
      | 28.01.2020      | Nei  |

  Scenariomal: Flere arbeidsforhold

    Gitt følgende medlemsunntak fra MEDL
      | Dekning | Fra og med dato | Til og med dato | Er medlem | Lovvalg | Lovvalgsland |
      |         | 01.01.2019      | 01.01.2021      | Nei        | ENDL    | NOR          |

    Og følgende arbeidsforhold fra AAReg
      | Fra og med dato   | Til og med dato   | Arbeidsgivertype | Arbeidsforholdstype |
      | 01.01.2018        | <Til og med dato> | Organisasjon     | NORMALT             |
      | <Fra og med dato> |                   | Organisasjon     | NORMALT             |

    Og følgende arbeidsgiver i arbeidsforhold 1
      | Identifikator | Arbeidsgivertype | Landkode | Antall ansatte |
      | 1             | BEDR             | NOR      | 9              |

    Og følgende arbeidsgiver i arbeidsforhold 2
      | Identifikator   | Arbeidsgivertype | Landkode | Antall ansatte |
      | <Identifikator> | BEDR             | NOR      | 9              |


    Når regel "1.2.2" kjøres med følgende parametre
      | Fra og med dato | Til og med dato | Har hatt arbeid utenfor Norge |
      | 30.01.2020      | 10.02.2020      | Nei                           |

    Så skal svaret være "<Svar>"

    Eksempler:
      | Til og med dato | Fra og med dato | Identifikator | Svar |
      | 01.02.2020      | 30.03.2020      | 2             | Ja   |
      |                 | 01.01.2018      | 2             | Ja   |
      | 01.01.2019      | 02.01.2019      | 2             | Nei  |
      | 31.05.2019      | 01.06.2019      | 2             | Nei  |
      | 28.01.2020      | 01.01.2018      | 2             | Nei  |
      |                 | 01.08.2019      | 2             | Nei  |
      | 31.05.2019      | 01.06.2019      | 1             | Ja   |