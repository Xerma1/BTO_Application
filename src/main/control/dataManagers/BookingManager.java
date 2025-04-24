package main.control.dataManagers;

import java.util.Scanner;
import java.util.ArrayList;
import java.util.List;
import java.io.IOException;

import main.control.InputManager;
import main.entity.Applicant;
import main.entity.Officer;
import main.entity.Project;

/**
 * Manages booking operations for flats in the BTO application system.
 * Includes functionality for handling booking requests and updating booking statuses.
 */
public class BookingManager extends DataManager {

    private static final int COL_PROJ_NAME = 4; 
    private static final int COL_FLAT_TYPE = 5; 
    private static final int COL_STATUS = 9;
    
    private static final String FILEPATH_BOOKING = "data/processed/booking_requests.csv";
    private static final String FILEPATH_BTO = "data/processed/bto_applications.csv";
    private static enum status {
        PENDING, SUCCESSFUL, UNSUCCESSFUL, BOOKED
    }
    
    public static boolean initiateBooking(Applicant applicant, Scanner scanner) {

        // Get the application details of applicant
        String[] application = null;
        List<String[]> applications = ApplicationManager.getFetchAllApplications();

        if (applications == null || applications.isEmpty()) {
            return false; // No applications exist
        }

        for (String[] appli : applications) {
            if (appli.length > 1 && appli[1].trim().equals(applicant.getUserID())) {
                application = appli; 
            }
        }
        // Otherwise, check if the status == SUCCESSFUL
        if (!application[COL_STATUS].equals(status.SUCCESSFUL.name())){
            System.out.println("Your application cannot proceed to book flat");
            return false;
        }

        // Check if applicant has already requested to book/booked a flat

        boolean hasBooked = hasRequestedBooking(applicant);
        if (hasBooked || application[COL_STATUS].equals(status.BOOKED.name())) {
            System.out.println("You have already requested to book/booked a flat");
            return false;
        }

        // Get applicant to review the application details and confirm their booking
        System.out.println("Please review your application and confirm booking(Y = yes, N = no): ");
        ApplicationManager.viewApplication(applicant);
        String response = scanner.nextLine();
        if (response.equalsIgnoreCase("N")){
            return false;
        }

        // Proceed with booking request
        String projName = application[COL_PROJ_NAME];
        Project appliedProject = ProjectManager.getProjectByName(projName);
        String[] officers = appliedProject.getOfficers();
        String officersString = "\"" + String.join(",", officers) + "\"";

        String[] bookingRequest = {
            applicant.getUserID(),                      // UserID
            projName,                                   // Project Name
            application[COL_FLAT_TYPE],                 // Flat Type (Column 5 in application)
            officersString,       // Selected Officer            
            status.PENDING.name()                       // Booking outcome
        };

        appendToCSV(FILEPATH_BOOKING, bookingRequest);
        System.out.println("Booking request has been submitted successfully.");
        return true;
    
        // Probably require a new csv that file that stores Booking request that contains project name, type of flat, officer to confirm
        // Finish
    }

    public static boolean hasRequestedBooking(Applicant applicant){
        // Check if applicant is already booking a flat
        List<String[]> bookingRequests;
        try {
            bookingRequests = readCSV(FILEPATH_BOOKING);
        } catch (IOException e) {
            System.out.println("Error reading booking requests: " + e.getMessage());
            return false;
        }
        if (bookingRequests != null) {
            for (String[] booking : bookingRequests) {
                if (booking.length > 1 && booking[0].trim().equals(applicant.getUserID())) {
                    return true;
                }
            }
        }
        return false;
    }

    public static boolean hasSuccessfullyBooked(Applicant applicant){
        // Check if applicant is in booking_rquest.csv
        List<String[]> bookingRequests;
        try {
            bookingRequests = readCSV(FILEPATH_BOOKING);
        } catch (IOException e) {
            System.out.println("Error reading booking requests: " + e.getMessage());
            return false;
        }
        if (bookingRequests != null) {
            for (String[] booking : bookingRequests) {
                if (booking[0].trim().equalsIgnoreCase(applicant.getUserID()) && booking[4].trim().equalsIgnoreCase(status.BOOKED.name())) {
                    return true;
                }
            }
        }
        return false;
    }

