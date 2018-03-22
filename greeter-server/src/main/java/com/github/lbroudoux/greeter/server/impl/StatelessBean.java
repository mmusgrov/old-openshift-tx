package com.github.lbroudoux.greeter.server.impl;

import java.rmi.RemoteException;
import javax.ejb.Remote;
import javax.ejb.Stateless;

import javax.annotation.Resource;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.transaction.TransactionSynchronizationRegistry;

import com.github.lbroudoux.greeter.server.StatelessRemote;

@Stateless
@Remote (StatelessRemote.class)
public class StatelessBean implements StatelessRemote {

    @Resource
    private TransactionSynchronizationRegistry transactionSynchronizationRegistry;

    @Override
    @TransactionAttribute(TransactionAttributeType.SUPPORTS)
    public int transactionStatus() throws RemoteException {
        return transactionSynchronizationRegistry.getTransactionStatus();
    }
}
