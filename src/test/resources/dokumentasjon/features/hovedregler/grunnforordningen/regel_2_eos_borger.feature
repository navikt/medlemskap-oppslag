# language: no
# encoding: UTF-8

Egenskap: Eøs-land

  Scenariomal: Regel 2: EØS-borgere får "Ja"

    Gitt følgende statsborgerskap i personhistorikken
      | Landkode | Fra og med dato |
      | <Land>   | 30.06.2020      |

    Når regel "2" kjøres med følgende parametre
      | Fra og med dato | Til og med dato | Har hatt arbeid utenfor Norge |
      | 30.01.2020      | 30.01.2021      | Nei                           |

    Så skal svaret være "<Svar>"

    Eksempler:
      | Land | Beskrivelse   | Svar |
      | BEL  | BELGIA        | Ja   |
      | BGR  | BULGARIA      | Ja   |
      | DNK  | DANMARK       | Ja   |
      | EST  | ESTLAND       | Ja   |
      | FIN  | FINLAND       | Ja   |
      | FRA  | FRANKRIKE     | Ja   |
      | GRC  | HELLAS        | Ja   |
      | IRL  | IRLAND        | Ja   |
      | ISL  | ISLAND        | Ja   |
      | ITA  | ITALIA        | Ja   |
      | HRV  | KROATIA       | Ja   |
      | CYP  | KYPROS        | Ja   |
      | LVA  | LATVIA        | Ja   |
      | LIE  | LIECHTENSTEIN | Ja   |
      | LTU  | LITAUEN       | Ja   |
      | LUX  | LUXEMBOURG    | Ja   |
      | MLT  | MALTA         | Ja   |
      | NLD  | NEDERLAND     | Ja   |
      | NOR  | NORGE         | Ja   |
      | POL  | POLEN         | Ja   |
      | PRT  | PORTUGAL      | Ja   |
      | ROU  | ROMANIA       | Ja   |
      | SVK  | SLOVAKIA      | Ja   |
      | SVN  | SLOVENIA      | Ja   |
      | ESP  | SPANIA        | Ja   |
      | SWE  | SVERIGE       | Ja   |
      | CZE  | TSJEKKIA      | Ja   |
      | DEU  | TYSKLAND      | Ja   |
      | HUN  | UNGARN        | Ja   |
      | AUT  | ØSTERRIKE     | Ja   |
      | CHE  | SVEITS        | Ja   |
      | GBR  | STORBRITANNIA | Ja   |
      | USA  | USA           | Nei  |

