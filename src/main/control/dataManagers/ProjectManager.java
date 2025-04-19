package main.control.dataManagers;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;

import main.entity.Project;

public class ProjectManager extends DataManager {
    private static final String PROJ_CSV_PATH = "data/processed/projectList.csv";

    public static List<Project> getFetchAll() {
    List<String[]> rows = null;
    try {
        rows = readCSV(PROJ_CSV_PATH);
    } catch (IOException e) {
        System.err.println("Error reading file: " + PROJ_CSV_PATH);
    }
    if (rows == null || rows.isEmpty()) {
        return null;
    }

    List<Project> projects = new ArrayList<>();
    for (int i = 1; i < rows.size(); i++) { // skip header row
        String[] row = rows.get(i);
        try {
            String projectName = row[0];
            String neighborhood = row[1];
            List<String[]> flatTypes = List.of(
                new String[] { row[2], row[3], row[4] },
                new String[] { row[5], row[6], row[7] }
            );
            String openDate = row[8];
            String closeDate = row[9];
            String manager = row[10];
            int officerSlots = Integer.parseInt(row[11]);
            String[] officers = row[12].replace("\"", "").split(",");
            officers = Arrays.stream(officers).map(String::trim).toArray(String[]::new);
            boolean visibility = Boolean.parseBoolean(row[13]);

            projects.add(new Project(projectName, neighborhood, flatTypes, openDate, closeDate, manager, officerSlots, officers, visibility));
        } catch (Exception e) {
            System.err.println("Error processing row: " + String.join(",", row));
        }
    }

    return projects;
}

    public static void createEditDeleteProject(Scanner scanner) {
        List<String[]> projects = null;
try {
    projects = new ArrayList<>(readCSV(PROJ_CSV_PATH)); // FIX: make modifiable copy
} catch (IOException e) {
    System.err.println("Error reading file: " + PROJ_CSV_PATH);
}
if (projects == null) projects = new ArrayList<>();

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

            String[] newProject = {
                name, neighborhood, has2Room, twoRoomNo, twoRoomPrice,
                has3Room, threeRoomNo, threeRoomPrice, openDate, closeDate, manager, "0", "", "true"
            };

            projects.add(newProject);

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
        List<String[]> projects = null;
        try {
            projects = readCSV(PROJ_CSV_PATH);
        } catch (IOException e) {
            System.err.println("Error reading file: " + PROJ_CSV_PATH);
        }
        if (projects == null) {
            System.out.println("No projects found.");
            return;
        }

        System.out.print("Enter project name to toggle visibility: ");
        String target = scanner.nextLine();
        boolean found = false;
        for (String[] p : projects) {
            if (p[0].equalsIgnoreCase(target)) {
                p[13] = p[13].equalsIgnoreCase("true") ? "false" : "true";
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