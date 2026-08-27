package com.coursework.montecarlo.model;

public class SimulationResult {
    private final int totalPoints;
    private final int insideCirclePoints;
    private final double piEstimate;
    private final double error;
    private final long runtimeMillis;

    public SimulationResult(int totalPoints, int insideCirclePoints, double piEstimate, double error, long runtimeMillis) {
        this.totalPoints = totalPoints;
        this.insideCirclePoints = insideCirclePoints;
        this.piEstimate = piEstimate;
        this.error = error;
        this.runtimeMillis = runtimeMillis;
    }

    public int getTotalPoints() { return totalPoints; }
    public int getInsideCirclePoints() { return insideCirclePoints; }
    public double getPiEstimate() { return piEstimate; }
    public double getError() { return error; }
    public long getRuntimeMillis() { return runtimeMillis; }
}
