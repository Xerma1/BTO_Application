package main.control.viewFilters;

import java.util.List;

import main.control.dataManagers.ProjectManager;
import main.entity.Project;

/**
 * ViewAll class implements IFilterProjectsByUserGroup and IViewFilter interfaces.
 * This class provides functionality to view all projects without any filtering.
 * It retrieves all projects from the ProjectManager and displays their details.
 */
public class ViewAll implements IFilterProjectsByUserGroup, IViewFilter {

    
    @Override
    public List<Project> getValidProjects() {
        List<Project> projects = ProjectManager.getFetchAll();
        return projects;
    }   


    @Override
    public void view(List<Project> projects) {
        // Print header
        System.out.printf("%-15s %-15s %-10s %-10s %-10s %-10s %-10s %-10s %-15s %-15s %-10s%n",
                "Project Name", "Neighbourhood", "2-Room", "Amount", "Price", "3-Room", "Amount", "Price", "Opening Date", "Closing Date", "Manager");
        System.out.println("=".repeat(140));

        // Print project details
        for (Project project : projects) {
            List<String[]> flatTypes = project.getFlatTypes();
            String[] twoRoom = flatTypes.get(0); // Assuming 2-room details are at index 0
            String[] threeRoom = flatTypes.get(1); // Assuming 3-room details are at index 1

            System.out.printf("%-15s %-15s %-10s %-10s %-10s %-10s %-10s %-10s %-15s %-15s %-10s%n",
                    project.getProjectName(), project.getNeighbourhood(),
                    twoRoom[0], twoRoom[1], twoRoom[2],
                    threeRoom[0], threeRoom[1], threeRoom[2],
                    project.getOpenDate(), project.getCloseDate(), project.getManager());
        } 
        System.out.println(); 
    } 
}
