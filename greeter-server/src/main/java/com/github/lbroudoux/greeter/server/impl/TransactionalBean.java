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
    public int transactionStatus() throws RemoteException {
        int status = transactionSynchronizationRegistry.getTransactionStatus();
        System.out.printf("TransactionalStatelessBean:transactionStatus: %d%n", status);
        return status;
    }
}
