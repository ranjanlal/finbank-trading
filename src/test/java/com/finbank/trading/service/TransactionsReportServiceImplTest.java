package com.finbank.trading.service;

import com.finbank.trading.dao.FuturesTransaction;
import com.finbank.trading.dao.FuturesTransactionRepository;
import com.finbank.trading.model.ResponseTuple;
import org.apache.commons.io.FileUtils;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Bean;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.File;
import java.nio.charset.Charset;
import java.time.LocalDate;
import java.time.Month;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;

@RunWith(SpringJUnit4ClassRunner.class)
public class TransactionsReportServiceImplTest {

    @TestConfiguration
    static class TransactionsReportServiceImplTestContextConfiguration {
        @Bean
        public TransactionsReportService transactionsReportService() {
            return new TransactionsReportServiceImpl();
        }
    }

    @Autowired
    private TransactionsReportService transactionsReportService;

    @MockBean
    private FuturesTransactionRepository futuresTransactionRepository;

    @MockBean
    private FileHandlerService fileHandlerService;

    @Rule
    public TemporaryFolder temporaryFolder = new TemporaryFolder();

    @Test
    public void givenEmptyDatabase_whenReportGeneratedForAllClientsAndProducts_thenReturnEmptyListOfResponseTuple() {

        // given
        when(futuresTransactionRepository.findAll()).thenReturn(Collections.EMPTY_LIST);

        // when
        List<ResponseTuple> transactionsFound = transactionsReportService.generateReport();

        // then
        assertThat(transactionsFound.size(), is(0));
    }

    @Test
    public void givenOneProductForAClientInDatabase_whenReportGenerated_thenReturnOneRecordOfResponseTuple() {

        // given
        setUpOneProductForAClientInDatabase();

        // when
        List<ResponseTuple> transactionsFound = transactionsReportService.generateReport();

        // then
        assertThat(transactionsFound.size(), is(1));

        assertThat(transactionsFound.get(0).getClientType(), is("315"));
        assertThat(transactionsFound.get(0).getClientNumber(), is("1234"));
        assertThat(transactionsFound.get(0).getAccountNumber(), is("001"));
        assertThat(transactionsFound.get(0).getSubAccountNumber(), is("002"));
        assertThat(transactionsFound.get(0).getProductGroupCode(), is("FU"));
        assertThat(transactionsFound.get(0).getExchangeCode(), is("SGX"));
        assertThat(transactionsFound.get(0).getSymbol(), is("N1"));
        assertThat(transactionsFound.get(0).getTotalTransactionAmount(), is(100));
    }

    @Test
    public void givenTwoIdenticalProductsForAClientInDatabase_whenReportGenerated_thenReturnOneRecordOfResponseTuple() {

        // given
        setUpTwoIdenticalProductsForAClientInDatabase();

        // when
        List<ResponseTuple> transactionsFound = transactionsReportService.generateReport();

        // then
        assertThat(transactionsFound.size(), is(1));

        assertThat(transactionsFound.get(0).getClientType(), is("315"));
        assertThat(transactionsFound.get(0).getClientNumber(), is("1234"));
        assertThat(transactionsFound.get(0).getAccountNumber(), is("001"));
        assertThat(transactionsFound.get(0).getSubAccountNumber(), is("002"));
        assertThat(transactionsFound.get(0).getProductGroupCode(), is("FU"));
        assertThat(transactionsFound.get(0).getExchangeCode(), is("SGX"));
        assertThat(transactionsFound.get(0).getSymbol(), is("N1"));
        assertThat(transactionsFound.get(0).getTotalTransactionAmount(), is(105));
    }

    @Test
    public void givenTwoDistinctProductsForAClientInDatabase_whenReportGenerated_thenReturnTwoRecordsOfResponseTuple() {

        // given
        setUpTwoDistinctProductsForAClientInDatabase();

        // when
        List<ResponseTuple> transactionsFound = transactionsReportService.generateReport();

        // then
        assertThat(transactionsFound.size(), is(2));

        assertThat(transactionsFound.get(0).getClientType(), is("315"));
        assertThat(transactionsFound.get(0).getClientNumber(), is("1234"));
        assertThat(transactionsFound.get(0).getAccountNumber(), is("001"));
        assertThat(transactionsFound.get(0).getSubAccountNumber(), is("002"));
        assertThat(transactionsFound.get(0).getProductGroupCode(), is("FU"));
        assertThat(transactionsFound.get(0).getExchangeCode(), is("SGX"));
        assertThat(transactionsFound.get(0).getSymbol(), is("N1"));
        assertThat(transactionsFound.get(0).getTotalTransactionAmount(), is(100));

        assertThat(transactionsFound.get(1).getClientType(), is("315"));
        assertThat(transactionsFound.get(1).getClientNumber(), is("1234"));
        assertThat(transactionsFound.get(1).getAccountNumber(), is("002"));
        assertThat(transactionsFound.get(1).getSubAccountNumber(), is("100"));
        assertThat(transactionsFound.get(1).getProductGroupCode(), is("VC"));
        assertThat(transactionsFound.get(1).getExchangeCode(), is("LND"));
        assertThat(transactionsFound.get(1).getSymbol(), is("GL"));
        assertThat(transactionsFound.get(1).getTotalTransactionAmount(), is(5));

    }

