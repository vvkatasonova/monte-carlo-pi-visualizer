package com.coursework.montecarlo.ui;

import com.coursework.montecarlo.algorithm.MonteCarloSimulation;
import com.coursework.montecarlo.algorithm.IMonteCarloAlgorithm;
import com.coursework.montecarlo.model.*;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.TextAlignment;
import javafx.stage.Stage;
import javafx.util.Duration;

public class MainApp extends Application {
    private static final int CANVAS_SIZE = 650;

    private final IMonteCarloAlgorithm simulation = new MonteCarloSimulation();
    private final ResultHistory history = new ResultHistory();
    private SimulationResult currentResult;
    private Timeline timeline;

    private Canvas canvas;
    private GraphicsContext gc;
    private TextField pointsField;
    private ComboBox<VisualizationMode> modeBox;
    private Slider speedSlider;
    private Label speedValueLabel;
    private Button startButton;
    private Button pauseButton;
    private Button stepButton;
    private Button resetButton;
    private Button saveButton;
    private Label piLabel;
    private Label totalLabel;
    private Label insideLabel;
    private Label errorLabel;
    private XYChart.Series<Number, Number> errorSeries;

    @Override
    public void start(Stage stage) {
        BorderPane root = new BorderPane();
        root.setPadding(new Insets(18));
        root.setStyle("-fx-background-color: #f7f8fb;");

        root.setLeft(createControlPanel());
        root.setCenter(createVisualizationArea());
        root.setRight(createResultsPanel());

        drawBaseArea();

        Scene scene = new Scene(root, 1600, 900);
        stage.setTitle("Метод Монте-Карло для оцінки значення числа π");
        stage.setScene(scene);
        stage.setMaximized(true);
        stage.show();
    }

    private VBox createControlPanel() {
        Label title = createSectionTitle("ПАНЕЛЬ КЕРУВАННЯ");

        pointsField = new TextField("1000");
        pointsField.setPrefHeight(38);
        pointsField.setMaxWidth(Double.MAX_VALUE);
        pointsField.setStyle(inputStyle());

        modeBox = new ComboBox<>();
        modeBox.getItems().addAll(VisualizationMode.ANIMATED, VisualizationMode.AUTOMATIC);
        modeBox.setValue(VisualizationMode.ANIMATED);
        modeBox.setMaxWidth(Double.MAX_VALUE);
        modeBox.setPrefHeight(38);
        modeBox.setStyle(inputStyle());

        speedSlider = new Slider(1, 100, 30);
        speedSlider.setShowTickMarks(false);
        speedSlider.setShowTickLabels(false);
        speedSlider.setMaxWidth(Double.MAX_VALUE);
        speedValueLabel = new Label("30");
        speedValueLabel.setStyle("-fx-font-size: 15px; -fx-text-fill: #1f2937; -fx-font-weight: bold;");
        speedSlider.valueProperty().addListener((obs, oldValue, newValue) ->
                speedValueLabel.setText(String.valueOf(newValue.intValue()))
        );

        HBox speedTitle = new HBox(new Label("Швидкість анімації"), new Region(), speedValueLabel);
        HBox.setHgrow(speedTitle.getChildren().get(1), Priority.ALWAYS);
        speedTitle.setAlignment(Pos.CENTER_LEFT);

        HBox speedText = new HBox();
        Label slowLabel = new Label("повільно");
        Label fastLabel = new Label("швидко");
        slowLabel.setStyle(smallTextStyle());
        fastLabel.setStyle(smallTextStyle());
        Region speedSpacer = new Region();
        HBox.setHgrow(speedSpacer, Priority.ALWAYS);
        speedText.getChildren().addAll(slowLabel, speedSpacer, fastLabel);

        startButton = createPrimaryButton("Запуск");
        pauseButton = createSecondaryButton("Пауза");
        stepButton = createSecondaryButton("Крок");
        resetButton = createSecondaryButton("Скидання");
        saveButton = createSecondaryButton("Збереження результатів");

        startButton.setOnAction(e -> startSimulation());
        pauseButton.setOnAction(e -> pauseSimulation());
        stepButton.setOnAction(e -> executeManualStep());
        resetButton.setOnAction(e -> resetSimulation());
        saveButton.setOnAction(e -> saveResult());
        setInitialButtonState();

        VBox box = new VBox(18,
                title,
                createFieldBlock("Введення кількості точок \n(Від 100 до 100000)", pointsField),
                createFieldBlock("Вибір режиму візуалізації", modeBox),
                new VBox(8, speedTitle, speedSlider, speedText),
                createSpacer(22),
                startButton, pauseButton, stepButton, resetButton, saveButton
        );
        box.setPadding(new Insets(28, 28, 28, 28));
        box.setPrefWidth(360);
        box.setMinWidth(340);
        box.setMaxWidth(380);
        box.setAlignment(Pos.TOP_CENTER);
        box.setStyle(panelStyle());
        return box;
    }

