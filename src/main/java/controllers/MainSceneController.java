package controllers;

import data.Process;
import data.SystemInformation;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.layout.AnchorPane;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import vizualization.Effects;
import vizualization.ShakeAnimation;

import java.io.IOException;
import java.util.Random;

public class MainSceneController {

    SystemInformation si;

    @FXML
    private Button createNewProcessButton;

    @FXML
    private AnchorPane createNewProcessAnchorPane;

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
    private TableColumn<?, ?> readyTableBlockedColumn;

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

    int idCounter = 1;

    @FXML
    void initialize() {
        tablesInit();
        priorityCreateChoiceBoxInit();

        execution();
    }

    @FXML
    void createNewProcess() {
        try {
            int id;
            if (idCreateTextField.getText().equals("")) {
                while (true) {
                    if (si.isIdExist(idCounter)){
                        idCounter++;
                        continue;
                    }
                    id = idCounter;
                    idCounter++;
                    break;
                }
            }
            else if(!si.isIdExist(Integer.valueOf(idCreateTextField.getText())))
                id = Integer.valueOf(idCreateTextField.getText());
            else
                throw new Exception();
            si.getUsedIdList().add(id);
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
            addProcessToReady(new Process(id, name, time, blocked, priority));
            nameCreateTextField.setText("");
            idCreateTextField.setText("");
            timeCreateTextField.setText("");
            blockedCreateCheckBox.setSelected(false);
            priorityCreateChoiceBox.setValue("Medium");
            createNewProcessButton.setEffect(Effects.successShadow());
            createNewProcessAnchorPane.setEffect(Effects.successShadow());
        } catch (Exception e) {
            System.err.println("ERROR #1");
            createNewProcessButton.setEffect(Effects.errorShadow());
            createNewProcessAnchorPane.setEffect(Effects.errorShadow());
            new ShakeAnimation(){
                @Override
                public void activityOnFinished() {
                    createNewProcessAnchorPane.setEffect(null);
                }
            }.shake(createNewProcessAnchorPane);
        }
        new Thread(() -> {
            try {
                Thread.sleep(3000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            createNewProcessButton.setEffect(null);
            createNewProcessAnchorPane.setEffect(null);
        }).start();
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
                ProgressBarThread progressBarThread = null;
                super.run();
                while (true) {
                    //Time counter
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    sortReadyLists();
                    suspendedControl();
                    sortReadyLists();

                    //Run field
                    if (si.getCurrentProcess() != null){
                        currentProcessTimeLeft--;
                        currentProcessTimeCounter--;
                        Process process = si.getCurrentProcess();
                        leftTimeText.setText(String.valueOf(currentProcessTimeLeft));
                        if (si.getCurrentProcess().isBlocked()) {
                            Random random = new Random(System.nanoTime());
                            int r;
                            if (currentProcessTimeLeft < currentProcessTimeCounter)
                             r = random.nextInt(currentProcessTimeLeft+1);
                            else
                                r = random.nextInt(currentProcessTimeCounter+1);
                            if (r == 0) {
                                si.setCurrentProcess(null);
                                currentProcessTimeLeft=0;
                                currentProcessTimeCounter=0;
                                clearRunFields();
                                progressBarThread.stop();
                                process.setBlocked(false);
                                addProcessToBlocked(process);
                            }
                        } else if(currentProcessTimeCounter == 0){
                            si.setCurrentProcess(null);
                            currentProcessTimeLeft=0;
                            currentProcessTimeCounter=0;
                            clearRunFields();
                            progressBarThread.stop();
                            si.getExitList().add(process);
                        }else if (currentProcessTimeLeft == 0){
                            si.setCurrentProcess(null);
                            currentProcessTimeLeft=0;
                            currentProcessTimeCounter=0;
                            clearRunFields();
                            progressBarThread.stop();
                            process.setTime(process.getTime()-si.getTimeoutValue());
                            addProcessToReady(process);
                        }

                    }else if (si.getCurrentProcess() == null && si.getReadyList().size() > 0){
                        Process process = si.getReadyList().get(0);
                        currentProcessTimeLeft = si.getTimeoutValue();
                        currentProcessTimeCounter = process.getTime();
                        si.setCurrentProcess(process);
                        si.getReadyList().remove(0);
                        runIdText.setText(String.valueOf(process.getId()));
                        runNameText.setText(process.getName());
                        runTimeText.setText(String.valueOf(process.getTime()));
                        leftTimeText.setText(String.valueOf(currentProcessTimeLeft));
                        progressBarThread = new ProgressBarThread();
                        progressBarThread.start();
                    }

                    /*
                    if (si.getCurrentProcess() != null) {
                        currentProcessTimeCounter--;
                        currentProcessTimeLeft--;
                        if (si.getCurrentProcess().isBlocked()) {
                            Random random = new Random(System.nanoTime());
                            int r = random.nextInt(currentProcessTimeLeft);
                            if (r == 0) {

                            }
                        }
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
                    */



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

    void clearRunFields(){
        runNameText.setText("");
        runIdText.setText("");
        runTimeText.setText("");
        leftTimeText.setText("");
        progressBar.setProgress(0);
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
        readyTableBlockedColumn.setCellValueFactory(new PropertyValueFactory<>("blocked"));

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

    void addProcessToReady(Process process) {
        if (si.getReadyList().size() < si.getReadyCapacity()) {
            si.getReadyList().add(process);
        } else {
            si.getReadySuspendedList().add(process);
        }
    }

    void addProcessToBlocked(Process process) {
        if (si.getBlockedList().size() < si.getBlockedCapacity()) {
            si.getBlockedList().add(process);
        } else {
            si.getBlockedSuspendedList().add(process);
        }
    }

    void suspendedControl() {
        if (si.getBlockedSuspendedList().size() != 0 && si.getBlockedList().size() < si.getBlockedCapacity()) {
            try {
                Process process = si.getBlockedSuspendedList().get(0);
                si.getBlockedSuspendedList().remove(0);
                si.getBlockedList().add(process);
            } catch (Exception e) {
                System.err.println("ERROR #3");
            }
        }
        if (si.getReadySuspendedList().size() != 0 && si.getReadyList().size() < si.getReadyCapacity()) {
            try {
                Process process = si.getReadySuspendedList().get(0);
                si.getReadySuspendedList().remove(0);
                si.getReadyList().add(process);
            } catch (Exception e) {
                System.err.println("ERROR #4");
            }
        }
    }

    void sortReadyLists() {
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
            for (int i = 0; i < si.getReadySuspendedList().size() - 1; i++) {
                for (int j = i + 1; j < si.getReadySuspendedList().size(); j++) {
                    if (si.getReadySuspendedList().get(i).getPriority() < si.getReadySuspendedList().get(j).getPriority()) {
                        Process temp = si.getReadySuspendedList().get(i);
                        si.getReadySuspendedList().set(i, si.getReadySuspendedList().get(j));
                        si.getReadyList().set(j, temp);
                    }
                }
            }
        } catch (Exception e) {
            System.err.println("ERROR #2");
        }
    }

}