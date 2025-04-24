package main.control.viewFilters;

import java.util.List;
import main.entity.Project;

/**
 * IViewFilter interface to filter projects based on user preferences.
 * This interface provides a method to view projects based on various criteria.
 */
public interface IViewFilter {
    public void view(List<Project> projects); // View the projects based on the filter criteria
       
}
