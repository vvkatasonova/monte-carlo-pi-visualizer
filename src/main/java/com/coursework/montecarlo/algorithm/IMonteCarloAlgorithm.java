package com.coursework.montecarlo.algorithm;

import com.coursework.montecarlo.model.IterationState;
import com.coursework.montecarlo.model.SimulationParameters;
import com.coursework.montecarlo.model.SimulationResult;

public interface IMonteCarloAlgorithm {
    void initialize(SimulationParameters parameters);
    IterationState nextStep();
    boolean isFinished();
    SimulationResult createResult();
}
