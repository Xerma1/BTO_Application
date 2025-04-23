package main.control.dataManagers;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;

import main.entity.Applicant;
import main.entity.Officer;
import main.entity.Project;

public class ProjectManager extends DataManager {
    // Constants for file paths and column indices
    private static final String PROJ_CSV_PATH = "data/processed/projects.csv";
    private static final int COL_NAME = 0;
    private static final int COL_NEIGHBORHOOD = 1;
    private static final int COL_2_ROOM = 2;
    private static final int COL_2_ROOM_NO = 3;
    private static final int COL_2_ROOM_PRICE = 4;
    private static final int COL_3_ROOM = 5;
    private static final int COL_3_ROOM_NO = 6;
    private static final int COL_3_ROOM_PRICE = 7;
    private static final int COL_OPEN_DATE= 8;
    private static final int COL_CLOSE_DATE = 9;
    private static final int COL_MANAGER = 10;
    private static final int COL_OFFICER_SLOTS = 11;
    private static final int COL_OFFICERS = 12;
    private static final int COL_VISIBILITY = 13;

    // Private method to fetch sensitive project data. 
    private static List<Project> fetchAll() {
        List<String[]> rows = null;
        try {
            rows = DataManager.readCSV(PROJ_CSV_PATH); // Use utility method
        } catch (IOException e) {
            System.err.println("Error reading file: " + PROJ_CSV_PATH);
            e.printStackTrace();
        }

        if (rows == null || rows.isEmpty()) {
            return null; // Return null if rows couldn't be read or are empty
        }

        // Skip the header row and map rows to Project objects
        List<Project> projects = new ArrayList<>();
        for (int i = 1; i < rows.size(); i++) { // Start from index 1 to skip the header
            String[] row = rows.get(i);
            try {
                String projectName = row[COL_NAME];
                String neighbourhood = row[COL_NEIGHBORHOOD];
                List<String[]> flatTypes = List.of(
                    new String[] { row[COL_2_ROOM], row[COL_2_ROOM_NO], row[COL_2_ROOM_PRICE] },
                    new String[] { row[COL_3_ROOM], row[COL_3_ROOM_NO], row[COL_3_ROOM_PRICE] }
                );
                String openDate = row[COL_OPEN_DATE];
                String closeDate = row[COL_CLOSE_DATE];
                String manager = row[COL_MANAGER];
                int officerSlots = Integer.parseInt(row[COL_OFFICER_SLOTS]);
                String[] officers = row[COL_OFFICERS]
                    .replace("\"", "") // Remove surrounding quotation marks
                    .split(","); // Split by commas
                officers = Arrays.stream(officers)
                    .map(String::trim) // Trim whitespace
                    .toArray(String[]::new); // Convert back to an array
                boolean visibility = Boolean.parseBoolean(row[COL_VISIBILITY]);

                projects.add(new Project(projectName, neighbourhood, flatTypes, openDate, closeDate, manager, officerSlots, officers, visibility));
            } catch (ArrayIndexOutOfBoundsException e) {
                System.err.println("Error processing row: " + String.join(",", row));
                e.printStackTrace();
            }
        }

        return projects;
    }
    
    public static List<Project> getFetchAll() {
        return ProjectManager.fetchAll();
    }

    public static boolean isRegistered(Officer officer, Project project) {
        String[] registeredOfficers = project.getOfficers();
        boolean isOfficerRegistered = false;

        if (registeredOfficers != null) {
            isOfficerRegistered = Arrays.stream(registeredOfficers)
                .map(String::trim) // Trim whitespace from each officer's name
                .anyMatch(officerName -> officerName.equalsIgnoreCase(officer.getName())); // Case-insensitive comparison
        }
        return isOfficerRegistered;
    }


    public static String askProjName(Scanner scanner) {
        System.out.print("Project Name: ");
        return scanner.nextLine().trim();
    }
    
    public static String askRoomType(Applicant applicant, Scanner scanner) {
        if (applicant.getMarried().equalsIgnoreCase("Single")) {
            System.out.println("Applying for 2-room..."); // Singles default
            return "2-room";
        }

        while (true) {
            System.out.print("2-room or 3-room? (Enter 2 or 3): ");
            if (scanner.hasNextInt()) {
                int input = scanner.nextInt();
                scanner.nextLine(); // Clear newline
                if (input == 2) return "2-room";
                if (input == 3) return "3-room";
            } else {
                scanner.nextLine(); // Clear invalid input
            }
            System.out.println("Invalid room type. Please enter 2 or 3.");
        }
    }

