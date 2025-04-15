package main.boundary;

import java.util.Scanner;
import main.control.dataManager;
import main.control.viewFilters.IviewFilter;
import main.control.viewFilters.viewFilterFactory;
import main.entity.applicant;
import main.entity.user;

public class applicantUI implements IusergroupUI {

     private static final String applicantMenu = """
                
                1.  Change password
                2.  View list of BTO projects
                3.  Apply for BTO project
                4.  View details of applied BTO project and application status
                5.  Book flat
                6.  View receipt of booked flat
                7.  Request withdrawl from BTO application/booking
                8.  Submit enquiry
                9.  View/edit/delete enquiries
                10. Exit

                """;
    @Override
    public void runMenu(Scanner scanner, String username, String userID) {

        // Create instance of applicant class
            user userdata = dataManager.getFetch(userID);
            String name = userdata.getName();
            String ID = userdata.getUserID();
            int age = userdata.getAge();
            boolean married = userdata.getMarried();

            applicant Applicant = new applicant(name, ID, age, married);
       
        // Switch statement to process each option
        int choice;
        do{
            // Print UI
            System.out.println("<< Viewing as applicant: " + username + " >>");
            System.out.println(applicantMenu);
            System.out.print("Input: ");
            choice = scanner.nextInt();
            scanner.nextLine();

            switch (choice){
                case 1 -> {
                    Applicant.changePassword();
                }
                case 2 -> {
                    IviewFilter viewInterface = viewFilterFactory.getViewFilter(Applicant.filterType);
                    viewInterface.view();
                    System.out.println("Press 'enter' to continue...");
                    scanner.nextLine();
                    }
                
                case 10 -> System.out.println("Exiting....");
                default -> System.out.print("default");
            }
        }while (choice != 10);
        
    }

}
