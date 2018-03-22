package com.github.lbroudoux.greeter.client;

import javax.transaction.NotSupportedException;
import javax.transaction.Status;
import javax.transaction.UserTransaction;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.naming.NamingException;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.SecurityContext;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.rmi.RemoteException;
import java.util.Hashtable;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.annotation.Resource;
import javax.enterprise.context.ApplicationScoped;

import com.github.lbroudoux.greeter.server.Greeter;
import com.github.lbroudoux.greeter.server.TransactionalRemote;
import org.omg.CORBA.SystemException;

/**
 * A JAX-RS resource for exposing REST endpoints for Greeter manipulation
 */
@Stateless
@LocalBean
@ApplicationScoped
@Path("greeter")
public class GreeterResource {

    private static Logger log = Logger.getLogger(GreeterResource.class.getName());

    private Greeter greeter;
    private TransactionalRemote transactionalBean;

    @Resource
    private UserTransaction userTransaction;

    @GET
    @Path("greet/{name}")
    @Produces({"application/json"})
    public String greet(@Context SecurityContext context, @PathParam("name") String name) {
        log.log(Level.INFO, "Getting new greet request for " + name);
        String response = "default";

        try {
            // Try a simple JNDI lookup if no greeter has been injected.
            if (greeter == null) {
                Hashtable properties = new Hashtable();
                properties.put(javax.naming.Context.URL_PKG_PREFIXES, "org.jboss.ejb.client.naming");
                javax.naming.Context jndiContext = new javax.naming.InitialContext(properties);
                Object obj = jndiContext.lookup("ejb:/greeter-server//GreeterBean!" + Greeter.class.getCanonicalName());
                //com.github.lbroudoux.greeter.server.Greeter");
                log.log(Level.INFO, "Lookup object class: " + obj.getClass());
                greeter  = (Greeter)obj;
            }
            // Invoke remote Greeter EJB.
            response = greeter.greet(name);
            int status = greeter.transactionStatus();

            response = response + " status: " + stringForm(status);
        } catch (Throwable t) {
            // Put some diagnostic traces...
            log.log(Level.WARNING, "Error: " + t.getMessage());
            t.printStackTrace();
        }
        log.log(Level.INFO, "Found greeting result " + response);
        return "{\"response\":\"" + response + "\"}";
    }

    @GET
    @Path("status/{name}")
    @Produces({"application/json"})
    public String txGreet(@Context SecurityContext context, @PathParam("name") String name) {
        log.log(Level.INFO, "Getting new greet request for " + name);
        String message;

        try {
            cleanThread();
            TransactionalRemote bean = getTransactionalBean("TransactionalBean", TransactionalRemote.class.getCanonicalName());

            assert Status.STATUS_NO_TRANSACTION == bean.transactionStatus() : "No transaction expected!";
            userTransaction.begin();
            try {
                log.log(Level.INFO, "basicTransactionPropagationTest: asserting Status.STATUS_ACTIVE%n");

                assert Status.STATUS_ACTIVE == bean.transactionStatus() : "Active transaction expected!";
            } finally {
                userTransaction.rollback();
                message = "status ok, rolled back";
            }
        } catch (RemoteException | NotSupportedException | SystemException | IllegalStateException |
                SecurityException | NamingException | javax.transaction.SystemException e) {
            StringWriter sw = new StringWriter();
            e.printStackTrace(new PrintWriter(sw));
            message = sw.toString();
        }

        return "{\"response\":\"" + message + "\"}";
    }

    private void cleanThread() {
//        if (userTransaction.getStatus() != Status.STATUS_NO_TRANSACTION)
        try {
            userTransaction.rollback();
        } catch (Throwable e) {
        }

    }
    private TransactionalRemote getTransactionalBean(String beanName, String remoteName) throws NamingException {
        if (transactionalBean == null) {
            Hashtable properties = new Hashtable();
            properties.put(javax.naming.Context.URL_PKG_PREFIXES, "org.jboss.ejb.client.naming");
            javax.naming.Context jndiContext = new javax.naming.InitialContext(properties);
            // "ejb:myapp/myejbmodule//FooBean!org.myapp.ejb.Foo"
            Object obj = jndiContext.lookup("ejb:/greeter-server//" + beanName + "!" + remoteName);
            //com.github.lbroudoux.greeter.server.TransactionalRemote");
            log.log(Level.INFO, "Lookup object class: " + obj.getClass());
            transactionalBean = (TransactionalRemote)obj;
        }

        return transactionalBean;
    }

    public static String stringForm (int status) {
        switch (status) {
            case javax.transaction.Status.STATUS_ACTIVE:
                return "javax.transaction.Status.STATUS_ACTIVE";
            case javax.transaction.Status.STATUS_COMMITTED:
                return "javax.transaction.Status.STATUS_COMMITTED";
            case javax.transaction.Status.STATUS_MARKED_ROLLBACK:
                return "javax.transaction.Status.STATUS_MARKED_ROLLBACK";
            case javax.transaction.Status.STATUS_NO_TRANSACTION:
                return "javax.transaction.Status.STATUS_NO_TRANSACTION";
            case javax.transaction.Status.STATUS_PREPARED:
                return "javax.transaction.Status.STATUS_PREPARED";
            case javax.transaction.Status.STATUS_PREPARING:
                return "javax.transaction.Status.STATUS_PREPARING";
            case javax.transaction.Status.STATUS_ROLLEDBACK:
                return "javax.transaction.Status.STATUS_ROLLEDBACK";
            case javax.transaction.Status.STATUS_ROLLING_BACK:
                return "javax.transaction.Status.STATUS_ROLLING_BACK";
            case javax.transaction.Status.STATUS_UNKNOWN:
            default:
                return "javax.transaction.Status.STATUS_UNKNOWN";
        }
    }
}
