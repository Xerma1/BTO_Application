package main.control.dataManagers;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;

import main.entity.Applicant;

import java.util.ArrayList;

import main.entity.Project;


public class ProjectManager extends DataManager {
    // Constants for file paths and column indices
    private static final String PROJ_CSV_PATH = "data/processed/projectList.csv";
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
    
    public static List<Project> getFetchAll(){
        return ProjectManager.fetchAll();
    }

    public static String askProjName(Scanner scanner) {
        System.out.print("Which project would you like to apply for?: ");
        return scanner.nextLine().trim();
    }
    
    public static String askRoomType(Applicant applicant, Scanner scanner) {
        if (!applicant.getMarried()) {
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



    public static void createEditDeleteProject(Scanner scanner) {
        List<String[]> projects = new ArrayList<>(getFetchAll()); // make a modifiable copy!
    
        System.out.println("""
            1. Create project
            2. Edit project
            3. Delete project
            """);
        System.out.print("Select option: ");
        int option = scanner.nextInt();
        scanner.nextLine(); // clear buffer
    
        if (option == 1) {
            // CREATE PROJECT
            System.out.print("Enter project name: ");
            String name = scanner.nextLine();
            System.out.print("Enter neighborhood: ");
            String neighborhood = scanner.nextLine();
            System.out.print("Enter 2-room type (yes/no): ");
            String has2Room = scanner.nextLine().equalsIgnoreCase("yes") ? "Yes" : "No";
            System.out.print("Enter number of 2-room flats: ");
            String twoRoomNo = scanner.nextLine();
            System.out.print("Enter 2-room price: ");
            String twoRoomPrice = scanner.nextLine();
            System.out.print("Enter 3-room type (yes/no): ");
            String has3Room = scanner.nextLine().equalsIgnoreCase("yes") ? "Yes" : "No";
            System.out.print("Enter number of 3-room flats: ");
            String threeRoomNo = scanner.nextLine();
            System.out.print("Enter 3-room price: ");
            String threeRoomPrice = scanner.nextLine();
            System.out.print("Enter opening date (M/d/yyyy): ");
            String openDate = scanner.nextLine();
            System.out.print("Enter closing date (M/d/yyyy): ");
            String closeDate = scanner.nextLine();
            System.out.print("Enter manager in charge: ");
            String manager = scanner.nextLine();
    
            // Create a String[] for the new project
            String[] newProject = {
                name, neighborhood, has2Room, twoRoomNo, twoRoomPrice,
                has3Room, threeRoomNo, threeRoomPrice, openDate, closeDate, manager, "true"
            };
    
            projects.add(newProject); // Add to list
    
            try {
                writeCSV(PROJ_CSV_PATH, projects);
                System.out.println("Project created successfully.");
            } catch (IOException e) {
                System.out.println("Error writing to file.");
                e.printStackTrace();
            }
        }
        else if (option == 2) {
            // EDIT PROJECT
            System.out.print("Enter project name to edit: ");
            String target = scanner.nextLine();
            boolean found = false;
            for (String[] p : projects) {
                if (p[0].equalsIgnoreCase(target)) {
                    System.out.print("Enter new neighborhood: ");
                    p[1] = scanner.nextLine();
                    System.out.print("Enter new manager: ");
                    p[10] = scanner.nextLine();
                    found = true;
                    break;
                }
            }
            if (found) {
                try {
                    writeCSV(PROJ_CSV_PATH, projects);
                    System.out.println("Project edited successfully.");
                } catch (IOException e) {
                    System.out.println("Error writing to file.");
                    e.printStackTrace();
                }
            } else {
                System.out.println("Project not found.");
            }
        }
        else if (option == 3) {
            // DELETE PROJECT
            System.out.print("Enter project name to delete: ");
            String target = scanner.nextLine();
            boolean removed = projects.removeIf(p -> p[0].equalsIgnoreCase(target));
    
            if (removed) {
                try {
                    writeCSV(PROJ_CSV_PATH, projects);
                    System.out.println("Project deleted successfully.");
                } catch (IOException e) {
                    System.out.println("Error writing to file.");
                    e.printStackTrace();
                }
            } else {
                System.out.println("Project not found.");
            }
        }
        else {
            System.out.println("Invalid option.");
        }
    }

public static void toggleProjectVisibility(Scanner scanner) {
    List<String[]> projects = getFetchAll();
    System.out.print("Enter project name to toggle visibility: ");
    String target = scanner.nextLine();
    boolean found = false;
    for (String[] p : projects) {
        if (p[0].equalsIgnoreCase(target)) {
            p[11] = p[11].equalsIgnoreCase("true") ? "false" : "true";
            found = true;
            break;
        }
    }
    if (found) {
        try {
            writeCSV(PROJ_CSV_PATH, projects);
            System.out.println("Project visibility toggled successfully.");
        } catch (IOException e) {
            System.out.println("Error writing to file.");
        }
    } else {
        System.out.println("Project not found.");
    }
}
}