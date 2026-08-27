package com.coursework.montecarlo.model;

public class Statistics {
    public static final double TRUE_PI = 3.1415926535;

    private int generatedPoints;
    private int insideCirclePoints;

    public void reset() {
        generatedPoints = 0;
        insideCirclePoints = 0;
    }

    public void update(Point point) {
        generatedPoints++;
        if (point.isInsideCircle()) {
            insideCirclePoints++;
        }
    }

    public int getGeneratedPoints() {
        return generatedPoints;
    }

    public int getInsideCirclePoints() {
        return insideCirclePoints;
    }

    public double calculatePi() {
        if (generatedPoints == 0) {
            return 0.0;
        }
        return 4.0 * insideCirclePoints / generatedPoints;
    }

    public double calculateError() {
        if (generatedPoints == 0) {
            return 0.0;
        }
        return Math.abs(calculatePi() - TRUE_PI);
    }
}
