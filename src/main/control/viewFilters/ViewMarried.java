package main.control.viewFilters;

import main.control.TimeManager;
import main.control.dataManagers.ProjectManager;
import main.entity.Project;

import java.util.List;
import java.util.ArrayList;

public class ViewMarried implements IViewFilter {

    public void view() {
        List<Project> projects = ProjectManager.getFetchAll();

        if (projects == null || projects.isEmpty()) {
            System.out.println("No projects available.");
            return;
        }

        // Print header
        System.out.printf("%-15s %-15s %-10s %-10s %-10s %-10s %-10s %-10s %-15s %-15s %-10s%n",
                "Project Name", "Neighbourhood", "2-Room", "Amount", "Price", "3-Room", "Amount", "Price", "Opening Date", "Closing Date", "Manager");
        System.out.println("=".repeat(140));

        // Print project details
        for (Project project : projects) {
            List<String[]> flatTypes = project.getFlatTypes();
            String[] twoRoom = flatTypes.get(0); // Assuming 2-room details are at index 0
            String[] threeRoom = flatTypes.get(1); // Assuming 3-room details are at index 1

            if(TimeManager.isAfter(project.getCloseDate())) { // Hides expired projects
                continue;
            }

            if (!(twoRoom[1].trim().equals("0") && threeRoom[1].trim().equals("0"))) { // If there is at least one 2 or 3 room flat
                System.out.printf("%-15s %-15s %-10s %-10s %-10s %-10s %-10s %-10s %-15s %-15s %-10s%n",
                        project.getProjectName(), project.getNeighbourhood(),
                        twoRoom[0], twoRoom[1], twoRoom[2],
                        threeRoom[0], threeRoom[1], threeRoom[2],
                        project.getOpenDate(), project.getCloseDate(), project.getManager());
            }
        }
        System.out.println();
    }

    public List<Project> getValidProjects() {
        List<Project> projects = ProjectManager.getFetchAll();

        if (projects == null || projects.isEmpty()) {
            return new ArrayList<>(); // Return an empty list instead of null
        }

        List<Project> validProjects = new ArrayList<>();
        for (Project project : projects) {
            List<String[]> flatTypes = project.getFlatTypes();

            if (flatTypes == null || flatTypes.size() < 2) {
                continue; // Skip projects with invalid or incomplete flat type data
            }

            String[] twoRoom = flatTypes.get(0); // Assuming 2-room details are at index 0
            String[] threeRoom = flatTypes.get(1); // Assuming 3-room details are at index 1

            if (twoRoom.length > 1 && threeRoom.length > 1) {
                if (!(twoRoom[1].trim().equals("0") && threeRoom[1].trim().equals("0"))) { 
                    // If there is at least one 2-room or 3-room flat available
                    validProjects.add(project);
                }
            }
        }
        return validProjects;
    }
}
