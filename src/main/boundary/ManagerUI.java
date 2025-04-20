package main.boundary;


import main.control.ProjectSorter;
import main.control.InputManager;
import main.control.dataManagers.UserManager;
import main.control.dataManagers.ProjectManager;
import main.control.dataManagers.OfficerManager;
import main.control.viewFilters.*;
import main.entity.Manager;
import main.entity.Project;
import main.entity.User;
import main.control.dataManagers.DataManager;

import java.util.List;
import java.util.Scanner;

public class ManagerUI implements IUserGroupUI {

    private static final String managerMenu = """
                
                1.  Change password
                2.  View list of BTO projects
                3.  Create/edit/delete BTO project listing
                4.  Toggle visibility of project
                5.  View pending and approved HDB officer registrations
                6.  Approve/reject HDB officer registrations
                7.  Approve/reject applicant BTO applications
                8.  Approve/reject applicant withdrawal requests
                9.  Generate report of applicants with respective flat bookings
                10. View all enquiries of all projects
                11. View/reply to enquiries regarding own handling projects
                12. Exit

                """;

    @Override
    public void runMenu(Scanner scanner, User user) {

        Manager manager = (Manager) UserManager.createUser(user);
        String username = manager.getName();

        int choice;
        do {
            System.out.println("<< Logged in as manager: " + username + " >>");
            System.out.println(managerMenu);
            choice = InputManager.promptUserChoice(scanner, 1, 12);


            switch (choice) {
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

                case 3 -> ProjectManager.createEditDeleteProject(scanner);

                case 4 -> ProjectManager.toggleProjectVisibility(scanner);

                case 5 -> OfficerManager.viewOfficerRegistrations();

                case 6 -> OfficerManager.approveRejectOfficerRegistrations(scanner);

                case 12 -> System.out.println("Exiting Manager menu...");

                default -> System.out.println("Invalid choice. Try again.");
            }
        } while (choice != 12);
    }

} 




