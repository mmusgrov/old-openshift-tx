package com.github.lbroudoux.greeter.server;

import java.rmi.RemoteException;

public interface Greeter {
  String setName(String name);
  int transactionStatus() throws RemoteException;

  String greet(String user);
}
