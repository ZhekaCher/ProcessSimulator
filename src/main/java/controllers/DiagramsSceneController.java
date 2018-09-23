package controllers;

import data.Process;
import data.SystemInformation;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.PieChart;
import javafx.scene.chart.XYChart;

import java.util.ArrayList;

public class DiagramsSceneController {

    @FXML
    private PieChart pieChart;

    @FXML
    private BarChart<String, Integer> barChart;


    @FXML
    void initialize() {
        refreshAll();
    }

    @FXML
    void refreshPieChart() {
        ObservableList<PieChart.Data> pieChartData =
                FXCollections.observableArrayList(
                        new PieChart.Data("Ready", SystemInformation.getReadyList().size()),
                        new PieChart.Data("Ready Suspended", SystemInformation.getReadySuspendedList().size()),
                        new PieChart.Data("Blocked", SystemInformation.getBlockedList().size()),
                        new PieChart.Data("Blocked Suspended", SystemInformation.getBlockedSuspendedList().size()),
                        new PieChart.Data("Exit", SystemInformation.getExitList().size())
                );
        pieChart.setData(pieChartData);
    }


    @FXML
    void refreshBarChart() {
        int lowCount = 0;
        int mediumCount = 0;
        int highCount = 0;
        ArrayList <Process> processes = new ArrayList();
        processes.addAll(SystemInformation.getReadyList());
        processes.addAll(SystemInformation.getReadySuspendedList());
        for (Process process : processes){
            System.out.println(process.getPriority());
            switch (process.getPriority()) {
                case 0:
                    lowCount++;
                    break;
                case 1:
                    mediumCount++;
                    break;
                case 2:
                    highCount++;
                    break;
            }
        }
        barChart.getData().clear();

        XYChart.Series<String, Integer> low = new XYChart.Series<>();
        low.setName("Low");
        low.getData().add(new XYChart.Data<String, Integer>("Low", lowCount));

        XYChart.Series<String, Integer> medium = new XYChart.Series<>();
        medium.setName("Medium");
        medium.getData().add(new XYChart.Data<String, Integer>("Medium", mediumCount));

        XYChart.Series<String, Integer> high = new XYChart.Series<>();
        high.setName("High");
        high.getData().add(new XYChart.Data<String, Integer>("High", highCount));

        barChart.getData().addAll(low, medium, high);
    }

    @FXML
    void refreshAll() {
        refreshPieChart();
        refreshBarChart();
    }
}
