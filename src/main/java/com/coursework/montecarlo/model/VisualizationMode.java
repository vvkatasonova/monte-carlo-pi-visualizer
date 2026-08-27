package com.coursework.montecarlo.model;

public enum VisualizationMode {
    AUTOMATIC("Автоматичний"),
    ANIMATED("Анімований");

    private final String title;

    VisualizationMode(String title) {
        this.title = title;
    }

    @Override
    public String toString() {
        return title;
    }
}
