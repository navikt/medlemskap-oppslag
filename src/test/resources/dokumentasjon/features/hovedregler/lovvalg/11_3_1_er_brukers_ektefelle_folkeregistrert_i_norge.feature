# language: no
# encoding: UTF-8

Egenskap: Regel 11.3.1: Er brukers ektefelle folkeregistrert som bosatt i Norge?

  Scenariomal: Regel 11.3.1 - Ja hvis folkeregistrert bosatt i Norge og eventuell postadresse og midlertidig adresse er norsk
    Gitt følgende sivilstand i personhistorikk fra PDL
      | Sivilstandstype | Gyldig fra og med dato | Relatert ved sivilstand |
      | GIFT            | 29.06.2015             | 10108000398             |

    Og følgende personhistorikk for ektefelle fra PDL
      | Ident       | Bosted | Kontaktadresse   | Oppholdsadresse   | Fra og med dato | Til og med dato |
      | 10108000398 | NOR    | <Kontaktadresse> | <Oppholdsadresse> | 18.07.2010      | 15.05.2019      |


    Når regel "11.3.1" kjøres med følgende parametre
      | Fra og med dato | Til og med dato | Har hatt arbeid utenfor Norge |
      | 30.01.2020      | 30.01.2021      | Nei                           |

    Så skal svaret være "<Svar>"
    Og skal avklaringen være som definert i RegelId
    Og skal begrunnelse utfylt være "<Utfylt begrunnelse>"

    Eksempler:
      | Kontaktadresse | Oppholdsadresse | Svar | Utfylt begrunnelse |
      |                |                 | Ja   | Nei                |
      | NOR            | NOR             | Ja   | Nei                |
      | FRA            | NOR             | Nei  | Ja                 |
      | NOR            | FRA             | Nei  | Ja                 |
      | FRA            | FRA             | Nei  | Ja                 |
