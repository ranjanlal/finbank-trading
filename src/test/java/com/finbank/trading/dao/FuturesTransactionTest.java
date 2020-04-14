package com.finbank.trading.dao;

import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.junit4.SpringRunner;

import java.time.LocalDate;
import java.time.Month;
import java.util.List;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

@RunWith(SpringJUnit4ClassRunner.class)
@DataJpaTest
public class FuturesTransactionTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private FuturesTransactionRepository futuresTransactionRepository;

    @Test
    public void givenNoTransactionPersisted_whenFindByClientNumber_thenReturnEmptyList() {
        // when
        List<FuturesTransaction> transactionsFound = futuresTransactionRepository.findByClientNumber("1234");
        // then
        assertThat(transactionsFound.size(), is(0));
    }

    @Test
    public void givenOneTransactionPersisted_whenFindByClientNumber_thenReturnOneTransaction() {
        // given
        FuturesTransaction transaction = new FuturesTransaction("315", "1234",
                "001", "002", "FU", "SGX",
                "N1", LocalDate.of(2012, Month.JULY, 07),
                LocalDate.of(2010, Month.MARCH, 18), 100, 200);
        entityManager.persist(transaction);
        entityManager.flush();

        // when
        List<FuturesTransaction> transactionsFound = futuresTransactionRepository.findByClientNumber("1234");
        // then
        assertThat(transactionsFound.size(), is(1));

        assertThat(transactionsFound.get(0).getClientType(), is("315"));
        assertThat(transactionsFound.get(0).getClientNumber(), is("1234"));
        assertThat(transactionsFound.get(0).getAccountNumber(), is("001"));
        assertThat(transactionsFound.get(0).getSubAccountNumber(), is("002"));
        assertThat(transactionsFound.get(0).getTransactionDate(), is(LocalDate.of(2010, Month.MARCH, 18)));

    }

    @Test
    public void givenOneTransactionPersisted_whenFindByUnknownClientNumber_thenReturnZeroTransaction() {
        // given
        FuturesTransaction transaction = new FuturesTransaction("315", "1234",
                "001", "002", "FU", "SGX",
                "N1", LocalDate.of(2012, Month.JULY, 07),
                LocalDate.of(2010, Month.MARCH, 18), 100, 200);
        entityManager.persist(transaction);
        entityManager.flush();

        // when
        List<FuturesTransaction> transactionsFound = futuresTransactionRepository.findByClientNumber("8899");
        // then
        assertThat(transactionsFound.size(), is(0));
    }

    @Test
    public void givenTwoTransactionsPersisted_whenFindByClientNumber_thenReturnTwoTransactions() {
        // given
        FuturesTransaction transaction1 = new FuturesTransaction(
                "315",
                "1234",
                "001",
                "002",
                "FU",
                "SGX",
                "N1",
                LocalDate.of(2012, Month.JULY, 07),
                LocalDate.of(2010, Month.MARCH, 18),
                100,
                200
        );
        FuturesTransaction transaction2 = new FuturesTransaction(
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
                3
        );
        entityManager.persist(transaction1);
        entityManager.persist(transaction2);
        entityManager.flush();

        // when
        List<FuturesTransaction> transactionsFound = futuresTransactionRepository.findByClientNumber("1234");

        // then
        assertThat(transactionsFound.size(), is(2));

        assertThat(transactionsFound.get(0).getClientType(), is("315"));
        assertThat(transactionsFound.get(0).getClientNumber(), is("1234"));
        assertThat(transactionsFound.get(0).getAccountNumber(), is("001"));
        assertThat(transactionsFound.get(0).getSubAccountNumber(), is("002"));
        assertThat(transactionsFound.get(0).getTransactionDate(), is(LocalDate.of(2010, Month.MARCH, 18)));

        assertThat(transactionsFound.get(1).getClientType(), is("315"));
        assertThat(transactionsFound.get(1).getClientNumber(), is("1234"));
        assertThat(transactionsFound.get(1).getAccountNumber(), is("002"));
        assertThat(transactionsFound.get(1).getSubAccountNumber(), is("100"));
        assertThat(transactionsFound.get(1).getExpirationDate(), is(LocalDate.of(2015, Month.JULY, 18)));
    }

    @Test
    public void givenTwoTransactionsForDifferentClientsPersisted_whenFindByOneClientNumber_thenReturnOneTransaction() {
        // given
        FuturesTransaction transaction1 = new FuturesTransaction(
                "315",
                "1234",
                "001",
                "002",
                "FU",
                "SGX",
                "N1",
                LocalDate.of(2012, Month.JULY, 07),
                LocalDate.of(2010, Month.MARCH, 18),
                100,
                200
        );
        FuturesTransaction transaction2 = new FuturesTransaction(
                "315",
                "6789",
                "002",
                "100",
                "VC",
                "LND",
                "GL",
                LocalDate.of(2015, Month.JULY, 18),
                LocalDate.of(2014, Month.MARCH, 20),
                8,
                3
        );
        entityManager.persist(transaction1);
        entityManager.persist(transaction2);
        entityManager.flush();

        // when
        List<FuturesTransaction> transactionsFound = futuresTransactionRepository.findByClientNumber("1234");

        // then
        assertThat(transactionsFound.size(), is(1));

        assertThat(transactionsFound.get(0).getClientType(), is("315"));
        assertThat(transactionsFound.get(0).getClientNumber(), is("1234"));
        assertThat(transactionsFound.get(0).getAccountNumber(), is("001"));
        assertThat(transactionsFound.get(0).getSubAccountNumber(), is("002"));
        assertThat(transactionsFound.get(0).getTransactionDate(), is(LocalDate.of(2010, Month.MARCH, 18)));
    }

    @Test
    public void givenNoTransactionPersisted_whenFindByClientNumberAndTransactionDate_thenReturnEmptyList() {
        // when
        List<FuturesTransaction> transactionsFound = futuresTransactionRepository
                .findByClientNumberAndTransactionDate("1234", LocalDate.now());
        // then
        assertThat(transactionsFound.size(), is(0));
    }

    @Test
    public void givenOneTransactionPersisted_whenFindByClientNumberAndTransactionDate_thenReturnOneTransaction() {
        // given
        FuturesTransaction transaction = new FuturesTransaction("315", "1234",
                "001", "002", "FU", "SGX",
                "N1", LocalDate.of(2012, Month.JULY, 07),
                LocalDate.of(2010, Month.MARCH, 18), 100, 200);
        entityManager.persist(transaction);
        entityManager.flush();

        // when
        List<FuturesTransaction> transactionsFound = futuresTransactionRepository
                .findByClientNumberAndTransactionDate("1234", LocalDate.of(2010, Month.MARCH, 18));
        // then
        assertThat(transactionsFound.size(), is(1));

        assertThat(transactionsFound.get(0).getClientType(), is("315"));
        assertThat(transactionsFound.get(0).getClientNumber(), is("1234"));
        assertThat(transactionsFound.get(0).getAccountNumber(), is("001"));
        assertThat(transactionsFound.get(0).getSubAccountNumber(), is("002"));
        assertThat(transactionsFound.get(0).getTransactionDate(), is(LocalDate.of(2010, Month.MARCH, 18)));

    }

    @Test
    public void givenOneTransactionPersisted_whenFindByUnknownClientNumberAndTransactionDate_thenReturnZeroTransaction() {
        // given
        FuturesTransaction transaction = new FuturesTransaction("315", "1234",
                "001", "002", "FU", "SGX",
                "N1", LocalDate.of(2012, Month.JULY, 07),
                LocalDate.of(2010, Month.MARCH, 18), 100, 200);
        entityManager.persist(transaction);
        entityManager.flush();

        // when unknkown client number
        List<FuturesTransaction> transactionsFound = futuresTransactionRepository.
                findByClientNumberAndTransactionDate("8800", LocalDate.of(2010, Month.MARCH, 18));
        // then
        assertThat(transactionsFound.size(), is(0));

        // when unknown transaction date
        transactionsFound = futuresTransactionRepository.
                findByClientNumberAndTransactionDate("1234", LocalDate.now());
        // then
        assertThat(transactionsFound.size(), is(0));
    }

    @Test
    public void givenTwoTransactionsPersisted_whenFindByClientNumberAndTransactionDate_thenReturnTwoTransactions() {
        // given
        FuturesTransaction transaction1 = new FuturesTransaction(
                "315",
                "1234",
                "001",
                "002",
                "FU",
                "SGX",
                "N1",
                LocalDate.of(2012, Month.JULY, 07),
                LocalDate.of(2014, Month.MARCH, 20),
                100,
                200
        );
        FuturesTransaction transaction2 = new FuturesTransaction(
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
                3
        );
        entityManager.persist(transaction1);
        entityManager.persist(transaction2);
        entityManager.flush();

        // when
        List<FuturesTransaction> transactionsFound = futuresTransactionRepository
                .findByClientNumberAndTransactionDate("1234", LocalDate.of(2014, Month.MARCH, 20));

        // then
        assertThat(transactionsFound.size(), is(2));

        assertThat(transactionsFound.get(0).getClientType(), is("315"));
        assertThat(transactionsFound.get(0).getClientNumber(), is("1234"));
        assertThat(transactionsFound.get(0).getAccountNumber(), is("001"));
        assertThat(transactionsFound.get(0).getSubAccountNumber(), is("002"));
        assertThat(transactionsFound.get(0).getTransactionDate(), is(LocalDate.of(2014, Month.MARCH, 20)));

        assertThat(transactionsFound.get(1).getClientType(), is("315"));
        assertThat(transactionsFound.get(1).getClientNumber(), is("1234"));
        assertThat(transactionsFound.get(1).getAccountNumber(), is("002"));
        assertThat(transactionsFound.get(1).getSubAccountNumber(), is("100"));
        assertThat(transactionsFound.get(1).getTransactionDate(), is(LocalDate.of(2014, Month.MARCH, 20)));
        assertThat(transactionsFound.get(1).getExpirationDate(), is(LocalDate.of(2015, Month.JULY, 18)));
    }

    @Test
    public void givenTwoTransactionsForDifferentClientsPersisted_whenFindByOneClientNumberAndTransactionDate_thenReturnOneTransaction() {
        // given
        FuturesTransaction transaction1 = new FuturesTransaction(
                "315",
                "1234",
                "001",
                "002",
                "FU",
                "SGX",
                "N1",
                LocalDate.of(2012, Month.JULY, 07),
                LocalDate.of(2010, Month.MARCH, 18),
                100,
                200
        );
        FuturesTransaction transaction2 = new FuturesTransaction(
                "315",
                "6789",
                "002",
                "100",
                "VC",
                "LND",
                "GL",
                LocalDate.of(2015, Month.JULY, 18),
                LocalDate.of(2014, Month.MARCH, 20),
                8,
                3
        );
        entityManager.persist(transaction1);
        entityManager.persist(transaction2);
        entityManager.flush();

        // when
        List<FuturesTransaction> transactionsFound = futuresTransactionRepository
                .findByClientNumberAndTransactionDate("6789", LocalDate.of(2014, Month.MARCH, 20));

        // then
        assertThat(transactionsFound.size(), is(1));

        assertThat(transactionsFound.get(0).getClientType(), is("315"));
        assertThat(transactionsFound.get(0).getClientNumber(), is("6789"));
        assertThat(transactionsFound.get(0).getAccountNumber(), is("002"));
        assertThat(transactionsFound.get(0).getSubAccountNumber(), is("100"));
        assertThat(transactionsFound.get(0).getExpirationDate(), is(LocalDate.of(2015, Month.JULY, 18)));
        assertThat(transactionsFound.get(0).getTransactionDate(), is(LocalDate.of(2014, Month.MARCH, 20)));
    }
}