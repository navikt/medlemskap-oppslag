#!/bin/bash
pod=$(kubectl get pods | grep medlemskap-oppslag | cut -d " " -f1)
kubectl port-forward ${pod} 8080:7070
