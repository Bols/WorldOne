package no.bols.w1.physics;
//


import javafx.application.Application;
import javafx.geometry.Orientation;
import javafx.scene.Scene;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.FlowPane;
import javafx.stage.Stage;
import javafx.util.Pair;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class JfxVisualize extends Application {
    Map<String, Map<Integer, List<Pair<Long, Double>>>> dataMap = new HashMap<>();

    public void addDataPoint(String stat, int iteration, long timeMs, double position) {
        if (dataMap.get(stat) == null) {
            dataMap.put(stat, new HashMap<>());
        }
        if (dataMap.get(stat).get(iteration) == null) {
            dataMap.get(stat).put(iteration, new ArrayList<>());
        }
        dataMap.get(stat).get(iteration).add(new Pair(timeMs, position));
    }


    @Override
    public void start(Stage stage) {
        List<LineChart> stats = new ArrayList<>();
        for (String stat : dataMap.keySet()) {
            final NumberAxis xAxis = new NumberAxis();
            final NumberAxis yAxis = new NumberAxis();
            xAxis.setLabel("Time");
            yAxis.setLabel(stat);
            final LineChart<Number, Number> lineChart =
                    new LineChart<>(xAxis, yAxis);
            lineChart.setCreateSymbols(false);

            for (int iteration : dataMap.get(stat).keySet()) {
                XYChart.Series positionSeries = new XYChart.Series();
                positionSeries.setName(String.valueOf(iteration));
                for (Pair<Long, Double> integerDoublePair : dataMap.get(stat).get(iteration)) {
                    positionSeries.getData().add(new XYChart.Data(integerDoublePair.getKey() / 1000.0, integerDoublePair.getValue()));
                }
                lineChart.getData().add(positionSeries);
            }
            stats.add(lineChart);
        }

        FlowPane root = new FlowPane(Orientation.VERTICAL);
        root.getChildren().addAll(stats);
        Scene scene = new Scene(root, 1600, 1000);
        scene.addEventHandler(KeyEvent.KEY_PRESSED, t -> {
            stage.close();
        });
        stage.setScene(scene);
        stage.show();

    }

}
