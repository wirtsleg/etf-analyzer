package com.github.wirtsleg.etf.grabber.service;

import com.github.wirtsleg.etf.grabber.config.TopicProps;
import com.github.wirtsleg.etf.grabber.domain.avro.Etf;
import com.github.wirtsleg.etf.grabber.domain.avro.HistoricalPrice;
import com.github.wirtsleg.etf.grabber.domain.LoadState;
import com.github.wirtsleg.etf.grabber.domain.LoadState.Item;
import com.github.wirtsleg.etf.grabber.domain.LoadState.Status;
import com.github.wirtsleg.etf.grabber.domain.avro.StockDividends;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

@Slf4j
@Service
@RequiredArgsConstructor
public class GrabberServiceImpl implements GrabberService {
    private final FinancialModelingClient financialModelingClient;
    private final KafkaTemplate<String, Object> kafkaTemplate;
    private final TopicProps topicProps;

    private final AtomicBoolean loading = new AtomicBoolean();
    private volatile LoadState loadState;

    @Override
    public void loadAll() {
        if (loading.compareAndSet(false, true)) {
            try {
                log.info("Loading started.");

                List<Etf> etfList = financialModelingClient.getEtfList();
                loadState = new LoadState(etfList);

                loadEtfList(etfList);
                etfList.forEach(etf -> {
                    loadHistoricalPrice(etf);
                    loadStockDividends(etf);
                });

                log.info("Loading finished.");
            } finally {
                loading.set(false);
            }
        } else {
            throw new RuntimeException("Loading is already running.");
        }
    }

    private void loadEtfList(List<Etf> etfList) {
        for (Etf etf : etfList) {
            kafkaTemplate.send(topicProps.getEtfInfo(), etf.getSymbol(), etf)
                    .addCallback(
                            result -> loadState.getState().get(etf.getSymbol()).put(Item.ETF_INFO, Status.OK),
                            ex -> loadState.getState().get(etf.getSymbol()).put(Item.ETF_INFO, Status.ERROR)
                    );
        }
    }

    private void loadHistoricalPrice(Etf etf) {
        try {
            HistoricalPrice historicalPrice = financialModelingClient.getStockHistoricalPrice(etf.getSymbol());
            kafkaTemplate.send(topicProps.getPrice(), etf.getSymbol(), historicalPrice)
                    .addCallback(
                            result -> loadState.getState().get(etf.getSymbol()).put(Item.PRICES, Status.OK),
                            ex -> loadState.getState().get(etf.getSymbol()).put(Item.PRICES, Status.ERROR)
                    );
        } catch (Exception e) {
            log.error("Failed to load historical price for etf: {}", etf.getSymbol(), e);
            loadState.getState().get(etf.getSymbol()).put(Item.PRICES, Status.ERROR);
        }
    }

    private void loadStockDividends(Etf etf) {
        try {
            StockDividends stockDividends = financialModelingClient.getStockDividends(etf.getSymbol());
            kafkaTemplate.send(topicProps.getDividend(), etf.getSymbol(), stockDividends)
                    .addCallback(
                            result -> loadState.getState().get(etf.getSymbol()).put(Item.DIVIDENDS, Status.OK),
                            ex -> loadState.getState().get(etf.getSymbol()).put(Item.DIVIDENDS, Status.ERROR)
                    );
        } catch (Exception e) {
            log.error("Failed to load stock dividends for etf: {}", etf.getSymbol(), e);
            loadState.getState().get(etf.getSymbol()).put(Item.DIVIDENDS, Status.ERROR);
        }
    }

    @Override
    public LoadState getLoadState() {
        return loadState;
    }
}
