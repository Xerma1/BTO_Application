package main.control.dataManagers;

import main.control.viewFilters.IFilterProjectsByUserGroup;
import main.control.viewFilters.ViewFilterFactory;
import main.control.TimeManager;
import main.entity.Application.ApplicationStatus;
import main.entity.Manager;
import main.entity.Applicant;
import main.entity.Officer;
import main.entity.Project;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

/**
 * Manages BTO applications in the system.
 * Includes functionality for applying for flats, viewing applications, and handling withdrawals.
 */
public class ApplicationManager extends DataManager {
    private static final String APPL_CSV_PATH = "data/processed/bto_applications.csv";
    private static final String WITHDRAWAL_PATH = "data/processed/withdrawal_requests.csv";

    private static List<String[]> fetchAllApplications() {
        List<String[]> rows = null;
        try {
            rows = readCSV(APPL_CSV_PATH); // Use utility method
        } catch (IOException e) {
            System.err.println("Error reading file: " + APPL_CSV_PATH);
            e.printStackTrace();
        }
        return rows;
    }

    public static List<String[]> getFetchAllApplications() {
        return ApplicationManager.fetchAllApplications();
    }

    public static boolean applyBTO(Applicant applicant, Scanner scanner) {
        // Reading files
        List<Project> projects = ProjectManager.getFetchAll();
        if (projects == null || projects.isEmpty()) {
            System.out.println("No projects available.");
            return false;
        }

        // Checks if user has already applied for a project. If so, skip the entire process and return false.
        String projname = ApplicationManager.hasUserApplied(applicant.getUserID());
        if(projname != null){
            System.out.println("You have already applied for project " + projname + ".");
            return false;
        }

        // Cleared to proceed with application
        // Get valid projects based on filter type
        IFilterProjectsByUserGroup viewInterface = ViewFilterFactory.getProjectByMartialStatus(applicant.getMarried());
        List<Project> validProjects = viewInterface.getValidProjects();
        if (validProjects == null || validProjects.isEmpty()) {
            System.out.println("No projects available.");
            return false;
        }

        // Ask for project name
        String projName = ProjectManager.askProjName(scanner);

        // Check against validProjects to see if project is valid
        Project validProject = null;
        for (Project project : validProjects) {
            if (projName.equalsIgnoreCase(project.getProjectName().trim())) { // Project name found
                validProject = project;
                projName = project.getProjectName().trim(); // Normalize project name to Title Case
                break;
            }
        }
        if (validProject == null) { // Project name not found
            System.out.println("Project is not available.");
            return false;
        }

        // Check if project is actively open to applications, if not, return false
        if (!TimeManager.isValidDate(validProject.getOpenDate().trim(), validProject.getCloseDate().trim())) {
            System.out.println("Project not active.");
            return false;
        }
        
        // Additional checks for officer applicants
        if (applicant instanceof Officer) {
            boolean isRegistered = ProjectManager.isRegistered((Officer) applicant, validProject);

            // Check if applicant is not also registered as an officer for the same project
            if (isRegistered) {
                System.out.println("You cannot apply for project " +  projName + " as you are already registered as an officer for this project.");
                return false;
            }

            // Check if the officer has any active projects
            List<Project> handling = ((Officer) applicant).getHandling();
            if (handling != null && !handling.isEmpty()) {
                for (Project project : handling) {
                    if (TimeManager.isValidDate(project.getOpenDate().trim(), project.getCloseDate().trim())) {
                        System.out.println("You cannot apply for project " + projName + " as you have active projects.");
                        return false;
                    }
                }
            } 
       }

        // Asking for room type
        String roomType = ProjectManager.askRoomType(applicant, scanner);
        int roomIndex = roomType.equals("2-room") ? 0 : 1;

        // Check room availability
        String[] roomDetails = validProject.getFlatTypes().get(roomIndex);
        if ("0".equals(roomDetails[1].trim())) {
            System.out.println("No vacancies.");
            return false;
        }

        // Writing application back to CSV file
        String[] newApplication = {
                applicant.getName(),
                applicant.getUserID(),
                String.valueOf(applicant.getAge()),
                applicant.getMarried(),
                projName,
                roomType,
                roomDetails[2], // Price
                validProject.getOpenDate(), // Opening date
                validProject.getCloseDate(), // Closing date
                ApplicationStatus.PENDING.name(),
                validProject.getManager() // Manager
        };
        appendToCSV(APPL_CSV_PATH, newApplication);

        return true;
    }


