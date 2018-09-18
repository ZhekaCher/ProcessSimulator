package data;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.stage.Stage;

import java.util.ArrayList;

public class SystemInformation {

    private static Stage mainStage;
    private static int readyCapacity = 5;
    private static int blockedCapacity = 5;
    private static int timeoutValue = 5;
    private static int timeSubtract = 5;

    private static Process currentProcess = null;

    private static ArrayList <Integer> usedIdList = new ArrayList<>();
    private static ObservableList <Process> readyList = FXCollections.observableList(new ArrayList<>());
    private static ObservableList <Process> blockedList = FXCollections.observableList(new ArrayList<>());
    private static ObservableList <Process> readySuspendedList = FXCollections.observableList(new ArrayList<>());
    private static ObservableList <Process> blockedSuspendedList = FXCollections.observableList(new ArrayList<>());
    private static ObservableList <Process> exitList = FXCollections.observableList(new ArrayList<>());

    public static Stage getMainStage() {
        return mainStage;
    }

    public static void setMainStage(Stage mainStage) {
        SystemInformation.mainStage = mainStage;
    }

    public static int getReadyCapacity() {
        return readyCapacity;
    }

    public static int getBlockedCapacity() {
        return blockedCapacity;
    }

    public static int getTimeoutValue() {
        return timeoutValue;
    }

    public static ObservableList<Process> getReadyList() {
        return readyList;
    }

    public static ObservableList<Process> getBlockedList() {
        return blockedList;
    }

    public static ObservableList<Process> getReadySuspendedList() {
        return readySuspendedList;
    }

    public static ObservableList<Process> getBlockedSuspendedList() {
        return blockedSuspendedList;
    }

    public static ObservableList<Process> getExitList() {
        return exitList;
    }


    public static Process getCurrentProcess() {
        return currentProcess;
    }

    public static void setCurrentProcess(Process currentProcess) {
        SystemInformation.currentProcess = currentProcess;
    }

    public static int getTimeSubtract() {
        return timeSubtract;
    }

    public static void setTimeSubtract(int timeSubtract) {
        SystemInformation.timeSubtract = timeSubtract;
    }

    public static ArrayList<Integer> getUsedIdList() {
        return usedIdList;
    }
}
