package data;

public class Process {

    private int id;
    private String name;
    private int time;

    private boolean blocked;
    private int priority;

    public Process(int id, String name, int time, boolean blocked, int priority) {
        this.id = id;
        this.name = name;
        this.time = time;
        this.blocked = blocked;
        this.priority = priority;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public int getTime() {
        return time;
    }

    public void setTime(int time) {
        this.time = time;
    }

    public boolean isBlocked() {
        return blocked;
    }

    public void setBlocked(boolean blocked) {
        this.blocked = blocked;
    }

    public int getPriority() {
        return priority;
    }
}
