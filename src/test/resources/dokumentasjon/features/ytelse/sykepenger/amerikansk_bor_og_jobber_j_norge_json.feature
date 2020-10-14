# language: no
# encoding: UTF-8

Egenskap: Borgere som ikke er EØS-borgere skal ha uavklart medlemskap i MVP

  Scenario: Amerikansk statsborger som bor og jobber i Norge

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
  "pdlpersonhistorikk": {
    "statsborgerskap": [
       {
        "landkode": "USA",
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
      "arbeidsforholdstype": "NORMALT",
      "arbeidsavtaler": [
        {
          "periode": {
            "fom": "2020-01-30",
            "tom": "2020-06-30"
          },
          "gyldighetsperiode": {
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

    Så skal medlemskap i Folketrygden være "UAVKLART"
    Og omfattet av grunnforordningen være "Nei"
