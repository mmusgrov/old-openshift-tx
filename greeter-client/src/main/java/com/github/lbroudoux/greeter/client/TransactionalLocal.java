package com.github.lbroudoux.greeter.client;

import java.rmi.RemoteException;

public interface TransactionalLocal {
    String transactionStatus();
}
