package main.control.dataManagers;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import main.control.TimeManager;
import main.entity.Officer;
import main.entity.Manager;
import main.entity.Project;

public class OfficerRegistrationManager extends DataManager {
    private static final String OFFICER_REG_CSV_PATH = "data/processed/officer_registrations.csv";
    private static final int COL_NAME = 0;
    private static final int COL_NRIC = 1;
    private static final int COL_AGE = 2;
    private static final int COL_MARITAL_STATUS = 3;
    private static final int COL_PROJECT = 4;
    private static final int COL_STATUS = 5;

    // Method to fetch all officer registrations
    private static List<String[]> fetchAllRegistrations() {
        try {
            return DataManager.readCSV(OFFICER_REG_CSV_PATH);
        } catch (IOException e) {
            System.err.println("Error reading file: " + OFFICER_REG_CSV_PATH);
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    // Method to view officer registrations based on status
    public static void viewOfficerRegistrations(String statusFilter) {
        List<String[]> registrations = fetchAllRegistrations();
        if (registrations.isEmpty()) {
            System.out.println("No officer registrations found.");
            return;
        }

        // Determine the title based on the filter
        String title;
        if (statusFilter.equalsIgnoreCase("pending")) {
            title = "Pending Officer Registrations";
        } else if (statusFilter.equalsIgnoreCase("approved")) {
            title = "Approved Officer Registrations";
        } else {
            title = "All Officer Registrations";
        }

        System.out.println(title + ":");
        System.out.printf("%-15s %-15s %-5s %-15s %-20s %-10s%n", "Name", "NRIC", "Age", "Marital Status", "Project", "Status");
        System.out.println("=".repeat(90));

        boolean found = false;

        int count = 0;
        for (String[] registration : registrations) {
            String status = registration[COL_STATUS] == null ? "pending" : registration[COL_STATUS].toLowerCase();
            if (count == 0){
                count++; 
                continue; // Skip the header row
            }

            // Filter based on the status
            if (statusFilter.equalsIgnoreCase("all") || status.equalsIgnoreCase(statusFilter)) {
                System.out.printf("%-15s %-15s %-5s %-15s %-20s %-10s%n",
                        registration[COL_NAME],
                        registration[COL_NRIC],
                        registration[COL_AGE],
                        registration[COL_MARITAL_STATUS],
                        registration[COL_PROJECT],
                        status.substring(0, 1).toUpperCase() + status.substring(1)); // Capitalize status
                found = true;
            }
        }

        if (!found) {
            System.out.println("No " + statusFilter + " registrations found.");
        }
    }

    public static void viewOfficerRegistrationStatus(Officer officer) {
        try {
            // Fetch all officer registrations
            List<String[]> registrations = readCSV(OFFICER_REG_CSV_PATH);
    
            // Check if the officer's registration exists
            boolean found = false;
            System.out.println("Showing officer registrations: ");
            System.out.println("=".repeat(50));
            for (String[] registration : registrations) {
                if (registration[1].equalsIgnoreCase(officer.getUserID())) {
                    System.out.printf("Project: %s, Status: %s%n", 
                            registration[4], registration[5]);
                    found = true;
                }
            }
            System.out.println("");

            if (!found) {
                System.out.println("No registration record found for the officer.");
            }
        } catch (IOException e) {
            System.err.println("Error reading officer registrations: " + e.getMessage());
        }
    }

    // Method to register an officer for a project
    public static boolean registerOfficer(Officer officer, Scanner scanner) {
        // Fetch all projects
        List<Project> projects = ProjectManager.getFetchAll();
        if (projects == null || projects.isEmpty()) {
            System.out.println("No projects available.");
            return false;
        }

        // Check if the officer is already applying for a project as an applicant
        String appliedProject = ApplicationManager.hasUserApplied(officer.getUserID());
        if (appliedProject != null) {
            System.out.println("You cannot register as an officer because you are already applying for the project: " + appliedProject);
            return false;
        }


        // Display available projects
        System.out.println("Available projects:");
        for (Project project : projects) {
            System.out.println("- " + project.getProjectName());
        }

        // Prompt the officer to select a project
        System.out.print("Enter the name of the project you want to register for: ");
        String projectName = scanner.nextLine();

        // Find the selected project
        Project selectedProject = ProjectManager.getProjectByName(projectName);
        if (selectedProject == null) {
            System.out.println("Project not found.");
            return false;
        }

         // Check if the officer is already registered for another project with overlapping dates
         for (Project project : projects) {
            if (ProjectManager.isRegistered(officer, project)) {
                // Check if the dates overlap
                if (TimeManager.isDateRangeOverlapping(
                        project.getOpenDate(),
                        project.getCloseDate(),
                        selectedProject.getOpenDate(),
                        selectedProject.getCloseDate())) {
                    System.out.println("You are already registered as an officer for the project: " + project.getProjectName() +
                            " with overlapping dates.");
                    return false;
                }
            }
        }

        // Check if the project has available officer slots
        if (selectedProject.getOfficerSlots() ==  0) {
            System.out.println("No available officer slots for this project.");
            return false;
        }

        // Add the registration to the CSV file
        String[] newRegistration = {
            officer.getName(),
            officer.getUserID(),
            String.valueOf(officer.getAge()),
            officer.getMarried(),
            selectedProject.getProjectName(),
            "pending" // Registration status
        };

        appendToCSV(OFFICER_REG_CSV_PATH, newRegistration);
        return true;
    }

    // Method to approve an officer registration
    public static void manageOfficerRegistrations(Scanner scanner, Manager manager) {
        try {
            // Fetch all officer registrations
            List<String[]> registrations = fetchAllRegistrations();
            List<String[]> updatedRegistrations = new ArrayList<>();

            System.out.println("Pending Officer Registrations for Your Projects:");
            System.out.printf("%-5s %-15s %-15s %-20s%n", "ID", "Name", "NRIC", "Project");
            System.out.println("=".repeat(60));

            int id = 1;
            List<String[]> pendingRegistrations = new ArrayList<>();
            for (String[] registration : registrations) {
                if (registration[COL_STATUS].equalsIgnoreCase("pending")) { // Check for pending status
                    String projectName = registration[COL_PROJECT];
                    Project project = ProjectManager.getProjectByName(projectName);

                    // Check if the manager is handling the project
                    if (project != null && project.getManager().equalsIgnoreCase(manager.getName())) {
                        System.out.printf("%-5d %-15s %-15s %-20s%n", id, registration[COL_NAME], registration[COL_NRIC], projectName);
                        pendingRegistrations.add(registration);
                        id++;
                    }
                } else {
                    updatedRegistrations.add(registration); // Keep non-pending registrations as is
                }
            }

            if (pendingRegistrations.isEmpty()) {
                System.out.println("No pending registrations found for your projects.");
                return;
            }

            // Prompt the manager to approve or reject a registration
            System.out.print("Enter the ID of the registration to manage (or 0 to cancel): ");
            int choice = scanner.nextInt();
            scanner.nextLine(); // Clear the newline character

            if (choice == 0) {
                System.out.println("Operation cancelled.");
                return;
            }

            if (choice < 1 || choice > pendingRegistrations.size()) {
                System.out.println("Invalid ID. Operation cancelled.");
                return;
            }

            // Get the selected registration
            String[] selectedRegistration = pendingRegistrations.get(choice - 1);
            String officerName = selectedRegistration[COL_NAME].trim();
            String projectName = selectedRegistration[COL_PROJECT].trim();

            // Prompt for approval or rejection
            System.out.print("Approve or reject this registration? (approve/reject): ");
            String decision = scanner.nextLine().trim().toLowerCase();

            if (decision.equals("approve")) {
                selectedRegistration[COL_STATUS] = "approved"; // Update status to approved

                // Update the project in projects.csv
                Project project = ProjectManager.getProjectByName(projectName);
                if (project != null) {
                    // Add the officer to the project
                    List<String> officers = new ArrayList<>(List.of(project.getOfficers()));
                    officers.add(officerName);
                    project.setOfficers(officers.toArray(new String[0]));

                    // Decrement the available officer slots
                    project.setOfficerSlots(project.getOfficerSlots() - 1);

                    // Update the project in the CSV file
                    ProjectManager.updateProject(project);
                }

                System.out.println("Registration approved and project updated.");
            } else if (decision.equals("reject")) {
                selectedRegistration[COL_STATUS] = "rejected"; // Update status to rejected
                System.out.println("Registration rejected.");
            } else {
                System.out.println("Invalid decision. Operation cancelled.");
                return;
            }

            // Add the updated registration to the list
            updatedRegistrations.add(selectedRegistration);

            // Write the updated registrations back to the CSV file
            writeCSV(OFFICER_REG_CSV_PATH, updatedRegistrations);
            System.out.println("Changes saved successfully!");

        } catch (IOException e) {
            System.err.println("Error managing officer registrations: " + e.getMessage());
        }
    }

}
