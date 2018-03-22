package com.github.lbroudoux.greeter.server.impl;

import javax.annotation.Resource;
import javax.ejb.Remote;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.transaction.TransactionSynchronizationRegistry;

import com.github.lbroudoux.greeter.server.Greeter;

import java.rmi.RemoteException;

@Stateless
@Remote (Greeter.class)
public class GreeterBean implements Greeter {

  @Override
  public String greet(String user) {
    return "Hello " + user + ", have a pleasant day!";
  }

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
