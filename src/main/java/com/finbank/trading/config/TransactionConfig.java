package com.finbank.trading.config;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Getter
@Setter
@ToString
@ConfigurationProperties
public class TransactionConfig {
    private int ref;
    private String fieldName;
    private String description;
    private int length;
    private int beginIndex;
    private int endIndex;
}