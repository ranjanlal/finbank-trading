package com.finbank.trading.dao;

import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;

public interface FuturesTransactionRepository extends JpaRepository<FuturesTransaction, Long> {

    List<FuturesTransaction> findByClientNumber(String clientNumber);

    List<FuturesTransaction> findByClientNumberAndTransactionDate(String clientNumber, LocalDate transactionDate);
}
