package main.control.dataManagers;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Scanner;
import java.util.stream.Collectors;

import main.control.viewFilters.IFilterProjectsByUserGroup;
import main.control.InputManager;

import main.control.viewFilters.ViewFilterFactory;
import main.entity.Applicant;
import main.entity.Enquiry;
import main.entity.Manager;
import main.entity.Officer;
import main.entity.Project;

/**
 * Manages enquiries in the BTO application system.
 * Includes functionality for creating, viewing, replying to, and managing enquiries.
 */
public class EnquiryManager extends DataManager {
    private static final String ENQ_CSV_PATH = "data/processed/enquiries.csv";
    private static final int COL_USER_NAME = 0;
    private static final int COL_USER_ID = 1;
    private static final int COL_PROJECT_NAME = 2;
    private static final int COL_QUESTION = 3;
    private static final int COL_ANSWER = 4;

    // Fetches all enquiries
    private static List<String[]> fetchAll() {
        try {
            return DataManager.readCSV(ENQ_CSV_PATH);
        } catch (IOException e) {
            System.err.println("Error reading file: " + ENQ_CSV_PATH);
            e.printStackTrace();
            return Collections.emptyList();
        }
    }

    // Public method to fetch all enquiries
    public static List<String[]> getFetchAll() {
        return EnquiryManager.fetchAll();
    }

    // Method to view all enquiries across all projects
    public static void viewAllEnquiries() {
        // Fetch all enquiries
        List<String[]> enquiries = getFetchAll();
        if (enquiries == null || enquiries.isEmpty()) {
            System.out.println("No enquiries found.");
            return;
        }
    
        // Print header
        System.out.println("Viewing all enquiries: ");
        System.out.println("=".repeat(160));
    
        // Print each enquiry using the same UI structure as view by officer
        for (int i = 1; i < enquiries.size(); i++) { // Start from index 1 to skip the header
            String[] enquiry = enquiries.get(i);
            printHandlingEnquiry(enquiry); // Reuse the printHandlingEnquiry method
        }
    }

    // Prints enquiries either by User or by Project for applicants
    private static void printEnquiry(String[] enquiry, boolean byUser) {
        if (byUser) System.out.println("Project Name: " + enquiry[COL_PROJECT_NAME]);
        else System.out.println("Username: " + enquiry[COL_USER_NAME]);
    
        System.out.println("=".repeat(140));
        System.out.println("Question:  " + enquiry[COL_QUESTION]);
        System.out.println("Answer:    " + enquiry[COL_ANSWER]);
        System.out.println("=".repeat(140));
        System.out.println();
    }

    private static void printHandlingEnquiry(String[] enquiry) {
        System.out.printf("%-10s %-10s %-10s%n",
        "Username", "User ID", "Project Name");
        System.out.println("=".repeat(140));
        System.out.printf("%-10s %-10s %-10s%n",
            enquiry[COL_USER_NAME],
            enquiry[COL_USER_ID],
            enquiry[COL_PROJECT_NAME]);
        System.out.println("Question:  " + enquiry[COL_QUESTION]);
        System.out.println("Answer:    "+ enquiry[COL_ANSWER]);
        System.out.println("=".repeat(140));
        System.out.println();
    }

    // Returns an editable or deletable enquiry
    private static String[] isMutable(String userID, Scanner scanner) {
        List<String[]> enquiries = getFetchAll(); // Gets all enquiries from file
        if (enquiries == null || enquiries.isEmpty()) { // No enquiries in file
            System.out.println("No enquiries.");
            return null;
        }

        List<String[]> mutableEnquiries = new ArrayList<>();
        for (String[] mutableEnquiry : enquiries) {
            if (mutableEnquiry[COL_USER_ID].equalsIgnoreCase(userID) && // Finding enquiries by User ID
            mutableEnquiry[COL_ANSWER].equalsIgnoreCase("nil"))  {// If enquiry has no answer
                
                mutableEnquiries.add(mutableEnquiry);
            }
        }
        if (mutableEnquiries.isEmpty()) { // No enquiries
            System.out.println("No enquiries changeable.");
            return null;
        }

        System.out.println("Choose: ");
        for (int i = 0; i < mutableEnquiries.size(); i++) { // Printing enquiries for user to choose from
            System.out.println((i + 1) + ". ");
            printEnquiry(mutableEnquiries.get(i), true);
        }

        int choice = InputManager.promptUserChoice(scanner, 1, mutableEnquiries.size()); // Asking for choice
        String[] selectedEnquiry = mutableEnquiries.get(choice - 1);
        return selectedEnquiry;
    }

