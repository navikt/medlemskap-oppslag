# language: no
# encoding: UTF-8

Egenskap: Mapping av utenlandsopphold i arbeidsforhold

  Scenario: Bruker følgende utenlandsopphold i arbeidsforholdet
    Gitt følgende om landkode i AaRegArbeidsforhold.AaRegUtenlandsopphold
      | Landkode |
      | BEL      |

    Og følgende om AaRegPeriode i fra AaRegArbeidsforhold.AaRegUtenlandsopphold
      | Gyldig fra og med dato      | Gyldig til og med dato |
      | 2015-03-25                  | 2016-02-03             |

    Og følgende rapporteringsperiode i AaRegArbeidsforhold.AaRegUtenlandsopphold
      | Rapporteringsperiode    |
      | 2015-12                 |


    Når arbeidsforholdene mappes

    Så skal mappet landkode i utenlandsoppholdet være
      | Landkode    |
      | BEL         |

    Og mappet periode være i utenlandsoppholdet være
      | Fra og med dato    | Til og med dato |
      | 2015-03-25         | 2016-02-03     |

    Og mappet rapporteringsperiode i utenlandsoppholdet være
      | Rapporteringsperiode    |
      | 2015-12                 |















