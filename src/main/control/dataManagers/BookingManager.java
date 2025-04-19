package main.control.dataManagers;

import java.util.Scanner;
import java.util.List;
import java.io.IOException;


import main.entity.Applicant;
import main.entity.Project;


public class BookingManager {

    private static final int COL_PROJ_NAME = 4; 
    private static final int COL_FLAT_TYPE = 5; 
    private static final int COL_STATUS = 9;
    
    private static final String FILEPATH_BOOKING = "data/processed/booking_requests.csv";
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

        // Check if applicant is already booking a flat

        boolean hasBooked = hasBooked(applicant);
        if (hasBooked){
            System.out.println("You have already booked a flat");
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

        DataManager.appendToCSV(FILEPATH_BOOKING, bookingRequest);
        System.out.println("Booking request has been submitted successfully.");
        return true;
    
        // Probably require a new csv that file that stores Booking request that contains project name, type of flat, officer to confirm
        // Finish
    }

    public static boolean hasBooked(Applicant applicant){
        // Check if applicant is already booking a flat
        List<String[]> bookingRequests;
        try {
            bookingRequests = DataManager.readCSV(FILEPATH_BOOKING);
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
}
