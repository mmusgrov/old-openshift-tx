package com.github.lbroudoux.greeter.client;

public interface TransactionalLocal {
    String transactionStatus();
    String testSameTransactionEachCall();
}
