package com.github.wirtsleg.etf.grabber.domain;

import lombok.Data;

@Data
public class Etf {
    private String symbol;
    private String name;
    private String currency;
    private String stockExchange;
    private String exchangeShortName;
}
