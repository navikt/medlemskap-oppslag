# language: no
# encoding: UTF-8

Egenskap: Mapping av arbeidsavtaler i arbeidsforhold

  Scenario: Bruker følgende arbeidsavtaler i arbeidsforholdet
    Gitt følgende om AaRegPeriode i fra AaRegArbeidsavtale AaRegBruksperiode
      | Gyldig fra og med dato      | Gyldig til og med dato |
      | 2018-11-15T21:37:40.835     | 2020-11-15T21:37:40.835|

    Og følgende om gyldighetsperiode fra AaRegArbeidsavtale AaRegGyldighetsperiode
      | Gyldig fra og med dato      | Gyldig til og med dato |
      | 2015-03-25                  | 2016-02-03             |

    Og følgende om yrke fra AaRegArbeidsavtale
      | Yrke   |
      | MATROS |

    Og følgende om skipsregister fra AaRegArbeidsavtale
      | Skipsregister |
      | NOR           |

    Og følgende om stillingsprosent fra AaRegArbeidsavtale
      | Stillingsprosent|
      | 100.0           |

    Og følgende om beregnetAntallTimerPrUke
      | BeregnetAntallTimerPrUke |
      | 37.5                     |

    Når arbeidsforholdene mappes

    Så skal mappet periode i arbeidsavtale være
      | Fra og med dato    | Til og med dato |
      | 2018-11-15         | 2020-11-15      |

    Og mappet gyldighetsperiode i arbeidsavtale være
      | Fra og med dato    | Til og med dato |
      | 2018-11-15         | 2020-11-15      |

    Og mappet skipsregister være
      | Skipsregister |
      | NOR           |

    Og mappet stillingsprosent være
      | Stillingsprosent|
      | 100.0           |

    Og mappet beregnetAntallTimerPrUke være
      | Beregnet antall timer pr uke |
      | 37.5                         |

    Og mappet yrkeskode være
      | Yrkeskode |
      | MATROS    |

















