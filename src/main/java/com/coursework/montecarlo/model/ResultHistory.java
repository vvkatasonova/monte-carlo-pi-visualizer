package com.coursework.montecarlo.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ResultHistory {
    private final List<SimulationResult> results = new ArrayList<>();

    public void addResult(SimulationResult result) {
        results.add(result);
    }

    public void clear() {
        results.clear();
    }

    public List<SimulationResult> getResults() {
        return Collections.unmodifiableList(results);
    }
}
