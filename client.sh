#!/bin/bash
set -e

# git add -u && git commit -m "config" && git push origin master

oc delete is,bc,dc,service greeter-client 
oc new-app jboss-eap-64~https://github.com/mmusgrov/openshift-tx.git#eap64-txn --context-dir='greeter-client' --name='greeter-client' --labels name='greeter-client'

oc env dc/greeter-client JAVA_OPTS_APPEND="-Dgreeting.server.host=greeter-server.eap-tests.svc.cluster.local"

# oc expose service greeter-client
# oc rollout latest greeter-client

# curl -XGET 'http://greeter-client-eap-tests.192.168.99.100.nip.io/greeter-client/api/greeter/greet/Me'
