#!/bin/bash
pod=$(kubectl get pods | grep -m 1 medlemskap-oppslag | cut -d " " -f1 )
kubectl port-forward ${pod} 8080:7070
