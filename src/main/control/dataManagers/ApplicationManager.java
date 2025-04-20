package main.control.dataManagers;

import main.control.viewFilters.IViewFilter;
import main.control.viewFilters.ViewFilterFactory;
import main.control.TimeManager;
import main.entity.Application.ApplicationStatus;
import main.entity.Applicant;
import main.entity.Officer;
import main.entity.Project;

import java.io.IOException;
import java.util.List;
import java.util.Scanner;

public class ApplicationManager extends DataManager {
    private static final String APPL_CSV_PATH = "data/processed/bto_applications.csv";
    private static final String OFF_RSG_PATH = "data/processed/officer_registrations.csv";

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
        IViewFilter viewInterface = ViewFilterFactory.getViewFilter(applicant.filterType);
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
        
        // Finally, check if applicant is not also registered as an officer for the same project
        if (applicant instanceof Officer) {
            boolean isRegistered = ProjectManager.isRegistered((Officer) applicant, validProject);

            if (isRegistered) {
                System.out.println("You cannot apply for project " +  projName + " as you are already registered as an officer for this project.");
                return false;
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
                applicant.getMarried() ? "Married" : "Single",
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


    public static String hasUserApplied(String userId) {
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

   
}



