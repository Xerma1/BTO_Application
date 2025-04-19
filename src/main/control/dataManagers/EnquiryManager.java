package main.control.dataManagers;

import java.io.IOException;
import java.util.List;
import java.util.Scanner;

import main.control.viewFilters.IViewFilter;
import main.control.viewFilters.ViewFilterFactory;
import main.entity.Applicant;
import main.entity.Enquiry;
import main.entity.Project;

public class EnquiryManager {
    private static final String ENQ_CSV_PATH = "data/processed/enquiries.csv";
    private static final int COL_USER_ID = 0;
    private static final int COL_PROJECT_NAME = 1;
    private static final int COL_QUESTION = 2;
    private static final int COL_ANSWER = 3;


    // Used by applicants to submit enquiries
    public static boolean askEnquiry(Applicant applicant, Scanner scanner) {
        System.out.print("Enter project name: ");
        String projectName = scanner.nextLine();
        System.out.print("Enter question: ");
        String question = scanner.nextLine();

        IViewFilter viewInterface = ViewFilterFactory.getViewFilter(applicant.getFilterType());
        List<Project> validProjects = viewInterface.getValidProjects();

        boolean projectFound = false;
        for (Project project : validProjects) { // If applicant is making an enquiry for a project that is available to them
            if (project.getProjectName().equalsIgnoreCase(projectName)) {
                projectFound = true;
                projectName = project.getProjectName(); // Normalising to Title Case
            }
        }
        if (!projectFound) {
            System.out.println("Project not available.");
            return false;
        }

        if (question.equals("\n")) {
            System.out.println("No question entered.");
            return false;
        }

        Enquiry enquiry = new Enquiry(applicant.getUserID(), projectName, question);
        writeNewEnquiry(enquiry);
        return true; // Successful enquiry
    }

    // Write new enquiry to csv file
    public static void writeNewEnquiry(Enquiry enquiry) {
        String answer = "\"nil\"";

        String[] newEnquiry = {
            enquiry.getUserID(),
            enquiry.getProjectName(),
            "\"" + enquiry.getQuestion() + "\"", // Formatting for csv
            answer
        };

        DataManager.appendToCSV(ENQ_CSV_PATH, newEnquiry);
    }

    public static void viewEnquiries(List<Project> projects) {
        List<String[]> enquiries = null;
        try {
            enquiries = DataManager.readCSV(ENQ_CSV_PATH); // Use utility method
        } catch (IOException e) {
            System.err.println("Error reading file: " + ENQ_CSV_PATH);
            e.printStackTrace();
        }

        if (enquiries == null || enquiries.isEmpty()) { // No enquiries
            System.out.println("No enquiries.");
            return;
        }

        if (projects == null || projects.isEmpty()) { // No projects
            System.out.println("No enquiries.");
            return;
        }

        // Header
        // TODO: make header

        for (Project project : projects) {
            for (String[] enquiry : enquiries) { // n^2 gg
                if (enquiry[COL_PROJECT_NAME].equalsIgnoreCase(project.getProjectName())) { // Finding enquiries by project name
                    // TODO: make content
                    System.out.println();
                }
            }
        }
        System.out.println();
    }
}
