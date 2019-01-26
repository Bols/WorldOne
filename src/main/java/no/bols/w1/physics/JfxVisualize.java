package no.bols.w1.physics;
//


import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.stage.Stage;
import javafx.util.Pair;

import java.util.ArrayList;
import java.util.List;

public class JfxVisualize extends Application {
    List<Pair<Long, Double>> dataList = new ArrayList<>();

    public void addDataPoint(long timeMs, double position) {
        dataList.add(new Pair(timeMs, position));
    }


    @Override
    public void start(Stage stage) {
        stage.setTitle("Oneleg performance");
        //defining the axes
        final NumberAxis xAxis = new NumberAxis();
        final NumberAxis yAxis = new NumberAxis();
        xAxis.setLabel("Time");
        final LineChart<Number, Number> lineChart =
                new LineChart<Number, Number>(xAxis, yAxis);


        XYChart.Series positionSeries = new XYChart.Series();
        positionSeries.setName("oneLeg Position");
        for (Pair<Long, Double> integerDoublePair : dataList) {
            positionSeries.getData().add(new XYChart.Data(integerDoublePair.getKey() / 1000.0, integerDoublePair.getValue()));
        }
        Scene scene = new Scene(lineChart, 1600, 1000);
        lineChart.getData().add(positionSeries);

        stage.setScene(scene);
        stage.show();

    }

}
