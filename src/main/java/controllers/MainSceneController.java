package controllers;

import data.Process;
import data.SystemInformation;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.Time;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;

public class MainSceneController {

    SystemInformation si;

    @FXML
    private TextField nameCreateTextField;

    @FXML
    private TextField idCreateTextField;

    @FXML
    private TextField timeCreateTextField;

    @FXML
    private TableView<Process> readyTable;

    @FXML
    private TableColumn<?, ?> readyTableIdColumn;

    @FXML
    private TableColumn<?, ?> readyTableNameColumn;

    @FXML
    private TableColumn<?, ?> readyTableTimeColumn;

    @FXML
    private TableColumn<?, ?> readyTablePriorityColumn;

    @FXML
    private TableView<Process> readySuspendedTable;

    @FXML
    private TableColumn<?, ?> readySuspendedTableIdColumn;

    @FXML
    private TableColumn<?, ?> readySuspendedTableNameColumn;

    @FXML
    private TableColumn<?, ?> readySuspendedTableTimeColumn;

    @FXML
    private TableView<Process> blockedSuspendedTable;

    @FXML
    private TableColumn<?, ?> blockedSuspendedTableIdColumn;

    @FXML
    private TableColumn<?, ?> blockedSuspendedTableNameColumn;

    @FXML
    private TableColumn<?, ?> blockedSuspendedTableTimeColumn;

    @FXML
    private TableView<Process> exitTable;

    @FXML
    private TableColumn<?, ?> exitTableIdColumn;

    @FXML
    private TableColumn<?, ?> exitTableNameColumn;

    @FXML
    private TableColumn<?, ?> exitTableTimeColumn;

    @FXML
    private TableView<Process> blockedTable;

    @FXML
    private TableColumn<?, ?> blockedTableIdColumn;

    @FXML
    private TableColumn<?, ?> blockedTableNameColumn;

    @FXML
    private TableColumn<?, ?> blockedTableTimeColumn;

    @FXML
    private ChoiceBox<String> priorityCreateChoiceBox;

    @FXML
    private CheckBox blockedCreateCheckBox;

    @FXML
    private Text runIdText;

    @FXML
    private Text runNameText;

    @FXML
    private Text runTimeText;

    @FXML
    private Text leftTimeText;

    @FXML
    private Button statisticsButton;

    @FXML
    private ProgressBar progressBar;

    @FXML
    void initialize() {
        tablesInit();
        priorityCreateChoiceBoxInit();

        execution();
    }

    @FXML
    void createNewProcess() {
        try {
            int id = Integer.valueOf(idCreateTextField.getText());
            String name = nameCreateTextField.getText();
            int time = Integer.valueOf(timeCreateTextField.getText());
            boolean blocked = blockedCreateCheckBox.isSelected();
            int priority = 0;
            switch (priorityCreateChoiceBox.getValue()) {
                case "Low":
                    priority = 0;
                    break;
                case "Medium":
                    priority = 1;
                    break;
                case "High":
                    priority = 2;
                    break;
            }

            if (si.getReadyList().size() < si.getReadyCapacity())
                si.getReadyList().add(new Process(id, name, time, blocked, priority));
            else
                si.getReadySuspendedList().add(new Process(id, name, time, blocked, priority));
        } catch (Exception e) {
            System.err.println("ERROR #1");
        }
    }

    class ProgressBarThread extends Thread {
        @Override
        public void run() {
            super.run();
            while (true) {
                try {
                    Thread.sleep(9);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                progressBar.setProgress(progressBar.getProgress() + 1 / (double) (si.getTimeoutValue() * 100));
            }
        }
    }

    @FXML
    private void execution() {
        new Thread() {
            int currentProcessTimeLeft;
            int currentProcessTimeCounter;

            @Override
            public void run() {
                ProgressBarThread progressBarThread = new ProgressBarThread();
                super.run();
                while (true) {
                    sortReadyLists();

                    //Time counter
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }

                    //Run field
                    if (si.getCurrentProcess() != null) {
                        currentProcessTimeCounter--;
                        currentProcessTimeLeft--;
                        leftTimeText.setText(String.valueOf(currentProcessTimeLeft));
                        if (currentProcessTimeCounter == 0) {
                            si.getExitList().add(si.getCurrentProcess());
                            si.setCurrentProcess(null);
                            runIdText.setText("");
                            runNameText.setText("");
                            runTimeText.setText("");
                            leftTimeText.setText("");
                            progressBarThread.stop();
                            progressBar.setProgress(0);
                        } else if (currentProcessTimeLeft == 0) {
                            si.getCurrentProcess().setTime(si.getCurrentProcess().getTime() - si.getTimeSubtract());
                            if (si.getBlockedList().size() >= si.getBlockedCapacity()) {
                                si.getBlockedSuspendedList().add(si.getCurrentProcess());
                            } else {
                                si.getBlockedList().add(si.getCurrentProcess());
                            }
                            si.setCurrentProcess(null);
                            runIdText.setText("");
                            runNameText.setText("");
                            runTimeText.setText("");
                            leftTimeText.setText("");
                            progressBarThread.stop();
                            progressBar.setProgress(0);
                        }
                    } else if (si.getReadyList().size() != 0) {
                        Process newProcess = si.getReadyList().get(0);
                        currentProcessTimeCounter = newProcess.getTime();
                        currentProcessTimeLeft = si.getTimeoutValue();
                        leftTimeText.setText(String.valueOf(currentProcessTimeLeft));
                        si.setCurrentProcess(newProcess);
                        runIdText.setText(String.valueOf(newProcess.getId()));
                        runNameText.setText(newProcess.getName());
                        runTimeText.setText(String.valueOf(newProcess.getTime()));
                        si.getReadyList().remove(0);
                        progressBarThread = new ProgressBarThread();
                        progressBarThread.start();
                    }

                    //ReadySuspendedControl
                    if (si.getReadySuspendedList().size() > 0) {
                        if (si.getReadyList().size() < si.getReadyCapacity()) {
                            si.getReadyList().add(si.getReadySuspendedList().get(0));
                            si.getReadySuspendedList().remove(0);
                        }
                    }

                }
            }
        }.start();
    }

