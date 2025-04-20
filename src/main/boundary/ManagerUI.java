package main.boundary;

import main.control.ProjectSorter;
import main.control.dataManagers.UserManager;
import main.control.viewFilters.*;
import main.entity.Manager;
import main.entity.Project;
import main.entity.User;

import java.util.List;
import java.util.Scanner;

public class ManagerUI implements IusergroupUI {

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
            System.out.print("Input: ");

            choice = scanner.nextInt();
            scanner.nextLine();

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
                case 12 -> System.out.println("Exiting....");
                default -> System.out.print("default");
            }
        }while (choice != 12);
        
    }

}
