# language: no
# encoding: UTF-8

Egenskap: Norsk statsborger som er bosatt i Norge, og som har jobbet i Norge hele perioden uten utenlandsopphold.

  Scenario:

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
        "adresselinje": "Oslo",
        "landkode": "NOR",
        "fom": "2020-01-01",
        "tom": null
      }
    ],
    "postadresser": [],
    "midlertidigAdresser": []
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
        "landkode": "NOR",
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
  "dokument": []
}
"""

    Når lovvalg og medlemskap beregnes fra datagrunnlag json

    Så skal medlemskap i Folketrygden være "Ja"
    Så skal delresultat "LOV-1" være "Nei"
    Og skal delresultat "LOV-4" være "Ja"