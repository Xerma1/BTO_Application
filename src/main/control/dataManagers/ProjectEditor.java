package main.control.dataManagers;

import main.entity.Project;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class ProjectEditor extends ProjectManager {

    // Method to create a new project
    public static void addProject(Scanner scanner) {
        System.out.println("Enter project details:");

        System.out.print("Project Name: ");
        String projectName = scanner.nextLine();

        System.out.print("Neighborhood: ");
        String neighborhood = scanner.nextLine();

        System.out.print("Number of 2-room flats: ");
        int twoRoomCount = scanner.nextInt();
        scanner.nextLine(); // Clear newline

        System.out.print("Price of 2-room flats: ");
        int twoRoomPrice = scanner.nextInt();
        scanner.nextLine(); // Clear newline

        System.out.print("Number of 3-room flats: ");
        int threeRoomCount = scanner.nextInt();
        scanner.nextLine(); // Clear newline

        System.out.print("Price of 3-room flats: ");
        int threeRoomPrice = scanner.nextInt();
        scanner.nextLine(); // Clear newline

        System.out.print("Application opening date (MM/DD/YYYY): ");
        String openDate = scanner.nextLine();

        System.out.print("Application closing date (MM/DD/YYYY): ");
        String closeDate = scanner.nextLine();

        System.out.print("Manager: ");
        String manager = scanner.nextLine();

        System.out.print("Number of officer slots: ");
        int officerSlots = scanner.nextInt();
        scanner.nextLine(); // Clear newline

        System.out.print("Visibility (true/false): ");
        boolean visibility = scanner.nextBoolean();
        scanner.nextLine(); // Clear newline

        // Create the project object
        List<String[]> flatTypes = new ArrayList<>();
        flatTypes.add(new String[]{"2-Room", String.valueOf(twoRoomCount), String.valueOf(twoRoomPrice)});
        flatTypes.add(new String[]{"3-Room", String.valueOf(threeRoomCount), String.valueOf(threeRoomPrice)});

        Project newProject = new Project(projectName, neighborhood, flatTypes, openDate, closeDate, manager, officerSlots, new String[]{}, visibility);

        // Save the project using ProjectManager
        ProjectManager.addProject(newProject);
        System.out.println("Project created successfully!");
    }

    // Method to edit an existing project
    public static void updateProject(Scanner scanner) {
        System.out.print("Enter the name of the project to edit: ");
        String projectName = scanner.nextLine();

        Project project = ProjectManager.getProjectByName(projectName);
        if (project == null) {
            System.out.println("Project not found.");
            return;
        }

        System.out.println("Editing project: " + project.getProjectName());
        System.out.println("Leave fields blank to keep current values.");

        System.out.print("New Neighborhood (" + project.getNeighbourhood() + "): ");
        String neighborhood = scanner.nextLine();
        if (!neighborhood.isEmpty()) {
            project.setNeighbourhood(neighborhood);
        }

        System.out.print("New Number of 2-room flats (" + project.getFlatTypes().get(0)[1] + "): ");
        String twoRoomCount = scanner.nextLine();
        if (!twoRoomCount.isEmpty()) {
            project.getFlatTypes().get(0)[1] = twoRoomCount;
        }

        System.out.print("New Price of 2-room flats (" + project.getFlatTypes().get(0)[2] + "): ");
        String twoRoomPrice = scanner.nextLine();
        if (!twoRoomPrice.isEmpty()) {
            project.getFlatTypes().get(0)[2] = twoRoomPrice;
        }

        System.out.print("New Number of 3-room flats (" + project.getFlatTypes().get(1)[1] + "): ");
        String threeRoomCount = scanner.nextLine();
        if (!threeRoomCount.isEmpty()) {
            project.getFlatTypes().get(1)[1] = threeRoomCount;
        }

        System.out.print("New Price of 3-room flats (" + project.getFlatTypes().get(1)[2] + "): ");
        String threeRoomPrice = scanner.nextLine();
        if (!threeRoomPrice.isEmpty()) {
            project.getFlatTypes().get(1)[2] = threeRoomPrice;
        }

        System.out.print("New Application opening date (" + project.getOpenDate() + "): ");
        String openDate = scanner.nextLine();
        if (!openDate.isEmpty()) {
            project.setOpenDate(openDate);
        }

        System.out.print("New Application closing date (" + project.getCloseDate() + "): ");
        String closeDate = scanner.nextLine();
        if (!closeDate.isEmpty()) {
            project.setCloseDate(closeDate);
        }

        System.out.print("New Visibility (" + project.isVisibility() + "): ");
        String visibility = scanner.nextLine();
        if (!visibility.isEmpty()) {
            project.setVisibility(Boolean.parseBoolean(visibility));
        }

        ProjectManager.updateProject(project);
        System.out.println("Project updated successfully!");
    }

    // Method to delete a project
    public static void deleteProject(Scanner scanner) {
        System.out.print("Enter the name of the project to delete: ");
        String projectName = scanner.nextLine();

        boolean isDeleted = ProjectManager.deleteProject(projectName);
        if (isDeleted) {
            System.out.println("Project deleted successfully!");
        } else {
            System.out.println("Project not found.");
        }
    }

    public static void toggleVisibility(Scanner scanner) {

        // Fetch all projects
        List<Project> projects = ProjectManager.getFetchAll();
        if (projects == null || projects.isEmpty()) {
            System.out.println("No projects available.");
            return;
        }

        // Display available projects
        System.out.println("Available projects:");
        for (Project project : projects) {
            System.out.println("- " + project.getProjectName());
        }

        System.out.print("Enter the name of the project to toggle visibility: ");
        String projectName = scanner.nextLine();
    
        // Retrieve the project by name
        Project project = ProjectManager.getProjectByName(projectName);
    
        if (project == null) {
            System.out.println("Project not found.");
            return;
        }
    
        // Toggle the visibility
        boolean currentVisibility = project.isVisibility();
        System.out.println("Current visibility for " + projectName + ": " + (currentVisibility ? "Visible" : "Hidden"));
        System.out.println("Do you want to toggle the visibility? (y/n)");
        String choice = scanner.nextLine();
        if (choice.equalsIgnoreCase("n")) {
            System.out.println("Visibility toggle cancelled.");
            return;
        }
        else{
             project.setVisibility(!currentVisibility);
        }
       
    
        // Update the project in the CSV
        ProjectManager.updateProject(project);
    
        System.out.println("Project visibility has been toggled.");
        System.out.println("New visibility status: " + (project.isVisibility() ? "Visible" : "Hidden"));
    }
}