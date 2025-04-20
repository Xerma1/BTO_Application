package main.control.dataManagers;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Scanner;

import main.control.viewFilters.IFilterProjectsByUserGroup;
import main.control.InputManager;

import main.control.viewFilters.IViewFilter;
import main.control.viewFilters.ViewFilterFactory;
import main.entity.Applicant;
import main.entity.Enquiry;
import main.entity.Officer;
import main.entity.Project;

public class EnquiryManager {
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

    // Public method
    public static List<String[]> getFetchAll() {
        return EnquiryManager.fetchAll();
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

    // Returns an editable or deletable enquiry
    private static String[] isMutable(String userId, Scanner scanner) {
        List<String[]> enquiries = getFetchAll(); // Gets all enquiries from file
        if (enquiries == null || enquiries.isEmpty()) { // No enquiries in file
            System.out.println("No enquiries.");
            return null;
        }

        List<String[]> mutableEnquiries = new ArrayList<>();
        for (String[] mutableEnquiry : enquiries) {
            if (mutableEnquiry[COL_USER_ID].equalsIgnoreCase(userId) && // Finding enquiries by User ID
            mutableEnquiry[COL_ANSWER].equalsIgnoreCase("nil"))  {// If enquiry has no answer
                
                    mutableEnquiries.add(mutableEnquiry);
            }
        }
        if (mutableEnquiries.isEmpty()) { // No enquiries
            System.out.println("No enquiries changeable.");
            return null;
        }

        for (int i = 0; i < mutableEnquiries.size(); i++) { // Printing enquiries for user to choose from
            System.out.println((i + 1) + ". ");
            printEnquiry(mutableEnquiries.get(i), true);
        }

        int choice = InputManager.promptUserChoice(scanner, 1, mutableEnquiries.size()); // Asking for choice
        String[] selectedEnquiry = mutableEnquiries.get(choice - 1);
        return selectedEnquiry;
    }

    // === Applicants ===

    // Used by applicants to submit enquiries
    public static boolean createEnquiry(Applicant applicant, Scanner scanner) {
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

    public static void viewEditDeleteEnquiries(Applicant applicant, Scanner scanner) {
        System.out.println("1. View enquiries.");
        System.out.println("2. Edit enquiries.");
        System.out.println("3. Delete enquiries.");
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
                IViewFilter viewInterface = ViewFilterFactory.getViewFilter(applicant.filterType);
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
        selectedEnquiry[COL_QUESTION] = question; // Replacing old question with new question

        // TODO: write back to file
        return true;
    }

    // Used by applicants to delete enquiries
    public static boolean deleteEnquiry(Applicant applicant, Scanner scanner) {
        String[] selectedEnquiry = isMutable(applicant.getUserID(), scanner);
        if (selectedEnquiry == null) {
            return false;
        }

        System.out.println("Type 'confirm' to confirm: ");
        // TODO: delete from file
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
                    System.out.printf("%-10s %-15s %-10s%n",
                    "Username", "User ID", "Project Name");
                    System.out.println("=".repeat(140));
                    System.out.printf("%-10s %-15s %-10s%n",
                        enquiry[COL_USER_NAME],
                        enquiry[COL_USER_ID],
                        enquiry[COL_PROJECT_NAME]);
                    System.out.println("Question:  " + enquiry[COL_QUESTION]);
                    System.out.println("Answer:    "+ enquiry[COL_ANSWER]);
                    System.out.println("=".repeat(140));
                    System.out.println();
                    foundEnquiry = true;
                }
            }
        }
        if (!foundEnquiry) {
            System.out.print("No enquiries.");
        }

        System.out.println();
    }

    // Used by officers to reply to enquiries
    public static boolean replyEnquiry(Officer officer, Enquiry enquiry, Scanner scanner) {
        List<Project> handling = officer.getHandling();
        boolean foundEnquiry = false;
        for (Project project : handling) {
            if (project.getProjectName().equalsIgnoreCase(enquiry.getProjectName())) { // If enquiry comes from a project being handled
                foundEnquiry = true;
            }
        }
        if (!foundEnquiry) {
            System.out.println("No such enquiry found.");
            return false;
        }
        
        if (!enquiry.getAnswer().equalsIgnoreCase("nil")) { // If enquiry already has an answer
            System.out.println("Enquiry already has reply.");
            return false;
        }
        
        System.out.print("Reply: "); // Asking for reply
        String answer = scanner.nextLine();
        enquiry.setAnswer(answer);
        return true;
    }

}
