package com.github.wirtsleg.etf.grabber.config;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.github.wirtsleg.etf.grabber.service.FinancialModelingClient;
import feign.Feign;
import feign.Logger;
import feign.jackson.JacksonDecoder;
import feign.jackson.JacksonEncoder;
import feign.okhttp.OkHttpClient;
import feign.slf4j.Slf4jLogger;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class HttpConfig {


    @Bean
    public ObjectMapper isoObjectMapper() {
        ObjectMapper objectMapper = new ObjectMapper();

        objectMapper.registerModule(new JavaTimeModule());
        objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        objectMapper.setPropertyNamingStrategy(PropertyNamingStrategy.SNAKE_CASE);
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

        return objectMapper;
    }

    @Bean
    public FinancialModelingClient redditClient(ObjectMapper isoObjectMapper) {
        return Feign.builder()
                .client(new OkHttpClient())
                .encoder(new JacksonEncoder(isoObjectMapper))
                .decoder(new JacksonDecoder(isoObjectMapper))
                .logger(new Slf4jLogger(FinancialModelingClient.class))
                .logLevel(Logger.Level.FULL)
                .target(FinancialModelingClient.class, "https://financialmodelingprep.com");
    }
}
