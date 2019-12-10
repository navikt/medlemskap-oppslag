# medlemskap-oppslag
Oppslagstjeneste for medlemskap i Folketrygden

## Kjøre fra laptop
* NAV-tunell må være koblet opp
* Kubeconfig riktig satt opp
* `kubectl port-forward <pod-navn> 8080:7070`
* `curl -X GET -k http://localhost:8080`
