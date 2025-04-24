package main.control;

import java.util.List;
import java.util.Scanner;
import main.control.dataManagers.ApplicationManager;

/**
 * Generates reports for the BTO application system.
 * Includes functionality for creating reports on applicants and their bookings.
 */
public class ReportGenerator {
    public static void generateApplicantReport(Scanner scanner) {
        try {
            // Fetch all applications
            List<String[]> applications = ApplicationManager.getFetchAllApplications();
            if (applications == null || applications.isEmpty()) {
                System.out.println("No applications found.");
                return;
            }

            // Prompt the manager for filter criteria
            System.out.println("Generate report of applicants based on:");
            System.out.println("1. All applicants");
            System.out.println("2. Married applicants");
            System.out.println("3. Single applicants");
            System.out.println("4. Specific flat type (2-room or 3-room)");
            int choice = InputManager.promptUserChoice(scanner, 1, 4);

            String filterType = null;
            if (choice == 2) {
                filterType = "Married";
            } else if (choice == 3) {
                filterType = "Single";
            } else if (choice == 4) {
                System.out.print("Enter flat type (type '2-room' or '3-room'): ");
                filterType = scanner.nextLine().trim();
            }

            // Generate the report
            System.out.printf("%-15s %-15s %-5s %-15s %-15s %-10s%n", 
                    "Name", "Project Name", "Age", "Marital Status", "Flat Type", "Status");
            System.out.println("=".repeat(80));

            boolean found = false;
            for (String[] application : applications) {
                String name = application[0];
                String projectName = application[4];
                String age = application[2];
                String maritalStatus = application[3];
                String flatType = application[5];
                String status = application[9];

                // Apply filters
                if (choice == 2 && !maritalStatus.equalsIgnoreCase("Married")) {
                    continue;
                }
                if (choice == 3 && !maritalStatus.equalsIgnoreCase("Single")) {
                    continue;
                }
                if (choice == 4 && !flatType.equalsIgnoreCase(filterType)) {
                    continue;
                }

                // Print the filtered application
                System.out.printf("%-15s %-15s %-5s %-15s %-15s %-10s%n", 
                        name, projectName, age, maritalStatus, flatType, status);
                found = true;
            }

            if (!found) {
                System.out.println("No applicants are found.");
            }
        } catch (Exception e) {
            System.err.println("Error generating report: " + e.getMessage());
        }
    }
}
