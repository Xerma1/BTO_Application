package main.boundary;

import main.control.dataManagers.UserManager;
import main.control.ProjectSorter;
import main.control.dataManagers.ApplicationManager;
import main.control.dataManagers.BookingManager;
import main.control.dataManagers.EnquiryManager;
import main.control.viewFilters.IFilterProjectsByUserGroup;
import main.control.viewFilters.IViewFilter;
import main.control.viewFilters.ViewFilterFactory;
import main.entity.Applicant;
import main.entity.User;
import main.entity.Project;

import java.util.Scanner;
import java.util.List;

public class ApplicantUI implements IusergroupUI {

     private static final String applicantMenu = """
                
                1.  Change password
                2.  View list of BTO projects
                3.  Apply for BTO project
                4.  View details of applied BTO project and application status
                5.  Request booking of flat
                6.  Request withdrawl from BTO application/booking
                7.  Submit enquiry
                8.  View/edit/delete enquiries
                9. Exit

                """;
    @Override
    public void runMenu(Scanner scanner, User user) {

        // Create instance of applicant class
        Applicant applicant = (Applicant) UserManager.createUser(user);
        String username = applicant.getName();

        // Switch statement to process each option
        int choice;
        do {
            // Print UI
            System.out.println("<< Logged in as applicant: " + username + " >>");
            System.out.println(applicantMenu);
            System.out.print("Input: ");
            choice = scanner.nextInt();
            scanner.nextLine();

            switch (choice) {
                case 1 -> {
                    applicant.changePassword(scanner);
                    System.out.println("Press 'enter' to continue...");
                    scanner.nextLine();
                }
                case 2 -> {
                    IFilterProjectsByUserGroup viewInterface1 = ViewFilterFactory.getProjectByMartialStatus(applicant.getMarried());
                    System.out.println("Showing all active projects available to you: ");
                    System.out.println();
                    List<Project> projects = viewInterface1.getValidProjects(); // First get valid projects

                    // Sort them by applicant's existing sortType
                    projects = ProjectSorter.sort(projects, applicant); 
                    
                    // Then view them using the filter type
                    IViewFilter viewInterface2 = ViewFilterFactory.getViewFilterType(applicant.getMarried()); 
                    viewInterface2.view(projects);
                    
                    // Ask users if they want to sort the projects in a new way
                    System.out.println("Would you like to sort the projects in a different way? (y/n)");
                    String sortChoice = scanner.nextLine();
                    if (sortChoice.equalsIgnoreCase("y")) {
                        SortAndReturnUI sortAndReturnUI = new SortAndReturnUI();
                        sortAndReturnUI.viewSortedProject(scanner, projects, applicant);
                    } else {
                        System.out.println("Returning to main menu...");
                    }
                    System.out.println("Press 'enter' to continue...");
                    scanner.nextLine();
                }
                case 3 -> {                   
                    if (ApplicationManager.applyBTO(applicant, scanner)) {
                        System.out.println("Applied successfully!");
                    } else {
                        System.out.println("Failed to apply.");
                    }
                    System.out.println("Press 'enter' to continue...");
                    scanner.nextLine();
                    
                }
                case 4 -> {
                    System.out.println("Viewing application details: ");
                    ApplicationManager.viewApplication(applicant);
                    System.out.println("Press 'enter' to continue...");
                    scanner.nextLine();
                }
                case 5 -> {
                    Boolean isSuccessful = BookingManager.initiateBooking(applicant, scanner);
                    if (!isSuccessful) {
                        System.out.println("Booking unsuccessful.");
                    }
                    else {
                        System.out.println("Booking successful!");
                    }
                    System.out.println("Press 'enter' to continue...");
                    scanner.nextLine();
                }
                case 6 -> {
                    Boolean isSuccessful = ApplicationManager.requestWithdrawal(applicant, scanner);
                    if (!isSuccessful) {
                        System.out.println("Withdrawl request is unsuccessful.");
                    }
                    else {
                        System.out.println("Withdrawl request successfully lodged!");
                    }
                    System.out.println("Press 'enter' to continue...");
                    scanner.nextLine();
                }
                case 7 -> {
                    Boolean isSuccessful = EnquiryManager.askEnquiry(applicant, scanner);
                    if (!isSuccessful) {
                        System.out.println("Enquiry not submitted.");
                    }
                    else {
                        System.out.println("Enquiry submitted!");
                    }
                    System.out.println("Press 'enter' to continue...");
                    scanner.nextLine();
                }
                
                case 9 -> System.out.println("Exiting....");
                default -> System.out.print("default");
            }
        } while (choice != 9);
        
    }

}
