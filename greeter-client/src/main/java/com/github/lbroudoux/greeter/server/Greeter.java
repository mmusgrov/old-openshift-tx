package com.github.lbroudoux.greeter.server;

import java.rmi.RemoteException;

public interface Greeter {
    String setName(String name);
    String transactionStatus() throws RemoteException;

    String greet(String user);
}
