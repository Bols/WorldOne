package no.bols.w1.physics;
//


import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.input.KeyEvent;
import javafx.stage.Stage;
import javafx.util.Pair;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class JfxVisualize extends Application {
    Map<String, List<Pair<Long, Double>>> dataMap = new HashMap<>();

    public void addDataPoint(String stat, long timeMs, double position) {
        if (dataMap.get(stat) == null) {
            dataMap.put(stat, new ArrayList<>());
        }
        dataMap.get(stat).add(new Pair(timeMs, position));
    }


    @Override
    public void start(Stage stage) {
        final NumberAxis xAxis = new NumberAxis();
        final NumberAxis yAxis = new NumberAxis();
        xAxis.setLabel("Time");
        final LineChart<Number, Number> lineChart =
                new LineChart<Number, Number>(xAxis, yAxis);
        lineChart.setCreateSymbols(false);
        Scene scene = new Scene(lineChart, 1600, 1000);
        for (String stat : dataMap.keySet()) {
            XYChart.Series positionSeries = new XYChart.Series();
            positionSeries.setName(stat);
            for (Pair<Long, Double> integerDoublePair : dataMap.get(stat)) {
                positionSeries.getData().add(new XYChart.Data(integerDoublePair.getKey() / 1000.0, integerDoublePair.getValue()));
            }
            lineChart.getData().add(positionSeries);
        }

        scene.addEventHandler(KeyEvent.KEY_PRESSED, t -> {
            stage.close();
        });
        stage.setScene(scene);
        stage.show();

    }

}