    // Returns a repliable enquiry
    private static String[] isRepliable(Officer officer, Scanner scanner) {
        List<Project> handling = officer.getHandling();
        if (handling == null || handling.isEmpty()) {
            System.out.println("No handling projects"); // No projects
            return null;
        }
        List<String[]> enquiries = getFetchAll(); // Gets all enquiries from file
        if (enquiries == null || enquiries.isEmpty()) { // No enquiries in file
            System.out.println("No enquiries.");
            return null;
        }

        List<String[]> repliableEnquiries = new ArrayList<>();
        for (Project project : handling) {
            for (String[] enquiry : enquiries) {
                if (enquiry[COL_PROJECT_NAME].equalsIgnoreCase(project.getProjectName()) && // Finding enquiries by project name
                    enquiry[COL_ANSWER].equalsIgnoreCase("nil")) { // Enquiry already has answer

                    repliableEnquiries.add(enquiry);
                }
            }
        }
        if (repliableEnquiries.isEmpty()) {
            System.out.println("No enquiries.");
            return null;
        }

        System.out.println("Choose: ");
        for (int i = 0; i < repliableEnquiries.size(); i++) { // Printing enquiries for user to choose from
            System.out.println((i + 1) + ". ");
            printEnquiry(repliableEnquiries.get(i), true);
        }

        int choice = InputManager.promptUserChoice(scanner, 1, repliableEnquiries.size()); // Asking for choice
        String[] selectedEnquiry = repliableEnquiries.get(choice - 1);
        return selectedEnquiry;
    }

    // === Applicants ===

    // Used by applicants to submit enquiries
    public static boolean createEnquiry(Applicant applicant, Scanner scanner) {
        // Fetch all projects
        List<Project> projects = ProjectManager.getFetchAll();
        if (projects == null || projects.isEmpty()) {
            System.out.println("No projects available.");
            return false;
        }

        // Display available projects
        System.out.println("Available projects:");
        for (Project project : projects) {
            System.out.println("- " + project.getProjectName());
        }

        System.out.print("Enter project name: ");
        String projectName = scanner.nextLine();
        System.out.print("Enter question: ");
        String question = scanner.nextLine();

        IFilterProjectsByUserGroup viewInterface = ViewFilterFactory.getProjectByMartialStatus(applicant.getMarried());
        List<Project> validProjects = viewInterface.getValidProjects();

        boolean projectFound = false;
        for (Project project : validProjects) { // If applicant is making an enquiry for a project that is available to them
            if (project.getProjectName().equalsIgnoreCase(projectName)) {
                projectFound = true;
                projectName = project.getProjectName(); // Normalising to Title Case
            }
        }
        if (!projectFound) { 
            System.out.println("Project not available."); // No projects avaiable to applicant
            return false;
        }

        if (question.equals("\n")) {
            System.out.println("No question entered."); // No question entered
            return false;
        }

        Enquiry enquiry = new Enquiry(applicant.getName(), applicant.getUserID(), projectName, question);
        writeEnquiry(enquiry);
        return true; // Successful enquiry
    }

    // Write new enquiry to csv file
    public static void writeEnquiry(Enquiry enquiry) {
        String answer = "\"nil\"";

        String[] newEnquiry = {
            enquiry.getUserName(),
            enquiry.getUserID(),
            enquiry.getProjectName(),
            "\"" + enquiry.getQuestion() + "\"", // Formatting for csv
            answer
        };

        DataManager.appendToCSV(ENQ_CSV_PATH, newEnquiry);
    }

    // Choose between viewing, editing and deleting enquiries
    public static void viewEditDeleteEnquiries(Applicant applicant, Scanner scanner) {
        System.out.println("1. View enquiries");
        System.out.println("2. Edit enquiries");
        System.out.println("3. Delete enquiries");
        int choice = InputManager.promptUserChoice(scanner, 1, 3);
        switch (choice) {
            case 1 -> viewEnquiries(applicant, scanner);
            case 2 -> {
                boolean isSuccessful = editEnquiry(applicant, scanner);
                if (isSuccessful) {
                    System.out.println("Successfully edited!");
                }
            }
            case 3 -> {
                boolean isSuccessful = deleteEnquiry(applicant, scanner);
                if (isSuccessful) {
                    System.out.println("Successfully deleted!");
                } else {
                    System.out.println("Failed to delete.");
                }
            }
        }
    }

