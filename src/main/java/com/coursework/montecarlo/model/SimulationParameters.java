package com.coursework.montecarlo.model;

public class SimulationParameters {
    private final int totalPoints;
    private final int animationSpeed;
    private final VisualizationMode mode;

    public SimulationParameters(int totalPoints, int animationSpeed, VisualizationMode mode) {
        if (totalPoints < 100 || totalPoints > 100000) {
            throw new IllegalArgumentException("Кількість точок має бути від 100 до 100000.");
        }
        this.totalPoints = totalPoints;
        this.animationSpeed = animationSpeed;
        this.mode = mode;
    }

    public int getTotalPoints() {
        return totalPoints;
    }

    public int getAnimationSpeed() {
        return animationSpeed;
    }

    public VisualizationMode getMode() {
        return mode;
    }
}
