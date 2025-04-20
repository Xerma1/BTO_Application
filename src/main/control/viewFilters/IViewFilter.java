package main.control.viewFilters;

import java.util.List;
import main.entity.Project;

public interface IViewFilter {
    public void view(List<Project> projects); // View the projects based on the filter criteria
       
}