    // Used by applicants to view enquiries
    public static void viewEnquiries(Applicant applicant, Scanner scanner) {
        List<String[]> enquiries = getFetchAll(); // Gets all enquiries from file
        if (enquiries == null || enquiries.isEmpty()) { // No enquiries in file
            System.out.println("No enquiries.");
            return;
        }

        System.out.println("1. View your enquiries.");
        System.out.println("2. View enquiries for a project.");
        int choice = InputManager.promptUserChoice(scanner, 1, 2);

        switch (choice) {
            case 1 -> {
                boolean foundEnquiry = false;
                for (String[] enquiry : enquiries) {
                    if (enquiry[COL_USER_ID].equalsIgnoreCase(applicant.getUserID())) { // Finding enquiries by User ID
                        printEnquiry(enquiry, true);
                        foundEnquiry = true;
                    }
                }
                if (!foundEnquiry) {
                    System.out.print("No enquiries.");
                }
        
                System.out.println();
                return;
            }
            case 2 -> {
                IFilterProjectsByUserGroup viewInterface = ViewFilterFactory.getProjectByMartialStatus(applicant.getMarried());
                List<Project> validProjects = viewInterface.getValidProjects();
                if (validProjects == null || validProjects.isEmpty()) { // No projects available
                    System.out.println("No projects available.");
                    return;
                }

                Project targetProject = null;
                String projectName = ProjectManager.askProjName(scanner);
                for (Project project : validProjects) {
                    if (project.getProjectName().equalsIgnoreCase(projectName)) {
                        targetProject = project;
                    }
                }
                if (targetProject == null) { // Project name not found in available projects 
                    System.out.println("Invalid project");
                    return;
                }

                boolean foundEnquiry = false;
                for (String[] enquiry : enquiries) {
                    if (enquiry[COL_PROJECT_NAME].equalsIgnoreCase(targetProject.getProjectName())) { // Finding enquiries by project name
                        printEnquiry(enquiry, false);
                        foundEnquiry = true;
                    }
                }
                if (!foundEnquiry) {
                    System.out.print("No enquiries.");
                }
            }
            default -> System.out.println("default");
        }
    }    
    
    // Used by applicants to edit enquiries
    public static boolean editEnquiry(Applicant applicant, Scanner scanner) {
        String[] selectedEnquiry = isMutable(applicant.getUserID(), scanner);
        if (selectedEnquiry == null) {
            return false;
        }

        System.out.print("New Question: ");
        String question = scanner.nextLine();
        String[] updatedEnquiry = Arrays.copyOf(selectedEnquiry, selectedEnquiry.length); // Deep copy
        updatedEnquiry[COL_QUESTION] = question;
        
        List<String[]> enquiries = getFetchAll();
        List<String[]> updatedEnquiries = enquiries.stream()
            .map(row -> Arrays.equals(row, selectedEnquiry) ? updatedEnquiry : row)
            .collect(Collectors.toList());

        try {
            writeCSV(ENQ_CSV_PATH, updatedEnquiries);
        } catch (IOException e) {
            System.err.println("Error writing to file: " + ENQ_CSV_PATH);
            e.printStackTrace();
        }
        return true;
    }

    // Used by applicants to delete enquiries
    public static boolean deleteEnquiry(Applicant applicant, Scanner scanner) {
        String[] selectedEnquiry = isMutable(applicant.getUserID(), scanner);
        if (selectedEnquiry == null) {
            return false;
        }

        System.out.println("Type 'confirm' to confirm: ");
        String confirm = scanner.nextLine();
        if (!confirm.equalsIgnoreCase("confirm")) return false;

        List<String[]> enquiries = getFetchAll();
        List<String[]> updatedEnquiries = enquiries.stream()
            .filter(row -> !Arrays.equals(row, selectedEnquiry))
            .collect(Collectors.toList());

        try {
            writeCSV(ENQ_CSV_PATH, updatedEnquiries);
        } catch (IOException e) {
            System.err.println("Error writing to file: " + ENQ_CSV_PATH);
            e.printStackTrace();
        }
        return true;
    }

    // === Officers ===

