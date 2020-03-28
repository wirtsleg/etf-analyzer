package com.github.wirtsleg.etf.grabber.service;

import com.github.wirtsleg.etf.grabber.domain.LoadState;

public interface GrabberService {

    void loadAll();

    LoadState getLoadState();

}
