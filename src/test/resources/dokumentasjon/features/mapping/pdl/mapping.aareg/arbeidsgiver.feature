# language: no
# encoding: UTF-8

Egenskap: Mapping av arbeidsgiver i arbeidsforhold

  #Endre skrift
  Scenario: Bruker har arbeidsgiver i arbeidsforholdet
    Gitt følgende om enhetstype fra enhetstyper fra organisasjonsdetaljer fra ereg
      | Enhetstype|
      | BEDR      |

    Og følgende om organisasjonsnummer fra Organiasjon fra ereg
      | Organisasjonsnummer |
      | 985672744           |

    Og følgende om Ansatte fra  Organisasjonsdetaljer fra Organiasjon fra ereg
      | Antall    | Bruksperiode gyldig fra   | Bruksperiode gyldig til | Gyldighetsperiode gyldig fra | Gyldighetsperiode gyldig til  |
      | 8         | 2015-03-25                | 2015-03-25              | 2015-03-25                   | 2015-03-25                    |

    Og følgende om konkursstatus fra statuser fra organisasjonDetaljer fra organisasjon
      | Konkurstatus |
      | USL          |


    Når arbeidsforholdene mappes

    Så skal mappet type til arbeidsgiver i arbeidsforholdet være
      | Arbeidsgivertype |
      | BEDR             |

    Og mappet organisasjonsnummer til arbeidsgiver i arbeidsforholdet være
      | Organisasjonsnummer |
      | 985672744           |

    Og mappet ansatte til arbeidsgiver i arbeidsforholdet være
      | Antall ansatte   | Bruksperiode gyldig fra   | Bruksperiode gyldig til | Gyldighetsperiode gyldig fra | Gyldighetsperiode gyldig til  |
      | 8                | 2015-03-25                | 2015-03-25              | 2015-03-25                   | 2015-03-25                    |

    Og mappet konkursstatus til arbeidsgiver i arbeidsforholdet være
      | Status |
      | USL    |



















