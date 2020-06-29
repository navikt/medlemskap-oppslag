# language: no
# encoding: UTF-8

Egenskap: Man er medlem i Folketrygden hvis man bor i Norge eller jobber i Norge.

  Scenario: Norsk statsborger som er bosatt i Norge, men som ikke har hatt jobb siste 12 måneder

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
    "sivilstand": [],
    "familierelasjoner": []
  },
  "pdlpersonhistorikk": {
    "statsborgerskap": [],
    "personstatuser": [],
    "bostedsadresser": [],
    "postadresser": [],
    "midlertidigAdresser": [],
    "sivilstand": [],
    "familierelasjoner": []
  },
  "medlemskap": [],
  "arbeidsforhold": [],
  "inntekt": [],
  "oppgaver": [],
  "dokument": [],
  "ytelse": "SYKEPENGER"
}
"""

    Når lovvalg og medlemskap beregnes fra datagrunnlag json

    Så skal medlemskap i Folketrygden være "UAVKLART"
