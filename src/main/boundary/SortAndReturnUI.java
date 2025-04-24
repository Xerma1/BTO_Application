package main.boundary;

import java.io.IOException;
import java.util.List;
import java.util.Scanner;
import java.util.ArrayList;

import main.entity.Project;
import main.entity.User;
import main.entity.Manager;
import main.control.dataManagers.DataManager;
import main.control.ProjectSorter;
import main.control.viewFilters.IViewFilter;
import main.control.viewFilters.ViewFilterFactory;

/**
 * SortAndReturnUI class to handle the user interface for sorting projects.
 */
public class SortAndReturnUI {
    private static final String FILEPATH_USERS = "data/processed/users.csv";
    private static final String UI = 
            """
            1. Sort by name
            2. Sort by location
            3. Sort by 2-room flat availability
            4. Sort by 3-room flat availability
            5. Sort by price of 2-room flat
            6. Sort by price of 3-room flat """;

        
    public void viewSortedProject(Scanner scanner, List<Project> projects, User u) {
        // Prompt the user for sorting criteria
        String category;
        do{ 
            System.out.println("Choose your sorting criteria: ");
            System.out.println(UI);
            System.out.print("Input: ");
            int choice = scanner.nextInt();
            scanner.nextLine();

            category = switch (choice) {
                case 1 -> "name";
                case 2 -> "location";
                case 3 -> "2-room flat availability";
                case 4 -> "3-room flat availability";
                case 5 -> "price of 2-room flat";
                case 6 -> "price of 3-room flat";
                default -> {
                    System.out.println("Invalid choice. Please try again.");
                    yield null; 
                }
            };
            if (category != null) {
                break;
            }
        } while (true); 

        //Updates users.csv with the new fiter type
        try {
            // Read all rows from the CSV file
            List<String[]> users = DataManager.readCSV(FILEPATH_USERS);
            // Make a modifilable copy of users
            List<String[]> copyUsers = new ArrayList<>(users);
            boolean isUpdated = false;

            // Iterate through the rows and update the matching one
            for (String[] user : copyUsers) {
                if (user[1].equals(u.getUserID())) { // Assuming the identifier is in the first column
                    user[6] = category; // Update the row with the new filter type
                    isUpdated = true;
                    break;
                }
            }

            if (isUpdated) {
                // Write the updated rows back to the file
                DataManager.writeCSV(FILEPATH_USERS, copyUsers);
            } else {
                System.out.println("No matching record found to update.");
            }
        } catch (IOException e) {
            System.out.println("Error updating the CSV file: " + e.getMessage());
        }

        // Get the sorted projects
        projects = ProjectSorter.sort(projects, u);
        //view the sorted projects
        IViewFilter viewInterface;
        if (u instanceof Manager) {
            // Add logic for Manager type user here
            viewInterface = ViewFilterFactory.getViewFilterType("all");
        }
        else{
            viewInterface = ViewFilterFactory.getViewFilterType(u.getMarried());
        }
        System.out.println("Sorted projects: ");
        System.out.println();
        viewInterface.view(projects); // Display the sorted projects

    }

}