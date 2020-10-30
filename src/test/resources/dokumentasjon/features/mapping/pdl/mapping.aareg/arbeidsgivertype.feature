# language: no
# encoding: UTF-8

Egenskap: Mapping av arbeidsgivertype i arbeidsforhold

  Scenario: Bruker har flere arbeidsfohrold

    Gitt følgende om arbeidgivertype fra AaRegArbeidsforhold.AaRegOpplysningspliktigArbeidsgiver
      | Arbeidsgivertype |
      | Organisasjon     |

    Når arbeidsforholdene mappes

    Så skal mappet arbeidsgivertype i arbeidsforholdet være
      | Arbeidsgivertype |
      | Organisasjon     |










