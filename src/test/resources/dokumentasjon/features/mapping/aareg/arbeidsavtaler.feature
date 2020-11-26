# language: no
# encoding: UTF-8

Egenskap: Mapping av arbeidsavtaler i arbeidsforhold

  Scenariomal: Bruker følgende arbeidsavtaler i arbeidsforholdet
    Gitt følgende om AaRegPeriode fra AaRegArbeidsavtale fra AaRegBruksperiode
      | Gyldig fra og med dato      | Gyldig til og med dato |
      | 2018-11-15T21:37:40.835     | 2020-11-15T21:37:40.835|

    Og følgende om gyldighetsperiode fra AaRegArbeidsavtale fra AaRegGyldighetsperiode
      | Gyldig fra og med dato      | Gyldig til og med dato |
      | 2015-03-25                  | 2016-02-03             |

    Og følgende om yrke fra AaRegArbeidsavtale
      | Yrke   |
      | MATROS |

    Og følgende om skipsregister fra AaRegArbeidsavtale
      | Skipsregister         |
      | <SkipsregisterAareg>  |

    Og følgende om fartsområde fra AaRegArbeidsavtale
      | Fartsområde          |
      | <FartsområdeAareg>   |

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
      | Skipsregister   |
      | <Skipsregister> |

    Og mappet fartsområde skal være
      | Fartsområde     |
      | <Fartsområde>   |

    Og mappet stillingsprosent være
      | Stillingsprosent|
      | 100.0           |

    Og mappet beregnetAntallTimerPrUke være
      | Beregnet antall timer pr uke |
      | 37.5                         |

    Og mappet yrkeskode være
      | Yrkeskode |
      | MATROS    |


    Eksempler:
      | SkipsregisterAareg | Skipsregister | FartsområdeAareg | Fartsområde |
      | NOR                | NOR           | innenriks        | INNENRIKS   |
      | nor                | NOR           | utenriks         | UTENRIKS    |
      | NIS                | NIS           | innenriks        | INNENRIKS   |
      | nis                | NIS           | utenriks         | UTENRIKS    |
      | UTL                | UTL           | innenriks        | INNENRIKS   |
      | utl                | UTL           | utenriks         | UTENRIKS    |




















