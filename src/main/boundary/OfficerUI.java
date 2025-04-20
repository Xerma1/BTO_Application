package main.boundary;


import main.control.ProjectSorter;
import main.control.InputManager;
import main.control.dataManagers.ApplicationManager;
import main.control.dataManagers.BookingManager;
import main.control.dataManagers.EnquiryManager;
import main.control.dataManagers.UserManager;
import main.control.viewFilters.*;
import main.entity.Officer;
import main.entity.Project;
import main.entity.User;

import java.util.List;
import java.util.Scanner;

public class OfficerUI implements IUserGroupUI {

    private static final String officerMenu = """
                
                1. Change password
                2. View list of BTO projects

                                < Officer >

                3. Register to join BTO project as an officer
                4. View status of officer registration
                5. View details of handling projects
                6. View enquiries of handling projects
                7. Reply to enquiries 
                8. Book flat for client

                                < Applicant >

                9.  Apply for BTO project as an applicant
                10. View details of applied BTO project and application status
                11. Request booking of flat
                12. Request withdrawl from BTO application/booking
                13. Submit enquiry 
                14. View/edit/delete enquiries 
                15. Exit
                
                """;

    @Override
    public void runMenu(Scanner scanner, User user) {

        // Create instance of officer class
        Officer officer = (Officer) UserManager.createUser(user);
        String username = officer.getName();
       
        // Switch statement to process each option
        int choice;
        do{
            // Print UI
            System.out.println("<< Logged in as officer: " + username + " >>");
            System.out.println(officerMenu);
            choice = InputManager.promptUserChoice(scanner, 1, 16);

            switch (choice){
                case 1 -> {
                    officer.changePassword(scanner);
                    System.out.println("Press 'enter' to continue...");
                    scanner.nextLine();
                }
                case 2 -> {
                    IFilterProjectsByUserGroup viewInterface1 = ViewFilterFactory.getProjectByMartialStatus(officer.getMarried());
                    System.out.println("Showing all active projects available to you: ");
                    System.out.println();
                    List<Project> projects = viewInterface1.getValidProjects(); // First get valid projects

                    // Sort them by applicant's existing sortType
                    projects = ProjectSorter.sort(projects, officer); 
                    
                    // Then view them using the filter type
                    IViewFilter viewInterface2 = ViewFilterFactory.getViewFilterType(officer.getMarried()); 
                    viewInterface2.view(projects);
                    
                    // Ask users if they want to sort the projects in a new way
                    System.out.println("Would you like to sort the projects in a different way? (y/n)");
                    String sortChoice = scanner.nextLine();
                    if (sortChoice.equalsIgnoreCase("y")) {
                        SortAndReturnUI sortAndReturnUI = new SortAndReturnUI();
                        sortAndReturnUI.viewSortedProject(scanner, projects, officer);
                    } else {
                        System.out.println("Returning to main menu...");
                    }
                    System.out.println("Press 'enter' to continue...");
                    scanner.nextLine();
                }
                case 5 -> {
                    officer.viewHandling();
                    System.out.println("Press 'enter' to continue...");
                    scanner.nextLine();
                }
                case 6 -> {
                    EnquiryManager.viewEnquiries(officer);
                    System.out.println("Press 'enter' to continue...");
                    scanner.nextLine();
                }
                case 7 -> {
                    boolean isSuccessful = EnquiryManager.replyEnquiry(officer, scanner);
                    if (!isSuccessful) {
                        System.out.println("Reply unsuccessful.");
                    }
                    else {
                        System.out.println("Reply successful!");
                    }
                    System.out.println("Press 'enter' to continue...");
                    scanner.nextLine();
                }
                case 9 -> {
                    if (ApplicationManager.applyBTO(officer, scanner)) {
                        System.out.println("Applied successfully!");
                    } else {
                        System.out.println("Failed to apply.");
                    }
                    System.out.println("Press 'enter' to continue...");
                    scanner.nextLine();
                }
                case 10 -> {
                    ApplicationManager.viewApplication(officer);
                    System.out.println("Press 'enter' to continue...");
                    scanner.nextLine();
                }
                case 11 -> {
                    Boolean isSuccessful = BookingManager.initiateBooking(officer, scanner);
                    if (!isSuccessful) {
                        System.out.println("Booking unsuccessful.");
                    }
                    else {
                        System.out.println("Booking successful!");
                    }
                    System.out.println("Press 'enter' to continue...");
                    scanner.nextLine();
                }
                case 13 -> {
                    Boolean isSuccessful = EnquiryManager.createEnquiry(officer, scanner);

                    if (!isSuccessful) {
                        System.out.println("Enquiry not submitted.");
                    }
                    else {
                        System.out.println("Enquiry submitted!");
                    }
                    System.out.println("Press 'enter' to continue...");
                    scanner.nextLine();
                }
                case 14 -> {
                    EnquiryManager.viewEditDeleteEnquiries(officer, scanner);
                    System.out.println("Press 'enter' to continue...");
                    scanner.nextLine();
                }
                case 15 -> System.out.println("Exiting....");
                default -> System.out.print("default");
            }
        } while (choice != 15);
        
    }

}
