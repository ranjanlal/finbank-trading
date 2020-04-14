package com.finbank.trading.service;

import com.finbank.trading.model.ResponseTuple;

import java.io.File;
import java.util.List;

public interface TransactionsReportService {

    List<ResponseTuple> generateReport();

    List<ResponseTuple> generateReport(String clientNumber);

    List<ResponseTuple> generateReport(String clientNumber, String transactionDate);

    File generateReportCSV(String clientNumber) throws Exception;

    File generateReportCSV(String clientNumber, String transactionDate) throws Exception;
}
