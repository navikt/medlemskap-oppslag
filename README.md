# medlemskap-oppslag
Oppslagstjeneste for lovvalg og medlemskap i Folketrygden

Denne tjenesten gjør REST-kall mot følgende tjenester/registre:

TODO: Beskrivelse av registerne
* MEDL:
* GSAK:
* JOARK:
* TPS/DL: 

# Funksjonell dokumentasjon
* [Funksjonell dokumentasjon](src/test/resources/dokumentasjon/README.md)

## URL til tjeneste
* preprod: https://medlemskap-oppslag.nais.preprod.local
* prod: https://medlemskap-oppslag.nais.adeo.no

## Autentisering
Forventer et AzureAD-token utstedt til servicebruker, satt Authorization-header (Bearer)

## Headere
I tillegg til Authorization-headeren kreves det at Content-Type er satt til application/json

## Eksempel på kall

TODO: Lag kjørende Cucumber-ekspempel på kall
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

## Eksempel på kall med CURL, gitt at port-forwarding er satt opp på port 8080:
```
curl -X POST -H "Authorization: Bearer <AAD_TOKEN>" -H "Content-Type: application/json" -d '{ "fnr": "123456789", "periode": { "fom": "2019-01-01", "tom": "2019-12-31" }, "brukerinput": { "arbeidUtenforNorge": false } }' localhost:8080
```

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
            "stillingsprosent": 100
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
    "avklaring": "Er bruker medlem?",
    "begrunnelse": "Bruker er medlem",
    "svar": "JA",
    "delresultat": [
      {
        "identifikator": "OPPLYSNINGER",
        "avklaring": "Finnes det registrerte opplysninger på bruker?",
        "begrunnelse": "Alle de følgende ble NEI",
        "svar": "NEI",
        "delresultat": [
          {
            "identifikator": "OPPLYSNINGER-MEDL",
            "avklaring": "Finnes det registrerte opplysninger i MEDL?",
            "begrunnelse": "",
            "svar": "NEI",
            "delresultat": []
          },
          {
            "identifikator": "OPPLYSNINGER-JOARK",
            "avklaring": "Finnes det dokumenter i JOARK på medlemskapsområdet?",
            "begrunnelse": "",
            "svar": "NEI",
            "delresultat": []
          },
          {
            "identifikator": "OPPLYSNINGER-GOSYS",
            "avklaring": "Finnes det åpne oppgaver i GOSYS på medlemskapsområdet?",
            "begrunnelse": "",
            "svar": "NEI",
            "delresultat": []
          }
        ]
      },
      {
        "identifikator": "GRUNNFORORDNING-EØS",
        "avklaring": "Er brukeren statsborger i et EØS land?",
        "begrunnelse": "",
        "svar": "JA",
        "delresultat": []
      },
      {
        "identifikator": "ARB-1",
        "avklaring": "Har bruker et registrert arbeidsforhold?",
        "begrunnelse": "",
        "svar": "JA",
        "delresultat": []
      },
      {
        "identifikator": "ARB-2",
        "avklaring": "Jobber bruker for en norsk arbeidsgiver?",
        "begrunnelse": "",
        "svar": "JA",
        "delresultat": []
      },
      {
        "identifikator": "ARB-3",
        "avklaring": "Er bruker pilot eller kabinansatt?",
        "begrunnelse": "",
        "svar": "NEI",
        "delresultat": []
      },
      {
        "identifikator": "ARB-4",
        "avklaring": "Har bruker et maritimt arbeidsforhold?",
        "begrunnelse": "",
        "svar": "NEI",
        "delresultat": []
      },
      {
        "identifikator": "LOV-1",
        "avklaring": "Har bruker oppgitt å ha jobbet utenfor Norge?",
        "begrunnelse": "",
        "svar": "NEI",
        "delresultat": []
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

## Testing med jMeter
En jMeter-test som henter ned MiniNorge populasjonen og gjør et kall mot medlemskap-oppslag for hver person kan kjøres med følgende script
```
jmeter/runJMeterTest.sh <AAD_TOKEN>
```
jMeter-testen krever port-forwarding for medlemskap-oppslag satt opp på port 8080, og for testnorge-hodejegeren på port 8081. Dette kan enklest gjøres med "Kube Forwarder", hvor konfigurasjonen ligger på kube-forwarder-config/cluster-dev-fss — nais-user.kpf-export.v2.json

For å kjøre jMeter med GUI, enten fordi man liker det bedre eller fordi man skal redigere test planen, så kan følgende kommando kjøres:
```
jmeter/apache-jmeter-5.2.1/bin/jmeter -JAAD_TOKEN=<AAD_TOKEN> -t jmeter/MedlemskapOppslagMedMiniNorge.jmx
```

# Autentisere klienter
Klienter som ønsker å kalle oss må generere et token med vår klientid som scope.