    @Test
    public void givenEmptyDatabase_whenReportGeneratedForAllProductsForAClient_thenReturnEmptyListOfResponseTuple() {

        // given
        when(futuresTransactionRepository.findByClientNumber("1234")).thenReturn(Collections.EMPTY_LIST);

        // when
        List<ResponseTuple> transactionsFound = transactionsReportService.generateReport("1234");

        // then
        assertThat(transactionsFound.size(), is(0));
    }

    @Test
    public void givenOneProductForAClientInDatabase_whenReportGeneratedForAllProductsForAClient_thenReturnOneRecordOfResponseTuple() {

        // given
        setUpOneProductForAClientInDatabase();

        // when
        List<ResponseTuple> transactionsFound = transactionsReportService.generateReport("1234");

        // then
        assertThat(transactionsFound.size(), is(1));

        assertThat(transactionsFound.get(0).getClientType(), is("315"));
        assertThat(transactionsFound.get(0).getClientNumber(), is("1234"));
        assertThat(transactionsFound.get(0).getAccountNumber(), is("001"));
        assertThat(transactionsFound.get(0).getSubAccountNumber(), is("002"));
        assertThat(transactionsFound.get(0).getProductGroupCode(), is("FU"));
        assertThat(transactionsFound.get(0).getExchangeCode(), is("SGX"));
        assertThat(transactionsFound.get(0).getSymbol(), is("N1"));
        assertThat(transactionsFound.get(0).getTotalTransactionAmount(), is(100));
    }

    @Test
    public void givenTwoIdenticalProductsForAClientInDatabase_whenReportGeneratedForAllProductsForAClient_thenReturnOneRecordOfResponseTuple() {

        // given
        setUpTwoIdenticalProductsForAClientInDatabase();

        // when
        List<ResponseTuple> transactionsFound = transactionsReportService.generateReport("1234");

        // then
        assertThat(transactionsFound.size(), is(1));

        assertThat(transactionsFound.get(0).getClientType(), is("315"));
        assertThat(transactionsFound.get(0).getClientNumber(), is("1234"));
        assertThat(transactionsFound.get(0).getAccountNumber(), is("001"));
        assertThat(transactionsFound.get(0).getSubAccountNumber(), is("002"));
        assertThat(transactionsFound.get(0).getProductGroupCode(), is("FU"));
        assertThat(transactionsFound.get(0).getExchangeCode(), is("SGX"));
        assertThat(transactionsFound.get(0).getSymbol(), is("N1"));
        assertThat(transactionsFound.get(0).getTotalTransactionAmount(), is(105));
    }

    @Test
    public void givenTwoDistinctProductsForAClientInDatabase_whenReportGeneratedForAllProductsForAClient_thenReturnTwoRecordsOfResponseTuple() {

        // given
        setUpTwoDistinctProductsForAClientInDatabase();

        // when
        List<ResponseTuple> transactionsFound = transactionsReportService.generateReport("1234");

        // then
        assertThat(transactionsFound.size(), is(2));

        assertThat(transactionsFound.get(0).getClientType(), is("315"));
        assertThat(transactionsFound.get(0).getClientNumber(), is("1234"));
        assertThat(transactionsFound.get(0).getAccountNumber(), is("001"));
        assertThat(transactionsFound.get(0).getSubAccountNumber(), is("002"));
        assertThat(transactionsFound.get(0).getProductGroupCode(), is("FU"));
        assertThat(transactionsFound.get(0).getExchangeCode(), is("SGX"));
        assertThat(transactionsFound.get(0).getSymbol(), is("N1"));
        assertThat(transactionsFound.get(0).getTotalTransactionAmount(), is(100));

        assertThat(transactionsFound.get(1).getClientType(), is("315"));
        assertThat(transactionsFound.get(1).getClientNumber(), is("1234"));
        assertThat(transactionsFound.get(1).getAccountNumber(), is("002"));
        assertThat(transactionsFound.get(1).getSubAccountNumber(), is("100"));
        assertThat(transactionsFound.get(1).getProductGroupCode(), is("VC"));
        assertThat(transactionsFound.get(1).getExchangeCode(), is("LND"));
        assertThat(transactionsFound.get(1).getSymbol(), is("GL"));
        assertThat(transactionsFound.get(1).getTotalTransactionAmount(), is(5));

    }