    private VBox createFieldBlock(String labelText, Control control) {
        Label label = new Label(labelText);
        label.setStyle("-fx-font-size: 15px; -fx-text-fill: #1f2937;");
        VBox box = new VBox(8, label, control);
        box.setAlignment(Pos.CENTER_LEFT);
        box.setMaxWidth(Double.MAX_VALUE);
        return box;
    }

    private Button createPrimaryButton(String text) {
        Button button = new Button(text);
        button.setMaxWidth(Double.MAX_VALUE);
        button.setPrefHeight(48);
        button.setStyle("-fx-background-color: #0d6efd; -fx-text-fill: white; -fx-font-size: 16px; " +
                "-fx-font-weight: bold; -fx-background-radius: 7; -fx-cursor: hand;");
        return button;
    }

    private Button createSecondaryButton(String text) {
        Button button = new Button(text);
        button.setMaxWidth(Double.MAX_VALUE);
        button.setPrefHeight(46);
        button.setStyle("-fx-background-color: white; -fx-text-fill: #1f2937; -fx-font-size: 15px; " +
                "-fx-border-color: #d1d5db; -fx-border-radius: 7; -fx-background-radius: 7; -fx-cursor: hand;");
        return button;
    }

    private Region createSpacer(double height) {
        Region spacer = new Region();
        spacer.setPrefHeight(height);
        return spacer;
    }

    private void setInitialButtonState() {
        startButton.setDisable(false);
        pauseButton.setDisable(true);
        stepButton.setDisable(true);
        resetButton.setDisable(true);
        saveButton.setDisable(true);
    }

    private void setAnimatedRunningButtonState() {
        startButton.setDisable(true);
        pauseButton.setDisable(false);
        stepButton.setDisable(true);
        resetButton.setDisable(true);
        saveButton.setDisable(true);
    }

    private void setPausedButtonState() {
        startButton.setDisable(true);
        pauseButton.setDisable(false);
        stepButton.setDisable(false);
        resetButton.setDisable(false);
        saveButton.setDisable(true);
    }

    private void setFinishedButtonState() {
        startButton.setDisable(true);
        pauseButton.setDisable(true);
        stepButton.setDisable(true);
        resetButton.setDisable(false);
        saveButton.setDisable(false);
    }
    private VBox createVisualizationArea() {
        Label title = createSectionTitle("ВІЗУАЛІЗАЦІЯ");
        canvas = new Canvas(CANVAS_SIZE, CANVAS_SIZE);
        gc = canvas.getGraphicsContext2D();

        Label caption = new Label("Графічна область моделювання");
        caption.setStyle("-fx-font-size: 16px; -fx-text-fill: #6b7280;");

        VBox pane = new VBox(18, title, canvas, caption);
        pane.setPadding(new Insets(28, 28, 28, 28));
        pane.setAlignment(Pos.TOP_CENTER);
        pane.setStyle(panelStyle());
        BorderPane.setMargin(pane, new Insets(0, 12, 0, 12));
        return pane;
    }

