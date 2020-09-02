# language: no
# encoding: UTF-8

Egenskap: Man er medlem i Folketrygden hvis man er EØS-borger og bor og jobber i Norge

  Scenario: Norsk statsborger som bor og jobber i Norge

    Gitt følgende datagrunnlag json
"""
{
  "periode": {
    "fom": "2020-01-30",
    "tom": "2021-01-30"
  },
  "brukerinput": {
    "arbeidUtenforNorge": false
  },
  "personhistorikk": {
    "statsborgerskap": [
      {
        "landkode": "NOR",
        "fom": "1991-06-30",
        "tom": null
      }
    ],
    "personstatuser": [],
    "bostedsadresser": [
      {
        "landkode": "NOR",
        "fom": "2020-01-01",
        "tom": null
      }
    ],
    "postadresser": [],
    "midlertidigAdresser": [],
    "kontaktadresser": [],
    "oppholdsadresser": [],
    "sivilstand": [],
    "familierelasjoner": []
  },
  "pdlpersonhistorikk": {
    "statsborgerskap": [],
    "personstatuser": [],
    "bostedsadresser": [],
    "postadresser": [],
    "midlertidigAdresser": [],
    "kontaktadresser": [],
    "oppholdsadresser": [],
    "sivilstand": [],
    "familierelasjoner": []
  },
  "medlemskap": [],
  "arbeidsforhold": [
    {
      "periode": {
        "fom": "2018-01-01",
        "tom": "2022-06-30"
      },
      "utenlandsopphold": [],
      "arbeidsgivertype": "Organisasjon",
      "arbeidsgiver": {
        "type": "BEDR",
        "identifikator": "1",
        "antallAnsatte": "6"
      },
      "arbeidsfolholdstype": "NORMALT",
      "arbeidsavtaler": [
        {
          "periode": {
            "fom": "2020-01-30",
            "tom": "2020-06-30"
          },
          "yrkeskode": "0001"
        }
      ]
    }
  ],
  "inntekt": [],
  "oppgaver": [],
  "dokument": [],
  "ytelse": "SYKEPENGER"
}
"""

    Når lovvalg og medlemskap beregnes fra datagrunnlag json

    Så skal medlemskap i Folketrygden være "Ja"
    Og omfattet av grunnforordningen være "Ja"
