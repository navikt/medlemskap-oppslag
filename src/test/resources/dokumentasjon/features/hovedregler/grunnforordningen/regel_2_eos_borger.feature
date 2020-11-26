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
    Og skal begrunnelse utfylt være "<Svar begrunnelse>"

    Eksempler:
      | Land | Beskrivelse   | Svar | Svar begrunnelse |
      | BEL  | BELGIA        | Ja   | Ja               |
      | BGR  | BULGARIA      | Ja   | Ja               |
      | DNK  | DANMARK       | Ja   | Ja               |
      | EST  | ESTLAND       | Ja   | Ja               |
      | FIN  | FINLAND       | Ja   | Ja               |
      | FRA  | FRANKRIKE     | Ja   | Ja               |
      | GRC  | HELLAS        | Ja   | Ja               |
      | IRL  | IRLAND        | Ja   | Ja               |
      | ISL  | ISLAND        | Ja   | Ja               |
      | ITA  | ITALIA        | Ja   | Ja               |
      | HRV  | KROATIA       | Ja   | Ja               |
      | CYP  | KYPROS        | Ja   | Ja               |
      | LVA  | LATVIA        | Ja   | Ja               |
      | LIE  | LIECHTENSTEIN | Ja   | Ja               |
      | LTU  | LITAUEN       | Ja   | Ja               |
      | LUX  | LUXEMBOURG    | Ja   | Ja               |
      | MLT  | MALTA         | Ja   | Ja               |
      | NLD  | NEDERLAND     | Ja   | Ja               |
      | NOR  | NORGE         | Ja   | Ja               |
      | POL  | POLEN         | Ja   | Ja               |
      | PRT  | PORTUGAL      | Ja   | Ja               |
      | ROU  | ROMANIA       | Ja   | Ja               |
      | SVK  | SLOVAKIA      | Ja   | Ja               |
      | SVN  | SLOVENIA      | Ja   | Ja               |
      | ESP  | SPANIA        | Ja   | Ja               |
      | SWE  | SVERIGE       | Ja   | Ja               |
      | CZE  | TSJEKKIA      | Ja   | Ja               |
      | DEU  | TYSKLAND      | Ja   | Ja               |
      | HUN  | UNGARN        | Ja   | Ja               |
      | AUT  | ØSTERRIKE     | Ja   | Ja               |
      | CHE  | SVEITS        | Ja   | Ja               |
      | GBR  | STORBRITANNIA | Ja   | Ja               |
      | USA  | USA           | Nei  | Nei              |

