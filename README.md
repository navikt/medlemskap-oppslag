# medlemskap-oppslag
Oppslagstjeneste for lovvalg og medlemskap i Folketrygden

Denne tjenesten gjør REST-kall mot følgende tjenester/registre:

* AAREG: Arbeidstaker og arbeidsgiver registeret
* EREG: Enhetsregisteret
* MEDL: Medlemskapsregisteret, inneholder medlemskapsperioder som avviker fra normalen
* GSAK: Saksbehandlingsløsning
* JOARK: Journalarkiv
* TPS: Tjenestebasert Persondatasystem
* PDL: Ny persondataløsning, mangler foreløpig utenlandsopphold 

# Funksjonell dokumentasjon
* [Funksjonell dokumentasjon](src/test/resources/dokumentasjon/README.md)

## URL til tjeneste
* preprod: https://medlemskap-oppslag.nais.preprod.local
* prod: https://medlemskap-oppslag.nais.adeo.no

## Autentisering
Forventer et AzureAD-token utstedt til servicebruker, satt Authorization-header (Bearer)

Hvordan hente ut et AzureAD-token for servicebruker:
`curl -X POST -H "Content-Type: application/x-www-form-urlencoded" -d 'client_id=<client_id>&scope=api://<client_id>/.default&client_secret=<client_secret>&grant_type=client_credentials' 'https://login.microsoftonline.com/966ac572-f5b7-4bbe-aa88-c76419c0f851/oauth2/v2.0/token'`

Client ID og passord hentes fra Vault: `azuread / dev / creds / servicebruker`

## Headere
I tillegg til Authorization-headeren kreves det at Content-Type er satt til application/json

## Eksempel på kall

Kallet er en POST på `/`
```
{
    "fnr": "123456789",
    "periode": {
        "fom": "2019-01-01", 
        "tom": "2019-12-31"
    }, 
    "brukerinput": {
        "arbeidUtenforNorge": false
    }
}
```

### Inn-parametre
* fnr: Fødselsnummer, identifiserer brukeren
* periode: 
    * For ytelsen sykepenger er dette perioden brukeren søker sykepenger for
    * For ytelsen sykepenger brukes dagen før start på perioden som første sykemeldingsdag
* brukerinput: Input fra bruker som må fylles ut i søknadsdialogen og er nødvendig for å avgjøre medlemskap
    * arbeidUtenforNorge: Har brukeren jobbet utenfor Norge siste 12 måneder?
* ytelse
    * Utledes fra request, ved å se på callerId

### Ut-parametere
        val tidspunkt: LocalDateTime,
        val versjonTjeneste: String,
        val versjonRegler: String,
        val datagrunnlag: Datagrunnlag,
        val resultat: Resultat
Resultat inneholder:

        val regelId: RegelId? = null,
        val avklaring: String = "",
        val begrunnelse: String = "",
        val svar: Svar,
        var harDekning: Svar? = null,
        var dekning: String = "",
        val delresultat: List<Resultat> = listOf()

Feltene harDekning og dekning er ikke i bruk inntil konsumenter kan håndtere dekning funksjonelt. Dekning sier noe om brukeren har rett på ytelse.
Inntil videre vil alle som ikke har dekning gå til uavklart.

## Eksempel på kall med CURL, gitt at port-forwarding er satt opp på port 8080:
```
curl -X POST -H "Authorization: Bearer <AAD_TOKEN>" -H "Content-Type: application/json" -d '{ "fnr": "123456789", "periode": { "fom": "2019-01-01", "tom": "2019-12-31" }, "brukerinput": { "arbeidUtenforNorge": false } }' localhost:8080
```

## Eksempel på svar
* [norsk borger](src/test/resources/testpersoner/autogenerert/norsk_borger_response.json)
* [eøs-borger som er uavklart](src/test/resources/testpersoner/autogenerert/eøs_borger_uavklart_response.json)

## Kjøre fra laptop
* NAV-tunnel må være koblet opp
* Kubeconfig riktig satt opp
* `kubectl port-forward <pod-navn> 8080:7070`
* Endepunktet er nå tilgjengelig på `localhost:8080

## Hvordan skaffe token i preprod
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
Klienter som ønsker å kalle oss må generere et token med vår klientid som scope

