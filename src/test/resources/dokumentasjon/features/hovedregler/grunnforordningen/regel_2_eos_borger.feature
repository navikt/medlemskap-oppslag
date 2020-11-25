# language: no
# encoding: UTF-8

Egenskap: Eøs-land

  Scenariomal: Regel 2: EØS-borgere får "Ja"

    Gitt følgende statsborgerskap i personhistorikken
      | Landkode | Fra og med dato |
      | <Land>   | 30.01.2000      |

    Når regel "2" kjøres med følgende parametre
      | Fra og med dato | Til og med dato | Har hatt arbeid utenfor Norge |
      | 30.01.2020      | 30.01.2021      | Nei                           |

    Så skal svaret være "<Svar>"
    Og skal avklaringen være som definert i RegelId
    Og skal begrunnelse utfylt være "<Begrunnelse utfylt>"

    Eksempler:
      | Land | Beskrivelse   | Svar | Begrunnelse utfylt |
      | BEL  | BELGIA        | Ja   | Nei                |
      | BGR  | BULGARIA      | Ja   | Nei                |
      | DNK  | DANMARK       | Ja   | Nei                |
      | EST  | ESTLAND       | Ja   | Nei                |
      | FIN  | FINLAND       | Ja   | Nei                |
      | FRA  | FRANKRIKE     | Ja   | Nei                |
      | GRC  | HELLAS        | Ja   | Nei                |
      | IRL  | IRLAND        | Ja   | Nei                |
      | ISL  | ISLAND        | Ja   | Nei                |
      | ITA  | ITALIA        | Ja   | Nei                |
      | HRV  | KROATIA       | Ja   | Nei                |
      | CYP  | KYPROS        | Ja   | Nei                |
      | LVA  | LATVIA        | Ja   | Nei                |
      | LIE  | LIECHTENSTEIN | Ja   | Nei                |
      | LTU  | LITAUEN       | Ja   | Nei                |
      | LUX  | LUXEMBOURG    | Ja   | Nei                |
      | MLT  | MALTA         | Ja   | Nei                |
      | NLD  | NEDERLAND     | Ja   | Nei                |
      | NOR  | NORGE         | Ja   | Nei                |
      | POL  | POLEN         | Ja   | Nei                |
      | PRT  | PORTUGAL      | Ja   | Nei                |
      | ROU  | ROMANIA       | Ja   | Nei                |
      | SVK  | SLOVAKIA      | Ja   | Nei                |
      | SVN  | SLOVENIA      | Ja   | Nei                |
      | ESP  | SPANIA        | Ja   | Nei                |
      | SWE  | SVERIGE       | Ja   | Nei                |
      | CZE  | TSJEKKIA      | Ja   | Nei                |
      | DEU  | TYSKLAND      | Ja   | Nei                |
      | HUN  | UNGARN        | Ja   | Nei                |
      | AUT  | ØSTERRIKE     | Ja   | Nei                |
      | CHE  | SVEITS        | Ja   | Nei                |
      | GBR  | STORBRITANNIA | Ja   | Nei                |
      | USA  | USA           | Nei  | Ja                 |

