# language: no
# encoding: UTF-8

Egenskap: Mapping av arbeidsforholdstype i arbeidsforhold

  Scenario: Bruker har flere arbeidsfohrold
    Gitt følgende om type fra AaRegArbeidsforhold
      | Type                    |
      | ordinaertArbeidsforhold |

    Når arbeidsforholdene mappes

    Så skal mappet arbeidsforholdstype i arbeidsforholdet være
      | Arbeidsforholdstype |
      | NORMALT             |








