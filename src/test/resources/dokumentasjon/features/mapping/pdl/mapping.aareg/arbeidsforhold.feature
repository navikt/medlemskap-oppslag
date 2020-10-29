# language: no
# encoding: UTF-8

Egenskap: Mapping av arbeidsforhold

  Scenario: Bruker har flere arbeidsfohrold
    Gitt følgende om AaRegPeriode i fra AaRegArbeidsforhold.AaRegAnsettelsesperiode
      | Gyldig fra og med dato   | Gyldig til og med dato|
      | 2015-03-25               | 2016-02-03            |

    Og følgende om arbeidgivertype fra AaRegArbeidsforhold.AaRegOpplysningspliktigArbeidsgiver
      | Arbeidsgivertype |
      | Organisasjon     |

    Og følgende om type fra AaRegArbeidsforhold
      | Type                      |
      | ordinaertArbeidsforhold   |

    Når arbeidsforholdene mappes

    Så skal mappede periode i arbeidsforhold være
      | Fra og med dato | Til og med dato | Arbeidsgivertype | Arbeidsforholdstype|
      | 2015-03-25      |  2016-02-03     | Organisasjon     | NORMALT            |





