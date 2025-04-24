package main.control;

import main.boundary.*;

/**
 * Factory class to create instances of user group UIs.
 * This class is responsible for creating the appropriate UI based on the user group.
 */
public class UserGroupUIFactory {
    public static IUserGroupUI getUI(String usergroup) {
        return switch (usergroup) {
            case "applicant" -> new ApplicantUI();
            case "officer" -> new OfficerUI();
            case "manager" -> new ManagerUI();
            default -> throw new IllegalArgumentException("Invalid usergroup: " + usergroup);
        };
    }
}