    @Test
    public void givenEmptyDatabase_whenReportGeneratedForOneProductForAClient_thenReturnEmptyListOfResponseTuple() {

        // given
        when(futuresTransactionRepository
                .findByClientNumberAndTransactionDate("1234",
                        LocalDate.of(2014, Month.MARCH, 20)))
                .thenReturn(Collections.EMPTY_LIST);

        // when
        List<ResponseTuple> transactionsFound = transactionsReportService.generateReport("1234", "2014-03-20");

        // then
        assertThat(transactionsFound.size(), is(0));
    }

    @Test
    public void givenOneProductForAClientInDatabase_whenReportGeneratedForOneProductForAClient_thenReturnOneRecordOfResponseTuple() {

        // given
        setUpOneProductForAClientInDatabase();

        // when
        List<ResponseTuple> transactionsFound = transactionsReportService.generateReport("1234", "2014-03-20");

        // then
        assertThat(transactionsFound.size(), is(1));

        assertThat(transactionsFound.get(0).getClientType(), is("315"));
        assertThat(transactionsFound.get(0).getClientNumber(), is("1234"));
        assertThat(transactionsFound.get(0).getAccountNumber(), is("001"));
        assertThat(transactionsFound.get(0).getSubAccountNumber(), is("002"));
        assertThat(transactionsFound.get(0).getProductGroupCode(), is("FU"));
        assertThat(transactionsFound.get(0).getExchangeCode(), is("SGX"));
        assertThat(transactionsFound.get(0).getSymbol(), is("N1"));
        assertThat(transactionsFound.get(0).getTotalTransactionAmount(), is(100));
    }

    @Test
    public void givenTwoIdenticalProductsForAClientInDatabase_whenReportGeneratedForOneProductForAClient_thenReturnOneRecordOfResponseTuple() {

        // given
        setUpTwoIdenticalProductsForAClientInDatabase();

        // when
        List<ResponseTuple> transactionsFound = transactionsReportService.generateReport("1234", "2014-03-20");

        // then
        assertThat(transactionsFound.size(), is(1));

        assertThat(transactionsFound.get(0).getClientType(), is("315"));
        assertThat(transactionsFound.get(0).getClientNumber(), is("1234"));
        assertThat(transactionsFound.get(0).getAccountNumber(), is("001"));
        assertThat(transactionsFound.get(0).getSubAccountNumber(), is("002"));
        assertThat(transactionsFound.get(0).getProductGroupCode(), is("FU"));
        assertThat(transactionsFound.get(0).getExchangeCode(), is("SGX"));
        assertThat(transactionsFound.get(0).getSymbol(), is("N1"));
        assertThat(transactionsFound.get(0).getTotalTransactionAmount(), is(105));
    }

    @Test
    public void givenTwoDistinctProductsForAClientInDatabase_whenReportGeneratedForOneProductForAClient_thenReturnTwoRecordsOfResponseTuple() {

        // given
        setUpTwoDistinctProductsForAClientInDatabase();

        // when
        List<ResponseTuple> transactionsFound = transactionsReportService.generateReport("1234", "2014-03-20");

        // then
        assertThat(transactionsFound.size(), is(2));

        assertThat(transactionsFound.get(0).getClientType(), is("315"));
        assertThat(transactionsFound.get(0).getClientNumber(), is("1234"));
        assertThat(transactionsFound.get(0).getAccountNumber(), is("001"));
        assertThat(transactionsFound.get(0).getSubAccountNumber(), is("002"));
        assertThat(transactionsFound.get(0).getProductGroupCode(), is("FU"));
        assertThat(transactionsFound.get(0).getExchangeCode(), is("SGX"));
        assertThat(transactionsFound.get(0).getSymbol(), is("N1"));
        assertThat(transactionsFound.get(0).getTotalTransactionAmount(), is(100));

        assertThat(transactionsFound.get(1).getClientType(), is("315"));
        assertThat(transactionsFound.get(1).getClientNumber(), is("1234"));
        assertThat(transactionsFound.get(1).getAccountNumber(), is("002"));
        assertThat(transactionsFound.get(1).getSubAccountNumber(), is("100"));
        assertThat(transactionsFound.get(1).getProductGroupCode(), is("VC"));
        assertThat(transactionsFound.get(1).getExchangeCode(), is("LND"));
        assertThat(transactionsFound.get(1).getSymbol(), is("GL"));
        assertThat(transactionsFound.get(1).getTotalTransactionAmount(), is(5));

    }

