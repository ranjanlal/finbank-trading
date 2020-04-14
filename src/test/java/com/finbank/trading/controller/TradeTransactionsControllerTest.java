package com.finbank.trading.controller;

import com.finbank.trading.model.ResponseTuple;
import com.finbank.trading.service.FileHandlerService;
import com.finbank.trading.service.TransactionsReportService;
import com.finbank.trading.util.Messages;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@WebMvcTest(TradeTransactionsController.class)
public class TradeTransactionsControllerTest {

    @Autowired
    private MockMvc mvc;


    @MockBean
    Messages messages;

    @MockBean
    private TransactionsReportService transactionsReportService;

    @MockBean
    private FileHandlerService fileHandlerService;

    @Test
    public void givenAWelcomeMessage_whenGetRooAPICalled_thenReturnJsonObjectOfWelcome() throws Exception {

        // given
        given(messages.get("welcome.message")).willReturn("Welcome to API");

        // when then
        mvc.perform(
                get("/api/")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status", is("OK")))
                .andExpect(jsonPath("$.message", is("Welcome to API")));
    }


    @Test
    public void givenOneRecordInDatabase_whenGetAllReport_thenReturnJsonArrayOfTransactionSummary() throws Exception {

        // given
        List<ResponseTuple> responseTuples = setUpOneProductForAClientInResponseTuple();

        given(transactionsReportService.generateReport()).willReturn(responseTuples);

        // when then
        mvc.perform(
                get("/api/report/all")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].['CLIENT TYPE']", is("315")))
                .andExpect(jsonPath("$[0].['CLIENT NUMBER']", is("1234")))
                .andExpect(jsonPath("$[0].['ACCOUNT NUMBER']", is("001")))
                .andExpect(jsonPath("$[0].['SUB ACCOUNT NUMBER']", is("002")))
                .andExpect(jsonPath("$[0].['PRODUCT GROUP CODE']", is("FU")))
                .andExpect(jsonPath("$[0].['EXCHANGE CODE']", is("SGX")))
                .andExpect(jsonPath("$[0].SYMBOL", is("N1")))
                .andExpect(jsonPath("$[0].['TOTAL TRANSACTION AMOUNT']", is(10)));
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
        return responseTuples;
    }

}