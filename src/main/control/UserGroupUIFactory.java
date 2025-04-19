package main.control;

import main.boundary.*;

public class UserGroupUIFactory {
    public static IusergroupUI getUI(String usergroup) {
        return switch (usergroup) {
            case "applicant" -> new ApplicantUI();
            case "officer" -> new OfficerUI();
            case "manager" -> new ManagerUI();
            default -> throw new IllegalArgumentException("Invalid usergroup: " + usergroup);
        };
    }
}
