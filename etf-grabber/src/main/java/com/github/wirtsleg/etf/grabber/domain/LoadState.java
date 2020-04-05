package com.github.wirtsleg.etf.grabber.domain;

import com.github.wirtsleg.etf.grabber.domain.avro.Etf;
import lombok.Getter;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import static java.util.Map.entry;

@Getter
public class LoadState {
    private final ConcurrentHashMap<String, ConcurrentHashMap<Item, Status>> state;

    public LoadState(List<Etf> etfList) {
        state = new ConcurrentHashMap<>();
        for (Etf etf : etfList) {
            var etfState = new ConcurrentHashMap<>(Map.ofEntries(
                    entry(Item.ETF_INFO, Status.NONE),
                    entry(Item.PRICES, Status.NONE),
                    entry(Item.DIVIDENDS, Status.NONE)
            ));
            state.put(etf.getSymbol(), etfState);
        }
    }

    public enum Item {
        ETF_INFO, PRICES, DIVIDENDS
    }

    public enum Status {
        NONE,
        OK,
        ERROR
    }
}
