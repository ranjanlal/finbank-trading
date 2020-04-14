package com.finbank.trading.service;

import java.io.IOException;

public interface TransactionsProducer {

    void loadTransactions(String filePath) throws IOException;

}