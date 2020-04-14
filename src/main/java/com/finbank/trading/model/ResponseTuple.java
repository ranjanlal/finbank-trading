package com.finbank.trading.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.opencsv.bean.CsvBindByName;
import lombok.*;

import static com.finbank.trading.util.Constants.*;

@Getter
@Setter
@EqualsAndHashCode
@ToString
@AllArgsConstructor
public class ResponseTuple {

    @JsonProperty(RESPONSE_CLIENT_TYPE)
    @CsvBindByName(column = RESPONSE_CLIENT_TYPE)
    private String clientType;

    @JsonProperty(RESPONSE_CLIENT_NUMBER)
    @CsvBindByName(column = RESPONSE_CLIENT_NUMBER)
    private String clientNumber;

    @JsonProperty(RESPONSE_ACCOUNT_NUMBER)
    @CsvBindByName(column = RESPONSE_ACCOUNT_NUMBER)
    private String accountNumber;

    @JsonProperty(RESPONSE_SUB_ACCOUNT_NUMBER)
    @CsvBindByName(column = RESPONSE_SUB_ACCOUNT_NUMBER)
    private String subAccountNumber;

    @JsonProperty(RESPONSE_PRODUCT_GROUP_CODE)
    @CsvBindByName(column = RESPONSE_PRODUCT_GROUP_CODE)
    private String productGroupCode;

    @JsonProperty(RESPONSE_EXCHANGE_CODE)
    @CsvBindByName(column = RESPONSE_EXCHANGE_CODE)
    private String exchangeCode;

    @JsonProperty(RESPONSE_SYMBOL)
    @CsvBindByName(column = RESPONSE_SYMBOL)
    private String symbol;

    @JsonProperty(RESPONSE_TOTAL_TRANSACTION_AMOUNT)
    @CsvBindByName(column = RESPONSE_TOTAL_TRANSACTION_AMOUNT)
    private int totalTransactionAmount;
}