    public static void viewApplication(Applicant applicant) {
        List<String[]> applications;
        try {
            applications = readCSV(APPL_CSV_PATH);
            for (String[] values : applications) {
                if (values.length > 1 && values[1].trim().equals(applicant.getUserID())) {
                    // Print application details
                    System.out.printf("%-15s %-15s %-10s %-15s %-15s %-10s%n",
                            "Project Name", "Room Type", "Price", "Opening Date", "Closing Date", "Status");
                    System.out.println("=".repeat(90));
                    System.out.printf("%-15s %-15s %-10s %-15s %-15s %-10s%n",
                            values[4], values[5], values[6], values[7], values[8], values[9]);
                    return;
                }
            }
            System.out.println("No applications found for this user.");
        } catch (IOException e) {
            System.out.println("An error occurred while reading applications: " + e.getMessage());
        }
    }


    public static String hasUserApplied(String userId) { // Perhaps rename to getProjectNameByUserId
        List<String[]> applications = getFetchAllApplications();

        if (applications == null || applications.isEmpty()) {
            return null; // No applications exist
        }
        
        for (String[] application : applications) {
            if (application.length > 1 && application[1].trim().equals(userId)) {
                return application[4].trim(); // Return the project name (column 4)
            }
        }

        return null; // User has not applied for any project
    }

    public static boolean requestWithdrawal(Applicant applicant, Scanner scanner) {
        // Check if user has applied for a project. If not, return false.
        String projname = ApplicationManager.hasUserApplied(applicant.getUserID());
        if(projname == null){
            System.out.println("You have not applied for any project.");
            return false;
        }
        // Check is the time of withdrawal is within the opening and closing date of the project
        Project project = ProjectManager.getProjectByName(projname);
        if (!TimeManager.isValidDate(project.getOpenDate().trim(), project.getCloseDate().trim())) {
            System.out.println("You cannot withdraw your application as the project is already closed.");
            return false;
        }

        // Check if user has already requested a withdrawal
        List<String[]> withdrawalRequests = null;
        try {
            withdrawalRequests = readCSV(WITHDRAWAL_PATH);
        } catch (IOException e) {
            System.out.println("Error reading withdrawal requests: " + e.getMessage());
        }
        if (withdrawalRequests != null) {
            for (String[] request : withdrawalRequests) {
                if (request[0].trim().equals(applicant.getUserID())) {
                    System.out.println("You have already requested a withdrawal for project " + projname + ".");
                    return false;
                }
            }
        }

        // Proceed making withdrawal request
        System.out.println("You are about to withdraw your application for project " + projname + ".");
        System.out.print("Are you sure? (Y/N): ");
        String confirmation = scanner.nextLine().trim();
        if (confirmation.equalsIgnoreCase("N")) {
            System.out.println("Withdrawal cancelled.");
            return false;
        }

        String[] withdrawalRequest = {
                applicant.getUserID(),
                projname,
                project.getManager(),        
        };
        // Append withdrawal request to CSV file
        appendToCSV(WITHDRAWAL_PATH, withdrawalRequest);
        return true;
   
    }

