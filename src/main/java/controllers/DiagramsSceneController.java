package controllers;

import data.SystemInformation;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.chart.PieChart;

public class DiagramsSceneController {

    @FXML
    private PieChart pieChart;


    @FXML
    void initialize() {
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
    void refresh() {
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

}
