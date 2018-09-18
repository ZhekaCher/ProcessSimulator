import java.util.ArrayList;

public class SystemInformation {

    private static int readyCapacity = 5;
    private static int blockedCapacity = 5;
    private static int timeoutValue = 5;

    private static ArrayList <Process> readyList = new ArrayList();
    private static ArrayList <Process> blockedList = new ArrayList();
    private static ArrayList <Process> readySuspendedList = new ArrayList();
    private static ArrayList <Process> blockedSuspendedList = new ArrayList();
    private static ArrayList <Process> exitList = new ArrayList();
}
