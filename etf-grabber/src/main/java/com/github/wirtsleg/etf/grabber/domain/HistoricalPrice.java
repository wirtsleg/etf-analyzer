package com.github.wirtsleg.etf.grabber.domain;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Data
public class HistoricalPrice {
    private String symbol;

    private List<Price> historical;

    @Data
    public static class Price {
        private LocalDate date;
        private BigDecimal open;
        private BigDecimal high;
        private BigDecimal low;
        private BigDecimal close;
        private BigDecimal adjClose;
        private BigDecimal volume;
        private BigDecimal unadjustedVolume;
        private BigDecimal change;
        private BigDecimal changePercent;
        private BigDecimal vwap;
        private String label;
        private BigDecimal changeOverTime;
    }
}
