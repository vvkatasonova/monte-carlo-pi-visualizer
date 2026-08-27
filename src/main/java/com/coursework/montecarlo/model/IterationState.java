package com.coursework.montecarlo.model;

public class IterationState {
    private final Point point;
    private final int generatedPoints;
    private final int insideCirclePoints;
    private final double piEstimate;
    private final double error;
    private final boolean finished;

    public IterationState(Point point, int generatedPoints, int insideCirclePoints,
                          double piEstimate, double error, boolean finished) {
        this.point = point;
        this.generatedPoints = generatedPoints;
        this.insideCirclePoints = insideCirclePoints;
        this.piEstimate = piEstimate;
        this.error = error;
        this.finished = finished;
    }

    public Point getPoint() { return point; }
    public int getGeneratedPoints() { return generatedPoints; }
    public int getInsideCirclePoints() { return insideCirclePoints; }
    public double getPiEstimate() { return piEstimate; }
    public double getError() { return error; }
    public boolean isFinished() { return finished; }
}