    public static void bookFlatForClient(Scanner scanner, Officer officer) {
        try {
            // Fetch all projects
            List<Project> projects = ProjectManager.getFetchAll();
            if (projects == null || projects.isEmpty()) {
                System.out.println("No projects available.");
                return;
            }

            // Display projects handled by the officer
            System.out.println("Projects you are handling:");
            List<Project> officerProjects = new ArrayList<>();
            for (Project project : projects) {
                if (java.util.Arrays.asList(project.getOfficers()).contains(officer.getName())) {
                    officerProjects.add(project);
                }
            }

            if (officerProjects.isEmpty()) {
                System.out.println("You are not handling any projects.");
                return;
            }

            for (int i = 0; i < officerProjects.size(); i++) {
                System.out.printf("%d. %s (%s)%n", i + 1, officerProjects.get(i).getProjectName(), officerProjects.get(i).getNeighbourhood());
            }

            // Prompt for project selection
            System.out.print("Enter the number of the project to view booking requests (type '0' to cancel): ");
            int projectChoice = InputManager.promptUserChoice(scanner, 0, officerProjects.size());
            if (projectChoice == 0) {
                System.out.println("Operation cancelled.");
                return;
            }

            Project selectedProject = officerProjects.get(projectChoice - 1);

            // Fetch all booking requests
            List<String[]> bookingRequests = readCSV("data/processed/booking_requests.csv");
            List<String[]> projectBookingRequests = new ArrayList<>();

            // Filter booking requests for the selected project
            for (String[] request : bookingRequests) {
                if (request[1].equalsIgnoreCase(selectedProject.getProjectName()) && request[4].equalsIgnoreCase("PENDING")) {
                    projectBookingRequests.add(request);
                }
            }

            if (projectBookingRequests.isEmpty()) {
                System.out.println("No pending booking requests for this project.");
                return;
            }

            // Display booking requests
            System.out.println("Pending Booking Requests:");
            System.out.println("");
            System.out.printf("%-5s %-15s %-15s %-20s %-15s%n", "ID", "Applicant ID", "Flat Type", "Officers", "Status");
            System.out.println("=".repeat(80));
            for (int i = 0; i < projectBookingRequests.size(); i++) {
                String[] request = projectBookingRequests.get(i);
                System.out.printf("%-5d %-15s %-15s %-20s %-15s%n", i + 1, request[0], request[2], request[3], request[4]);
            }

            // Prompt for booking request selection
            System.out.print("Enter the ID of the booking request to complete (type '0' to cancel): ");
            int requestChoice = InputManager.promptUserChoice(scanner, 0, projectBookingRequests.size());
            if (requestChoice == 0) {
                System.out.println("Operation cancelled.");
                return;
            }

            String[] selectedRequest = projectBookingRequests.get(requestChoice - 1);
            String clientID = selectedRequest[0];
            String flatType = selectedRequest[2];

            // Check flat availability
            List<String[]> flatTypes = selectedProject.getFlatTypes();
            String[] selectedFlatType = null;
            for (String[] type : flatTypes) {
                if (type[0].equalsIgnoreCase(flatType.trim())) {
                    selectedFlatType = type;
                    System.out.println("Client ID = " + clientID);
                    break;
                }
            }

            if (selectedFlatType == null) {
                System.out.println("Flat type " + flatType + " not found in the project.");
                return;
            }

            int availableFlats = Integer.parseInt(selectedFlatType[1]);
            if (availableFlats <= 0) {
                System.out.println("No flats available for the selected type.");
                return;
            }

            // Update the application status
            List<String[]> applications = readCSV(FILEPATH_BTO);
            for (String[] application : applications) {
                if (application[1].equalsIgnoreCase(clientID)) {
                    application[9] = "BOOKED";
                    break;
                }
            }
            
            writeCSV(FILEPATH_BTO, applications);

            // Update the booking request status
            selectedRequest[4] = "SUCCESSFUL";
            writeCSV(FILEPATH_BOOKING, bookingRequests);

            System.out.println("Flat booked successfully for client.");
        } catch (IOException e) {
            System.err.println("Error booking flat: " + e.getMessage());
        }
    }

}
