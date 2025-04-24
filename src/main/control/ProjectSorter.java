package main.control;

import java.util.List;
import java.util.ArrayList;
import main.entity.User;
import main.entity.Project;
import main.control.dataManagers.DataManager;
import main.control.viewFilters.ISortProjects;
import main.control.viewFilters.sortTypes.SortFactory;

/**
 * Provides functionality to sort projects based on user preferences.
 * Retrieves the user's sorting preference from a CSV file and applies the appropriate sorting algorithm.
 */
public class ProjectSorter {

    private static final String FILEPATH_USERS = "data/processed/users.csv";
    private static String sortType; // The type of sorting the user has chosen

    public static List<Project> sort(List<Project> projects, User u) { //Sort and return sorted projects
        List<String[]> copyUsers = null; // Declare copyUsers as a local variable
        // find the user sort type in users.csv
        try{
            List<String[]> users = DataManager.readCSV(FILEPATH_USERS);
            // Make a modifilable copy of users
            copyUsers = new ArrayList<>(users);
        }
        catch (Exception e){
            System.out.println("Error reading file: " + FILEPATH_USERS);
            e.printStackTrace();
        }

        // get sort type from users.csv
        for (String[] user : copyUsers) {
            if (user[1].equals(u.getUserID())) {
                sortType = user[6].trim();
                break;
            }
        }

        // Sort the projects based on the chosen sort type
        System.out.println(sortType);
        ISortProjects projectSorter = SortFactory.getSortType(sortType);
        List<Project> sortedProjects = projectSorter.sortProjects(projects);
        return sortedProjects;
    }

}