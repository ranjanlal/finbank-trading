package com.finbank.trading.dao;

import lombok.*;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import java.time.LocalDate;

@ToString
@Getter
@Setter
@NoArgsConstructor
@RequiredArgsConstructor
@Entity
public class FuturesTransaction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Client fields
    @NonNull
    private String clientType;

    @NonNull
    private String clientNumber;

    @NonNull
    private String accountNumber;

    @NonNull
    private String subAccountNumber;

    // Product fields
    @NonNull
    private String productGroupCode;

    @NonNull
    private String exchangeCode;

    @NonNull
    private String symbol;

    @NonNull
    private LocalDate expirationDate;

    @NonNull
    private LocalDate transactionDate;

    // Amount fields
    @NonNull
    private int quantityLong;

    @NonNull
    private int quantityShort;

}
