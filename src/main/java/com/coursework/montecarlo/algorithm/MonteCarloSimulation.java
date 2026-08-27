package com.coursework.montecarlo.algorithm;

import com.coursework.montecarlo.model.*;

import java.util.Random;

public class MonteCarloSimulation implements IMonteCarloAlgorithm {
    private final Random random = new Random();
    private final Statistics statistics = new Statistics();
    private SimulationParameters parameters;
    private long startTime;

    @Override
    public void initialize(SimulationParameters parameters) {
        this.parameters = parameters;
        this.statistics.reset();
        this.startTime = System.currentTimeMillis();
    }

    @Override
    public IterationState nextStep() {
        ensureInitialized();

        if (isFinished()) {
            return createState(null, true);
        }

        Point point = generatePoint();
        statistics.update(point);
        return createState(point, isFinished());
    }

    @Override
    public boolean isFinished() {
        return parameters != null && statistics.getGeneratedPoints() >= parameters.getTotalPoints();
    }

    @Override
    public SimulationResult createResult() {
        return new SimulationResult(
                statistics.getGeneratedPoints(),
                statistics.getInsideCirclePoints(),
                statistics.calculatePi(),
                statistics.calculateError(),
                System.currentTimeMillis() - startTime
        );
    }

    private Point generatePoint() {
        return new Point(random.nextDouble(), random.nextDouble());
    }

    private IterationState createState(Point point, boolean finished) {
        return new IterationState(
                point,
                statistics.getGeneratedPoints(),
                statistics.getInsideCirclePoints(),
                statistics.calculatePi(),
                statistics.calculateError(),
                finished
        );
    }

    private void ensureInitialized() {
        if (parameters == null) {
            throw new IllegalStateException("Параметри моделювання не ініціалізовано.");
        }
    }
}
