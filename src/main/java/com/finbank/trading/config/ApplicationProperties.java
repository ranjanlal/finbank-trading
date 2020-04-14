package com.finbank.trading.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.Map;

@Getter
@Setter
@ConfigurationProperties(prefix = "trading")
public class ApplicationProperties {
    private String uploadDirectory;
    private String downloadDirectory;
    private String downloadFleName;
    private Map<String, TransactionConfig> metadata;
}