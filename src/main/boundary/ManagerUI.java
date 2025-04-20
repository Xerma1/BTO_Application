package main.boundary;

import main.control.dataManagers.UserManager;
import main.control.dataManagers.ProjectManager;
import main.control.dataManagers.OfficerManager;
import main.control.viewFilters.*;
import main.entity.Manager;
import main.entity.User;

import java.util.Scanner;

public class ManagerUI implements IusergroupUI {

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
    public void runMenu(Scanner scanner, User userdata) {
        Manager manager = new Manager(
            userdata.getName(),
            userdata.getUserID(),
            userdata.getAge(),
            userdata.getMarried(),
            userdata.getPassword(),
            userdata.getAccessLevel()
        );

        int choice;
        do {
            System.out.println("<< Logged in as manager: " + userdata.getName() + " >>");
            System.out.println(managerMenu);
            System.out.print("Input: ");

            choice = scanner.nextInt();
            scanner.nextLine(); // consume leftover newline

            switch (choice) {
                case 1 -> manager.changePassword(scanner);

                case 2 -> {
                    IViewFilter viewInterface = ViewFilterFactory.getViewFilter("all");
                    System.out.println("Showing all projects:");
                    System.out.println();
                    viewInterface.view();
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