    @Test
    public void givenSomeRecordsInDatabase_whenCSVReportGeneratedForAClient_thenReturnTheOutputCSVFile() throws Exception {

        setUpTemporaryFileFixture();
        setUpTwoDistinctProductsForAClientInDatabase();

        // when
        File csvReportFile = transactionsReportService.generateReportCSV("1234");

        // then
        assertThat(csvReportFile.getName(), is("Output.csv"));
        assertNotNull(csvReportFile.getTotalSpace());
    }

    @Test
    public void givenSomeRecordsInDatabase_whenCSVReportGeneratedForAGivenTransactionDateForAClient_thenReturnTheOutputCSVFile() throws Exception {

        setUpTemporaryFileFixture();
        setUpTwoDistinctProductsForAClientInDatabase();

        // when
        File csvReportFile = transactionsReportService.generateReportCSV("1234", "2014-03-20");

        // then
        assertThat(csvReportFile.getName(), is("Output.csv"));
        assertNotNull(csvReportFile.getTotalSpace());
    }

    private void setUpTemporaryFileFixture() throws Exception {
        final File tempFile = temporaryFolder.newFile("Output.csv");
        FileUtils.write(tempFile, "\"ACCOUNT NUMBER\",\"CLIENT NUMBER\",\"CLIENT TYPE\",\"EXCHANGE CODE\",\"PRODUCT GROUP CODE\",\"SUB ACCOUNT NUMBER\",\"SYMBOL\",\"TOTAL TRANSACTION AMOUNT\"\n" +
                "\"0003\",\"1234\",\"315\",\"CME\",\"FU\",\"0001\",\"N1\",\"570\"\n" +
                "\"0003\",\"1234\",\"315\",\"CME\",\"FU\",\"0001\",\"NK.\",\"-430\"", Charset.defaultCharset());
        when(fileHandlerService.writeCsvFileToDownloads(anyList())).thenReturn(tempFile);
    }

    private void setUpOneProductForAClientInDatabase() {
        List<FuturesTransaction> transactions = new ArrayList<>(
                List.of(new FuturesTransaction(
                                "315",
                                "1234",
                                "001",
                                "002",
                                "FU",
                                "SGX",
                                "N1",
                                LocalDate.of(2012, Month.JULY, 07),
                                LocalDate.of(2010, Month.MARCH, 18),
                                200,
                                100
                        )
                ));
        when(futuresTransactionRepository.findAll()).thenReturn(transactions);
        when(futuresTransactionRepository.findByClientNumber(anyString())).thenReturn(transactions);
        when(futuresTransactionRepository.findByClientNumberAndTransactionDate(anyString(), any(LocalDate.class))).thenReturn(transactions);
    }

    private void setUpTwoIdenticalProductsForAClientInDatabase() {
        List<FuturesTransaction> transactions = new ArrayList<>(
                List.of(new FuturesTransaction(
                        "315",
                        "1234",
                        "001",
                        "002",
                        "FU",
                        "SGX",
                        "N1",
                        LocalDate.of(2012, Month.JULY, 07),
                        LocalDate.of(2010, Month.MARCH, 18),
                        200,
                        100
                ), new FuturesTransaction(
                        "315",
                        "1234",
                        "001",
                        "002",
                        "FU",
                        "SGX",
                        "N1",
                        LocalDate.of(2015, Month.JULY, 18),
                        LocalDate.of(2014, Month.MARCH, 20),
                        8,
                        3))
        );
        when(futuresTransactionRepository.findAll()).thenReturn(transactions);
        when(futuresTransactionRepository.findByClientNumber(anyString())).thenReturn(transactions);
        when(futuresTransactionRepository.findByClientNumberAndTransactionDate(anyString(), any(LocalDate.class))).thenReturn(transactions);
    }


    private void setUpTwoDistinctProductsForAClientInDatabase() {
        List<FuturesTransaction> transactions = new ArrayList<>(
                List.of(new FuturesTransaction(
                        "315",
                        "1234",
                        "001",
                        "002",
                        "FU",
                        "SGX",
                        "N1",
                        LocalDate.of(2012, Month.JULY, 07),
                        LocalDate.of(2010, Month.MARCH, 18),
                        200,
                        100
                ), new FuturesTransaction(
                        "315",
                        "1234",
                        "002",
                        "100",
                        "VC",
                        "LND",
                        "GL",
                        LocalDate.of(2015, Month.JULY, 18),
                        LocalDate.of(2014, Month.MARCH, 20),
                        8,
                        3))
        );
        when(futuresTransactionRepository.findAll()).thenReturn(transactions);
        when(futuresTransactionRepository.findByClientNumber(anyString())).thenReturn(transactions);
        when(futuresTransactionRepository.findByClientNumberAndTransactionDate(anyString(), any(LocalDate.class))).thenReturn(transactions);
    }

}