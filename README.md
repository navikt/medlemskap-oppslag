# medlemskap-oppslag
Oppslagstjeneste for lovvalg og medlemskap i Folketrygden

## URL til tjeneste
* preprod: https://medlemskap-oppslag.nais.preprod.local
* prod: https://medlemskap-oppslag.nais.adeo.no

## Autentisering
Forventer et AzureAD-token utstedt til servicebruker.

## Eksempel på kall
Kallet er en POST på `/`
```
{
    "fnr": "123456789",
    "periode": {
        "fom": "2019-01-01", 
        "tom": "2019-12-31", 
    }, 
    "brukerinput": {
        "arbeidUtenforNorge": false
    }
}
```
### Inputperiode
Periode brukeren søker sykepenger for

### Brukerinput
Input fra bruker som må fylles ut i søknadsdialogen og er nødvendig for å avgjøre medlemskap

* `arbeidUtenforNorge` Om de har jobbet utenfor Norge siste 12 måneder

## Eksempel på svar
```
{
  "tidspunkt": "2020-02-17T13:40:42.103606",
  "versjonTjeneste": "TODO",
  "versjonRegler": "v1",
  "datagrunnlag": {
    "periode": {
      "fom": "2020-01-01",
      "tom": "2018-01-01"
    },
    "brukerinput": {
      "arbeidUtenforNorge": false
    },
    "personhistorikk": {
      "statsborgerskap": [
        {
          "landkode": "NOR",
          "fom": "1983-08-27",
          "tom": null
        }
      ],
      "personstatuser": [
        {
          "personstatus": "BOSA",
          "fom": "1983-08-27",
          "tom": null
        }
      ],
      "bostedsadresser": [
        {
          "adresselinje": "Ukjent adresse",
          "landkode": "NOR",
          "fom": "1983-08-27",
          "tom": null
        }
      ],
      "postadresser": [],
      "midlertidigAdresser": []
    },
    "medlemskapsunntak": [],
    "arbeidsforhold": [
      {
        "periode": {
          "fom": "2000-02-13",
          "tom": null
        },
        "utenlandsopphold": null,
        "arbeidsgiver": {
          "type": "ordinaertArbeidsforhold",
          "identifikator": "928497704",
          "landkode": "NOR"
        },
        "arbeidsfolholdstype": "NORMALT",
        "arbeidsavtaler": [
          {
            "periode": {
              "fom": "2020-02-13",
              "tom": null
            },
            "yrkeskode": "2130109",
            "skipsregister": null,
            "stillingsprosent": 100.0
          }
        ]
      }
    ],
    "inntekt": [],
    "oppgaver": [],
    "dokument": []
  },
  "resultat": {
    "identifikator": "LOVME",
    "avklaring": "Er personen medlem av folketrygden?",
    "resultat": "JA",
    "beskrivelse": "Personen er omfattet av norsk lovvalg, og dermed medlem",
    "delresultat": [
      {
        "identifikator": "VED",
        "avklaring": "Har personen manuelle vadtak fra NAV?",
        "resultat": "NEI",
        "beskrivelse": "Personen har ingen manuelle vedtak",
        "delresultat": [
          {
            "identifikator": "VED",
            "avklaring": "Sjekk om det finnes avklarte vedtak i MEDL ELLER Finnes det åpne oppgaver i GOSYS ELLER Finnes det åpne dokumenter i JOARK",
            "resultat": "NEI",
            "beskrivelse": "Personen har ingen vedtak i MEDL OG Personen har ingen åpne oppgaver i GOSYS. OG Personen har ingen dokumenter knyttet til medlemskapsaker.",
            "delresultat": [
              {
                "identifikator": "VED-1",
                "avklaring": "Sjekk om det finnes avklarte vedtak i MEDL",
                "resultat": "NEI",
                "beskrivelse": "Personen har ingen vedtak i MEDL",
                "delresultat": []
              },
              {
                "identifikator": "VED-3",
                "avklaring": "Finnes det åpne oppgaver i GOSYS",
                "resultat": "NEI",
                "beskrivelse": "Personen har ingen åpne oppgaver i GOSYS.",
                "delresultat": []
              },
              {
                "identifikator": "VED-2",
                "avklaring": "Finnes det åpne dokumenter i JOARK",
                "resultat": "NEI",
                "beskrivelse": "Personen har ingen dokumenter knyttet til medlemskapsaker.",
                "delresultat": []
              }
            ]
          }
        ]
      },
      {
        "identifikator": "EØS",
        "avklaring": "Er personen omfattet av Grunnforordningen?",
        "resultat": "JA",
        "beskrivelse": "Personen er omfattet av Grunnforordningen",
        "delresultat": [
          {
            "identifikator": "EØS-1",
            "avklaring": "Er personen statsborger i et EØS land?",
            "resultat": "JA",
            "beskrivelse": "Personen er statsborger i et EØS-land.",
            "delresultat": []
          }
        ]
      },
      {
        "identifikator": "LOV",
        "avklaring": "Er personen omfattet av norsk lovvalg?",
        "resultat": "JA",
        "beskrivelse": "Personen er omfattet av norsk lovvalg",
        "delresultat": [
          {
            "identifikator": "LOV-1",
            "avklaring": "Jobber personen for en norsk arbeidsgiver?",
            "resultat": "JA",
            "beskrivelse": "Arbeidsgiver er norsk",
            "delresultat": []
          },
          {
            "identifikator": "LOV-2",
            "avklaring": "Sjekk om personen jobber i det maritime",
            "resultat": "NEI",
            "beskrivelse": "Personen jobber ikke i det maritime",
            "delresultat": []
          },
          {
            "identifikator": "LOV-3",
            "avklaring": "Sjekk om personen er pilot eller kabinansatt",
            "resultat": "NEI",
            "beskrivelse": "Personen er ikke pilot eller kabinansatt",
            "delresultat": []
          },
          {
            "identifikator": "LOV-5",
            "avklaring": "Sjekk om personen har oppgitt å ha jobbet utenfor Norge",
            "resultat": "NEI",
            "beskrivelse": "Bruker har ikke jobbet utenfor Norge",
            "delresultat": []
          }
        ]
      }
    ]
  }
}
```

## Kjøre fra laptop
* NAV-tunell må være koblet opp
* Kubeconfig riktig satt opp
* `kubectl port-forward <pod-navn> 8080:7070`
* Endepunktet er nå tilgjengelig på `localhost:8080

## Hvordan skaffe token`til preprod
```
curl -X POST -H "Content-Type: application/x-www-form-urlencoded" -d 'client_id=<clientid>>&scope=api://<clientid>/.default&client_secret=<clientsecret>&grant_type=client_credentials' 'https://login.microsoftonline.com/966ac572-f5b7-4bbe-aa88-c76419c0f851/oauth2/v2.0/token'
```
Der `clientid` og `clientsecret` kan hentes fra vault under `azuread`
