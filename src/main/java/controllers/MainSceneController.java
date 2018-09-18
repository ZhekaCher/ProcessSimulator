package controllers;

import data.Process;
import data.SystemInformation;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.text.Text;

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
    private Text runIdText;

    @FXML
    private Text runNameText;

    @FXML
    private Text runTimeText;

    @FXML
    private Text lastTimeText;



    @FXML
    void initialize() {
        tablesInit();

        execution();
    }

    @FXML
    void createNewProcess() {
        try {
            int id = Integer.valueOf(idCreateTextField.getText());
            String name = nameCreateTextField.getText();
            int time = Integer.valueOf(timeCreateTextField.getText());
            if (si.getReadyList().size() < si.getReadyCapacity())
                si.getReadyList().add(new Process(id, name, time));
            else
                si.getReadySuspendedList().add(new Process(id, name, time));
        } catch (Exception e) {
            System.err.println("ERROR #1");
        }
    }

    @FXML
    private void execution() {
        new Thread() {
            int currentProcessTimeLast;
            int currentProcessTimeCounter;

            @Override
            public void run() {
                super.run();
                while (true) {

                    //Time counter
                    try {
                        Thread.sleep(1000);
                        System.out.println("1 sec");
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }


                    //Run field
                    if (si.getCurrentProcess() != null) {
                        currentProcessTimeCounter--;
                        currentProcessTimeLast--;
                        lastTimeText.setText(String.valueOf(currentProcessTimeLast));
                        if (currentProcessTimeCounter == 0) {
                            si.getExitList().add(si.getCurrentProcess());
                            si.setCurrentProcess(null);
                            runIdText.setText("");
                            runNameText.setText("");
                            runTimeText.setText("");
                            lastTimeText.setText("");
                        } else if (currentProcessTimeLast == 0) {
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
                            lastTimeText.setText("");
                        }
                    } else if (si.getReadyList().size() != 0) {
                        Process newProcess = si.getReadyList().get(0);
                        currentProcessTimeCounter = newProcess.getTime();
                        currentProcessTimeLast = si.getTimeoutValue();
                        lastTimeText.setText(String.valueOf(currentProcessTimeLast));
                        si.setCurrentProcess(newProcess);
                        runIdText.setText(String.valueOf(newProcess.getId()));
                        runNameText.setText(newProcess.getName());
                        runTimeText.setText(String.valueOf(newProcess.getTime()));
                        System.out.println("id: " + newProcess.getId());
                        si.getReadyList().remove(0);
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
    void blockedSuspendedAction(){
        if (si.getBlockedSuspendedList().size() > 0){
            if (si.getReadyList().size() < si.getReadyCapacity()) {
                si.getReadyList().add(si.getBlockedSuspendedList().get(0));
            }else {
                si.getReadySuspendedList().add(si.getBlockedSuspendedList().get(0));
            }
            si.getBlockedSuspendedList().remove(0);
        }
    }

    @FXML
    void blockedAction(){
        if (si.getBlockedList().size() != 0){
            if (si.getReadyList().size() < si.getReadyCapacity()) {
                si.getReadyList().add(si.getBlockedList().get(0));
            }else {
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


}