    // Used by officers to view enquiries for projects
    public static void viewEnquiries(Officer officer) {
        List<String[]> enquiries = getFetchAll(); // Gets all enquiries from file
        List<Project> projects = officer.getHandling(); // Gets all projects from officer
        if (enquiries.isEmpty() || officer.getHandling().isEmpty()) {
            System.out.println("No enquiries.");
            return;
        }

        boolean foundEnquiry = false;
        for (Project project : projects) {
            for (String[] enquiry : enquiries) {
                if (enquiry[COL_PROJECT_NAME].equalsIgnoreCase(project.getProjectName())) { // Finding enquiries by project name
                    printHandlingEnquiry(enquiry);
                    foundEnquiry = true;
                }
            }
        }
        if (!foundEnquiry) {
            System.out.println("No enquiries.");
        }

        System.out.println();
    }

    // Used by officers to reply to enquiries
    public static boolean replyEnquiry(Officer officer, Scanner scanner) {
        String[] selectedEnquiry = isRepliable(officer, scanner);
        if (selectedEnquiry == null) {
            return false;
        }

        System.out.print("Reply: "); // Asking for reply
        String answer = scanner.nextLine();
        String[] updatedEnquiry = Arrays.copyOf(selectedEnquiry, selectedEnquiry.length); // Deep copy
        updatedEnquiry[COL_ANSWER] = answer;
        
        List<String[]> enquiries = getFetchAll();
        List<String[]> updatedEnquiries = enquiries.stream()
            .map(row -> Arrays.equals(row, selectedEnquiry) ? updatedEnquiry : row)
            .collect(Collectors.toList());

        try {
            writeCSV(ENQ_CSV_PATH, updatedEnquiries);
        } catch (IOException e) {
            System.err.println("Error writing to file: " + ENQ_CSV_PATH);
            e.printStackTrace();
        }
        return true;
    }

    // Used by managers to view and reply to enquiries
    public static void viewAndReplyToEnquiries(Manager manager, Scanner scanner) {
        // Fetch all enquiries
        List<String[]> enquiries = getFetchAll();
        if (enquiries == null || enquiries.isEmpty()) {
            System.out.println("No enquiries found.");
            return;
        }
    
        // Fetch all projects managed by the manager
        List<Project> projects = ProjectManager.getFetchAll();
        if (projects == null || projects.isEmpty()) {
            System.out.println("No projects found.");
            return;
        }
    
        // Filter enquiries for projects managed by the manager and not replied to
        List<String[]> managerEnquiries = new ArrayList<>();
        for (String[] enquiry : enquiries) {
            for (Project project : projects) {
                if (project.getManager().equalsIgnoreCase(manager.getName()) &&
                    enquiry[COL_PROJECT_NAME].equalsIgnoreCase(project.getProjectName()) &&
                    enquiry[COL_ANSWER].equalsIgnoreCase("nil")) { // Only include enquiries with no reply
                    managerEnquiries.add(enquiry);
                }
            }
        }
    
        if (managerEnquiries.isEmpty()) {
            System.out.println("No unanswered enquiries found for your projects.");
            return;
        }
    
        // Allow the manager to select an enquiry to reply to
        final String[] selectedEnquiry;
        while (true) {
            System.out.println("Choose an enquiry to reply to:");
            for (int i = 0; i < managerEnquiries.size(); i++) {
                System.out.println((i + 1) + ". ");
                printEnquiry(managerEnquiries.get(i), true); // Reuse the printEnquiry method for consistent UI
            }

            int choice = InputManager.promptUserChoice(scanner, 1, managerEnquiries.size());
            String[] tempEnquiry = managerEnquiries.get(choice - 1);
            if (tempEnquiry != null) {
                selectedEnquiry = tempEnquiry;
                break;
            }
        }
    
        // Prompt for a reply
        System.out.print("Reply: ");
        String reply = scanner.nextLine().trim();
    
        // Update the enquiry with the reply
        String[] updatedEnquiry = Arrays.copyOf(selectedEnquiry, selectedEnquiry.length); // Deep copy
        updatedEnquiry[COL_ANSWER] = reply;
    
        // Update the enquiries in the CSV file
        List<String[]> updatedEnquiries = enquiries.stream()
            .map(row -> Arrays.equals(row, selectedEnquiry) ? updatedEnquiry : row)
            .collect(Collectors.toList());
    
        try {
            writeCSV("data/processed/enquiries.csv", updatedEnquiries);
            System.out.println("Reply saved successfully!");
        } catch (IOException e) {
            System.err.println("Error saving reply: " + e.getMessage());
        }
    }
}
