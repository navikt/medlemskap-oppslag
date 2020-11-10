# language: no
# encoding: UTF-8

Egenskap: Mapping av arbeidsgivertype i arbeidsforhold

  #lag alle scenario
  Scenario: Bruker har arbeidsforhold med arbeidsgivertype

    Gitt følgende om arbeidgivertype fra AaRegOpplysningspliktigArbeidsgiver fra AaRegArbeidsforhold
      | Arbeidsgivertype |
      | Organisasjon     |

    Når arbeidsforholdene mappes

    Så skal mappet arbeidsgivertype i arbeidsforholdet være
      | Arbeidsgivertype |
      | Organisasjon     |