    private static Project ProjectByName(String projectName) {
        List<Project> projects = getFetchAll(); // Fetch all projects
        if (projects == null || projects.isEmpty()) {
            System.out.println("No projects available.");
            return null;
        }
    
        for (Project project : projects) {
            if (project.getProjectName().equalsIgnoreCase(projectName)) { // Case-insensitive comparison
                return project;
            }
        }

        return null; // Return null if no matching project is found
    }

    public static Project getProjectByName(String projectName){
        return ProjectByName(projectName);
    }

    // Method to add a new project
    public static void addProject(Project project) {
        String[] newProject = {
            project.getProjectName(),
            project.getNeighbourhood(),
            project.getFlatTypes().get(0)[0], // Type 1
            project.getFlatTypes().get(0)[1], // Number of units for Type 1
            project.getFlatTypes().get(0)[2], // Selling price for Type 1
            project.getFlatTypes().get(1)[0], // Type 2
            project.getFlatTypes().get(1)[1], // Number of units for Type 2
            project.getFlatTypes().get(1)[2], // Selling price for Type 2
            project.getOpenDate(),
            project.getCloseDate(),
            project.getManager(),
            String.valueOf(project.getOfficerSlots()),
            String.join(",", project.getOfficers()), // Officers as a comma-separated string
            String.valueOf(project.isVisibility())
        };

        DataManager.appendToCSV(PROJ_CSV_PATH, newProject);
    }

    // Method to update an existing project
    public static void updateProject(Project updatedProject) {
        try {
            List<String[]> projects = DataManager.readCSV(PROJ_CSV_PATH);
            List<String[]> updatedProjects = new ArrayList<>();

            boolean projectFound = false;
            for (String[] project : projects) {
                if (project[0].equalsIgnoreCase(updatedProject.getProjectName())) { // Match by project name
                    // Update the project details
                    updatedProjects.add(new String[] {
                        updatedProject.getProjectName(),
                        updatedProject.getNeighbourhood(),
                        updatedProject.getFlatTypes().get(0)[0], // Type 1
                        updatedProject.getFlatTypes().get(0)[1], // Number of units for Type 1
                        updatedProject.getFlatTypes().get(0)[2], // Selling price for Type 1
                        updatedProject.getFlatTypes().get(1)[0], // Type 2
                        updatedProject.getFlatTypes().get(1)[1], // Number of units for Type 2
                        updatedProject.getFlatTypes().get(1)[2], // Selling price for Type 2
                        updatedProject.getOpenDate(),
                        updatedProject.getCloseDate(),
                        updatedProject.getManager(),
                        String.valueOf(updatedProject.getOfficerSlots()),
                        String.join(",", updatedProject.getOfficers()), // Officers as a comma-separated string
                        String.valueOf(updatedProject.isVisibility())
                    });
                    projectFound = true;
                } else {
                    updatedProjects.add(project); // Keep the existing project
                }
            }

            if (!projectFound) {
                System.out.println("Project not found.");
                return;
            }

            DataManager.writeCSV(PROJ_CSV_PATH, updatedProjects);
        } catch (IOException e) {
            System.err.println("Error updating project: " + e.getMessage());
        }
    }

    // Method to delete a project
    public static boolean deleteProject(String projectName) {
        try {
            List<String[]> projects = DataManager.readCSV(PROJ_CSV_PATH);
            List<String[]> updatedProjects = new ArrayList<>(projects);
    
            boolean projectFound = false;
    
            // Use an iterator to remove the project
            var iterator = updatedProjects.iterator();
            while (iterator.hasNext()) {
                String[] project = iterator.next();
                if (project[0].equalsIgnoreCase(projectName)) { // Match by project name
                    iterator.remove(); // Remove the project
                    projectFound = true;
                    break; // Exit the loop once the project is found and removed
                }
            }
    
            if (projectFound) {
                DataManager.writeCSV(PROJ_CSV_PATH, updatedProjects);
                return true;
            } else {
                return false;
            }
    
        } catch (IOException e) {
            System.err.println("Error deleting project: " + e.getMessage());
            return false;
        }
    }

}
