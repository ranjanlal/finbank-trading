package com.finbank.trading.service;

import com.finbank.trading.dao.FuturesTransaction;
import com.finbank.trading.dao.FuturesTransactionRepository;
import com.finbank.trading.model.ResponseTuple;
import com.finbank.trading.model.SearchConditionTuple;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.File;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static com.finbank.trading.util.AppUtils.parseDate;
import static com.finbank.trading.util.Constants.DATE_PATTERN_YYYY_MM_DD;

@Slf4j
@Service
public class TransactionsReportServiceImpl implements TransactionsReportService {

    @Autowired
    private FuturesTransactionRepository futuresTransactionRepository;

    @Autowired
    private FileHandlerService fileHandlerService;

    @Override
    public List<ResponseTuple> generateReport() {
        List<FuturesTransaction> allTransactions = futuresTransactionRepository.findAll();
        return parseFuturesTransactions(allTransactions);
    }

    @Override
    public List<ResponseTuple> generateReport(String clientNumber) {
        List<FuturesTransaction> allTransactions = futuresTransactionRepository.findByClientNumber(clientNumber);
        return parseFuturesTransactions(allTransactions);
    }

    @Override
    public List<ResponseTuple> generateReport(String clientNumber, String transactionDate) {
        List<FuturesTransaction> allTransactions =
                futuresTransactionRepository
                        .findByClientNumberAndTransactionDate(clientNumber, parseDate(transactionDate, DATE_PATTERN_YYYY_MM_DD));
        return parseFuturesTransactions(allTransactions);
    }


    @Override
    public File generateReportCSV(String clientNumber) throws Exception {
        List<ResponseTuple> result = generateReport(clientNumber);
        return fileHandlerService.writeCsvFileToDownloads(result);
    }

    @Override
    public File generateReportCSV(String clientNumber, String transactionDate) throws Exception {
        List<ResponseTuple> result = generateReport(clientNumber, transactionDate);
        return fileHandlerService.writeCsvFileToDownloads(result);
    }

    private List<ResponseTuple> parseFuturesTransactions(List<FuturesTransaction> transactions) {

        // group by
        Map<SearchConditionTuple, Integer> totalTransactionAmount = transactions
                .stream()
                .collect(
                        Collectors.groupingBy(p -> new SearchConditionTuple(
                                        p.getClientType(),
                                        p.getClientNumber(),
                                        p.getAccountNumber(),
                                        p.getSubAccountNumber(),
                                        p.getProductGroupCode(),
                                        p.getExchangeCode(),
                                        p.getSymbol()
                                ),
                                Collectors.summingInt(p -> p.getQuantityLong() - p.getQuantityShort())
                        ));

        // convert to API response records
        return totalTransactionAmount.entrySet().stream()
                .map(t -> new ResponseTuple(
                        t.getKey().getClientType().trim(),
                        t.getKey().getClientNumber().trim(),
                        t.getKey().getAccountNumber().trim(),
                        t.getKey().getSubAccountNumber().trim(),
                        t.getKey().getProductGroupCode().trim(),
                        t.getKey().getExchangeCode().trim(),
                        t.getKey().getSymbol().trim(),
                        t.getValue()
                ))
                .collect(Collectors.toList());
    }

}

