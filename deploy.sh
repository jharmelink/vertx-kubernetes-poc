#!/usr/bin/env bash

mvn clean install
kubectl delete -f kubernetes.yaml
docker build -t vertx-kubernetes-poc:1.0-SNAPSHOT .
kubectl apply -f kubernetes.yaml
