package main.control.viewFilters.sortTypes;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
import main.control.viewFilters.ISortProjects;
import main.entity.Project;

public class SortByTwoRoom implements ISortProjects {

    @Override
    public List<Project> sortProjects(List<Project> projects) {
        // Sort projects based on the price of the 2-room flat (or any specific flat type)
        return projects.stream()
                .sorted(Comparator.comparing(project -> {
                    List<String[]> flatTypes = project.getFlatTypes();
                    String[] twoRoom = flatTypes.get(0); // 2-room details are at index 0
                    return Integer.parseInt(twoRoom[1]); // Extract and parse the amount (index 1 for amount)
                }))
                .collect(Collectors.toList());
    }
}