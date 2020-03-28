package com.github.wirtsleg.etf.grabber.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@Component
@ConfigurationProperties("spring.kafka.topics")
public class TopicProps {
    private String etfInfo;
    private String price;
    private String dividend;
}
