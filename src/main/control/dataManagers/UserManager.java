package main.control.dataManagers;

import java.io.IOException;
import java.util.List;

import main.entity.User;
import main.entity.Applicant;
import main.entity.Officer;
import main.entity.Manager;

public class UserManager extends DataManager {
    // Constants for file paths and column indices
    private static final String USERS_CSV_PATH = "data/processed/users.csv";
    private static final int COL_NAME = 0;
    private static final int COL_USER_ID = 1;
    private static final int COL_AGE = 2;
    private static final int COL_MARTIAL_STATUS = 3;
    private static final int COL_PASSWORD = 4;
    private static final int COL_ACCESS_LEVEL = 5;
    private static final int COL_FILTER_TYPE = 6; // Assuming filter type is in the 7th column

    // Private method to fetch sensitive user data
    private static User _fetch(String userID) {
        List<String[]> users = null;
        try {
            users = readCSV(USERS_CSV_PATH);
        } catch (IOException e) {
            System.err.println("Error reading file: " + USERS_CSV_PATH);
            e.printStackTrace();
        }

        for (String[] user : users) {
            if (user[COL_USER_ID].equals(userID)) { // Find user using userID   
                String name = user[COL_NAME];
                int age = Integer.parseInt(user[COL_AGE]);
                String married = user[COL_MARTIAL_STATUS]; // Convert marital status to boolean
                String password = user[COL_PASSWORD]; 
                String accessLevel = user[COL_ACCESS_LEVEL];
                String filterType = user[COL_FILTER_TYPE]; // Assuming filter type is in the 7th column              
                return new User(name, userID, age, married, password, accessLevel, filterType); // Return User object
            }
        }
        return null; // Return null if user not found
    }

    // Public method to fetch User object
    public static User fetch(String userID){
        return UserManager._fetch(userID);
    }

    // Private method to write password to file
    private static void _writePassword(String userID, String newPassword) {
        List<String[]> users;
        try {
            users = readCSV(USERS_CSV_PATH); // Use utility method
        } catch (IOException e) {
            System.err.println("Error reading file: " + USERS_CSV_PATH);
            e.printStackTrace();
            return;
        }

        for (String[] user : users) {
            if (user[COL_USER_ID].trim().equals(userID)) { // Match userID
                    user[COL_PASSWORD] = newPassword; // Update the password
                }
        }

        // Write updated rows back to the file
        // TODO: update single line instead of rewriting the whole file
        try {
            writeCSV(USERS_CSV_PATH, users);
        } catch (IOException e) {
            System.err.println("Error writing to file: " + USERS_CSV_PATH);
            e.printStackTrace();
        }
    }

    // Public method to create a new user based on access level
    public static User createUser(User user) {
        String name = user.getName();
        String userID = user.getUserID();
        int age = user.getAge();
        String married = user.getMarried();
        String password = user.getPassword();
        String accessLevel = user.getAccessLevel();
        String filterType = user.getFilterType();
        switch (accessLevel) {
            case "applicant":
                return new Applicant(name, userID, age, married, password, accessLevel, filterType);
            case "officer":
                return new Officer(name, userID, age, married, password, accessLevel, filterType);
            case "manager":
                return new Manager(name, userID, age, married, password, accessLevel, filterType);
            default:
                System.out.println("Invalid access level. User not created.");
                return null;
        }
    }

    // Public method that calls writePassword
    public static void writePassword(String userID, String newPassword) {
        UserManager._writePassword(userID, newPassword);
    }
}

public class HDBManager extends User {
    private List<Project> createdProjects;

    public HDBManager(String name, String nric, int age, String maritalStatus) {
        super(name, nric, age, maritalStatus);
        this.createdProjects = new ArrayList<>();
    }

    public Project createProject(String name, String neighborhood, int twoRoomUnits, int threeRoomUnits,
                                 Date openingDate, Date closingDate) {
        for (Project p : createdProjects) {
            if (p.isWithinPeriod(openingDate, closingDate)) {
                System.out.println("Manager already handling a project during this period.");
                return null;
            }
        }
        Project project = new Project(name, neighborhood, twoRoomUnits, threeRoomUnits,
                                      openingDate, closingDate, this);
        createdProjects.add(project);
        return project;
    }

    public void editProject(Project project, String newName, String newNeighborhood) {
        project.setName(newName);
        project.setNeighborhood(newNeighborhood);
    }

    public void deleteProject(Project project) {
        if (createdProjects.remove(project)) {
            System.out.println("Project deleted.");
        } else {
            System.out.println("Project not found.");
        }
    }

    public void toggleProjectVisibility(Project project, boolean isVisible) {
        project.setVisible(isVisible);
    }

    public void approveOfficer(Project project, HDBOfficer officer) {
        if (project.getAvailableOfficerSlots() > 0) {
            project.addOfficer(officer);
            System.out.println("Officer approved.");
        } else {
            System.out.println("No officer slots available.");
        }
    }

    public void approveApplicant(Application app) {
        if (app.getProject().canAllocate(app.getFlatType())) {
            app.setStatus("Successful");
            app.getProject().allocateFlat(app.getFlatType());
        } else {
            app.setStatus("Unsuccessful");
        }
    }

    public void handleWithdrawal(Application app, boolean approve) {
        if (approve) {
            app.setStatus("Withdrawn");
            app.getProject().releaseFlat(app.getFlatType());
        } else {
            System.out.println("Withdrawal rejected.");
        }
    }

    public void generateReport(List<Application> applications, String filterCategory) {
        for (Application app : applications) {
            if (filterCategory.equals("married") && app.getApplicant().getMaritalStatus().equals("Married")) {
                System.out.println(app.getSummary());
            }
            // Add other filters as needed
        }
    }

    public void viewAllProjects(List<Project> allProjects) {
        for (Project p : allProjects) {
            System.out.println(p);
        }
    }

    public void viewAndReplyToEnquiries(Project project, String reply) {
        for (Enquiry e : project.getEnquiries()) {
            if (!e.isAnswered()) {
                System.out.println("Q: " + e.getQuestion());
                e.setAnswer(reply);
                System.out.println("Replied.");
            }
        }
    }

    public List<Project> getCreatedProjects() {
        return createdProjects;
    }

    @Override
    public boolean canApply() {
        return false;
    }
}
