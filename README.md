
# Starting minishift

 minishift delete
 cat ~/.minishift/config/config.json
 minishift start
 eval $(minishift oc-env)
 oc login -u developer -p developer
 oc project ejb-xa
 minishift docker-env
 eval $(minishift docker-env)
 minishift addon apply registry-route
 #docker pull brew-pulp-docker01.web.prod.ext.phx2.redhat.com:8888/jboss-eap-7/eap71

# create new project
 oc new-project eap-tests --display-name="JBoss EAP Transactional EJB"

## Importing EAP images
 oc import-image jboss-eap-70 --from=registry.access.redhat.com/jboss-eap-7/eap70-openshift --confirm
 oc import-image jboss-eap-71 --from=registry.access.redhat.com/jboss-eap-7/eap71-openshift --confirm
 oc import-image jboss-eap-64 --from=registry.access.redhat.com/jboss-eap-6/eap64-openshift --confirm

# create and deploy server
cat server.sh

# create and deploy client
cat client.sh

# trigger an ejb call
curl -XGET 'http://greeter-client-eap-tests.192.168.99.100.nip.io/greeter-client/api/greeter/greet/Laurent'

# Troubleshooting
oc build-logs greeter-client-1
# oc login -u system:admin 
oc rsh `oc get pods -n xa-ejb-client | grep Running | awk '{print $1}'
CLI reference: https://docs.openshift.com/enterprise/3.0/cli_reference/basic_cli_operations.html
