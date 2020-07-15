# language: no
# encoding: UTF-8

Egenskap: Regel 1 konklusjon for brukere uten medlemskap

  Scenariomal: Regel 1 konklusjon for brukere uten medlemskap

    Gitt følgende medlemsunntak fra MEDL
      | Fra og med dato | Til og med dato | Er medlem | Lovvalg | Lovvalgsland | Periodestatus |
      | 01.01.2019      | 01.06.2020      | Nei       | ENDL    | BEL          | GYLD          |

    Og følgende arbeidsforhold fra AAReg
      | Fra og med dato | Til og med dato   | Arbeidsgivertype | Arbeidsforholdstype | Arbeidsgiver Id |
      | 01.01.2019      | <Til og med dato> | Organisasjon     | NORMALT             | 1               |

    Når medlemskap beregnes med følgende parametre
      | Fra og med dato | Til og med dato | Har hatt arbeid utenfor Norge | Ytelse   |
      | 30.01.2020      | 10.02.2020      | Nei                           | <Ytelse> |

    Så skal svaret være "<medlemskap>"

    Eksempler:
      | Til og med dato | Ytelse           | medlemskap |
      |                 | SYKEPENGER       | Uavklart   |
      | 01.05.2019      | ENSLIG_FORSORGER | Uavklart   |
      |                 | DAGPENGER        | Uavklart   |
      |                 | ENSLIG_FORSORGER | Uavklart   |
      | 01.05.2019      | DAGPENGER        | Uavklart   |
      |                 | SYKEPENGER       | Uavklart   |
      |                 | DAGPENGER        | Uavklart   |
      | 01.05.2019      | ENSLIG_FORSORGER | Uavklart   |

  Scenariomal: Regel 1 konklusjon for brukere med medlemskap: Er det dekning for gjeldende ytelse med flere perioder?

    Gitt følgende medlemsunntak fra MEDL
      | Fra og med dato | Til og med dato | Er medlem | Lovvalg | Lovvalgsland | Periodestatus |
      | 01.01.2019      | 01.06.2019      | Nei       | ENDL    | BEL          | GYLD          |
      | 02.06.2019      | 31.12.2022      | Nei       | ENDL    | BEL          | GYLD          |

    Og følgende arbeidsforhold fra AAReg
      | Fra og med dato | Til og med dato   | Arbeidsgivertype | Arbeidsforholdstype | Arbeidsgiver Id |
      | 01.01.2019      | <Til og med dato> | Organisasjon     | NORMALT             | 1               |

    Når medlemskap beregnes med følgende parametre
      | Fra og med dato | Til og med dato | Har hatt arbeid utenfor Norge | Ytelse   |
      | 30.01.2020      | 10.02.2020      | Nei                           | <Ytelse> |

    Så skal svaret være "<medlemskap>"

    Eksempler:
      | Til og med dato | Ytelse     | medlemskap |
      |                 | SYKEPENGER | Uavklart   |
      | 01.05.2019      | SYKEPENGER | Uavklart   |
