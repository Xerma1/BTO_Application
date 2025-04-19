package main.control.viewFilters;

import main.control.TimeManager;
import main.control.dataManagers.ProjectManager;
import main.entity.Project;

import java.util.ArrayList;
import java.util.List;

public class ViewSingle implements IViewFilter {

    @Override
    public void view() {
        List<Project> projects = ProjectManager.getFetchAll();

        if (projects == null || projects.isEmpty()) {
            System.out.println("No projects available.");
            return;
        }

        // Print header
        System.out.printf("%-15s %-15s %-10s %-10s %-10s %-15s %-15s %-10s%n",
                "Project Name", "Neighbourhood", "2-Room", "Amount", "Price", "Opening Date", "Closing Date", "Manager");
        System.out.println("=".repeat(140));

        // Print project details
        for (Project project : projects) {
            List<String[]> flatTypes = project.getFlatTypes();
            String[] twoRoom = flatTypes.get(0); // Assuming 2-room details are at index 0

            if(TimeManager.isAfter(project.getCloseDate())) { // Hides expired projects
                continue;
            }

            if (!twoRoom[1].trim().equals("0")) { // If there is at least one 2-room flat
                System.out.printf("%-15s %-15s %-10s %-10s %-10s %-15s %-15s %-10s%n",
                        project.getProjectName(), project.getNeighbourhood(),
                        twoRoom[0], twoRoom[1], twoRoom[2],
                        project.getOpenDate(), project.getCloseDate(), project.getManager());
            }
        }
        System.out.println();
    }

    @Override
    public List<Project> getValidProjects() {
        List<Project> projects = ProjectManager.getFetchAll();

        if (projects == null || projects.isEmpty()) {
            return new ArrayList<>(); // Return an empty list instead of null
        }

        List<Project> validProjects = new ArrayList<>();
        for (Project project : projects) {
            List<String[]> flatTypes = project.getFlatTypes();
            String[] twoRoom = flatTypes.get(0); // Assuming 2-room details are at index 0

            if (!twoRoom[1].trim().equals("0")) { // If there is at least one 2-room flat
                validProjects.add(project);
            }
        }
        return validProjects;
    }
}