    private VBox createResultsPanel() {
        Label title = createSectionTitle("РЕЗУЛЬТАТИ ОБЧИСЛЕННЯ");
        Label resultsTitle = new Label("Результати обчислення");
        resultsTitle.setStyle("-fx-font-size: 15px; -fx-text-fill: #1f2937;");

        piLabel = createResultLabel("π ≈ 0.000000", true);
        totalLabel = createResultLabel("Кількість точок: 0", false);
        insideLabel = createResultLabel("У колі: 0", false);
        errorLabel = createResultLabel("Похибка: 0.000000", false);

        VBox resultsBox = new VBox(12, resultsTitle, piLabel, totalLabel, insideLabel, errorLabel);
        resultsBox.setAlignment(Pos.CENTER);
        resultsBox.setMaxWidth(Double.MAX_VALUE);

        NumberAxis xAxis = new NumberAxis(100, 100000, 10000);
        NumberAxis yAxis = new NumberAxis();
        xAxis.setLabel("Кількість точок N");
        yAxis.setLabel("Похибка");
        xAxis.setForceZeroInRange(false);
        yAxis.setForceZeroInRange(true);

        LineChart<Number, Number> chart = new LineChart<>(xAxis, yAxis);
        chart.setCreateSymbols(true);
        chart.setAnimated(false);
        chart.setLegendVisible(false);
        chart.setPrefSize(620, 440);
        chart.setMinSize(600, 420);
        chart.setMaxSize(660, 470);

        errorSeries = new XYChart.Series<>();
        chart.getData().add(errorSeries);

        Label chartCaption = new Label("Залежність похибки від кількості точок\n(за збереженими результатами)");
        chartCaption.setAlignment(Pos.CENTER);
        chartCaption.setTextAlignment(TextAlignment.CENTER);
        chartCaption.setStyle("-fx-font-size: 15px; -fx-text-fill: #1f2937;");

        VBox box = new VBox(24, title, resultsBox, createSpacer(10), chart, chartCaption);
        box.setPadding(new Insets(28, 28, 28, 28));
        box.setPrefWidth(650);
        box.setMinWidth(620);
        box.setAlignment(Pos.TOP_CENTER);
        box.setStyle(panelStyle());
        return box;
    }

    private Label createSectionTitle(String text) {
        Label label = new Label(text);
        label.setFont(Font.font("Arial", FontWeight.BOLD, 22));
        label.setStyle("-fx-text-fill: #111827;");
        label.setAlignment(Pos.CENTER);
        label.setMaxWidth(Double.MAX_VALUE);
        return label;
    }

    private Label createResultLabel(String text, boolean main) {
        Label label = new Label(text);
        label.setAlignment(Pos.CENTER);
        label.setMaxWidth(Double.MAX_VALUE);
        label.setTextAlignment(TextAlignment.CENTER);
        if (main) {
            label.setStyle("-fx-font-size: 23px; -fx-text-fill: #174ea6; -fx-font-weight: bold;");
        } else {
            label.setStyle("-fx-font-size: 17px; -fx-text-fill: #1f2937;");
        }
        return label;
    }

    private String panelStyle() {
        return "-fx-background-color: white; -fx-background-radius: 10; -fx-border-radius: 10; " +
                "-fx-border-color: #e5e7eb; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.08), 10, 0, 0, 2);";
    }

    private String inputStyle() {
        return "-fx-font-size: 15px; -fx-background-color: white; -fx-border-color: #d1d5db; " +
                "-fx-border-radius: 6; -fx-background-radius: 6;";
    }

    private String smallTextStyle() {
        return "-fx-font-size: 13px; -fx-text-fill: #374151;";
    }

    private void startSimulation() {
        try {
            int totalPoints = Integer.parseInt(pointsField.getText().trim());
            SimulationParameters params = new SimulationParameters(totalPoints, (int) speedSlider.getValue(), modeBox.getValue());

            simulation.initialize(params);
            currentResult = null;
            pauseButton.setText("Пауза");
            drawBaseArea();
            updateLabels(0, 0, 0, 0);

            if (params.getMode() == VisualizationMode.AUTOMATIC) {
                runAutomaticMode();
                setFinishedButtonState();
            } else {
                startAnimatedMode(params);
                setAnimatedRunningButtonState();
            }
        } catch (NumberFormatException ex) {
            showError("Кількість точок має бути цілим числом.");
        } catch (IllegalArgumentException ex) {
            showError(ex.getMessage());
        }
    }

