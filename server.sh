#!/bin/bash
set -e

# oc new-project eap-tests --display-name="JBoss EAP Tests"
# oc import-image jboss-eap-64 --from=registry.access.redhat.com/jboss-eap-6/eap64-openshift --confirm
# git add -u && git commit -m "config" && git push origin master

oc delete is,bc,dc,service greeter-server 
oc new-app jboss-eap-64~https://github.com/mmusgrov/openshift-tx.git#eap64 --context-dir='greeter-server' --name='greeter-server' --labels name='greeter-server'

# edit the dc and service yaml to add port 4447 to the ports section of the container specification:
# oc edit dc/greeter-server
# oc edit svc/greeter-server
# oc expose service greeter-server
# oc rollout latest greeter-server

# oc build-logs greeter-client-1
# oc rsh `oc get pods -n xa-ejb-client | grep Running | awk '{print $1}'`
