package main.control.dataManagers;

import main.entity.Manager;
import main.entity.Project;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class WithdrawalManager extends DataManager {
    private static final String WITHDRAWAL_CSV_PATH = "data/processed/withdrawal_requests.csv";
    private static final String APPLICATION_CSV_PATH = "data/processed/bto_applications.csv";
    private static final String BOOKING_CSV_PATH = "data/processed/booking_requests.csv";

    public static void manageWithdrawalRequests(Scanner scanner, Manager manager) {
        try {
            // Fetch all withdrawal requests
            List<String[]> withdrawalRequests = readCSV(WITHDRAWAL_CSV_PATH);
            List<String[]> updatedWithdrawalRequests = new ArrayList<>();

            System.out.println("Pending Withdrawal Requests for Your Projects:");
            System.out.printf("%-5s %-15s %-20s%n", "ID", "Applicant ID", "Project Name");
            System.out.println("=".repeat(50));

            int id = 1;
            List<String[]> pendingRequests = new ArrayList<>();
            for (String[] request : withdrawalRequests) {
                if (request[2].equalsIgnoreCase(manager.getName())) { // Check if the manager is handling the project
                    System.out.printf("%-5d %-15s %-20s%n", id, request[0], request[1]);
                    pendingRequests.add(request);
                    id++;
                } else {
                    updatedWithdrawalRequests.add(request); // Keep non-relevant requests
                }
            }

            if (pendingRequests.isEmpty()) {
                System.out.println("No withdrawal requests found for your projects.");
                return;
            }

            // Prompt the manager to approve or reject a withdrawal request
            System.out.print("Enter the ID of the withdrawal request to manage (type '0' to cancel): ");
            int choice = scanner.nextInt();
            scanner.nextLine(); // Clear the newline character

            if (choice == 0) {
                System.out.println("Operation cancelled.");
                return;
            }

            if (choice < 1 || choice > pendingRequests.size()) {
                System.out.println("Invalid ID. Operation cancelled.");
                return;
            }

            // Get the selected withdrawal request
            String[] selectedRequest = pendingRequests.get(choice - 1);
            String applicantID = selectedRequest[0];
            String projectName = selectedRequest[1];

            // Prompt for approval or rejection
            System.out.print("Approve or reject this withdrawal request? (approve/reject): ");
            String decision = scanner.nextLine().trim();
            System.out.println("");
            
            if (decision.equalsIgnoreCase("approve")) {

                // Increment the flat count if a booking with status "SUCCESSFUL" is deleted
                incrementFlatCountIfSuccessful(projectName, applicantID);

                // Delete the relevant application and booking request
                deleteApplicationAndBooking(applicantID, projectName);

                System.out.println("Withdrawal request approved. Relevant records have been updated.");
            } else if (decision.equals("reject")) {
                updatedWithdrawalRequests.add(selectedRequest); // Keep the request if rejected
                System.out.println("Withdrawal request rejected.");
            } else {
                System.out.println("Invalid decision. Operation cancelled.");
                return;
            }

            // Write the updated withdrawal requests back to the CSV file
            writeCSV(WITHDRAWAL_CSV_PATH, updatedWithdrawalRequests);
            System.out.println("Changes saved successfully!");

        } catch (IOException e) {
            System.err.println("Error managing withdrawal requests: " + e.getMessage());
        }
    }

    // Utility method to delete application and booking requests
    private static void deleteApplicationAndBooking(String applicantID, String projectName) throws IOException {
        // Delete from bto_applications.csv
        List<String[]> applications = readCSV(APPLICATION_CSV_PATH);
        List<String[]> updatedApplications = new ArrayList<>();
        for (String[] application : applications) {
            if (application.length < 5) {
                continue;
            }
            if (!(application[1].equalsIgnoreCase(applicantID) && application[4].equalsIgnoreCase(projectName))) {
                updatedApplications.add(application);
            }
        }
        writeCSV(APPLICATION_CSV_PATH, updatedApplications);

        // Delete from booking_requests.csv
        List<String[]> bookings = readCSV(BOOKING_CSV_PATH);
        List<String[]> updatedBookings = new ArrayList<>();
        for (String[] booking : bookings) {
            if (!(booking[0].equalsIgnoreCase(applicantID) && booking[1].equalsIgnoreCase(projectName))) {
                updatedBookings.add(booking);
            }
        }
        writeCSV(BOOKING_CSV_PATH, updatedBookings);
    }

    // Utility method to increment flat count if a booking with status "SUCCESSFUL" is deleted
    private static void incrementFlatCountIfSuccessful(String projectName, String applicantID) throws IOException {
        // Read all bookings
        List<String[]> bookings = readCSV(BOOKING_CSV_PATH);
       
        for (String[] booking : bookings) { 
            // Check if the booking matches the applicant and project, and has a "SUCCESSFUL" status
            if (booking[0].equalsIgnoreCase(applicantID) && booking[1].equalsIgnoreCase(projectName) && booking[4].equalsIgnoreCase("SUCCESSFUL")) {
                // Get the project details
                Project project = ProjectManager.getProjectByName(projectName);
                if (project != null) {
                    String roomType = booking[2].trim(); // Room type from the booking
                    int roomIndex = roomType.equalsIgnoreCase("2-room") ? 0 : 1; // Determine the room type index
    
                    // Create a mutable copy of the flat types list
                    List<String[]> flatTypes = new ArrayList<>(project.getFlatTypes());
                    String[] roomDetails = flatTypes.get(roomIndex); // Get the room details
    
                    // Increment the available rooms count
                    int availableRooms = Integer.parseInt(roomDetails[1]);
                    roomDetails[1] = String.valueOf(availableRooms + 1); // Increment the count
                    flatTypes.set(roomIndex, roomDetails); // Update the room details

                    // Update the project with the modified flat types
                    project.setFlatTypes(flatTypes);
    
                    // Save the updated project back to the CSV file
                    ProjectManager.updateProject(project);
                    System.out.println("Flat count incremented for project: " + projectName + ", Room Type: " + roomType);

                } else {
                    System.err.println("Project not found: " + projectName);
                }
                break; // Exit the loop after processing the successful booking
            }
        }
    }
}