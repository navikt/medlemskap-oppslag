#!/bin/bash
rm "`dirname $0`"/jmeter.log
rm "`dirname $0`"/MedlemskapOppslagMedMiniNorge.jtl
rm "`dirname $0`"/MedlemskapOppslagResults.log
rm "`dirname $0`"/mininorge_response.csv
"`dirname $0`"/apache-jmeter-5.2.1/bin/jmeter -JAAD_TOKEN=$1 -n  -t "`dirname $0`"/MedlemskapOppslagMedMiniNorge.jmx -l "`dirname $0`"/MedlemskapOppslagMedMiniNorge.jtl