    public static void manageApplications(Scanner scanner, Manager manager) {
        try {
            // Fetch all applications
            List<String[]> applications = fetchAllApplications();
            List<String[]> updatedApplications = new ArrayList<>(); // Holds all applications to be written back

            System.out.println("Pending Applications for Your Projects:");
            System.out.printf("%-5s %-15s %-15s %-20s %-15s%n", "ID", "Name", "NRIC", "Project", "Room Type");
            System.out.println("=".repeat(80));

            int id = 1;
            List<String[]> pendingApplications = new ArrayList<>(); // Holds all pending applications
            for (String[] application : applications) {
                if (application.length < 10) {
                    continue; // Skip invalid rows
                }

                if (application[9].equalsIgnoreCase("PENDING")) { // Check for pending status
                    String projectName = application[4];
                    Project project = ProjectManager.getProjectByName(projectName);

                    // Check if the manager is handling the project
                    if (project != null && project.getManager().equalsIgnoreCase(manager.getName())) {
                        System.out.printf("%-5d %-15s %-15s %-20s %-15s%n", id, application[0], application[1], projectName, application[5]);
                        pendingApplications.add(application);
                        id++;
                    } else {
                        updatedApplications.add(application); // Add pending applications not managed by this manager
                    }
                } else {
                    updatedApplications.add(application); // Add non-pending applications
                }
            }

            if (pendingApplications.isEmpty()) {
                System.out.println("No pending applications found for your projects.");
                return;
            }

            // Prompt the manager to approve or reject an application
            System.out.print("Enter the ID of the application to manage (or 0 to cancel): ");
            int choice = scanner.nextInt();
            scanner.nextLine(); // Clear the newline character

            if (choice == 0) {
                System.out.println("Operation cancelled.");
                return;
            }

            if (choice < 1 || choice > pendingApplications.size()) {
                System.out.println("Invalid ID. Operation cancelled.");
                return;
            }

            // Get the selected application
            String[] selectedApplication = pendingApplications.get(choice - 1);
            String applicantName = selectedApplication[0];
            String projectName = selectedApplication[4];
            String roomType = selectedApplication[5];

            // Check room availability
            Project project = ProjectManager.getProjectByName(projectName);
            if (project == null) {
                System.out.println("Project not found.");
                return;
            }

            int roomIndex = roomType.equalsIgnoreCase("2-room") ? 0 : 1;
            String[] roomDetails = project.getFlatTypes().get(roomIndex);
            int availableRooms = Integer.parseInt(roomDetails[1]);

            if (availableRooms <= 0) {
                // Automatically reject the application
                selectedApplication[9] = "UNSUCCESSFUL"; // Update status to unsuccessful
                System.out.println("Not enough rooms available for the requested room type. Application automatically rejected.");
            } else {
                // Prompt for approval or rejection
                System.out.print("Approve or reject this application? (approve/reject): ");
                String decision = scanner.nextLine().trim();

                if (decision.equalsIgnoreCase("approve")) {
                    selectedApplication[9] = "SUCCESSFUL"; // Update status to successful

                    // Decrement the available rooms for the requested room type
                    roomDetails[1] = String.valueOf(availableRooms - 1);
                    List<String[]> flatTypes = new ArrayList<>(project.getFlatTypes());
                    flatTypes.set(roomIndex, roomDetails);
                    project.setFlatTypes(flatTypes);

                    // Update the project in the CSV file
                    ProjectManager.updateProject(project);

                    System.out.println("Application approved and project updated.");
                } else if (decision.equalsIgnoreCase("reject")) {
                    selectedApplication[9] = "UNSUCCESSFUL"; // Update status to unsuccessful
                    System.out.println("Application rejected.");
                } else {
                    System.out.println("Invalid decision. Operation cancelled.");
                    return;
                }
            }

            // Add the updated application to the list
            updatedApplications.add(selectedApplication);

            // Add the remaining pending applications that were not chosen
            for (String[] pendingApplication : pendingApplications) {
                if (pendingApplication != selectedApplication) {
                    updatedApplications.add(pendingApplication);
                }
            }

            // Write the updated applications back to the CSV file
            writeCSV(APPL_CSV_PATH, updatedApplications);
            System.out.println("Changes saved successfully!");

        } catch (IOException e) {
            System.err.println("Error managing applications: " + e.getMessage());
        }
    }
}



