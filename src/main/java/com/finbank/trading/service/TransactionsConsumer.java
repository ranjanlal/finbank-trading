package com.finbank.trading.service;

import com.finbank.trading.config.ApplicationProperties;
import com.finbank.trading.config.TransactionConfig;
import com.finbank.trading.dao.FuturesTransaction;
import com.finbank.trading.dao.FuturesTransactionRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import java.util.Map;

import static com.finbank.trading.util.AppUtils.parseDate;
import static com.finbank.trading.util.Constants.*;

@Slf4j
@Service
public class TransactionsConsumer {

    private final Map<String, TransactionConfig> config;

    @Autowired
    private FuturesTransactionRepository futuresTransactionRepository;

    @Autowired
    private final ApplicationProperties applicationProperties;

    public TransactionsConsumer(final ApplicationProperties applicationProperties) {
        this.applicationProperties = applicationProperties;
        this.config = this.applicationProperties.getMetadata();
    }

    @KafkaListener(topics = TOPIC, groupId = TOPIC_GROUP_ID)
    public void consumeTransaction(final String message) {

        log.info(String.format("#### -> Consumed message -> %s", message));

        try {

            final FuturesTransaction futuresTransaction = new FuturesTransaction(parseField(message, RECORD_CODE),
                    parseField(message, CLIENT_NUMBER), parseField(message, ACCOUNT_NUMBER),
                    parseField(message, SUB_ACCOUNT_NUMBER), parseField(message, PRODUCT_GROUP_CODE),
                    parseField(message, EXCHANGE_CODE), parseField(message, SYMBOL),
                    parseDate(parseField(message, EXPIRATION_DATE), DATE_PATTERN_YYYYMMDD),
                    parseDate(parseField(message, TRANSACTION_DATE), DATE_PATTERN_YYYYMMDD),
                    Integer.parseInt(parseField(message, QUANTITY_LONG)),
                    Integer.parseInt(parseField(message, QUANTITY_SHORT)));

            futuresTransactionRepository.save(futuresTransaction);

        } catch (final DataIntegrityViolationException dive) {
            log.error("Skipping duplicate record : " + dive.getRootCause());
        } catch (final NumberFormatException nfe) {
            log.error("Error = " + nfe.getMessage());
        } catch (final Exception ex) {
            log.error("Error = " + ex.getMessage());
        }

    }

    private String parseField(final String message, final String fieldName) {
        return message.substring(config.get(fieldName).getBeginIndex() - 1, config.get(fieldName).getEndIndex());
    }
}

