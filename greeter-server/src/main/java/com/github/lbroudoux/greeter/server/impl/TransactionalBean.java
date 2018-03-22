package com.github.lbroudoux.greeter.server.impl;

import java.rmi.RemoteException;
import javax.ejb.Remote;
import javax.ejb.Stateless;

import javax.annotation.Resource;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.transaction.Status;
import javax.transaction.TransactionSynchronizationRegistry;

import com.github.lbroudoux.greeter.server.TransactionalRemote;

@Stateless
@Remote (TransactionalRemote.class)
public class TransactionalBean implements TransactionalRemote {

    @Resource
    private TransactionSynchronizationRegistry transactionSynchronizationRegistry;

    @Override
    public String setName(String name) {
        return "Hello " + name + ", have a pleasant day!";
    }

    @Override
    @TransactionAttribute(TransactionAttributeType.SUPPORTS)
    public String transactionStatus() throws RemoteException {
        int status = transactionSynchronizationRegistry.getTransactionStatus();
        System.out.printf("TransactionalStatelessBean:transactionStatus: %d%n", status);
        return stringForm(status);
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
