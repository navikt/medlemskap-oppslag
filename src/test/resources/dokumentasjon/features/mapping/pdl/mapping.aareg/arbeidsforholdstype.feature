# language: no
# encoding: UTF-8

Egenskap: Mapping av arbeidsforholdstype i arbeidsforhold

  // Todo lag alle scenario
  Scenario: Bruker har arbeidsforholdstype i arbeidsforholdet
    Gitt følgende om type fra AaRegArbeidsforhold
      | Type                    |
      | ordinaertArbeidsforhold |

    Når arbeidsforholdene mappes

    Så skal mappet arbeidsforholdstype i arbeidsforholdet være
      | Arbeidsforholdstype |
      | NORMALT             |








