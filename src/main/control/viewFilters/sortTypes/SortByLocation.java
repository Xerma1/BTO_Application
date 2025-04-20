package main.control.viewFilters.sortTypes;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
import main.control.viewFilters.ISortProjects;
import main.entity.Project;

public class SortByLocation implements ISortProjects {
    @Override
    public List<Project> sortProjects(List<Project> projects) {
        // Sort projects based on the given criteria
        return projects.stream()
                .sorted(Comparator.comparing(Project::getNeighbourhood)) // Sort by location (neighbourhood)
                .collect(Collectors.toList()); // Collect the sorted stream into a list

    
    }
}
