package com.github.wirtsleg.etf.grabber.controller;

import com.github.wirtsleg.etf.grabber.domain.LoadState;
import com.github.wirtsleg.etf.grabber.service.GrabberService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@CrossOrigin
@RestController
@RequestMapping("/api/load")
@RequiredArgsConstructor
public class LoadController {
    private final GrabberService grabberService;

    @PostMapping("/all")
    public void load() {
        grabberService.loadAll();
    }

    @GetMapping("/state")
    public Map<?, ?> state() {
        LoadState state = grabberService.getLoadState();
        return state == null ? Map.of() : state.getState();
    }
}
