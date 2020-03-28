package com.github.wirtsleg.etf.grabber.domain;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Data
public class StockDividends {
    private String symbol;

    private List<Dividend> historical;

    @Data
    public static class Dividend {
        private LocalDate date;
        private String label;
        private BigDecimal adjDividend;
    }
}
