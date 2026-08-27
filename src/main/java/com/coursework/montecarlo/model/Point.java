package com.coursework.montecarlo.model;

public class Point {
    private final double x;
    private final double y;

    public Point(double x, double y) {
        this.x = x;
        this.y = y;
    }

    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }

    public boolean isInsideCircle() {
        double dx = x - 0.5;
        double dy = y - 0.5;
        return dx * dx + dy * dy <= 0.25;
    }
}
