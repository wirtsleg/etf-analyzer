package com.github.wirtsleg.etf.grabber.service;

import com.github.wirtsleg.etf.grabber.domain.Etf;
import com.github.wirtsleg.etf.grabber.domain.HistoricalPrice;
import com.github.wirtsleg.etf.grabber.domain.StockDividends;
import feign.Param;
import feign.RequestLine;

import java.util.List;

public interface FinancialModelingClient {

    @RequestLine("GET /api/v3/symbol/available-etfs")
    List<Etf> getEtfList();

    @RequestLine("GET /api/v3/historical-price-full/stock_dividend/{stock}")
    StockDividends getStockDividends(@Param("stock") String stock);

    @RequestLine("GET /api/v3/historical-price-full/etf/{stock}")
    HistoricalPrice getStockHistoricalPrice(@Param("stock") String stock);
}
