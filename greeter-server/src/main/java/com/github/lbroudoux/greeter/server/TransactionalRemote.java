package com.github.lbroudoux.greeter.server;

import java.rmi.RemoteException;

public interface TransactionalRemote {
    String setName(String name);
    String transactionStatus() throws RemoteException;
}
