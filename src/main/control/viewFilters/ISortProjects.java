package main.control.viewFilters;

import java.util.List;
import main.entity.Project;

/**
 * ISortProjects interface to sort projects based on user preferences.
 * This interface provides a method to sort projects based on various criteria.
 */
public interface ISortProjects {
    public List<Project> sortProjects(List<Project> projects); // criteria can be "name", "price", etc.
    
}