    private void runAutomaticMode() {
        while (!simulation.isFinished()) {
            IterationState state = simulation.nextStep();
            if (state.getPoint() != null) {
                drawPoint(state.getPoint());
            }
        }

        currentResult = simulation.createResult();
        updateLabels(currentResult.getTotalPoints(), currentResult.getInsideCirclePoints(), currentResult.getPiEstimate(), currentResult.getError());
    }

    private void startAnimatedMode(SimulationParameters params) {
        if (timeline != null) {
            timeline.stop();
        }
        double delay = Math.max(3, 900.0 / params.getAnimationSpeed());
        timeline = new Timeline(new KeyFrame(Duration.millis(delay), e -> executeOneStep()));
        timeline.setCycleCount(Timeline.INDEFINITE);
        timeline.play();
    }

    private void executeOneStep() {
        try {
            IterationState state = simulation.nextStep();
            if (state.getPoint() != null) {
                drawPoint(state.getPoint());
            }
            updateLabels(state.getGeneratedPoints(), state.getInsideCirclePoints(), state.getPiEstimate(), state.getError());
            if (state.isFinished()) {
                if (timeline != null) {
                    timeline.stop();
                }
                currentResult = simulation.createResult();
                updateLabels(currentResult.getTotalPoints(), currentResult.getInsideCirclePoints(), currentResult.getPiEstimate(), currentResult.getError());
                setFinishedButtonState();
            }
        } catch (IllegalStateException ex) {
            showError("Спочатку натисніть кнопку 'Запуск'.");
        }
    }

    private void executeManualStep() {
        if (timeline == null || timeline.getStatus() == Timeline.Status.RUNNING) {
            return;
        }

        executeOneStep();

        if (simulation.isFinished()) {
            currentResult = simulation.createResult();
            setFinishedButtonState();
        }
    }

    private void pauseSimulation() {
        if (timeline == null) {
            return;
        }
        if (timeline.getStatus() == Timeline.Status.RUNNING) {
            timeline.pause();
            pauseButton.setText("Продовжити");
            setPausedButtonState();
        } else {
            timeline.play();
            pauseButton.setText("Пауза");
            setAnimatedRunningButtonState();
        }
    }

    private void resetSimulation() {
        if (timeline != null) {
            timeline.stop();
        }
        pauseButton.setText("Пауза");
        currentResult = null;
        drawBaseArea();
        updateLabels(0, 0, 0, 0);
        setInitialButtonState();
    }

    private void saveResult() {
        if (currentResult == null) {
            showError("Спочатку виконайте моделювання до кінця.");
            return;
        }
        history.addResult(currentResult);
        errorSeries.getData().add(new XYChart.Data<>(currentResult.getTotalPoints(), currentResult.getError()));
    }

    private void drawBaseArea() {
        gc.setFill(Color.WHITE);
        gc.fillRect(0, 0, CANVAS_SIZE, CANVAS_SIZE);

        gc.setStroke(Color.web("#1f2937"));
        gc.setLineWidth(1.3);
        gc.strokeRect(0, 0, CANVAS_SIZE, CANVAS_SIZE);

        gc.setStroke(Color.web("#6b7280"));
        gc.setLineWidth(1.5);
        gc.strokeOval(0, 0, CANVAS_SIZE, CANVAS_SIZE);
    }

    private void drawPoint(Point point) {
        double px = point.getX() * CANVAS_SIZE;
        double py = CANVAS_SIZE - point.getY() * CANVAS_SIZE;
        gc.setFill(point.isInsideCircle() ? Color.web("#0d6efd") : Color.web("#ff3b30"));
        gc.fillOval(px - 1.35, py - 1.35, 2.7, 2.7);
    }

    private void updateLabels(int total, int inside, double pi, double error) {
        piLabel.setText(String.format("π ≈ %.9f", pi));
        totalLabel.setText("Кількість точок: " + total);
        insideLabel.setText("У колі: " + inside);
        errorLabel.setText(String.format("Похибка: %.10f", error));
    }

    private void showError(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Помилка");
        alert.setHeaderText("Некоректні дані");
        alert.setContentText(message);
        alert.showAndWait();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