    @FXML
    void blockedSuspendedAction() {
        if (si.getBlockedSuspendedList().size() > 0) {
            if (si.getReadyList().size() < si.getReadyCapacity()) {
                si.getReadyList().add(si.getBlockedSuspendedList().get(0));
            } else {
                si.getReadySuspendedList().add(si.getBlockedSuspendedList().get(0));
            }
            si.getBlockedSuspendedList().remove(0);
        }
    }

    @FXML
    void blockedAction() {
        if (si.getBlockedList().size() != 0) {
            if (si.getReadyList().size() < si.getReadyCapacity()) {
                si.getReadyList().add(si.getBlockedList().get(0));
            } else {
                si.getReadySuspendedList().add(si.getBlockedList().get(0));
            }
            si.getBlockedList().remove(0);
        }
    }

    private void tablesInit() {
        readyTableInit();
        readySuspendedTableInit();
        blockedTableInit();
        blockedSuspendedTableInit();
        exitTableInit();
    }

    private void readyTableInit() {
        readyTableIdColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        readyTableNameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        readyTableTimeColumn.setCellValueFactory(new PropertyValueFactory<>("time"));
        readyTablePriorityColumn.setCellValueFactory(new PropertyValueFactory<>("priority"));

        readyTable.setItems(si.getReadyList());
    }

    private void readySuspendedTableInit() {
        readySuspendedTableIdColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        readySuspendedTableNameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        readySuspendedTableTimeColumn.setCellValueFactory(new PropertyValueFactory<>("time"));

        readySuspendedTable.setItems(si.getReadySuspendedList());
    }

    private void blockedTableInit() {
        blockedTableIdColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        blockedTableNameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        blockedTableTimeColumn.setCellValueFactory(new PropertyValueFactory<>("time"));

        blockedTable.setItems(si.getBlockedList());
    }

    private void blockedSuspendedTableInit() {
        blockedSuspendedTableIdColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        blockedSuspendedTableNameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        blockedSuspendedTableTimeColumn.setCellValueFactory(new PropertyValueFactory<>("time"));

        blockedSuspendedTable.setItems(si.getBlockedSuspendedList());
    }

    private void exitTableInit() {
        exitTableIdColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        exitTableNameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        exitTableTimeColumn.setCellValueFactory(new PropertyValueFactory<>("time"));

        exitTable.setItems(si.getExitList());
    }

    void priorityCreateChoiceBoxInit() {
        priorityCreateChoiceBox.getItems().add("Low");
        priorityCreateChoiceBox.getItems().add("Medium");
        priorityCreateChoiceBox.getItems().add("High");
        priorityCreateChoiceBox.setValue("Medium");
    }

    @FXML
    void openStatistics() throws IOException {
        Stage stage = new Stage();
        stage.setScene(new Scene(FXMLLoader.load(getClass().getResource("/scenes/diagrams_scene.fxml"))));
            stage.getIcons().add(new Image("/img/piechart.png"));
        stage.show();
        statisticsButton.setDisable(true);
        stage.setOnCloseRequest(event -> statisticsButton.setDisable(false));
    }

    void addProcessToReady(Process process){
        if(si.getReadyList().size() < si.getReadyCapacity()){
            si.getReadyList().add(process);
        }else{
            si.getReadySuspendedList().add(process);
        }
    }

    void sortReadyLists(){
        try {
            for (int i = 0; i < si.getReadyList().size() - 1; i++) {
                for (int j = i + 1; j < si.getReadyList().size(); j++) {
                    if (si.getReadyList().get(i).getPriority() < si.getReadyList().get(j).getPriority()) {
                        Process temp = si.getReadyList().get(i);
                        si.getReadyList().set(i, si.getReadyList().get(j));
                        si.getReadyList().set(j, temp);
                    }
                }
            }
            for (int i = 0; i < si.getReadyList().size() - 1; i++) {
                for (int j = i + 1; j < si.getReadyList().size(); j++) {
                    if (si.getReadyList().get(i).getPriority() < si.getReadyList().get(j).getPriority()) {
                        Process temp = si.getReadyList().get(i);
                        si.getReadyList().set(i, si.getReadyList().get(j));
                        si.getReadyList().set(j, temp);
                    }
                }
            }
        }catch (Exception e){
        }
    }

}