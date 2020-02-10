# medlemskap-oppslag
Oppslagstjeneste for medlemskap i Folketrygden

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
    "soknadsperiodeStart": "YYYY-MM-DD",
    "soknadsperiodeSlutt": "YYYY-MM-DD",
    "soknadstidspunkt": "YYYY-MM-DD",
    "brukerinput": {
        "arbeidUtenforNorge": false
    }
}
```

### Brukerinput
Input fra bruker som må fylles ut i søknadsdialogen og er nødvendig for å avgjøre medlemskap

* `arbeidUtenforNorge` Om de har jobbet utenfor Norge siste 12 måneder

## Eksempel på svar
```
{
  "tidspunkt": "YYYY-MM-DD",
  "versjonTjeneste": "1.2",
  "versjonRegler": "v1",
  "datagrunnlag": {
    "soknadsperiode" : {
      "fom" : "2020-01-30",
      "tom" : "2020-01-30"
    },
    "soknadstidspunkt" : "2020-01-30",
    "brukerinput" : {
      "arbeidUtenforNorge" : false
    },
    "personhistorikk" : {
      "statsborgerskap" : [ {
        "landkode" : "NOR",
        "fom" : "2020-01-30",
        "tom" : "2020-01-30"
      } ],
      "personstatuser" : [ ],
      "bostedsadresser" : [ ],
      "postadresser" : [ ],
      "midlertidigAdresser" : [ ]
    },
    "medlemskapsunntak" : [ ],
    "arbeidsforhold" : [ ],
    "inntekt" : [ ],
    "oppgaver" : [ ],
    "dokument" : [ ]
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
      },
      {
        "identifikator": "EØS",
        "avklaring": "Er personen omfattet av EØS-forordningen?",
        "resultat": "JA",
        "beskrivelse": "Personen er omfattet av EØS-ordningen",
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
