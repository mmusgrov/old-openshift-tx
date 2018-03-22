package com.github.lbroudoux.greeter.client;

import com.github.lbroudoux.greeter.server.TransactionalRemote;
import com.github.lbroudoux.greeter.server.TransactionalStatefulRemote;

import javax.annotation.Resource;
import javax.ejb.Remote;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.naming.NamingException;
import javax.transaction.NotSupportedException;
import javax.transaction.Status;
import javax.transaction.SystemException;
import javax.transaction.TransactionSynchronizationRegistry;
import javax.transaction.UserTransaction;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.rmi.RemoteException;
import java.util.Hashtable;
import java.util.logging.Level;
import java.util.logging.Logger;

@Stateless
@Remote (TransactionalLocal.class)
public class TransactionalLocalBean implements TransactionalLocal {
    private static Logger log = Logger.getLogger(GreeterResource.class.getName());

    @Resource
    private UserTransaction userTransaction;

    private TransactionalRemote transactionalBean;

    private TransactionalStatefulRemote statefulEJB;

    @Resource
    private TransactionSynchronizationRegistry transactionSynchronizationRegistry;

    @Override
    @TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
    public String transactionStatus() {
            String message;

            try {
                TransactionalRemote bean = getTransactionalBean(
                        "TransactionalBean", TransactionalRemote.class.getCanonicalName());

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
                    SecurityException | NamingException e) {
                StringWriter sw = new StringWriter();
                e.printStackTrace(new PrintWriter(sw));
                message = sw.toString();
            }

            return "{\"response\":\"" + message + "\"}";
    }

    @Override
    @TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
    public String testSameTransactionEachCall() {
        String message;

        try {
            TransactionalStatefulRemote bean = getTransactionalStatefulBean(
                    "TransactionalStatefulBean", TransactionalStatefulRemote.class.getCanonicalName());

            userTransaction.begin();
            try {
                bean.sameTransaction(true);
                bean.sameTransaction(false);
            } finally {
                userTransaction.rollback();
                message = "success";
            }
        } catch (NotSupportedException | SystemException | RemoteException | IllegalStateException | SecurityException
                | NamingException e) {
            StringWriter sw = new StringWriter();
            e.printStackTrace(new PrintWriter(sw));
            message = sw.toString();
        }

        return "{\"response\":\"" + message + "\"}";
    }

    private TransactionalRemote getTransactionalBean(String beanName, String viewClassName) throws NamingException {
        if (transactionalBean == null) {
            Hashtable properties = new Hashtable();
            properties.put(javax.naming.Context.URL_PKG_PREFIXES, "org.jboss.ejb.client.naming");
            javax.naming.Context jndiContext = new javax.naming.InitialContext(properties);
            // "ejb:myapp/myejbmodule//FooBean!org.myapp.ejb.Foo"
            Object obj = jndiContext.lookup("ejb:/greeter-server//" + beanName + "!" + viewClassName);
            //com.github.lbroudoux.greeter.server.TransactionalRemote");
            log.log(Level.INFO, "Lookup object class: " + obj.getClass());
            transactionalBean = (TransactionalRemote)obj;
        }

        return transactionalBean;
    }

    private TransactionalStatefulRemote getTransactionalStatefulBean(String beanName, String viewClassName) throws NamingException {
        if (statefulEJB == null) {
            Hashtable properties = new Hashtable();
            properties.put(javax.naming.Context.URL_PKG_PREFIXES, "org.jboss.ejb.client.naming");
            javax.naming.Context jndiContext = new javax.naming.InitialContext(properties);
            // "ejb:myapp/myejbmodule//FooBean!org.myapp.ejb.Foo"
            // context.lookup("ejb:" + appName + "/" + moduleName + "/" + distinctName + "/" + beanName + "!" + viewClassName + "?stateful");
            Object obj = jndiContext.lookup("ejb:/greeter-server//" + beanName + "!" + viewClassName + "?stateful");
            //com.github.lbroudoux.greeter.server.TransactionalRemote");
            log.log(Level.INFO, "Lookup object class: " + obj.getClass());
            statefulEJB = (TransactionalStatefulRemote)obj;
        }

        return statefulEJB;
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
