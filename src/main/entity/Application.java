package main.entity;

public class Application {
    private String username;
    private String projName;
    private String roomType;

    private ApplicationStatus status;
    public enum ApplicationStatus {
        PENDING,
        SUCCESSFUL,
        UNSUCCESSFUL,
        BOOKED;
    }

    //constructor
    public Application(String username, String projName, String roomType) {
        this.username = username;
        this.projName = projName;
        this.roomType = roomType;
        this.status = ApplicationStatus.PENDING;
    }

    public ApplicationStatus getStatus() {
        return status;
    }
}

