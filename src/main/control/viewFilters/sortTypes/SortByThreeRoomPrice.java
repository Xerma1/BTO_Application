package main.control.viewFilters.sortTypes;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
import main.control.viewFilters.ISortProjects;
import main.entity.Project;

public class SortByThreeRoomPrice implements ISortProjects {
    @Override
    public List<Project> sortProjects(List<Project> projects) {
        // Sort projects based on the price of the 2-room flat (or any specific flat type)
        return projects.stream()
                .sorted(Comparator.comparing(project -> {
                    List<String[]> flatTypes = project.getFlatTypes();
                    String[] threeRoom = flatTypes.get(1); // 3-room details are at index 1
                    return Integer.parseInt(threeRoom[2]); // Extract and parse the price (index 2 for price)
                }))
                .collect(Collectors.toList());
    }
}
