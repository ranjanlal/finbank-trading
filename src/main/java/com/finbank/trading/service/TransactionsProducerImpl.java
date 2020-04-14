package com.finbank.trading.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.stream.Stream;

import static com.finbank.trading.util.Constants.TOPIC;

@Slf4j
@Service
public class TransactionsProducerImpl implements TransactionsProducer {

    @Autowired
    private KafkaTemplate<String, String> kafkaTemplate;

    @Override
    public void loadTransactions(String filePath) throws IOException {
        try (Stream<String> stream = Files.lines(Paths.get(filePath))) {
            stream.forEach(this::sendMessage);
        }
    }

    private void sendMessage(String message) {
        log.info(String.format("#### -> Producing message -> %s", message));
        this.kafkaTemplate.send(TOPIC, message);
    }

}
