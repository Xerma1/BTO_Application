package main.boundary;

import main.control.InputManager;
import main.control.dataManagers.UserManager;
import main.control.viewFilters.*;
import main.entity.Manager;
import main.entity.User;

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
            choice = InputManager.promptUserChoice(scanner, 1, 12);

            switch (choice){
                case 1 -> {
                    manager.changePassword(scanner);
                    System.out.println("Press 'enter' to continue...");
                    scanner.nextLine();
                }
                case 2 -> {
                    IViewFilter viewInterface = ViewFilterFactory.getViewFilter("all");
                    System.out.println("Showing all projects: ");
                    System.out.println();
                    viewInterface.view();
                    System.out.println("Press 'enter' to continue...");
                    scanner.nextLine();
                }
                case 12 -> System.out.println("Exiting....");
                default -> System.out.print("default");
            }
        }while (choice != 12);
        
    }

}
