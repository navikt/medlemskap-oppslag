# language: no
# encoding: UTF-8

Egenskap: Regel 1.6: Er det dekning for gjeldende ytelse?

  Scenariomal: Regel 1.6: Er det dekning for gjeldende ytelse? Sjekk en periode fra MEDL

    Gitt følgende medlemsunntak fra MEDL
      | Dekning   | Fra og med dato | Til og med dato | Er medlem | Lovvalg | Lovvalgsland | Periodestatus |
      | <Dekning> | 01.01.2019      | 01.06.2020      | Ja        | ENDL    | NOR          | GYLD          |

    Når regel "1.6" kjøres med følgende parametre
      | Fra og med dato | Til og med dato | Har hatt arbeid utenfor Norge | Ytelse   |
      | 30.01.2020      | 10.02.2020      | Nei                           | <Ytelse> |

    Så skal svaret være "<Svar>"

    Eksempler:
      | Dekning           | Ytelse           | Svar |
      | FTL_2-6           | SYKEPENGER       | Ja   |
      | FTL_2-6           | ENSLIG_FORSORGER | Nei  |
      | FTL_2-6           | DAGPENGER        | Nei  |
      | FTL_2-7a_2_ledd_a | ENSLIG_FORSORGER | Ja   |
      | FTL_2-7a_2_ledd_a | DAGPENGER        | Ja   |
      | FTL_2-9_1_ledd_a  | SYKEPENGER       | Nei  |
      | FTL_2-9_1_ledd_a  | DAGPENGER        | Nei  |
      | FTL_2-9_1_ledd_a  | ENSLIG_FORSORGER | Nei  |

  Scenariomal: Regel 1.6: Er det dekning for gjeldende ytelse? Flere perioder fra MEDL

    Gitt følgende medlemsunntak fra MEDL
      | Dekning          | Fra og med dato | Til og med dato | Er medlem | Lovvalg | Lovvalgsland | Periodestatus |
      | FTL_2-9_1_ledd_a | 01.01.2019      | 01.06.2019      | Ja        | ENDL    | NOR          | GYLD          |
      | <Dekning>        | 02.06.2019      | 31.12.2022      | Ja        | ENDL    | NOR          | GYLD          |

    Når regel "1.6" kjøres med følgende parametre
      | Fra og med dato | Til og med dato | Har hatt arbeid utenfor Norge | Ytelse   |
      | 30.01.2020      | 10.02.2020      | Nei                           | <Ytelse> |

    Så skal svaret være "<Svar>"

    Eksempler:
      | Dekning             | Ytelse     | Svar |
      | FTL_2-9_2_ld_jfr_1c | SYKEPENGER | Ja   |
      | FTL_2-9_1_ledd_a    | SYKEPENGER | Nei  |
