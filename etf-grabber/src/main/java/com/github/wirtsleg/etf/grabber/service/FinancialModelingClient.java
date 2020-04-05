package com.github.wirtsleg.etf.grabber.service;

import com.github.wirtsleg.etf.grabber.domain.avro.Etf;
import com.github.wirtsleg.etf.grabber.domain.avro.HistoricalPrice;
import com.github.wirtsleg.etf.grabber.domain.avro.StockDividends;
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
