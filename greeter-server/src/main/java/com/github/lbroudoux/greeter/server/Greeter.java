package com.github.lbroudoux.greeter.server;

import java.rmi.RemoteException;

public interface Greeter {
  String greet(String user);

  String setName(String name);
  int transactionStatus() throws RemoteException;
}
