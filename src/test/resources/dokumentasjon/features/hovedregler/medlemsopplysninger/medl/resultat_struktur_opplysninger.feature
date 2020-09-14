# language: no
# encoding: UTF-8

Egenskap: Resultat-struktur for regel MEDL

  Scenario: Resultat-struktur for regel MEDL

    Gitt følgende medlemsunntak fra MEDL
      | Dekning | Fra og med dato | Til og med dato | Er medlem | Lovvalg | Lovvalgsland | Periodestatus |
      | FTL_2-6 | 01.01.2019      | 01.06.2020      | Ja        | ENDL    | NOR          | GYLD          |

    Og følgende arbeidsforhold fra AAReg
      | Fra og med dato | Til og med dato | Arbeidsgivertype | Arbeidsforholdstype | Arbeidsgiver Id |
      | 01.01.2019      | 01.06.2020      | Organisasjon     | NORMALT             | 1               |

    Når medlemskap beregnes med følgende parametre
      | Fra og med dato | Til og med dato | Har hatt arbeid utenfor Norge | Ytelse     |
      | 30.01.2020      | 10.02.2020      | Nei                           | SYKEPENGER |

    Så skal regel "MEDL" gi svaret "Ja"

    Og skal resultat gi følgende delresultater:
      | Regel          |
      | MEDL           |
      | ARBEIDSFORHOLD |
      | ANDRE BORGERE  |
      | 2              |
      | 9              |
      | 11             |

    Og skal regel "MEDL" inneholde følgende delresultater:
      | Regel        |
      | OPPLYSNINGER |
      | 1.1          |
      | 1.2          |
      | 1.3          |
      | 1.4          |
      | 1.5          |
      | 1.6          |
      | 1.7          |