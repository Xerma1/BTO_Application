package main.boundary;


import main.control.ProjectSorter;
import main.control.ReportGenerator;
import main.control.InputManager;
import main.control.dataManagers.ApplicationManager;
import main.control.dataManagers.EnquiryManager;
import main.control.dataManagers.OfficerRegistrationManager;
import main.control.dataManagers.ProjectEditor;
import main.control.dataManagers.UserManager;
import main.control.dataManagers.WithdrawalManager;
import main.control.viewFilters.*;
import main.entity.Manager;
import main.entity.Project;
import main.entity.User;

import java.util.List;
import java.util.Scanner;

public class ManagerUI implements IUserGroupUI {

    private static final String managerMenu = """
                
                1.  Change password
                2.  View list of BTO projects
                3.  Create/edit/delete BTO project listing
                4.  Toggle visiblity of project
                5.  View pending and approved HDB officer registrations
                6.  Approve/reject HDB officer registrations
                7.  Approve/reject applicant BTO applications
                8.  Approve/reject applicant withdrawl requests
                9.  Generate report of applicants with respective flat bookings
                10. View all enquiries of all projects
                11. View/reply to enquiries regarding own handling projects
                12. Exit

                """;

    @Override
    public void runMenu(Scanner scanner, User user) {

        // Create instance of manager class
        Manager manager = (Manager) UserManager.createUser(user);
        String username = manager.getName();
       
        // Switch statement to process each option
        int choice;
        do{
            // Print UI
            System.out.println("<< Logged in as manager: " + username + " >>");
            System.out.println(managerMenu);
            choice = InputManager.promptUserChoice(scanner, 1, 12);

            switch (choice){
                case 1 -> {
                    manager.changePassword(scanner);
                    System.out.println("Press 'enter' to continue...");
                    scanner.nextLine();
                }
                case 2 -> {
                    IFilterProjectsByUserGroup viewInterface1 = ViewFilterFactory.getProjectByMartialStatus("All");
                    System.out.println("Showing all active projects available to you: ");
                    System.out.println();
                    List<Project> projects = viewInterface1.getValidProjects(); // First get valid projects

                    // Sort them by applicant's existing sortType
                    projects = ProjectSorter.sort(projects, manager); 
                    
                    // Then view them using the filter type
                    IViewFilter viewInterface2 = ViewFilterFactory.getViewFilterType("All"); 
                    viewInterface2.view(projects);
                    
                    // Ask users if they want to sort the projects in a new way
                    System.out.println("Would you like to sort the projects in a different way? (y/n)");
                    String sortChoice = scanner.nextLine();
                    if (sortChoice.equalsIgnoreCase("y")) {
                        SortAndReturnUI sortAndReturnUI = new SortAndReturnUI();
                        sortAndReturnUI.viewSortedProject(scanner, projects, manager);
                    } else {
                        System.out.println("Returning to main menu...");
                    }
                    System.out.println("Press 'enter' to continue...");
                    scanner.nextLine();
                }

                case 3 -> {
                    System.out.println("1. Create a new project");
                    System.out.println("2. Edit an existing project");
                    System.out.println("3. Delete a project");
                    int subChoice = InputManager.promptUserChoice(scanner, 1, 3);

                    switch (subChoice) {
                        case 1 -> ProjectEditor.createProject(scanner);
                        case 2 -> ProjectEditor.editProject(scanner);
                        case 3 -> ProjectEditor.deleteProject(scanner);
                        default -> System.out.println("Invalid choice.");
                    }
                    System.out.println("Press 'enter' to continue...");
                    scanner.nextLine();
                }

                case 4 -> {
                    ProjectEditor.toggleVisibility(scanner);
                    System.out.println("Press 'enter' to continue...");
                    scanner.nextLine();
                }

                case 5 -> {
                    System.out.println("1. View pending officer registrations");
                    System.out.println("2. View approved officer registrations");
                    System.out.println("3. View all officer registrations");
                    int subChoice = InputManager.promptUserChoice(scanner, 1, 3);
                
                    switch (subChoice) {
                        case 1 -> OfficerRegistrationManager.viewOfficerRegistrations("pending");
                        case 2 -> OfficerRegistrationManager.viewOfficerRegistrations("approved");
                        case 3 -> OfficerRegistrationManager.viewOfficerRegistrations("all");
                        default -> System.out.println("Invalid choice.");
                    }
                    System.out.println("Press 'enter' to continue...");
                    scanner.nextLine();
                }

                case 6 -> {
                    OfficerRegistrationManager.manageOfficerRegistrations(scanner, manager);
                    System.out.println("Press 'enter' to continue...");
                    scanner.nextLine();
                }

                case 7 -> {
                    ApplicationManager.manageApplications(scanner, manager);
                    System.out.println("Press 'enter' to continue...");
                    scanner.nextLine();
                }

                case 8 -> {
                    WithdrawalManager.manageWithdrawalRequests(scanner, manager);
                    System.out.println("Press 'enter' to continue...");
                    scanner.nextLine();
                }

                case 9 -> {
                    ReportGenerator.generateApplicantReport(scanner);
                    System.out.println("Press 'enter' to continue...");
                    scanner.nextLine();
                }

                case 10 -> {
                    EnquiryManager.viewAllEnquiries();
                    System.out.println("Press 'enter' to continue...");
                    scanner.nextLine();
                }

                case 11 -> {
                    EnquiryManager.viewAndReplyToEnquiries(manager, scanner);
                    System.out.println("Press 'enter' to continue...");
                    scanner.nextLine();
                }
                
                case 12 -> System.out.println("Exiting....");
                default -> System.out.print("default");
            }
        }while (choice != 12);
        
    }

}
