package main.control.viewFilters;

import java.util.List;
import main.entity.Project;

public interface ISortProjects {
    public List<Project> sortProjects(List<Project> projects); // criteria can be "name", "price", etc.
    
}
