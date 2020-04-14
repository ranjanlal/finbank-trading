package com.finbank.trading.service;


import com.finbank.trading.config.ApplicationProperties;
import com.finbank.trading.model.ResponseTuple;
import org.apache.commons.io.FileUtils;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Bean;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;

@RunWith(SpringJUnit4ClassRunner.class)
public class FileHandlerServiceImplTest {

    @TestConfiguration
    static class TransactionsReportServiceImplTestContextConfiguration {
        @Bean
        public FileHandlerService fileHandlerService() {
            return new FileHandlerServiceImpl();
        }
    }

    @Autowired
    private FileHandlerService fileHandlerService;

    @MockBean
    private ApplicationProperties applicationProperties;

    @MockBean
    private TransactionsProducer transactionsProducer;

    @Rule
    public TemporaryFolder temporaryFolder = new TemporaryFolder();


    @Test
    public void givenEmptyResponseTupleList_whenWriteCSVFileFromResponseTuples_thenReturnEmptyGeneratedFile() throws Exception {

        // given
        setUpOneProductForAClientInResponseTuple();

        // when
        File csvReportFile = fileHandlerService.writeCsvFileToDownloads(Collections.EMPTY_LIST);

        // then
        assertThat(csvReportFile.getName(), is("Output.csv"));
        assertNotNull(csvReportFile.getTotalSpace());

        List<String> lines = FileUtils.readLines(csvReportFile, Charset.defaultCharset());

        assertThat(lines.size(), is(0));
    }

    @Test
    public void givenNullResponseTupleList_whenWriteCSVFileFromResponseTuples_thenReturnEmptyGeneratedFile() throws Exception {

        // given
        setUpOneProductForAClientInResponseTuple();

        // when
        File csvReportFile = fileHandlerService.writeCsvFileToDownloads(null);

        // then
        assertThat(csvReportFile.getName(), is("Output.csv"));
        assertNotNull(csvReportFile.getTotalSpace());

        List<String> lines = FileUtils.readLines(csvReportFile, Charset.defaultCharset());

        assertThat(lines.size(), is(0));
    }

    @Test
    public void givenResponseTuple_whenWriteCSVFileFromResponseTuples_thenReturnGeneratedFile() throws Exception {

        // given
        List<ResponseTuple> responseTuples = setUpOneProductForAClientInResponseTuple();

        // when
        File csvReportFile = fileHandlerService.writeCsvFileToDownloads(responseTuples);

        // then
        assertThat(csvReportFile.getName(), is("Output.csv"));
        assertNotNull(csvReportFile.getTotalSpace());

        List<String> lines = FileUtils.readLines(csvReportFile, Charset.defaultCharset());

        assertThat(lines.size(), is(2));
        assertThat(lines.get(0), is("\"ACCOUNT NUMBER\",\"CLIENT NUMBER\",\"CLIENT TYPE\",\"EXCHANGE CODE\",\"PRODUCT GROUP CODE\",\"SUB ACCOUNT NUMBER\",\"SYMBOL\",\"TOTAL TRANSACTION AMOUNT\""));
        assertThat(lines.get(1), is("\"001\",\"1234\",\"315\",\"SGX\",\"FU\",\"002\",\"N1\",\"10\""));
    }


    @Test
    public void givenAAAAResponseTuple_whenWriteCSVFileFromResponseTuples_thenReturnGeneratedFile() throws Exception {

        // given
        when(applicationProperties.getUploadDirectory()).thenReturn(temporaryFolder.getRoot().getAbsolutePath() + "/");
        doNothing().when(transactionsProducer).loadTransactions(anyString());

        MockMultipartFile mockMultipartFile = new MockMultipartFile(
                "user-file",
                "Upload.txt",
                "text/plain",
                "test data".getBytes());

        // when
        String uploadedFileName = fileHandlerService.storeFile(mockMultipartFile);

        // then
        assertThat(uploadedFileName, is("Upload.txt"));

        File[] files = temporaryFolder.getRoot().listFiles();

        assertThat(files.length, is(1));
        assertThat(files[0].getName(), is("Upload.txt"));

        List<String> lines = FileUtils.readLines(files[0], Charset.defaultCharset());

        assertThat(lines.size(), is(1));
        assertThat(lines.get(0), is("test data"));

    }


    private List<ResponseTuple> setUpOneProductForAClientInResponseTuple() throws IOException {
        List<ResponseTuple> responseTuples = new ArrayList<>(
                List.of(new ResponseTuple(
                                "315",
                                "1234",
                                "001",
                                "002",
                                "FU",
                                "SGX",
                                "N1",
                                10
                        )
                ));
        when(applicationProperties.getDownloadDirectory()).thenReturn(temporaryFolder.newFolder("output").getAbsolutePath() + "/");
        when(applicationProperties.getDownloadFleName()).thenReturn("Output.csv");
        return responseTuples;
    }

}