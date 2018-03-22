package com.github.lbroudoux.greeter.server;

import java.rmi.RemoteException;

public interface StatelessRemote {
    int transactionStatus() throws RemoteException;
}
