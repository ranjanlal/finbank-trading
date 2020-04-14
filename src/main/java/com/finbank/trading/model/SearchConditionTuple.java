package com.finbank.trading.model;

import lombok.*;

@Getter
@Setter
@EqualsAndHashCode
@ToString
@AllArgsConstructor
public class SearchConditionTuple {

    private String clientType;

    private String clientNumber;

    private String accountNumber;

    private String subAccountNumber;

    private String productGroupCode;

    private String exchangeCode;

    private String symbol;
}
